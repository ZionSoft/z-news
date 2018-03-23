/*
 * Copyright (C) 2018 ZionSoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.zionsoft.news.utils

import android.content.Context
import net.zionsoft.news.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*

private val STRING_BUILDER = StringBuilder()

private val FORMATTER_BUFFER = StringBuilder()
private val FORMATTER = Formatter(FORMATTER_BUFFER, Locale.getDefault())

private lateinit var DATE_TEMPLATE: String
private lateinit var MONTH_DATE_TEMPLATE: String
private lateinit var MONTHS: Array<String>

fun initTextFormatter(context: Context) {
    val resources = context.resources
    DATE_TEMPLATE = resources.getString(R.string.date_template)
    MONTH_DATE_TEMPLATE = resources.getString(R.string.month_date_template)
    MONTHS = resources.getStringArray(R.array.months)
}

fun Long.toLocalDateTime(): String {
    synchronized(STRING_BUILDER) {
        STRING_BUILDER.setLength(0)

        val dateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
        val now = LocalDateTime.now()
        if (now.year == dateTime.year) {
            if (now.monthValue == dateTime.monthValue && now.dayOfMonth == dateTime.dayOfMonth) {
                // today
                STRING_BUILDER.appendHourMinute(dateTime.hour, dateTime.minute)
            } else {
                // this year
                STRING_BUILDER.append(format(MONTH_DATE_TEMPLATE, MONTHS[dateTime.monthValue - 1], dateTime.dayOfMonth))
            }
        } else {
            STRING_BUILDER.append(format(DATE_TEMPLATE, MONTHS[dateTime.monthValue - 1], dateTime.dayOfMonth, dateTime.year))
        }

        return STRING_BUILDER.toString()
    }
}

private fun StringBuilder.appendHourMinute(hour: Int, minute: Int): StringBuilder {
    val normalizedHour = hour % 24
    if (is24HourFormat()) {
        if (normalizedHour < 10) {
            append('0')
        }
        append(normalizedHour).append(':')

        if (minute < 10) {
            append('0')
        }
        append(minute)
    } else {
        append(if (normalizedHour <= 12) normalizedHour else normalizedHour - 12).append(':')

        if (minute < 10) {
            append('0')
        }
        append(minute)

        append(' ').append(if (normalizedHour < 12) "am" else "pm")
    }

    return this
}

private fun format(format: String, vararg args: Any): String {
    synchronized(FORMATTER) {
        FORMATTER_BUFFER.setLength(0)
        return FORMATTER.format(format, *args).toString()
    }
}
