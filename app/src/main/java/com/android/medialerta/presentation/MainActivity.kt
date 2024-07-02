package com.android.medialerta.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.medialerta.MediAlertaApplication
import com.android.medialerta.model.entity.Usuario
import com.android.medialerta.presentation.home.HomeActivity
import com.android.medialerta.presentation.usuario.UsuarioViewModel
import com.android.medialerta.presentation.usuario.UsuarioViewModelFactory
import medialerta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((application as MediAlertaApplication).usuarioRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        usuarioViewModel.users.observe(this) { user ->
            if(user != null){
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            if (binding.txtNome.text.isBlank()) {
                binding.txtNome.error = "Nome é obrigatório"
                Toast.makeText(
                    baseContext,
                    "Preencha o campo nome antes de prosseguir",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            insereUsuario()
            endProcess()
        }
    }

    private fun insereUsuario() {
        usuarioViewModel.insert(Usuario(binding.txtNome.text.toString()))
    }
    private fun endProcess(){
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        startActivity(intent)
        finish()
    }
}
