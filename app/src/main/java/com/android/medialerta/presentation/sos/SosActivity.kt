package com.android.medialerta.presentation.sos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.medialerta.presentation.contact.ContactViewModel
import medialerta.databinding.ActivitySosBinding


class SosActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySosBinding.inflate(layoutInflater) }
    private val contactViewModel by lazy { ContactViewModel(contentResolver) }
    private lateinit var timer: CountDownTimer
    private var REQUEST_CODE_ALL: Int = 99
    private var phoneNumber: String? = null
    private var contactName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()

        if (!setPermissions()) {
            actionCall()
        }
    }

    private fun setupListeners() {
        binding.btnCancelarSOS.setOnClickListener {
            endCall()
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            actionCall()
        } else {
            Toast.makeText(
                baseContext,
                "Permissão negada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setPermissions(): Boolean {
        val permissionstoRequest: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()
        permissionsList.add(Manifest.permission.READ_CONTACTS)
        permissionsList.add(Manifest.permission.CALL_PHONE)

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
                REQUEST_CODE_ALL
            )
            return true
        }
        return false
    }

    private fun actionCall() {
        contactViewModel.getStarredContact()
            .also { contactName = it.first }
            .also { phoneNumber = it.second }

        timer = object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (contactName?.isNotEmpty() == true) {
                    binding.txtCountdown.text =
                        "Discando para ${contactName} em: ${millisUntilFinished.toInt() / 1000}"
                } else {
                    binding.txtCountdown.text =
                        "Nenhum contato favorito cadastrado"
                }
            }

            override fun onFinish() {
                if (phoneNumber?.isNotEmpty() == true) {
                    val uri = Uri.parse("tel:${phoneNumber}")
                    val intent = Intent(Intent.ACTION_CALL, uri)
                    startActivity(intent)
                    finish()
                } else {
                    endCall()
                }
            }
        }
        timer.start()
    }

    private fun endCall() {
        timer.cancel()
    }
}
