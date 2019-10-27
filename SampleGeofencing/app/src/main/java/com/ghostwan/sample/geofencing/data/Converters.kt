package com.ghostwan.sample.geofencing.data

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun sourceToValue(source: Source): String {
        return  source.name
    }

    @TypeConverter
    fun valueToSource(value: String): Source {
        return  Source.valueOf(value)
    }


}