package com.android.medialerta.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.medialerta.model.dao.UsuarioDao
import com.android.medialerta.model.entity.Usuario

@Database(entities = [Usuario::class], version = 1, exportSchema = false)
abstract class  UsuarioRoomDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: UsuarioRoomDatabase? = null

        fun getDatabase(
            context: Context,
        ): UsuarioRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsuarioRoomDatabase::class.java,
                    "usuario_table"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}