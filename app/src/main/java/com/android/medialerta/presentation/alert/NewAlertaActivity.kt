package com.android.medialerta.presentation.alert

import android.Manifest
import android.app.AlarmManager
import android.content.Context
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
import com.android.medialerta.presentation.datepicker.DatePickerActivity
import medialerta.databinding.ActivityNewAlertaBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class NewAlertaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewAlertaBinding
    private var bitmap: Bitmap? = null
    private val permissionViewModel: PermissionViewModel = PermissionViewModel()
    private val alertaViewModel: AlertaViewModel by viewModels {
        AlertaViewModelFactory((application as MediAlertaApplication).repository)
    }
    private lateinit var imgURI: String
    private var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAlertaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
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
                    binding.imgMedicamento.setImageBitmap(resizeImage(bitmap))
                }
            }
        }
    }

    private val registerSelectedDate = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let {
                if (it.hasExtra("selectedDate")) {
                    date = it.getSerializableExtra("selectedDate").toString()
                    binding.txtDiasDesc.text = Editable.Factory.getInstance().newEditable(date)
                    changeChecked()
                }
            }
        }
    }

    private fun setupListeners() {

        binding.btnCalendar.setOnClickListener {
            Intent(this@NewAlertaActivity, DatePickerActivity::class.java).also {
                registerSelectedDate.launch(it)
            }
        }

        binding.btnImg.setOnClickListener {
            Intent(this@NewAlertaActivity, CameraPreviewActivity::class.java).also {
                register.launch(it)
            }
        }

        binding.imgMedicamento.setOnClickListener {
            Intent(this@NewAlertaActivity, CameraPreviewActivity::class.java).also {
                register.launch(it)
            }
        }

        binding.btnSalvarNovo.setOnClickListener {
            if (checkAllPopulatedFields()) {
                requestPermissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
            }
        }

        binding.txtNomeMedicamentoNovo.setOnFocusChangeListener { _, isFocused ->
            if (hasError(binding.txtNomeMedicamentoNovo.text, isFocused)) {
                binding.txtNomeMedicamentoNovo.error = "Nome é obrigatório"
                return@setOnFocusChangeListener
            }
        }

        binding.txtHoraNovo.setOnFocusChangeListener { _, isFocused ->
            if (hasError(binding.txtHoraNovo.text, isFocused)) {
                binding.txtHoraNovo.error = "Hora é obrigatório"
                return@setOnFocusChangeListener
            }
            if (isValid(binding.txtHoraNovo.text, 23, isFocused)) {
                binding.txtHoraNovo.error = "Hora precisa ser entre 0 e 23"
                return@setOnFocusChangeListener
            }
        }

        binding.txtMinutoNovo.setOnFocusChangeListener { _, isFocused ->
            if (hasError(binding.txtMinutoNovo.text, isFocused)) {
                binding.txtMinutoNovo.error = "Minuto é obrigatório"
                return@setOnFocusChangeListener
            }
            if (isValid(binding.txtMinutoNovo.text, 59, isFocused)) {
                binding.txtMinutoNovo.error = "Minuto precisa ser entre 0 e 59"
                return@setOnFocusChangeListener
            }
        }

        binding.txtHoraNovo.setOnFocusChangeListener { _, isFocused ->
            if (hasError(binding.txtHoraNovo.text, isFocused)) {
                binding.txtHoraNovo.error = "Hora é obrigatório"
                return@setOnFocusChangeListener
            }
            if (isValid(binding.txtHoraNovo.text, 23, isFocused)) {
                binding.txtHoraNovo.error = "Hora precisa ser entre 0 e 23"
                return@setOnFocusChangeListener
            }
        }

        binding.txtMinutoNovo.setOnFocusChangeListener { _, isFocused ->
            if (hasError(binding.txtMinutoNovo.text, isFocused)) {
                binding.txtMinutoNovo.error = "Minuto é obrigatório"
                return@setOnFocusChangeListener
            }
            if (isValid(binding.txtMinutoNovo.text, 59, isFocused)) {
                binding.txtMinutoNovo.error = "Minuto precisa ser entre 0 e 59"
                return@setOnFocusChangeListener
            }
        }

        binding.cbDomingo.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
        binding.cbSegunda.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
        binding.cbTerca.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
        binding.cbQuarta.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
        binding.cbQuinta.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
        binding.cbSexta.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
        binding.cbSabado.setOnClickListener {
            binding.txtDiasDesc.text = null
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permissionViewModel.onPermissionRequested(
                getSystemService(Context.ALARM_SERVICE) as AlarmManager,
                this,
                getAlerta()
            )
            if (isGranted) {
                alertaViewModel.insert(getAlerta())
                finish()
            }
        }

    private fun getAlerta(): Alerta {
        val alarmDays = ArrayList<Int>()
        if (binding.cbDomingo.isChecked) alarmDays.add(Calendar.SUNDAY)
        if (binding.cbSegunda.isChecked) alarmDays.add(Calendar.MONDAY)
        if (binding.cbTerca.isChecked) alarmDays.add(Calendar.TUESDAY)
        if (binding.cbQuarta.isChecked) alarmDays.add(Calendar.WEDNESDAY)
        if (binding.cbQuinta.isChecked) alarmDays.add(Calendar.THURSDAY)
        if (binding.cbSexta.isChecked) alarmDays.add(Calendar.FRIDAY)
        if (binding.cbSabado.isChecked) alarmDays.add(Calendar.SATURDAY)

        return Alerta(
            binding.txtNomeMedicamentoNovo.text.toString(),
            binding.txtQtdNovo.text.toString().toInt(),
            binding.txtHoraNovo.text.toString().toInt(),
            binding.txtMinutoNovo.text.toString().toInt(),
            binding.ddlTipoMedicamento.selectedItem.toString(),
            binding.ddlLembrete.selectedItem.toString(),
            DiasDaSemana(alarmDays),
            bitmap?.let { resizeImage(bitmap) },
            getAlertDate()
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

    private fun isValid(text: Editable, maxValue: Int, isFocused: Boolean): Boolean {
        if (!isFocused)
            return text.toString().toInt() < 0 || text.toString().toInt() > maxValue
        return false
    }

    private fun checkAllPopulatedFields(): Boolean {
        if (binding.txtNomeMedicamentoNovo.text.isEmpty() ||
            binding.txtQtdNovo.text.isEmpty() ||
            binding.txtHoraNovo.text.isEmpty() ||
            binding.txtMinutoNovo.text.isEmpty()
        ) {
            Toast.makeText(
                baseContext,
                "Preencha todos os campos antes de Salvar",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (binding.imgMedicamento.drawable == null) {
            Toast.makeText(baseContext, "Capturar uma imagem é obrigatório", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun changeChecked()
    {
        binding.cbDomingo.isChecked = false
        binding.cbSegunda.isChecked = false
        binding.cbTerca.isChecked = false
        binding.cbQuarta.isChecked = false
        binding.cbQuinta.isChecked = false
        binding.cbSexta.isChecked = false
        binding.cbSabado.isChecked = false
    }

    private fun getAlertDate() : String
    {
        return SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
    }
}