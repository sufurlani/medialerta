package com.android.medialerta.presentation.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.medialerta.MediAlertaApplication
import com.android.medialerta.presentation.alert.AlertaListActivity
import com.android.medialerta.presentation.sms.SmsActivity
import com.android.medialerta.presentation.sos.SosActivity
import com.android.medialerta.presentation.usuario.EditUsuarioActivity
import com.android.medialerta.presentation.usuario.UsuarioViewModel
import com.android.medialerta.presentation.usuario.UsuarioViewModelFactory
import medialerta.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((application as MediAlertaApplication).usuarioRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        loadUser()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

    private fun setupListeners(){
        binding.btnAlerts.setOnClickListener {
            val intent = Intent(this@HomeActivity, AlertaListActivity::class.java)
            startActivity(intent)
        }
        binding.btnSms.setOnClickListener {
            val intent = Intent(this@HomeActivity, SmsActivity::class.java)
            startActivity(intent)
        }
        binding.btnSos.setOnClickListener {
            val intent = Intent(this@HomeActivity, SosActivity::class.java)
            startActivity(intent)
        }
        binding.txtEditar.setOnClickListener {
            val intent = Intent(this@HomeActivity, EditUsuarioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUser() {
        usuarioViewModel.users.observe(this) { user ->
            binding.txtUsuario.text = user.nome
        }
    }
}
