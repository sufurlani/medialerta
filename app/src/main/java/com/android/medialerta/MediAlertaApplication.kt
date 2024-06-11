package com.android.medialerta

import android.app.Application
import com.android.medialerta.repository.AlertaRepository
import com.android.medialerta.model.room.AlertaRoomDatabase

class MediAlertaApplication : Application() {
    val database by lazy { AlertaRoomDatabase.getDatabase(this) }
    val repository by lazy { AlertaRepository(database.alertaDao()) }
}
