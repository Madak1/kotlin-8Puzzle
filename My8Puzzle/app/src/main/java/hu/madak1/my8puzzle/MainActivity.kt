package hu.madak1.my8puzzle

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cards = arrayOf(
            MyCardButton(findViewById(R.id.main_start_btn), R.string.btn_text_tv_start),
            MyCardButton(findViewById(R.id.main_settings_btn), R.string.btn_text_tv_settings),
            MyCardButton(findViewById(R.id.main_leaderboard_btn), R.string.btn_text_tv_leaderboard)
        )

        for (card in cards) {
            val targetIntent: Intent? = when (card.getCardText()) {
                "Start" -> Intent(this, StartActivity::class.java)
                "Settings" -> Intent(this, SettingsActivity::class.java)
                "Leaderboard" -> Intent(this, LeaderboardActivity::class.java)
                else -> null
            }
            if (targetIntent != null) card.setClickAction {
                startActivity(targetIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.tileRain()
    }

    // === CREATE TILE RAIN ON THE MAIN SCREEN =====================================================

    override fun onPause() {
        super.onPause()

        for (tile in tiles) {
            tile.clearAnimation()
            tile.alpha = 0f
        }

        this.scope.cancel()
    }

    private lateinit var scope: CoroutineScope
    private lateinit var tiles: ArrayList<ImageView>

    private fun isRunningInTest(): Boolean {
        return System.getProperty("test")?.toBoolean() == true
    }

    private fun tileRain() {
        this.scope = CoroutineScope(Dispatchers.Main)
        this.tiles = arrayListOf(
            findViewById(R.id.main_n2_iv),
            findViewById(R.id.main_n1_iv),
            findViewById(R.id.main_n3_iv),
            findViewById(R.id.main_n4_iv),
            findViewById(R.id.main_n6_iv),
            findViewById(R.id.main_n5_iv),
            findViewById(R.id.main_n7_iv),
            findViewById(R.id.main_n8_iv),
            findViewById(R.id.main_n9_iv)
        )
        // Skip execution in test environment
        if (isRunningInTest()) return
        for (i in this.tiles.indices) {
            scope.launch {
                delay((i*600).toLong())
                tileFall(tiles[i])
            }
        }
    }

    private fun tileFall(tile: ImageView) {
        val rnd = (1 .. 9).random()
        tile.setImageResource(this.chooseTile(rnd))
        val transAnim = TranslateAnimation(
            (-tile.layoutParams.width..tile.layoutParams.width).random().toFloat(),
            0f,
            0f,
            screenHeight.toFloat()*1.2f
        )
        transAnim.duration = 600*9
        transAnim.interpolator = LinearInterpolator()
        transAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                tileFall(tile)
            }
        })
        val rotateAnim = RotateAnimation(
            0f,
            if ((0..1).random() == 0) 360f else -360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = transAnim.duration*(3..6).random()
        rotateAnim.repeatCount = rotateAnim.duration.toInt()
        rotateAnim.interpolator = LinearInterpolator()
        val alphaAnim = AlphaAnimation(0f, 1f)
        alphaAnim.duration = 1700
        alphaAnim.repeatMode = Animation.REVERSE
        alphaAnim.repeatCount = 1
        alphaAnim.interpolator = AccelerateInterpolator()

        val animSet = AnimationSet(true)
        animSet.addAnimation(transAnim)
        animSet.addAnimation(rotateAnim)
        animSet.addAnimation(alphaAnim)
        tile.alpha = 1f
        tile.startAnimation(animSet)
    }

    private fun chooseTile(r: Int): Int {
        return when (r) {
            1 -> R.drawable.n1
            2 -> R.drawable.n2
            3 -> R.drawable.n3
            4 -> R.drawable.n4
            5 -> R.drawable.n5
            6 -> R.drawable.n6
            7 -> R.drawable.n7
            8 -> R.drawable.n8
            else -> R.drawable.n9
        }
    }
}