package hu.madak1.my8puzzle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import hu.madak1.my8puzzle.data.PuzzleDatabase
import hu.madak1.my8puzzle.data.Score
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val timeScore: Int = intent.getIntExtra("EXTRA_TIME", 0)
        val steScore: Int = intent.getIntExtra("EXTRA_STEPS", 0)

        val username = findViewById<EditText>(R.id.score_username_et)
        val upload = MyCardButton(findViewById(R.id.score_up_btn), R.string.btn_text_tv_upload)
        val cancel = MyCardButton(findViewById(R.id.score_back_btn), R.string.btn_text_tv_cancel)

        val score = if (1000-(3*timeScore+(2*steScore)) > 0) 1000-(3*timeScore+(2*steScore)) else 0
        val scoreStr = "$score pts"
        findViewById<TextView>(R.id.score_score_tv).text = scoreStr

        val scoreDao = PuzzleDatabase.getDatabase(this).scoreDao()

        upload.setClickAction {
            if (username.text.length in 3..8) {
                CoroutineScope(Dispatchers.IO).launch {
                    scoreDao.addScore(Score(username = username.text.toString(), score = score))
                    finish()
                }
            } else {
                Toast.makeText(this,
                    "The username length must be between 3 and 8", Toast.LENGTH_SHORT).show()
            }
        }

        cancel.setClickAction {
            this.finish()
        }
    }
}