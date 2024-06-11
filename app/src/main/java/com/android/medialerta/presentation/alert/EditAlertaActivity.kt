package com.android.medialerta.presentation.alert

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.medialerta.MediAlertaApplication
import com.android.medialerta.model.entity.Alerta
import com.android.medialerta.model.entity.DiasDaSemana
import com.android.medialerta.presentation.camerapreview.CameraPreviewActivity
import com.android.medialerta.presentation.enums.LembreteEnum
import com.android.medialerta.presentation.enums.TipoMedicamentoEnum
import medialerta.databinding.ActivityEditAlertaBinding
import java.util.Calendar


class EditAlertaActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEditAlertaBinding.inflate(layoutInflater) }
    private var bitmap: Bitmap? = null
    private val alertaViewModel: AlertaViewModel by viewModels {
        AlertaViewModelFactory((application as MediAlertaApplication).repository)
    }
    private lateinit var imgURI: String
    private var alertaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
        loadValues()
    }

    private val register = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                if (it.hasExtra("imgURI")) {
                    imgURI = it.getSerializableExtra("imgURI").toString()
                    val source = ImageDecoder.createSource(contentResolver, Uri.parse(imgURI))
                    bitmap = ImageDecoder.decodeBitmap(source).copy(Bitmap.Config.RGBA_F16, true)
                    binding.imgMedicamentoEdit.setImageBitmap(resizeImage(bitmap))
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnImg.setOnClickListener {
            Intent(this@EditAlertaActivity, CameraPreviewActivity::class.java).also {
                register.launch(it)
            }
        }

        binding.imgMedicamentoEdit.setOnClickListener {
            Intent(this@EditAlertaActivity, CameraPreviewActivity::class.java).also {
                register.launch(it)
            }
        }

        binding.btnSalvarEdit.setOnClickListener {
            if (checkAllPopulatedFields()) {
                alertaViewModel.update(getAlerta())
                finish()
            }
        }

        binding.txtNomeMedicamentoEdit.setOnFocusChangeListener { _, isFocused ->
            if (hasError(binding.txtNomeMedicamentoEdit.text, isFocused)) {
                binding.txtNomeMedicamentoEdit.error = "Nome é obrigatório"
                return@setOnFocusChangeListener
            }
        }
    }

    private fun getAlerta(): Alerta {
        val alarmDays = ArrayList<Int>()
        if (binding.cbDomingoEdit.isChecked) alarmDays.add(Calendar.SUNDAY)
        if (binding.cbSegundaEdit.isChecked) alarmDays.add(Calendar.MONDAY)
        if (binding.cbTercaEdit.isChecked) alarmDays.add(Calendar.TUESDAY)
        if (binding.cbQuartaEdit.isChecked) alarmDays.add(Calendar.WEDNESDAY)
        if (binding.cbQuintaEdit.isChecked) alarmDays.add(Calendar.THURSDAY)
        if (binding.cbSextaEdit.isChecked) alarmDays.add(Calendar.FRIDAY)
        if (binding.cbSabadoEdit.isChecked) alarmDays.add(Calendar.SATURDAY)

        return Alerta(
            nomeMedicamento = binding.txtNomeMedicamentoEdit.text.toString(),
            quantidadeMedicamento = binding.txtQtdEdit.text.toString().toInt(),
            horaAlerta = binding.txtHoraEdit.text.toString().toInt(),
            minutoAlerta = binding.txtMinutoEdit.text.toString().toInt(),
            tipoMedicamento = binding.ddlTipoMedicamentoEdit.selectedItem.toString(),
            lembretemedicamento = binding.ddlLembreteEdit.selectedItem.toString(),
            diasDaSemanaAlerta = DiasDaSemana(alarmDays),
            imagem = resizeImage(bitmap),
            id = alertaId
        )
    }

    private fun resizeImage(bitmap: Bitmap?): Bitmap? {
        hideButton()
        return bitmap?.let { Bitmap.createScaledBitmap(it, 500, 500, true) }
    }

    private fun hideButton() {
        binding.btnImg.visibility = View.INVISIBLE
    }

    private fun hasError(text: Editable, isFocused: Boolean = false): Boolean {
        if (!isFocused) {
            if (text.isBlank()) {
                return true
            }
        }
        return false
    }

    private fun checkAllPopulatedFields(): Boolean {
        if (binding.txtNomeMedicamentoEdit.text.isEmpty() ||
            binding.txtQtdEdit.text.isEmpty()
        ) {
            Toast.makeText(
                baseContext,
                "Preencha todos os campos antes de Salvar",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.imgMedicamentoEdit.drawable == null) {
            Toast.makeText(baseContext, "Capturar uma imagem é obrigatório", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun loadValues() {
        if (intent.hasExtra("alertaId")) {
            alertaId = intent.getSerializableExtra("alertaId").toString().toInt()
            alertaViewModel.getById(alertaId)
            alertaViewModel.alerta.observe(this) { alert ->
                bitmap = resizeImage(alert.imagem)
                binding.txtNomeMedicamentoEdit.setText(alert.nomeMedicamento)
                binding.txtQtdEdit.setText(alert.quantidadeMedicamento?.toString())
                binding.txtHoraEdit.setText(alert.horaAlerta?.toString())
                binding.txtMinutoEdit.setText(alert.minutoAlerta?.toString())
                binding.imgMedicamentoEdit.setImageBitmap(bitmap)
                alert.diasDaSemanaAlerta.diasDaSemana.forEach { i ->
                    if (i == 0)
                        binding.cbDomingoEdit.isChecked = true
                    if (i == 1)
                        binding.cbSegundaEdit.isChecked = true
                    if (i == 2)
                        binding.cbTercaEdit.isChecked = true
                    if (i == 3)
                        binding.cbQuartaEdit.isChecked = true
                    if (i == 4)
                        binding.cbQuintaEdit.isChecked = true
                    if (i == 5)
                        binding.cbSextaEdit.isChecked = true
                    if (i == 6)
                        binding.cbSabadoEdit.isChecked = true
                }
                TipoMedicamentoEnum.values().map { x ->
                    if (x.tipo == alert.tipoMedicamento)
                        binding.ddlTipoMedicamentoEdit.setSelection(x.ordinal)
                }
                LembreteEnum.values().map { x ->
                    if (x.lembrete == alert.lembretemedicamento)
                        binding.ddlLembreteEdit.setSelection(x.ordinal)
                }
            }
        }
    }
}

