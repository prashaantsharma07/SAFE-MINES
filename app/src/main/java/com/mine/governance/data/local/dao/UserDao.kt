package com.mine.governance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mine.governance.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isCurrentlyLoggedIn = 1 ORDER BY lastActiveTimestamp DESC LIMIT 1")
    fun getActiveUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentlyLoggedIn = 1 ORDER BY lastActiveTimestamp DESC LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Query("SELECT * FROM users WHERE employeeId = :empId LIMIT 1")
    suspend fun getUserById(empId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("UPDATE users SET isCurrentlyLoggedIn = 0")
    suspend fun logoutAll()

    @Query("UPDATE users SET isCurrentlyLoggedIn = 1, lastActiveTimestamp = :now WHERE employeeId = :empId")
    suspend fun setActiveUser(empId: String, now: Long = System.currentTimeMillis())

    @Transaction
    suspend fun switchActiveUser(empId: String, now: Long = System.currentTimeMillis()) {
        logoutAll()
        setActiveUser(empId, now)
    }
}
