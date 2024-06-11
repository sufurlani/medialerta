package com.android.medialerta.presentation.alert

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.android.medialerta.model.entity.Alerta
import com.android.medialerta.presentation.AlarmReceiver

class PermissionViewModel : ViewModel() {
    fun onPermissionRequested(alarmManager: AlarmManager, context: Context, alerta: Alerta) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.setAction("ALARM")
        intent.putExtra("nomeMedicamento", alerta.nomeMedicamento )
        intent.putExtra("horaAlerta", alerta.horaAlerta )
        intent.putExtra("minutoAlerta", alerta.minutoAlerta )
        if(alerta.diasDaSemanaAlerta?.diasDaSemana?.isNotEmpty() == true)
        {
            intent.putExtra("diasAlerta", alerta.diasDaSemanaAlerta.diasDaSemana)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent)
        }
    }
}

//https://carterchen247.medium.com/android-14-behavior-change-schedule-exact-alarms-are-denied-by-default-7563a814dee4
