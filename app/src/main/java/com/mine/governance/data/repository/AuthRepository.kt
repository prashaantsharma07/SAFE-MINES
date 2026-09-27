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

        // Seed default demo field officer
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

        // Seed default Executive Admin user
        if (existingUser == null && (trimmedId.equals("admin", ignoreCase = true) || trimmedId.equals("ADMIN-001", ignoreCase = true))) {
            existingUser = UserEntity(
                employeeId = "admin",
                name = "Dr. Vikram Sethi (Director)",
                role = "Chief Safety Inspector / DGMS Director",
                department = "Directorate of Mine Safety & Governance",
                assignedMine = "All Colliery Sectors (Jharia Coalfield)",
                assignedColliery = "Jharia Colliery Central Command",
                dgmsCertification = "DGMS National Directorial Inspector Warrant (CMR-DIRECTOR-01)",
                permissionLevel = "LEVEL_5_DIRECTOR",
                passwordHash = PasswordHasher.hash("admin"),
                isCurrentlyLoggedIn = false
            )
            userDao.insertOrUpdate(existingUser)
        }

        if (existingUser == null) {
            return@withContext Result.failure(IllegalArgumentException("Account not recognized. Please use registered credentials."))
        }

        // Verify password hash (supports 'admin' / 'Admin@2026' for admin)
        val isPasswordValid = if (existingUser.employeeId.equals("admin", ignoreCase = true)) {
            trimmedPassword == "admin" || trimmedPassword == "Admin@2026" || PasswordHasher.verify(trimmedPassword, existingUser.passwordHash)
        } else {
            PasswordHasher.verify(trimmedPassword, existingUser.passwordHash)
        }

        if (!isPasswordValid) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect password. Please verify your credentials."))
        }

        // Atomically switch active session to prevent multi-user race conditions
        userDao.switchActiveUser(existingUser.employeeId)

        val updatedUser = userDao.getUserById(existingUser.employeeId) ?: existingUser
        Result.success(updatedUser)
    }

    suspend fun biometricLogin(targetEmpId: String = "EMP-7842"): Result<UserEntity> = withContext(Dispatchers.IO) {
        var user = userDao.getUserById(targetEmpId)
        if (user == null) {
            user = UserEntity(
                employeeId = "EMP-7842",
                name = "Rajesh Kumar",
                role = "Safety Field Officer",
                department = "Safety Operations & Compliance",
                assignedMine = "Mine B (Underground Shaft 4)",
                assignedColliery = "Jharia Colliery (Bharat Coking Coal Limited)",
                dgmsCertification = "DGMS First Class Manager Certificate (CMR-2019/4821)",
                permissionLevel = "LEVEL_3_INSPECTOR",
                passwordHash = PasswordHasher.hash("MineSafety@2026"),
                isCurrentlyLoggedIn = true
            )
            userDao.insertOrUpdate(user)
        }
        userDao.switchActiveUser(user.employeeId)
        val updatedUser = userDao.getUserById(user.employeeId) ?: user
        Result.success(updatedUser)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logoutAll()
    }
}
