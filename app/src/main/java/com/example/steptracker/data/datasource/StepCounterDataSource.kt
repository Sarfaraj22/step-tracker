package com.example.steptracker.data.datasource

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.steptracker.domain.model.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps the hardware TYPE_STEP_COUNTER sensor into a cold Flow<Int> that emits the
 * number of steps taken today.
 *
 * TYPE_STEP_COUNTER reports the total cumulative count since the last device reboot,
 * so we persist a "baseline" (the sensor value captured at the first reading of each
 * calendar day) in SharedPreferences.  Today's steps = currentSensorValue - baseline.
 *
 * Baseline is reset automatically when the epoch-day stored alongside it differs from
 * today's epoch-day — i.e., at the first sensor event after midnight.
 */
@Singleton
class StepCounterDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val prefs =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val isStepCounterAvailable: Boolean get() = stepCounterSensor != null

    val todayStepCount: Flow<Int> = callbackFlow {
        val sensor = stepCounterSensor
        if (sensor == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val totalSteps = event.values[0].toLong()
                val todayEpochDay = DateUtils.todayEpochDay()
                val savedEpochDay = prefs.getLong(KEY_BASELINE_DATE, -1L)

                if (savedEpochDay != todayEpochDay) {
                    // First event for a new calendar day — record this value as baseline
                    prefs.edit()
                        .putLong(KEY_BASELINE_STEPS, totalSteps)
                        .putLong(KEY_BASELINE_DATE, todayEpochDay)
                        .apply()
                    trySend(0)
                } else {
                    val baseline = prefs.getLong(KEY_BASELINE_STEPS, totalSteps)
                    val todaySteps = (totalSteps - baseline).coerceAtLeast(0L).toInt()
                    trySend(todaySteps)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose { sensorManager.unregisterListener(listener) }
    }.distinctUntilChanged()

    companion object {
        private const val PREFS_NAME = "step_counter_prefs"
        private const val KEY_BASELINE_STEPS = "baseline_steps"
        private const val KEY_BASELINE_DATE = "baseline_date_epoch_day"
    }
}
