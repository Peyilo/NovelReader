package org.anvei.novelreader.database

import androidx.room.TypeConverter
import java.sql.Date

class Converters {

    @TypeConverter
    fun timeStampToDate(timeStamp: Long?): Date?{
        return if (timeStamp == null) null else Date(timeStamp)
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun intListToString(list: List<Int>?): String? {
        if (list == null)
            return null
        if (list.isEmpty())
            return null
        val stringBuilder = StringBuilder().append("[")
        for (i in list.indices) {
            stringBuilder.append(list[i])
            if (i != list.size) {
                stringBuilder.append(", ")
            }
        }
        stringBuilder.append("]")
        return stringBuilder.toString()
    }

    @TypeConverter
    fun stringToIntList(s: String?): List<Int>? {
        if (s == null) {
            return null
        }
        val list = ArrayList<Int>()
        val splits = s.substring(1, s.length - 1).split(",")
        for (split in splits) {
            list.add(split.trim().toInt())
        }
        return if (list.isEmpty()) null else list
    }
}