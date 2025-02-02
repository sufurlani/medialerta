package com.android.medialerta.presentation.alert

import android.graphics.Color
import android.graphics.Paint
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.medialerta.model.entity.Alerta
import com.android.medialerta.presentation.alert.AlertaListAdapter.AlertViewHolder
import com.android.medialerta.presentation.enums.LembreteEnum
import com.android.medialerta.presentation.enums.TipoMedicamentoEnum
import medialerta.R
import medialerta.databinding.AlertasrecycleviewItemBinding
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


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
            binding.dtAlertaItem.text = getMensagem(alerta, binding.dtAlertaItem).toString()
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

    private fun getMensagem(alerta: Alerta, txtAlertaItem: TextView) : String? {
        val msg:ArrayList<String> = ArrayList()
        var stringEnd = " " + alerta.horaAlerta + ":" + alerta.minutoAlerta + " hrs"
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
            return msg.joinToString() + stringEnd
        } else {
            val dataCriacao = LocalDateTime.parse(alerta.dataCriacao,  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))

            if(now().dayOfMonth == dataCriacao.dayOfMonth
                && (alerta.horaAlerta!! < dataCriacao.hour || (alerta.horaAlerta!! == dataCriacao.hour && alerta.minutoAlerta!! < dataCriacao.minute)))
                    return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(dataCriacao.plusDays(1)) + stringEnd
            else if(now().dayOfMonth < dataCriacao.dayOfMonth || now().dayOfMonth == dataCriacao.dayOfMonth
                    && (now().hour > alerta.horaAlerta!! || (now().hour == alerta.horaAlerta!!  && now().minute > alerta.minutoAlerta!!)))
            {
                txtAlertaItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                txtAlertaItem.setTextColor(Color.RED)
                txtAlertaItem.tooltipText = "Este alerta já foi enviado e pode ser deletado da lista"
                return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(dataCriacao) + stringEnd
            }

            return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(dataCriacao) + stringEnd
        }
    }
}