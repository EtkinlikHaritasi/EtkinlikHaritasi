package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import java.util.Calendar

object DateTimeStrings
{
    fun dMy(date: Calendar, separator: String): String
    {
        var result = StringBuilder("")
        result.append(date.get(Calendar.DAY_OF_MONTH))
        result.append(separator)
        result.append(date.get(Calendar.MONTH) + 1)
        result.append(separator)
        result.append(date.get(Calendar.YEAR))
        return result.toString()
    }
    fun dMy(date: Long, separator: String): String
    {
        var cal = Calendar.Builder().setInstant(date).build()
        return dMy(cal, separator)
    }

    fun yMd(date: Calendar, separator: String): String
    {
        var result = StringBuilder("")
        result.append(date.get(Calendar.YEAR))
        result.append(separator)
        result.append(date.get(Calendar.MONTH) + 1)
        result.append(separator)
        result.append(date.get(Calendar.DAY_OF_MONTH))
        return result.toString()
    }
    fun yMd(date: Long, separator: String): String
    {
        var cal = Calendar.Builder().setInstant(date).build()
        return yMd(cal, separator)
    }
}