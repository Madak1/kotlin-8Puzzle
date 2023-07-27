package hu.madak1.my8puzzle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Database(entities = [Setting::class, Score::class], version = 1, exportSchema = false)
abstract class PuzzleDatabase: RoomDatabase() {

    abstract fun settingsDao(): SettingDao
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var INSTANCE: PuzzleDatabase? = null

        fun getDatabase(context: Context): PuzzleDatabase {
            val tmpInstance = INSTANCE
            if (tmpInstance != null) return tmpInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PuzzleDatabase::class.java,
                    "puzzle_database"
                ).addCallback(object: Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val settingsDao = getDatabase(context).settingsDao()
                        runBlocking {
                            CoroutineScope(Dispatchers.IO).launch {
                                // Add the settings when make the database
                                settingsDao.addSetting(Setting(0, "Music", true))
                                settingsDao.addSetting(Setting(1, "Tilt", false))
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}