package com.android.medialerta.repository

import com.android.medialerta.model.dao.AlertaDao
import com.android.medialerta.model.entity.Alerta
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar

class AlertaRepository(private val alertaDao: AlertaDao) {

    val allAlerts: Flow<List<Alerta>> = alertaDao.getAlertList()

    suspend fun insert(alerta: Alerta) {
        alertaDao.insert(alerta)
    }
    suspend fun deleteById(alertaId: Int) {
        alertaDao.deleteById(alertaId)
    }
    suspend fun update(alerta: Alerta) {
        alertaDao.update(alerta)
    }
    suspend fun getById(alertaId: Int) : Alerta {
        return alertaDao.getById(alertaId)
    }

    val nextAlerts: Flow<List<Alerta>> = alertaDao.getNextAlerts(
        SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time))
}