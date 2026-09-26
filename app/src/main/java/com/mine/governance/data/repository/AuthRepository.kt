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

    suspend fun login(employeeId: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedId = employeeId.trim()
        val trimmedPassword = password.trim()

        if (trimmedId.isBlank() || trimmedPassword.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Employee ID and Password cannot be empty."))
        }

        var existingUser = userDao.getUserById(trimmedId)

        // Seed default demo officer if database was empty or not yet initialized
        if (existingUser == null && (trimmedId.equals("EMP-7842", ignoreCase = true) || trimmedId.contains("7842"))) {
            existingUser = UserEntity(
                employeeId = "EMP-7842",
                name = "Rajesh Kumar",
                role = "Safety Field Officer",
                department = "Safety Operations & Compliance",
                assignedMine = "Mine B (Underground Shaft 4)",
                assignedColliery = "Jharia Colliery (Bharat Coking Coal Limited)",
                dgmsCertification = "DGMS First Class Manager Certificate (CMR-2019/4821)",
                permissionLevel = "LEVEL_3_INSPECTOR",
                passwordHash = PasswordHasher.hash("MineSafety@2026"),
                isCurrentlyLoggedIn = false
            )
            userDao.insertOrUpdate(existingUser)
        }

        if (existingUser == null) {
            return@withContext Result.failure(IllegalArgumentException("Officer ID not recognized. Please use registered credentials."))
        }

        // Verify cryptographic hash
        val isPasswordValid = PasswordHasher.verify(trimmedPassword, existingUser.passwordHash)
        if (!isPasswordValid) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect password. Please verify your credentials."))
        }

        // Atomically switch active session to prevent multi-user race conditions
        userDao.switchActiveUser(existingUser.employeeId)

        val updatedUser = userDao.getUserById(existingUser.employeeId) ?: existingUser
        Result.success(updatedUser)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logoutAll()
    }
}
