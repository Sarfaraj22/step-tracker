package com.example.steptracker.data.local.converter

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromIntList(list: List<Int>): String =
        list.joinToString(separator = ",")

    @TypeConverter
    fun toIntList(value: String): List<Int> =
        if (value.isBlank()) emptyList()
        else value.split(",").map { it.trim().toInt() }
}
