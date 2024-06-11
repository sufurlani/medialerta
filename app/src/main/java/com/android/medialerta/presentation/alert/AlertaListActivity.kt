package com.android.medialerta.presentation.alert

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.medialerta.MediAlertaApplication
import com.android.medialerta.model.entity.Alerta
import medialerta.databinding.ActivityAlertaListBinding

class AlertaListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAlertaListBinding.inflate(layoutInflater) }
    private val adapter by lazy { AlertaListAdapter()}
    private val alertaViewModel: AlertaViewModel by viewModels {
        AlertaViewModelFactory((application as MediAlertaApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        setupRecycleView()
        observeAlertList()
    }

    private fun setupListeners() {
        binding.btnNewAlert.setOnClickListener {
            val intent = Intent(this@AlertaListActivity, NewAlertaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecycleView() {
        adapter.onEditButtonClicked = { item ->
            onEditAlerta(item)
        }
        adapter.onDeleteButtonClicked = { id, position ->
            onDeleteAlerta(id, position)
        }
        binding.alertasRecycleView.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun observeAlertList() {
        alertaViewModel.allAlerts.observe(this) { alerts ->
            adapter.submitList(alerts)
        }
    }

    //Future feature - delete also removes Alarm clock
    private fun onDeleteAlerta(alertaId: Int, position: Int) {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("")
        alertDialog.setMessage("Deseja mesmo remover este alerta?")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Sim"
        ) { dialog, _ ->
            alertaViewModel.deleteById(alertaId)
            adapter.notifyItemRemoved(position)
            dialog.dismiss()
            Toast.makeText(this,
                "Alerta Removido",
                Toast.LENGTH_SHORT
            ).show()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Não"
        ) { dialog, _ -> dialog.dismiss() }

        alertDialog.show()

        val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }

    private fun onEditAlerta(alerta: Alerta) {
        val i = Intent(this@AlertaListActivity, EditAlertaActivity::class.java)
        .apply {
            putExtra("alertaId", alerta.id)
            setResult(RESULT_OK, this)
        }
        startActivity(i)
    }
}