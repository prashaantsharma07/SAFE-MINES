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
    val assignedColliery: String = "Jharia Colliery (Bharat Coking Coal Limited)",
    val dgmsCertification: String = "DGMS First Class Manager Certificate (CMR-2019/4821)",
    val permissionLevel: String,
    val passwordHash: String = "",
    val isCurrentlyLoggedIn: Boolean = false,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)
