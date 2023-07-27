package hu.madak1.my8puzzle

// Imports
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.exifinterface.media.ExifInterface
import hu.madak1.my8puzzle.data.PuzzleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

// Game states
enum class GameState {
    PLAY, PAUSE, OVER
}

// Game activity
class GameActivity : AppCompatActivity(), SensorEventListener {

    // Last click (avoid 2x click on tile)
    private var lastClickTime: Long = SystemClock.elapsedRealtime()
    // Game time
    private lateinit var timerServiceIntent: Intent
    private var elapsedTime = 0.0
    // Music player and tilt control
    private var mediaPlayer: MediaPlayer? = null
    private var musicOn = false
    private var sensorManager: SensorManager? = null
    private var tiltControlOn = false
    // UI elements
    private lateinit var timerTv: TextView
    private lateinit var board: View
    private lateinit var boardBackgroundImage: ImageView
    private lateinit var pauseBtn: MyCardButton
    private lateinit var exitBtn: MyCardButton
    private lateinit var popWindow: MyPopAsk
    private lateinit var tiles: Array<Array<Tile>>
    // Game logic
    private lateinit var game: Puzzle
    // Game state
    private var gameState = GameState.PLAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Getting a picture to play with
        val backgroundResource = intent.getIntExtra("EXTRA_BG_RES", -1)
        val backgroundPath = intent.getStringExtra("EXTRA_BG_PATH")

        // Set the music player and control style based on the settings
        val settingsDao = PuzzleDatabase.getDatabase(this).settingsDao()
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsDao.getAll()
            if (settings.isNotEmpty()) {
                if (settings[0].value) musicOn = true
                if (settings[1].value) tiltControlOn = true
            }
            // Setup media player if necessary
            if (musicOn) setupMediaPlayer()

