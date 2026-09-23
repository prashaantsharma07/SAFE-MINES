package com.mine.governance.data.local

import androidx.room.TypeConverter
import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.SyncStatus
import com.mine.governance.domain.model.TaskStatus

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = "|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromEmergencyType(value: EmergencyType?): String = value?.name ?: EmergencyType.OTHER.name

    @TypeConverter
    fun toEmergencyType(value: String?): EmergencyType =
        try {
            value?.let { EmergencyType.valueOf(it) } ?: EmergencyType.OTHER
        } catch (e: Exception) {
            EmergencyType.OTHER
        }

    @TypeConverter
    fun fromSeverity(value: Severity?): String = value?.name ?: Severity.LOW.name

    @TypeConverter
    fun toSeverity(value: String?): Severity =
        try {
            value?.let { Severity.valueOf(it) } ?: Severity.LOW
        } catch (e: Exception) {
            Severity.LOW
        }

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus?): String = value?.name ?: TaskStatus.PENDING.name

    @TypeConverter
    fun toTaskStatus(value: String?): TaskStatus =
        try {
            value?.let { TaskStatus.valueOf(it) } ?: TaskStatus.PENDING
        } catch (e: Exception) {
            TaskStatus.PENDING
        }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus?): String = value?.name ?: SyncStatus.PENDING.name

    @TypeConverter
    fun toSyncStatus(value: String?): SyncStatus =
        try {
            value?.let { SyncStatus.valueOf(it) } ?: SyncStatus.PENDING
        } catch (e: Exception) {
            SyncStatus.PENDING
        }
}
