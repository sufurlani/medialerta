package com.android.medialerta.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.medialerta.model.dao.AlertaDao
import com.android.medialerta.model.entity.Alerta

@Database(entities = [Alerta::class], version = 1, exportSchema = false)
abstract class  AlertaRoomDatabase : RoomDatabase() {
    abstract fun alertaDao(): AlertaDao

    companion object {
        @Volatile
        private var INSTANCE: AlertaRoomDatabase? = null

        fun getDatabase(
            context: Context,
        ): AlertaRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlertaRoomDatabase::class.java,
                    "alerta_table"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}