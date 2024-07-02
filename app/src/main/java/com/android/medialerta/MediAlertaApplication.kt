package com.android.medialerta

import android.app.Application
import com.android.medialerta.repository.AlertaRepository
import com.android.medialerta.model.room.AlertaRoomDatabase
import com.android.medialerta.model.room.UsuarioRoomDatabase
import com.android.medialerta.repository.UsuarioRepository

class MediAlertaApplication : Application() {
    val database by lazy { AlertaRoomDatabase.getDatabase(this) }
    val repository by lazy { AlertaRepository(database.alertaDao()) }
    val usuarioDatabase by lazy { UsuarioRoomDatabase.getDatabase(this) }
    val usuarioRepository by lazy { UsuarioRepository(usuarioDatabase.usuarioDao()) }
}
