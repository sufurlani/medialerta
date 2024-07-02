package com.android.medialerta.presentation.usuario

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.medialerta.model.entity.Usuario
import com.android.medialerta.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    val users: LiveData<Usuario> = repository.users.asLiveData()

    fun insert(usuario: Usuario) = viewModelScope.launch {
        repository.insert(usuario)
    }
    fun update(usuario: Usuario) = viewModelScope.launch {
        repository.update(usuario)
    }
}

    class UsuarioViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
                return UsuarioViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
