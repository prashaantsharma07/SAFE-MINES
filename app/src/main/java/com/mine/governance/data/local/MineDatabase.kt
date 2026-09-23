package com.mine.governance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mine.governance.data.local.dao.EmergencyDao
import com.mine.governance.data.local.dao.SyncQueueDao
import com.mine.governance.data.local.dao.TaskDao
import com.mine.governance.data.local.dao.UserDao
import com.mine.governance.data.local.entity.EmergencyReportEntity
import com.mine.governance.data.local.entity.SyncQueueEntity
import com.mine.governance.data.local.entity.TaskEntity
import com.mine.governance.data.local.entity.UserEntity
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.SyncStatus
import com.mine.governance.domain.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        EmergencyReportEntity::class,
        TaskEntity::class,
        SyncQueueEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MineDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun emergencyDao(): EmergencyDao
    abstract fun taskDao(): TaskDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: MineDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): MineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MineDatabase::class.java,
                    "mine_governance.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate with realistic mine field officer and demo tasks
                            scope.launch(Dispatchers.IO) {
                                populateInitialData(getInstance(context, scope))
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(database: MineDatabase) {
            val userDao = database.userDao()
            val taskDao = database.taskDao()

            // 1. Pre-populate field officer Rajesh Kumar
            userDao.insertOrUpdate(
                UserEntity(
                    employeeId = "EMP-7842",
                    name = "Rajesh Kumar",
                    role = "Safety Field Officer",
                    department = "Safety Operations",
                    assignedMine = "Mine B (Underground Shaft 4)",
                    permissionLevel = "LEVEL_3_INSPECTOR",
                    isCurrentlyLoggedIn = true
                )
            )

            // 2. Pre-populate active subterranean coal mine tasks
            val sampleTasks = listOf(
                TaskEntity(
                    id = "TSK-1049",
                    title = "Ventilation Shaft Damper Calibration",
                    issueDescription = "Airflow sensor reading lower than 4.5 m/s threshold in Sector 3 East Drift. Possible methane accumulation.",
                    originalReportId = "OBS-901",
                    priority = Severity.CRITICAL,
                    locationSector = "Sector 3 (East Drift)",
                    assignedBy = "Chief Inspector A. Verma",
                    assignedDate = System.currentTimeMillis() - 7200000,
                    deadlineDate = System.currentTimeMillis() + 14400000, // 4 hours from now
                    status = TaskStatus.PENDING,
                    aiRiskScore = 92,
                    requiredAction = "Inspect booster fan secondary baffles and measure CH4 concentration before recalibrating.",
                    beforeMediaUris = listOf("mock://media/vent_fan_clogged.jpg"),
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1050",
                    title = "Hydraulic Roof Support Bolt Inspection",
                    issueDescription = "Fissure observed along shearer conveyor roadway rib-line at 420m depth.",
                    originalReportId = "OBS-884",
                    priority = Severity.CRITICAL,
                    locationSector = "Deep Extraction Face 2",
                    assignedBy = "Mine Manager D. Sen",
                    assignedDate = System.currentTimeMillis() - 18000000,
                    deadlineDate = System.currentTimeMillis() + 7200000, // 2 hours from now
                    status = TaskStatus.IN_PROGRESS,
                    aiRiskScore = 88,
                    requiredAction = "Deploy tell-tale extensometers and verify anchor tension on canopy props.",
                    beforeMediaUris = listOf("mock://media/roof_crack_depth.jpg"),
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1051",
                    title = "Water Inrush Sump Pump Maintenance",
                    issueDescription = "Drainage valve 4B exhibiting pressure fluctuation in lower haulage sump.",
                    originalReportId = "OBS-879",
                    priority = Severity.HIGH,
                    locationSector = "Lower Haulage Level 5",
                    assignedBy = "Mechanical Lead S. Roy",
                    assignedDate = System.currentTimeMillis() - 86400000,
                    deadlineDate = System.currentTimeMillis() + 36000000,
                    status = TaskStatus.PENDING,
                    aiRiskScore = 67,
                    requiredAction = "Flush strainer and inspect impeller cavitation.",
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1052",
                    title = "Belt Conveyor Emergency Pull-Cord Test",
                    issueDescription = "Scheduled bi-weekly test of safety trip wires along Main Trunk Conveyor 1.",
                    originalReportId = "SCHED-22",
                    priority = Severity.MEDIUM,
                    locationSector = "Main Incline Beltway",
                    assignedBy = "Safety Operations",
                    assignedDate = System.currentTimeMillis() - 172800000,
                    deadlineDate = System.currentTimeMillis() + 86400000,
                    status = TaskStatus.PENDING,
                    aiRiskScore = 45,
                    requiredAction = "Walk entire 800m incline and trigger test trip switches.",
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1045",
                    title = "Dust Suppression Water Sprinklers Replacement",
                    issueDescription = "Nozzles 8 and 9 clogged with mineral residue at Transfer Chute 3.",
                    originalReportId = "OBS-862",
                    priority = Severity.LOW,
                    locationSector = "Transfer Chute 3",
                    assignedBy = "Environmental Safety",
                    assignedDate = System.currentTimeMillis() - 259200000,
                    deadlineDate = System.currentTimeMillis() - 3600000, // overdue
                    status = TaskStatus.PENDING,
                    aiRiskScore = 38,
                    requiredAction = "Replace brass spray nozzles with ceramic high-pressure tips.",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            taskDao.insertAll(sampleTasks)
        }
    }
}
