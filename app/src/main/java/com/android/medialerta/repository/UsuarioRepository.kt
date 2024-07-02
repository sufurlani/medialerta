package com.android.medialerta.repository

import com.android.medialerta.model.dao.UsuarioDao
import com.android.medialerta.model.entity.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    val users: Flow<Usuario> = usuarioDao.getUsuario()

    suspend fun insert(usuario: Usuario) {
        usuarioDao.insert(usuario)
    }
    suspend fun update(usuario: Usuario) {
        usuarioDao.update(usuario)
    }
}