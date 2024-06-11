package com.android.medialerta.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.medialerta.presentation.alert.AlertaListActivity
import com.android.medialerta.presentation.sms.SmsActivity
import com.android.medialerta.presentation.sos.SosActivity
import medialerta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners(){
        binding.btnAlerts.setOnClickListener {
            val intent = Intent(this@MainActivity, AlertaListActivity::class.java)
            startActivity(intent)
        }
        binding.btnSms.setOnClickListener {
            val intent = Intent(this@MainActivity, SmsActivity::class.java)
            startActivity(intent)
        }
        binding.btnSos.setOnClickListener {
            val intent = Intent(this@MainActivity, SosActivity::class.java)
            startActivity(intent)
        }
    }
}
