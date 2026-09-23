package com.mine.governance.data.repository

import com.mine.governance.data.local.dao.UserDao
import com.mine.governance.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuthRepository(private val userDao: UserDao) {

    val activeUserFlow: Flow<UserEntity?> = userDao.getActiveUserFlow()

    suspend fun getActiveUser(): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getActiveUser()
    }

    suspend fun login(employeeId: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        if (employeeId.isBlank() || password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Employee ID and Password cannot be empty."))
        }

        // Check if officer already exists in local DB or initialize
        var user = userDao.getActiveUser()
        if (user == null || user.employeeId != employeeId) {
            user = UserEntity(
                employeeId = employeeId,
                name = if (employeeId.contains("7842")) "Rajesh Kumar" else "Field Officer (${employeeId.take(8)})",
                role = "Safety Field Officer",
                department = "Safety Operations",
                assignedMine = "Mine B (Underground Shaft 4)",
                permissionLevel = "LEVEL_3_INSPECTOR",
                isCurrentlyLoggedIn = true
            )
            userDao.insertOrUpdate(user)
        } else {
            userDao.setActiveUser(user.employeeId)
        }

        Result.success(user)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logoutAll()
    }
}
