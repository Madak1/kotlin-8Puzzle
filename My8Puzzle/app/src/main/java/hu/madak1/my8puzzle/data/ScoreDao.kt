package hu.madak1.my8puzzle.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScoreDao {
    @Query("SELECT * FROM leaderboard_table ORDER BY score DESC")
    fun getAll(): List<Score>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addScore(score: Score)

    @Query("DELETE FROM leaderboard_table")
    suspend fun deleteAll()
}