            // Setup tilt sensor if necessary
            if (tiltControlOn) setUpSensor()
        }

        // Setup time service
        this.timerServiceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(this.updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        // Getting the UI elements to manipulate them later
        this.timerTv = findViewById(R.id.game_timer_tv)
        this.board = findViewById(R.id.board)
        this.boardBackgroundImage = findViewById(R.id.board_original_iv)
        this.pauseBtn = MyCardButton(findViewById(R.id.game_pause_btn), R.string.btn_text_tv_pause)
        this.exitBtn = MyCardButton(findViewById(R.id.game_exit_btn), R.string.btn_text_tv_exit)
        this.popWindow = MyPopAsk(findViewById(R.id.game_exit_pop), R.string.pop_question_tv_exit)

        // Exit the game when we click yes
        this.popWindow.setYesClickActions {
            // Finish this activity
            this.finish()
        }

        // Continue the game if we click no
        this.popWindow.setNoClickActions {
            // If the game was on pause or over mode, then the timer remain stopped
            if (this.gameState == GameState.PLAY) {
                this.timerServiceIntent.putExtra(TimerService.TIMER_EXTRA, this.elapsedTime)
                startService(this.timerServiceIntent)
            }
            // If the board is visible then set the alpha to 100%
            if (this.gameState != GameState.PAUSE) this.board.alpha = 1.0f
            // Reactivate the buttons
            this.pauseBtn.activateCard()
            this.exitBtn.activateCard()
            // Show the timer with 100% alpha
            this.timerTv.alpha = 1.0f
        }

        // Set the background board image (Original image)
        if (backgroundPath!=null) {
            val f = File(backgroundPath)
            val bm = rotateBitmap(BitmapFactory.decodeFile(f.absolutePath), backgroundPath)
            this.boardBackgroundImage.setImageBitmap(this.croppedBm(bm))
        } else {
            this.boardBackgroundImage.setImageResource(backgroundResource)
        }

        // Create the tiles
        this.tiles = arrayOf(
            arrayOf(
                Tile(findViewById(R.id.board_tile11_iv), 0, 0),
                Tile(findViewById(R.id.board_tile12_iv), 0, 1),
                Tile(findViewById(R.id.board_tile13_iv), 0, 2)
            ),
            arrayOf(
                Tile(findViewById(R.id.board_tile21_iv), 1, 0),
                Tile(findViewById(R.id.board_tile22_iv), 1, 1),
                Tile(findViewById(R.id.board_tile23_iv), 1, 2)
            ),
            arrayOf(
                Tile(findViewById(R.id.board_tile31_iv), 2, 0),
                Tile(findViewById(R.id.board_tile32_iv), 2, 1),
                Tile(findViewById(R.id.board_tile33_iv), 2, 2)
            )
        )

        // Setup the game based on the board
        this.game = Puzzle(this.getBoard())
        // Set the images for each tile
        this.fillTiles()
        // Randomize the tiles
        this.randomizeBoard()

        // Add click actions for each tile (Which tile where could go if we tap them)
        for (row in 0 until this.tiles.size) {
            for (col in 0 until this.tiles[row].size) {
                this.tiles[row][col].getImage().setOnClickListener {
                    // If the game is on pause or over then do nothing
                    if (this.board.alpha == 1.0f && this.gameState == GameState.PLAY)
                        this.tileMovement(this.tiles[row][col])
                }
            }
        }

        // Start the timer
        this.timerServiceIntent.putExtra(TimerService.TIMER_EXTRA, elapsedTime)
        startService(timerServiceIntent)

        // Add action to the pause/resume/upload button
        this.pauseBtn.setClickAction {
            // Disable the button to avoid 2x click (It will be reactivated after the animations)
            this.pauseBtn.disableCard()
            // Based on the game state this button can do various things
            when (this.gameState) {
                // When the game is in play state, then this button will be pause the game
                GameState.PLAY -> {
                    this.gameState = GameState.PAUSE
                    this.pauseBtn.setCardText(R.string.btn_text_tv_resume)
                    stopService(this.timerServiceIntent)
                    this.board.alpha = 0.0f
                    this.goToNewPos(this.timerTv, this.board.y)
                }
                // If the game is pause, then if we tap this button, the game will continue
                GameState.PAUSE -> {
                    this.gameState = GameState.PLAY
                    this.pauseBtn.setCardText(R.string.btn_text_tv_pause)
                    this.goToNewPos(this.timerTv, -this.board.y)
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(300)
                        board.alpha = 1.0f
                        timerServiceIntent.putExtra(TimerService.TIMER_EXTRA, elapsedTime)
                        startService(timerServiceIntent)
                    }
                }
                // If the game is over then we could upload our score (Go to upload activity)
                GameState.OVER -> {
                    val intent = Intent(this, ScoreActivity::class.java)
                    intent.putExtra("EXTRA_TIME", elapsedTime.roundToInt())
                    intent.putExtra("EXTRA_STEPS", this.game.getStepNum())
                    startActivity(intent)
                    this.finish()
                }
            }
        }

        // Show the pop window if we click the exit button (and disable the buttons)
        this.exitBtn.setClickAction {
            stopService(this.timerServiceIntent)
            this.pauseBtn.disableCard()
            this.exitBtn.disableCard()
            this.timerTv.alpha = 0.2f
            if (this.gameState != GameState.PAUSE) this.board.alpha = 0.2f
            this.popWindow.showPop()
        }
    }

    override fun onResume() {
        super.onResume()
        this.mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        this.mediaPlayer?.pause()
    }

    // If the activity is destroy then...
    override fun onDestroy() {
        // Stop time service
        stopService(this.timerServiceIntent)
        // Unregister the sensor manager
        this.sensorManager?.unregisterListener(this)
        // Release media player
        this.mediaPlayer?.release()
        super.onDestroy()
    }

    // What happen if the game is over
    private fun winner() {
        // Use the win animation for each tile
        for (row in 0 until this.tiles.size) {
            for (col in 0 until this.tiles[row].size) {
                this.tiles[row][col].win()
            }
        }
        // Set the game state to over
        this.gameState = GameState.OVER
        // Stop the timer service
        stopService(this.timerServiceIntent)
        // Change the pause button to upload button
        this.pauseBtn.setCardText(R.string.btn_text_tv_upload)
    }

    // Set a new position to a textView based by a delta (whit animation)
    private fun goToNewPos(tView: TextView, delta: Float) {
        val transAnim = TranslateAnimation(0f, 0f, 0f, delta)
        transAnim.duration = 400
        transAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                tView.clearAnimation()
                tView.y = tView.y + delta
                pauseBtn.activateCard()
            }
        })
        tView.startAnimation(transAnim)
    }

    // === Bitmap ==================================================================================

    private fun rotateBitmap(source: Bitmap, path: String): Bitmap {
        val ei = ExifInterface(path)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap
            .createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun croppedBm(bm: Bitmap): Bitmap {
        val targetSize = if (bm.width > bm.height) bm.height else bm.width
        val left = (bm.width-targetSize)/2
        val top = (bm.height-targetSize)/2
        return Bitmap.createBitmap(bm, left, top, targetSize, targetSize)
    }

    // === TIMER ===================================================================================

    // Update the time and set the timer textview
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            elapsedTime = intent.getDoubleExtra(TimerService.TIMER_EXTRA, 0.0)
            timerTv.text = getTimeStr()
        }
    }

    // Calculate the hour, minute and second
    private fun getTimeStr(): String {
        val resultInt = elapsedTime.roundToInt()
        val h = resultInt % 86400 / 3600
        val m = resultInt % 86400 % 3600 / 60
        val s = resultInt % 86400 % 3600 % 60
        return makeTimeStr(h, m, s)
    }

    // Create a string in a proper format for the timer
    private fun makeTimeStr(h: Int, m: Int, s: Int): String = String.format("%02d:%02d:%02d",h,m,s)

    // === BOARD ===================================================================================

    // Randomize the board while get a solvable game
    private fun randomizeBoard() {
        do {
            for (i in 0..100) {
                val randX1 = (0 until 3).random()
                val randY1 = (0 until 3).random()
                val randX2 = (0 until 3).random()
                val randY2 = (0 until 3).random()
                this.tiles[randX1][randY1].swapPosWithTile(this.tiles[randX2][randY2])
            }
            this.game.updateBoard(this.getBoard())
        } while (!this.game.isSolvable())
    }

    // Get the actual state of the board
    private fun getBoard(): Array<IntArray> {
        val board = Array(this.tiles.size) { IntArray(this.tiles[0].size) {0} }
        for (row in 0 until this.tiles.size) {
            for (col in 0 until this.tiles[row].size) {
                val tile = tiles[row][col]
                board[tile.getX()][tile.getY()] = tile.getNumber()
            }
        }
        return board
    }

    // === TILES ===================================================================================

    // This function is slice the original image to 9 part and fill the tiles with them
    private fun fillTiles() {
        // The original image which we will be cut to 9 part
        val originalBitmap = this.boardBackgroundImage.drawable.toBitmap()
        val lParams = this.board.layoutParams
        // Row and colum number
        val rNum = this.tiles.size
        val cNum = this.tiles[0].size
        // Set the pieces width and height to cut the original image correctly
        val pieceW = originalBitmap.width / cNum
        val pieceH = originalBitmap.height / rNum
        // Set the images for each tile
        for (row in 0 until rNum) {
            for (col in 0 until cNum) {
                // The starting point of the actual image part
                val startX = col*pieceW
                val startY = row*pieceH
                // Set the images for the actual tile
                val tile = this.tiles[row][col]
                tile.getImage().setImageBitmap(
                    Bitmap.createBitmap(originalBitmap, startX, startY, pieceW, pieceH)
                )
                // Set the images proper width and height for the tile
                tile.getImage().layoutParams.width = lParams.width/cNum
                tile.getImage().layoutParams.height = lParams.height/rNum
                // If the tile is the chosen one then set is invisible
                if (tile.getNumber() == this.game.getBlank()) {
                    tile.getImage().alpha = 0.0f
                }
            }
        }
        // Set the original image alpha to 10%, this will be the background of the board
        this.boardBackgroundImage.alpha = 0.1f
    }

    // How can the tiles move
    private fun tileMovement(tile: Tile) {
        // Avoid rapid tap
        if (SystemClock.elapsedRealtime() - this.lastClickTime < 300) return
        else this.lastClickTime = SystemClock.elapsedRealtime()
        // If tilt control is on then wait a little longer
        if (this.tiltControlOn) this.lastClickTime += 200
        // Find the blank tile
        val blankPos = this.game.findBlank()
        // Chose the correct direction based on where is the blank one
        val move = when {
            tile.getX() > blankPos.first && tile.getY() == blankPos.second -> Move.UP
            tile.getX() < blankPos.first && tile.getY() == blankPos.second -> Move.DOWN
            tile.getY() > blankPos.second && tile.getX() == blankPos.first -> Move.LEFT
            else -> Move.RIGHT
        }
        // Swap position with the blank tile
        val tmp = Pair(tile.getX(), tile.getY())
        tile.updatePos(move, blankPos.first, blankPos.second)
        findBlankTile().updatePos(inverseMove(move), tmp.first, tmp.second)
        // Update the board and check it is ready or not
        if (this.game.updateBoard(this.getBoard())) winner()
    }

    // Find the blank tile
    private fun findBlankTile(): Tile {
        for (row in 0 until this.tiles.size) {
            for (col in 0 until this.tiles[row].size) {
                if (this.tiles[row][col].getNumber() == this.game.getBlank()) {
                    return this.tiles[row][col]
                }
            }
        }
        throw IllegalArgumentException("The board doesn't have a blank tile")
    }

    // Get the opposite directions
    private fun inverseMove(move: Move): Move {
        return when (move) {
            Move.UP -> Move.DOWN
            Move.DOWN -> Move.UP
            Move.LEFT -> Move.RIGHT
            Move.RIGHT -> Move.LEFT
        }
    }

    // === TILT CONTROL ============================================================================

    // Find the tile which can move based on the tilt direction
    private fun tiltTarget(move: Move): ImageView? {
        val blankPos = this.game.findBlank()
        for (row in 0 until this.tiles.size) {
            for (col in 0 until this.tiles[row].size) {
                val tile = tiles[row][col]
                when (move) {
                    Move.UP -> if (
                        tile.getX()-1 == blankPos.first &&
                        tile.getY() == blankPos.second
                    ) return tile.getImage()
                    Move.LEFT -> if (
                        tile.getX() == blankPos.first &&
                        tile.getY()-1 == blankPos.second
                    ) return tile.getImage()
                    Move.RIGHT -> if (
                        tile.getX() == blankPos.first &&
                        tile.getY()+1 == blankPos.second
                    ) return tile.getImage()
                    Move.DOWN -> if (
                        tile.getX()+1 == blankPos.first &&
                        tile.getY() == blankPos.second
                    ) return tile.getImage()
                }
            }
        }
        return null
    }

    // Setup the sensor for tilt control
    private fun setUpSensor() {
        this.sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        this.sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            this.sensorManager!!.registerListener(this, it,
                SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    // What should happen if the sensor detect changes
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { return }
    override fun onSensorChanged(event: SensorEvent?) {
        if (this.tiltControlOn && event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            // Rotate the board based on tilt
            val leftRight = event.values[0]
            val upDown = event.values[1]
            this.board.apply {
                if (gameState == GameState.PLAY){
                    this.rotationX = upDown
                    this.rotationY = leftRight
                } else {
                    this.rotationX = 0f
                    this.rotationY = 0f
                }

            }
            // If the tilt bigger then 3 in any direction the perform move with the proper tile
            if (upDown.toInt() < -3) this.tiltTarget(Move.UP)?.performClick()
            if (upDown.toInt() > 3) this.tiltTarget(Move.DOWN)?.performClick()
            if (leftRight.toInt() > 3) this.tiltTarget(Move.LEFT)?.performClick()
            if (leftRight.toInt() < -3) this.tiltTarget(Move.RIGHT)?.performClick()
        }
    }

    // === MUSIC ===================================================================================

    private fun setupMediaPlayer(){
        this.mediaPlayer = MediaPlayer.create(this, R.raw.music)
        this.mediaPlayer?.isLooping = true
        this.mediaPlayer?.start()
    }
}

