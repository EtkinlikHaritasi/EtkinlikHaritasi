package com.github.EtkinlikHaritasi.EtkinlikHaritasi

import java.util.Calendar
import kotlin.Int

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
    fun dMy_toCalendar(dMy_string: String, separator: String): Calendar?
    {
        var cal: Calendar? = null
        try {
            var subs = dMy_string.split(separator)
            cal = Calendar.Builder().setDate(
                subs[2].toInt(), subs[1].toInt(), subs[0].toInt()
            ).build()
        }
        catch (e: Exception)
        {
            cal = null
        }
        return cal
    }

    fun dMyHms(date: Calendar, dateSeparator: String, timeSeparator: String,
               dtSeparator: String): String
    {
        var result = StringBuilder("")
        result.append(date.get(Calendar.DAY_OF_MONTH))
        result.append(dateSeparator)
        result.append(date.get(Calendar.MONTH) + 1)
        result.append(dateSeparator)
        result.append(date.get(Calendar.YEAR))
        result.append(dtSeparator)
        result.append(date.get(Calendar.HOUR_OF_DAY))
        result.append(timeSeparator)
        result.append(date.get(Calendar.MINUTE))
        return result.toString()
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
    fun yMd_toCalendar(dMy_string: String, separator: String): Calendar?
    {
        var cal: Calendar? = null
        try {
            var subs = dMy_string.split(separator)
            cal = Calendar.Builder().setDate(
                subs[0].toInt(), subs[1].toInt(), subs[2].toInt()
            ).build()
        }
        catch (e: Exception)
        {
            cal = null
        }
        return cal
    }
}