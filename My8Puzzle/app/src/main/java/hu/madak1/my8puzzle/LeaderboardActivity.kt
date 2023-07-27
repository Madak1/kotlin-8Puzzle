package hu.madak1.my8puzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.madak1.my8puzzle.data.PuzzleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val leaderboardRV = findViewById<RecyclerView>(R.id.leaderboards_list_rv)
        val back = MyCardButton(findViewById(R.id.leaderboards_back_btn), R.string.btn_text_tv_back)

        val scoreDao = PuzzleDatabase.getDatabase(this).scoreDao()
        CoroutineScope(Dispatchers.IO).launch {
            val leaderboard = scoreDao.getAll()
            findViewById<TextView>(R.id.leaderboard_empty_tv).setText(
                if (leaderboard.isEmpty()) R.string.main_empty_tv_text  else R.string.main_empty_tv_text2
            )
            leaderboardRV.adapter = AdapterRV(leaderboard)
            leaderboardRV.layoutManager = LinearLayoutManager(parent)
        }

        back.setClickAction {
            this.finish()
        }
    }
}