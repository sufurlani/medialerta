package com.android.medialerta.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.core.content.ContextCompat.startActivity


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
         val i = Intent(AlarmClock.ACTION_SET_ALARM)
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        i.putExtra(AlarmClock.EXTRA_MESSAGE, intent?.getSerializableExtra("nomeMedicamento"))
        i.putExtra(AlarmClock.EXTRA_HOUR, intent?.getSerializableExtra("horaAlerta"))
        i.putExtra(AlarmClock.EXTRA_MINUTES, intent?.getSerializableExtra("minutoAlerta"))
        if(intent?.hasExtra("diasAlerta") == true)
        {
            i.putExtra(AlarmClock.EXTRA_DAYS,  intent.getSerializableExtra("diasAlerta"))
        }
        startActivity(context as Context, i, null)
    }
}