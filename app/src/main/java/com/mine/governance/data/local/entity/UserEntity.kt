package com.mine.governance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val employeeId: String,
    val name: String,
    val role: String,
    val department: String,
    val assignedMine: String,
    val permissionLevel: String,
    val isCurrentlyLoggedIn: Boolean = false,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)
