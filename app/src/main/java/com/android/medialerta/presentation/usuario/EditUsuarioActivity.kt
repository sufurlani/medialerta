package com.android.medialerta.presentation.usuario

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.medialerta.MediAlertaApplication
import com.android.medialerta.model.entity.Usuario
import medialerta.databinding.ActivityEditUserBinding

class EditUsuarioActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEditUserBinding.inflate(layoutInflater) }
    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((application as MediAlertaApplication).usuarioRepository)
    }
    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        loadUser()
    }

    private fun setupListeners(){
        binding.btnEditar.setOnClickListener {
            if (binding.txtNome.text.isBlank()) {
                binding.txtNome.error = "Nome é obrigatório"
                Toast.makeText(
                    baseContext,
                    "Preencha o campo nome antes de prosseguir",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            updateUser()
            finish()
        }
    }

    private fun loadUser(){
        usuarioViewModel.users.observe(this) { usuario ->
            binding.txtNome.setText(usuario.nome)
            userId = usuario.id
        }
    }

    private fun updateUser(){
        usuarioViewModel.update(Usuario(binding.txtNome.text.toString(), userId))
    }
}