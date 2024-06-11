package com.android.medialerta.presentation.datepicker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import medialerta.databinding.ActivityDatePickerBinding

class DatePickerActivity: AppCompatActivity() {
    private val binding by lazy { ActivityDatePickerBinding.inflate(layoutInflater) }
    private val viewModel by lazy { DatePickerViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnDatePicker.setOnClickListener {
            onSelectADate()
        }
    }

    private fun onSelectADate() {
        val datePicker = binding.datePicker
        var calendar = viewModel.generateCalendarValue(datePicker)
        Intent().apply {
            putExtra("selectedDate", calendar)
            setResult(RESULT_OK, this)
        }
        finish()
    }
}