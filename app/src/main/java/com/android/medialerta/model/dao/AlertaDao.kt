package com.android.medialerta.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.medialerta.model.entity.Alerta
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Query("SELECT * FROM alerta_table ORDER BY hora_alerta,minuto_alerta")
    fun getAlertList(): Flow<List<Alerta>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alerta: Alerta)

    @Query("DELETE FROM alerta_table WHERE id = :id")
    suspend fun deleteById(id:Int)

    @Update
    suspend fun update(alerta: Alerta)

    @Query("SELECT * FROM alerta_table WHERE id = :id")
    suspend fun getById(id:Int) : Alerta

    @Query("SELECT * FROM alerta_table WHERE (data_criacao >= :hoje OR dias_da_semana IS NOT NULL)")
    fun getNextAlerts(hoje:String) : Flow<List<Alerta>>
}