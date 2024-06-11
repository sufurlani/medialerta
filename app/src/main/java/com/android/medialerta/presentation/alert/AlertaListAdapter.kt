package com.android.medialerta.presentation.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.medialerta.model.entity.Alerta
import com.android.medialerta.presentation.alert.AlertaListAdapter.AlertViewHolder
import com.android.medialerta.presentation.enums.LembreteEnum
import com.android.medialerta.presentation.enums.TipoMedicamentoEnum
import medialerta.R
import medialerta.databinding.AlertasrecycleviewItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar


class AlertaListAdapter : ListAdapter<Alerta, AlertViewHolder>(ALERT_COMPARATOR) {
    var onEditButtonClicked: ((Alerta) -> Unit)? = null
    var onDeleteButtonClicked: ((Int, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
          val binding = AlertasrecycleviewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, position)
    }

    inner class AlertViewHolder(private val binding: AlertasrecycleviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alerta: Alerta, position: Int) {
            binding.dtAlertaItem.text = getMensagem(alerta) + " " + alerta.horaAlerta.toString() + ":" + alerta.minutoAlerta.toString() + " hrs"
            binding.txtAlertaItem.text = alerta.nomeMedicamento
            binding.txtQtdItem.text = alerta.quantidadeMedicamento.toString()
            binding.txtTipoMedicamentoItem.text = alerta.tipoMedicamento
            binding.imgTipo.setImageResource(getImgTipo(alerta.tipoMedicamento))
            binding.txtLembreteMedicamentoItem.text = alerta.lembretemedicamento
            binding.imgLembrete.setImageResource(getImgLembrete(alerta.lembretemedicamento))
            binding.imgLstMedicamento.setImageBitmap(alerta.imagem)
            binding.btnEditar.setOnClickListener {
                onEditButtonClicked?.invoke(alerta)
            }
            binding.btnExcluir.setOnClickListener {
                onDeleteButtonClicked?.invoke(alerta.id, position)
            }
        }
    }

    private fun getImgTipo(tipo: String): Int {
        val ret = when(tipo) {
            TipoMedicamentoEnum.COMPRIMIDOS.tipo -> R.drawable.pill_1_svgrepo_com
            TipoMedicamentoEnum.CAPSULAS.tipo -> R.drawable.capsule_svgrepo_com
            TipoMedicamentoEnum.GOTAS.tipo -> R.drawable.full_dropper_svgrepo_com
            TipoMedicamentoEnum.ML.tipo -> R.drawable.ic_add_black_24dp
            TipoMedicamentoEnum.COMPRIMIDO_SUBLINGUAL.tipo -> R.drawable.pill_1_svgrepo_com
            TipoMedicamentoEnum.AMPOLAS.tipo -> R.drawable.bottle_svgrepo_com
            else -> {R.drawable.ic_add_black_24dp}
        }
        return ret
    }

    private fun getImgLembrete(lembrete: String): Int {
        val ret = when(lembrete) {
            LembreteEnum.JEJUM.lembrete -> R.drawable.plate_fork_and_knife_svgrepo_com
            LembreteEnum.DE_MANHA.lembrete -> R.drawable.ic_add_black_24dp
            LembreteEnum.ANTES_DO_ALMOCO.lembrete -> R.drawable.plate_fork_and_knife_svgrepo_com
            LembreteEnum.DE_NOITE.lembrete -> R.drawable.moon_sleep_svgrepo_com
            LembreteEnum.ANTES_DE_DORMIR.lembrete -> R.drawable.night_sleeping_svgrepo_com
            else -> {R.drawable.ic_add_black_24dp}
        }
        return ret
    }

    companion object {
        private val ALERT_COMPARATOR = object : DiffUtil.ItemCallback<Alerta>() {
            override fun areItemsTheSame(oldItem: Alerta, newItem: Alerta): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Alerta, newItem: Alerta): Boolean {
                return (
                    oldItem.nomeMedicamento == newItem.nomeMedicamento
                    && oldItem.quantidadeMedicamento == newItem.quantidadeMedicamento
                    && oldItem.lembretemedicamento == newItem.lembretemedicamento
                    && oldItem.tipoMedicamento == newItem.tipoMedicamento
                )
            }
        }
    }

    private fun getMensagem(alerta: Alerta) : String {
        val msg:ArrayList<String> = ArrayList()
        if(alerta.diasDaSemanaAlerta.diasDaSemana.isNotEmpty()) {
            alerta.diasDaSemanaAlerta.diasDaSemana.forEach {
                if(it == 1)
                    msg.add("Dom")
                if(it == 2)
                    msg.add("Seg")
                if(it == 3)
                    msg.add("Ter")
                if(it == 4)
                    msg.add("Qua")
                if(it == 5)
                    msg.add("Qui")
                if(it == 6)
                    msg.add("Sex")
                if(it == 7)
                    msg.add("Sab")
            }
            return msg.joinToString()
        } else {
            val calendar: Calendar = Calendar.getInstance()

            

            return SimpleDateFormat("dd/MM/yyyy HH:mm").format(calendar.time)

        }
    }
}