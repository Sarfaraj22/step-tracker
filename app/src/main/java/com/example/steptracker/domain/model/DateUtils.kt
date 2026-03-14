package com.example.steptracker.domain.model

import java.util.Calendar

/**
 * Date utilities that avoid java.time (which requires core library desugaring on API < 26).
 * All dates are represented as epoch days (Long) — days elapsed since 1970-01-01.
 */
object DateUtils {

    fun todayEpochDay(): Long {
        val cal = Calendar.getInstance()
        return epochDayOf(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Converts a calendar year/month/day to an epoch day.
     * Uses the same algorithm as java.time.LocalDate.toEpochDay().
     */
    fun epochDayOf(year: Int, month: Int, day: Int): Long {
        var y = year.toLong()
        val m = month.toLong()
        var total = 365L * y
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400
        } else {
            total -= y / -4 - y / -100 + y / -400
        }
        total += (367L * m - 362L) / 12L
        total += day - 1
        if (m > 2) {
            total--
            if (!isLeapYear(year)) total--
        }
        return total - DAYS_0000_TO_1970
    }

    /** Returns the start and end epoch days (inclusive) for the given month. */
    fun monthRangeEpochDays(year: Int, month: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { set(year, month - 1, 1) }
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return epochDayOf(year, month, 1) to epochDayOf(year, month, lastDay)
    }

    private fun isLeapYear(year: Int): Boolean =
        year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

    private const val DAYS_0000_TO_1970 = 719528L
}
