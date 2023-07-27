package hu.madak1.my8puzzle

import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout

class MyPopAsk(layout: ConstraintLayout, textId: Int) {

    private val layout: ConstraintLayout
    private val card: CardView
    private val yesBtn: MyCardButton
    private val noBtn: MyCardButton

    init {
        this.layout = layout
        this.layout.alpha = 0.0f
        this.layout.translationZ = 0.0f
        this.card = this.layout.findViewById(R.id.pop_window_cv)
        this.yesBtn = MyCardButton(this.layout.findViewById(R.id.pop_yes_btn), R.string.btn_text_tv_yes)
        this.noBtn = MyCardButton(this.layout.findViewById(R.id.pop_no_btn), R.string.btn_text_tv_no)
        this.yesBtn.disableCard()
        this.noBtn.disableCard()
        this.setCardText(textId)
    }

    private fun setCardText(textId: Int) {
        this.card.findViewById<TextView>(R.id.pop_question_tv).setText(textId)
    }

    fun showPop() {
        this.layout.alpha = 1.0f
        this.layout.translationZ = 90.0f
        this.yesBtn.activateCard()
        this.noBtn.activateCard()
        this.inAnim()
    }

    private fun inAnim() {
        val transAnim = TranslateAnimation(0f, 0f, 50f, -50f)
        transAnim.duration = 250
        transAnim.fillAfter = true
        val alphaAnim = AlphaAnimation(0f, 1f)
        alphaAnim.duration = 250
        alphaAnim.fillAfter = true
        this.layout.startAnimation(alphaAnim)
        this.card.startAnimation(transAnim)
    }

    private fun outAnim() {
        val transAnim = TranslateAnimation(0f, 0f, -50f, 50f)
        transAnim.duration = 250
        transAnim.fillAfter = true
        val alphaAnim = AlphaAnimation(1f, 0f)
        alphaAnim.duration = 250
        alphaAnim.fillAfter = true
        this.layout.startAnimation(alphaAnim)
        this.card.startAnimation(transAnim)
    }

    fun setYesClickActions(func: () -> Unit) {
        this.yesBtn.setClickAction {
            func()
            this.outAnim()
            this.yesBtn.disableCard()
            this.noBtn.disableCard()
            this.layout.translationZ = 0.0f
        }
    }

    fun setNoClickActions(func: () -> Unit) {
        this.noBtn.setClickAction {
            func()
            this.outAnim()
            this.yesBtn.disableCard()
            this.noBtn.disableCard()
            this.layout.translationZ = 0.0f
        }
    }

}