package com.android.medialerta.model.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream


@TypeConverters(Converters::class)
@Entity(tableName = "alerta_table")
data class Alerta (
    @ColumnInfo(name = "nome") val nomeMedicamento : String?=null,
    @ColumnInfo(name = "quantiadade") val quantidadeMedicamento : Int? = null,
    @ColumnInfo(name = "hora_alerta") val horaAlerta: Int? = null,
    @ColumnInfo(name = "minuto_alerta") val minutoAlerta: Int? = null,
    @ColumnInfo(name = "tipo") val tipoMedicamento : String = "Outros",
    @ColumnInfo(name = "lembrete") val lembretemedicamento: String = "Outros",
    @ColumnInfo(name = "dias_da_semana") val diasDaSemanaAlerta : DiasDaSemana,
    @ColumnInfo(name = "imagem") val imagem : Bitmap?=null,
    @ColumnInfo(name = "data_criacao") val dataCriacao : String? = null,
    @PrimaryKey(autoGenerate = true) val id : Int = 0
)

data class DiasDaSemana(
    val diasDaSemana: ArrayList<Int> = ArrayList()
)

class Converters {
    @TypeConverter
    fun toDiasDaSemana(value: String?): DiasDaSemana {
        if (value.isNullOrEmpty()) {
            return DiasDaSemana()
        }
        val list: List<String> = value.split(",")
        val intLst = ArrayList<Int>()
        for (item in list) {
            if (item.isNotEmpty()) {
                intLst.add(item.toInt())
            }
        }
        return DiasDaSemana(intLst)
    }

    @TypeConverter
    fun toString(diasDaSemana: DiasDaSemana?): String {
        var string = ""
        if (diasDaSemana == null) {
            return string
        }
        diasDaSemana.diasDaSemana.forEach {
            string += "$it,"
        }
        return string
    }

    @TypeConverter
    fun bitmapToBase64(bitmap: Bitmap) : String{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, DEFAULT)
    }

    @TypeConverter
    fun base64ToBitmap(base64String: String):Bitmap?{
        return try {
            val encodeByte = Base64.decode(base64String, DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e:Exception) {
            null
        }
    }
}