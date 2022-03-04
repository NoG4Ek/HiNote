package com.poly.hinote.util

import android.icu.text.SimpleDateFormat
import java.util.*

class Util {
    companion object {
        private val cFC: Calendar = Calendar.getInstance()
        val curTime: String
            get() {
                return cFC.time.toString()
            }

        fun formatDate(date: String): String {
            val calendar: Calendar = Calendar.getInstance()
            val formattedDate: StringBuilder = StringBuilder()

            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            calendar.time = sdf.parse(date) // "Mon Mar 14 16:02:37 GMT 2011"
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val cDayOfMonth = cFC.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val cMonth = cFC.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val cYear = cFC.get(Calendar.YEAR)

            val splitData = calendar.time.toString().split(" ")
            val sMonth = splitData[1]
            var sTime = splitData[3]
            val sYear = splitData[5]

            val gsm = calendar.get(Calendar.ZONE_OFFSET)
            val cGsm = cFC.get(Calendar.ZONE_OFFSET)
            if (gsm != cGsm) {
                sTime =
                    ((sTime.split(":")[0].toInt() - (gsm - cGsm)) % 24).toString() + sTime.split(":")
                        .drop(1)
            }

            when {
                year == cYear && month == cMonth && dayOfMonth == cDayOfMonth -> {
                    formattedDate.append("Today, ")

                }
                year == cYear && month == cMonth && dayOfMonth == cDayOfMonth - 1 -> {
                    formattedDate.append("Yesterday, ")
                }
                year != cYear -> {
                    formattedDate.append(sYear)
                    formattedDate.append(", ")
                }
            }
            formattedDate.append(sMonth)
            formattedDate.append(" ")
            formattedDate.append(dayOfMonth)
            formattedDate.append("     ")
            formattedDate.append(sTime)

            return formattedDate.toString()
        }
    }
}