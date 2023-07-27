package hu.madak1.my8puzzle

import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor

class MyCardButton(card: CardView, textId: Int) {

    private val card: CardView
    private val oElevation: Float
    private val activeColors: IntArray
    private val inactiveColors: IntArray
    private var isActive: Boolean

    init {
        this.card = card
        this.oElevation = card.elevation
        this.activeColors = intArrayOf(R.color.m_white, R.color.m_blue_primary)
        this.inactiveColors = intArrayOf(R.color.m_gray, R.color.m_blue_primary_dark)
        this.isActive = true
        this.setCardText(textId)
    }

    fun getCardText(): String = this.card.findViewById<TextView>(R.id.btn_text_tv).text.toString()

    fun setCardText(textId: Int) {
        this.card.findViewById<TextView>(R.id.btn_text_tv).setText(textId)
    }

    private fun setCardColor(colors: IntArray) {
        this.card.findViewById<TextView>(R.id.btn_text_tv)
            .setTextColor(getColor(this.card.context, colors[0]))
        this.card.findViewById<RelativeLayout>(R.id.btn_background_rl)
            .setBackgroundColor(getColor(this.card.context, colors[1]))
    }

    fun setClickAction(func: () -> Unit) {
        this.card.setOnClickListener {
            if(this.isActive) func()
        }
    }

    fun disableCard() {
        this.setCardColor(this.inactiveColors)
        this.isActive = false
        this.card.cardElevation = 0F
        this.card.alpha = 0.5f
    }

    fun activateCard() {
        this.setCardColor(this.activeColors)
        this.isActive = true
        this.card.cardElevation = this.oElevation
        this.card.alpha = 1.0f
    }

}