package com.module.core.utils.extensions.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Long.toCalendar(): Calendar {
    return Calendar.getInstance().apply { timeInMillis = this@toCalendar }
}

fun Calendar.toStringFormat(pattern: String = "dd/MM/yyyy"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(time)
}

fun Calendar.next() {
    this.add(Calendar.DAY_OF_YEAR, 1)
}

fun Calendar.previous() {
    this.add(Calendar.DAY_OF_YEAR, -1)
}