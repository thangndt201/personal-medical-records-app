package com.example.appquanlybenhancanhan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insert(patient: Patient)
    @Update
    suspend fun update(patient: Patient)
    @Delete
    suspend fun delete(patient: Patient)
    @Query("SELECT * FROM patient_table")
    fun getAllPatients(): Flow<List<Patient>>
    @Query("SELECT * FROM patient_table WHERE userId = :userId LIMIT 1")
    suspend fun getPatientByUserId(userId: Long): Patient?
}