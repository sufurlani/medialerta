package com.android.medialerta.presentation.datepicker

import android.widget.DatePicker
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class DatePickerViewModel : ViewModel() {
    fun generateCalendarValue(datePicker:DatePicker) : String
    {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(
            datePicker.year,
            datePicker.month,
            datePicker.dayOfMonth
        )
        return SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
    }
}