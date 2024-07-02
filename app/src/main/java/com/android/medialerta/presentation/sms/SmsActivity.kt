package com.android.medialerta.presentation.sms

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.medialerta.MediAlertaApplication
import com.android.medialerta.model.entity.Alerta
import com.android.medialerta.presentation.alert.AlertaViewModel
import com.android.medialerta.presentation.alert.AlertaViewModelFactory
import com.android.medialerta.presentation.contact.ContactViewModel
import com.android.medialerta.presentation.usuario.UsuarioViewModel
import com.android.medialerta.presentation.usuario.UsuarioViewModelFactory
import medialerta.databinding.ActivitySmsBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class SmsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySmsBinding.inflate(layoutInflater) }
    private val contactViewModel by lazy { ContactViewModel(contentResolver) }
    private val alertaViewModel: AlertaViewModel by viewModels {
        AlertaViewModelFactory((application as MediAlertaApplication).repository)
    }
    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((application as MediAlertaApplication).usuarioRepository)
    }

    private var contactName : String? = null
    private var phoneNumber : String? = null
    private var userName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        if (!setPermissions()) {
            loadValues()
        }
    }

    private fun setupListeners() {
        binding.btnCancelar.setOnClickListener {
            finish()
        }
        binding.btnSim.setOnClickListener {
            sendSms()
        }
    }

    private fun loadValues()
    {
        contactViewModel.getStarredContact()
            .also { contactName = it.first }
            .also {  phoneNumber = it.second}

        if (contactName?.isNotEmpty() == true) {
            binding.txtSMS.text = "Gostaria de enviar SMS com as próximas medicações para o contato ${contactName} ${phoneNumber}?"
        } else {
            binding.txtSMS.text =
                "Nenhum contato favorito cadastrado"
        }

        usuarioViewModel.users.observe(this) { user ->
            userName = user.nome
        }
    }

    private fun setPermissions(): Boolean {
        val permissionstoRequest: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()
        permissionsList.add(Manifest.permission.READ_CONTACTS)
        permissionsList.add(Manifest.permission.SEND_SMS)

        permissionsList.forEach() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    it,
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionstoRequest.add(it)
            }
        }

        if (permissionstoRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionstoRequest.toTypedArray(),
                99
            )
            return true
        }
        return false
    }

    private fun sendSms() {
        if(phoneNumber?.isNotEmpty() == true) {
            val messages: ArrayList<String> = ArrayList()
            messages.add("$userName possui as seguintes medicações: ")
            alertaViewModel.nextAlerts.observe(this) { alerts ->
                alerts.forEach {
                    messages.add(
                        getMensagem(it) + " - "
                    + "Medicamento: " + it.nomeMedicamento + " "
                    + it.quantidadeMedicamento + " "
                    + it.tipoMedicamento + " - "
                    + it.lembretemedicamento)
                }
            }

            if(messages.count() <= 1) {
                messages.add("Nenhum medicamento encontrado para os próximos dias")
            }

            val smsManager = applicationContext.getSystemService(SmsManager::class.java)
            smsManager.sendMultipartTextMessage(
                phoneNumber, null, messages, null, null
            )

            Toast.makeText(
                baseContext,
                "Mensagem enviada com sucesso",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadValues()
        } else {
            Toast.makeText(
                baseContext,
                "Permissão negada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getMensagem(alerta: Alerta) : String? {
        var stringEnd = " " + alerta.horaAlerta + ":" + alerta.minutoAlerta + " hrs"

        val dataCriacao = LocalDateTime.parse(
            alerta.dataCriacao,
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        )

        if (LocalDateTime.now().dayOfMonth == dataCriacao.dayOfMonth
            && (alerta.horaAlerta!! < dataCriacao.hour || (alerta.horaAlerta!! == dataCriacao.hour && alerta.minutoAlerta!! < dataCriacao.minute))
        ) {
            return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .format(dataCriacao.plusDays(1)) + stringEnd
        }

        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            .format(dataCriacao) + stringEnd
    }
}