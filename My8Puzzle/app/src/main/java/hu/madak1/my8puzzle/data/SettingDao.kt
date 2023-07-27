package hu.madak1.my8puzzle.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings_table ORDER BY id ASC")
    fun getAll(): List<Setting>

    @Update
    suspend fun updateSetting(setting: Setting)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSetting(setting: Setting)
}