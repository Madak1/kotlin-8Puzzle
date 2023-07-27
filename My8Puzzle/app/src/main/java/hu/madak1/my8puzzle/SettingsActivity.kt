package hu.madak1.my8puzzle

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hu.madak1.my8puzzle.data.PuzzleDatabase
import hu.madak1.my8puzzle.data.Setting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val music = MyCardButton(findViewById(R.id.settings_music_btn), R.string.btn_text_tv_m_off)
        val tilt = MyCardButton(findViewById(R.id.settings_tilt_btn), R.string.btn_text_tv_t_off)
        val del = MyCardButton(findViewById(R.id.settings_del_btn), R.string.btn_text_tv_del)
        val back = MyCardButton(findViewById(R.id.settings_back_btn), R.string.btn_text_tv_back)
        val pop = MyPopAsk(findViewById(R.id.settings_del_pop), R.string.pop_question_tv_reset)

        val scoreDao = PuzzleDatabase.getDatabase(this).scoreDao()
        val settingsDao = PuzzleDatabase.getDatabase(this).settingsDao()
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsDao.getAll()
            if (settings[0].value) music.setCardText(R.string.btn_text_tv_m_on)
            if (settings[1].value) tilt.setCardText(R.string.btn_text_tv_t_on)
        }

        pop.setYesClickActions {
            CoroutineScope(Dispatchers.IO).launch {
                scoreDao.deleteAll()
            }
            Toast.makeText(this, "Reset: Done!", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.settings_title_tv).alpha = 1.0f
            music.activateCard()
            tilt.activateCard()
            del.activateCard()
            back.activateCard()
        }

        pop.setNoClickActions {
            findViewById<TextView>(R.id.settings_title_tv).alpha = 1.0f
            music.activateCard()
            tilt.activateCard()
            del.activateCard()
            back.activateCard()
        }

        music.setClickAction {
            if (music.getCardText() == getString(R.string.btn_text_tv_m_on)) {
                music.setCardText(R.string.btn_text_tv_m_off)
            } else {
                music.setCardText(R.string.btn_text_tv_m_on)
            }
            CoroutineScope(Dispatchers.IO).launch {
                val old = settingsDao.getAll()[0]
                val new = Setting(0, old.name, !old.value)
                settingsDao.updateSetting(new)
            }
        }

        tilt.setClickAction {
            if (tilt.getCardText() == getString(R.string.btn_text_tv_t_on)) {
                tilt.setCardText(R.string.btn_text_tv_t_off)
            } else {
                tilt.setCardText(R.string.btn_text_tv_t_on)
            }
            CoroutineScope(Dispatchers.IO).launch {
                val old = settingsDao.getAll()[1]
                val new = Setting(1, old.name, !old.value)
                settingsDao.updateSetting(new)
            }
        }

        del.setClickAction {
            pop.showPop()
            findViewById<TextView>(R.id.settings_title_tv).alpha = 0.4f
            music.disableCard()
            tilt.disableCard()
            del.disableCard()
            back.disableCard()
        }

        back.setClickAction {
            this.finish()
        }
    }

}