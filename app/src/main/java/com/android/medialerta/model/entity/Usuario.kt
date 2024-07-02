package com.android.medialerta.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Entity(tableName = "usuario_table")
data class Usuario (
    @ColumnInfo(name = "nome") var nome : String?=null,
    @PrimaryKey(autoGenerate = true) val id : Int = 0
)
