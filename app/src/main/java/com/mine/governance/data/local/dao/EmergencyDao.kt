package com.mine.governance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mine.governance.data.local.entity.EmergencyReportEntity
import com.mine.governance.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergency(report: EmergencyReportEntity)

    @Query("SELECT * FROM emergency_reports ORDER BY timestamp DESC")
    fun getAllEmergenciesFlow(): Flow<List<EmergencyReportEntity>>

    @Query("SELECT * FROM emergency_reports WHERE id = :id")
    suspend fun getEmergencyById(id: String): EmergencyReportEntity?

    @Query("SELECT COUNT(*) FROM emergency_reports WHERE syncStatus != 'SYNCED'")
    fun getPendingEmergencyCountFlow(): Flow<Int>

    @Query("UPDATE emergency_reports SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT COUNT(*) FROM emergency_reports")
    fun getTotalEmergencyCountFlow(): Flow<Int>
}
