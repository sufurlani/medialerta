package com.android.medialerta.presentation.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.medialerta.model.entity.Alerta
import com.android.medialerta.repository.AlertaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar

class AlertaViewModel(private val repository: AlertaRepository) : ViewModel() {

    private val _alerta: MutableLiveData<Alerta> = MutableLiveData()
    val alerta: LiveData<Alerta> = _alerta
    val allAlerts: LiveData<List<Alerta>> = repository.allAlerts.asLiveData()
    val nextAlerts: LiveData<List<Alerta>> = repository.nextAlerts.asLiveData()

    fun insert(alerta: Alerta) = viewModelScope.launch {
        repository.insert(alerta)
    }

    fun deleteById(alertaId: Int) = viewModelScope.launch {
        repository.deleteById(alertaId)
    }

    fun update(alerta: Alerta) = viewModelScope.launch {
        repository.update(alerta)
    }

    fun getById(alertaId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _alerta.postValue(repository.getById(alertaId))
            }
        }
    }
}

    class AlertaViewModelFactory(private val repository: AlertaRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertaViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
