package hu.madak1.my8puzzle

import android.util.Log
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Tile(private var tView: ImageView, private var posX: Int, private var posY: Int) {

    fun getImage(): ImageView = this.tView
    fun getNumber(): Int = this.tView.contentDescription.toString().toInt()
    fun getX(): Int = this.posX
    fun getY(): Int = this.posY

    fun updatePos(move: Move, targetPosX: Int, targetPosY: Int) {
        val tmp = Pair(this.posX, this.posY)
        val validMove = when (move){
            Move.UP -> this.posX-- > 0
            Move.DOWN -> this.posX++ < 2
            Move.LEFT -> this.posY-- > 0
            Move.RIGHT -> this.posY++ < 2
        }
        if (validMove && targetPosX == this.posX && targetPosY == this.posY) {
            val delta = this.getDelta(move)
            this.goToNewPos(delta)
        } else {
            this.posX = tmp.first
            this.posY = tmp.second
            shake()
        }
    }

    private fun getDelta(move: Move): Pair<Float, Float> {
        val w = this.tView.width.toFloat()
        val h = this.tView.height.toFloat()
        return when (move){
            Move.UP -> Pair(0f, -h)
            Move.DOWN -> Pair(0f, h)
            Move.LEFT -> Pair(-w, 0f)
            Move.RIGHT -> Pair(w, 0f)
        }
    }

    private fun goToNewPos(delta: Pair<Float, Float>) {
        val transAnim = TranslateAnimation(0f, delta.first, 0f, delta.second)
        transAnim.duration = 250
        transAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                tView.clearAnimation()
                tView.x = tView.x + delta.first
                tView.y = tView.y + delta.second
            }
        })
        this.tView.startAnimation(transAnim)
    }

    fun swapPosWithTile(tile: Tile) {
        val deltaX = (this.posX - tile.posX) * this.tView.layoutParams.width
        val deltaY = (this.posY - tile.posY) * this.tView.layoutParams.height
        val tmpX = this.posX
        val tmpY = this.posY
        this.posX = tile.posX
        this.posY = tile.posY
        tile.posX = tmpX
        tile.posY = tmpY
        this.tView.x = this.tView.x - deltaY
        this.tView.y = this.tView.y - deltaX
        tile.tView.x = tile.tView.x + deltaY
        tile.tView.y = tile.tView.y + deltaX
    }

    private fun shake() {
        val shakeAnim = TranslateAnimation(-10f, 10f, 0f, 0f)
        shakeAnim.duration = 50
        shakeAnim.repeatMode = Animation.REVERSE
        shakeAnim.repeatCount = 4
        this.tView.startAnimation(shakeAnim)
    }

    fun win() {
        val pivotX = tView.width / 2f
        val pivotY = tView.height / 2f
        val scaleAnim = ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f, pivotX, pivotY)
        scaleAnim.duration = 250
        scaleAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                tView.clearAnimation()
                tView.alpha = 1.0f
                tView.scaleX = 1.0f
                tView.scaleY = 1.0f
            }
        })
        CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            tView.startAnimation(scaleAnim)
        }
    }

}