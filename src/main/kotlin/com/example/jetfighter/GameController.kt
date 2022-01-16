package com.example.jetfighter

import com.example.jetfighter.GameController.GameState.*
import javafx.animation.AnimationTimer
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import java.io.FileInputStream
import kotlin.math.cos
import kotlin.math.sin

class Jet(val body: ImageView, initPosX: Double, initPosY: Double, initRot: Double, private val speed: Double, private val label: Label) {
    init {
        body.x = initPosX
        body.y = initPosY
        body.rotate = Math.toDegrees(initRot)
    }

    val centerX
        get() = body.x + body.fitWidth / 2
    val centerY
        get() = body.y + body.fitHeight / 2

    var score = 0
        set(value) {
            label.text = value.toString()
            field = value
        }

    fun update(rot: Double) = with(body) {
        x = wrap(x + speed * cos(rot), JetFighter.gameWidth)
        y = wrap(y + speed * sin(rot), JetFighter.gameHeight)
        rotate = Math.toDegrees(rot)
    }

    fun checkWin(): Boolean = score == JetFighter.winPoints

    private fun wrap(newPos: Double, gameSize: Double): Double {
        val wrapped = newPos % gameSize
        return if (wrapped < -body.fitWidth) wrapped + gameSize else wrapped
    }
}

class Bullet(val body: Circle, private val dir: Double, private val speed: Double, var life: Int) {
    fun update() = with(body) {
        centerX = wrap(centerX + speed * cos(dir), JetFighter.gameWidth)
        centerY = wrap(centerY + speed * sin(dir), JetFighter.gameHeight)
        life--
    }

    fun checkCollision(jet: Jet): Boolean = jet.body.boundsInParent.intersects(body.boundsInParent)

    private fun wrap(newPos: Double, gameSize: Double): Double {
        val wrapped = newPos % gameSize
        return if (wrapped < 0) wrapped + gameSize else wrapped
    }
}

class GameController {
    companion object {
        private const val jet1ImgPath = "jet_black.png"
        private const val jet2ImgPath = "jet_white.png"
        private const val jetSpeed = 2.5
        private const val rotSpeed = 0.07
        private const val bulletSpeed = 5.0
        private const val bulletLifespan = 100
        private const val jet1InitPosX = JetFighter.gameWidth / 4
        private const val jet1InitPosY = JetFighter.gameHeight / 2
        private const val jet2InitPosX = JetFighter.gameWidth * 3 / 4
        private const val jet2InitPosY = JetFighter.gameHeight / 2
        private const val jet1InitRot = -90.0
        private const val jet2InitRot = -90.0
        private const val jet1ScorePosX = JetFighter.gameWidth / 4
        private const val jet1ScorePosY = 50.0
        private const val jet2ScorePosX = JetFighter.gameWidth * 3 / 4
        private const val jet2ScorePosY = 50.0
        private const val statusMsgPosX = JetFighter.gameWidth / 2
        private const val statusMsgPosY = JetFighter.gameHeight / 2
    }

    // Handles game state changes
    enum class GameState(val msg: String) {
        RUNNING("Running") {
            override fun apply(): GameState {
                timer.start()
                statusMsg.isVisible = false
                return this
            }
        },
        PAUSED("Paused") {
            override fun apply(): GameState {
                timer.stop()
                with(statusMsg) {
                    updateText(msg, statusMsgPosX, statusMsgPosY)
                    isVisible = true
                }
                return this
            }
        },
        JET_1_WON("Black won!") {
            override fun apply(): GameState {
                timer.stop()
                with(statusMsg) {
                    textFill = Color.BLACK
                    updateText(msg, statusMsgPosX, statusMsgPosY)
                    isVisible = true
                }
                return this
            }
        },
        JET_2_WON("White won!") {
            override fun apply(): GameState {
                timer.stop()
                with(statusMsg) {
                    textFill = Color.WHITE
                    updateText(msg, statusMsgPosX, statusMsgPosY)
                    isVisible = true
                }
                return this
            }
        };

        companion object {
            lateinit var statusMsg: Label
            lateinit var timer: AnimationTimer

            fun init(statusMsg: Label, timer: AnimationTimer) {
                this.statusMsg = statusMsg
                this.timer = timer
            }
        }

        abstract fun apply(): GameState
    }

    var rot1 = Math.toRadians(jet1InitRot)
    var rot2 = Math.toRadians(jet2InitRot)

    var bullets1 = mutableSetOf<Bullet>()
    var bullets2 = mutableSetOf<Bullet>()

    private var rotLeft1 = false
    private var rotRight1 = false
    private var rotLeft2 = false
    private var rotRight2 = false
    private var gameState = PAUSED

    @FXML
    private lateinit var gamePane: Pane

    @FXML
    private lateinit var jet1Score: Label

    @FXML
    private lateinit var jet2Score: Label

    @FXML
    private lateinit var statusMsg: Label
    private lateinit var jet1: Jet
    private lateinit var jet2: Jet

    // Game loop
    private val timer = object : AnimationTimer() {
        override fun handle(now: Long) {
            updateRotations()

            jet1.update(rot1)
            jet2.update(rot2)

            bullets1.toList().forEach {
                it.update()
                if (it.checkCollision(jet2)) {
                    jet1.score++
                    if (jet1.checkWin())
                        gameState = JET_1_WON.apply()
                    gamePane.destroyBullet(it)
                    bullets1.remove(it)
                }
                if (it.life == 0) {
                    gamePane.destroyBullet(it)
                    bullets1.remove(it)
                }
            }

            bullets2.toList().forEach {
                it.update()
                if (it.checkCollision(jet1)) {
                    jet2.score++
                    if (jet2.checkWin())
                        gameState = JET_2_WON.apply()
                    gamePane.destroyBullet(it)
                    bullets2.remove(it)
                }
                if (it.life == 0) {
                    gamePane.destroyBullet(it)
                    bullets2.remove(it)
                }
            }
        }
    }

    private fun updateRotations() {
        if (rotLeft1)
            rot1 -= rotSpeed
        if (rotRight1)
            rot1 += rotSpeed
        if (rotLeft2)
            rot2 -= rotSpeed
        if (rotRight2)
            rot2 += rotSpeed
    }

    @FXML
    fun onKeyPressed(e: KeyEvent) {
        when (e.code) {
            KeyCode.A -> rotLeft1 = true
            KeyCode.D -> rotRight1 = true
            KeyCode.LEFT -> rotLeft2 = true
            KeyCode.RIGHT -> rotRight2 = true
            KeyCode.SPACE -> {
                val bullet = gamePane.spawnBullet(jet1.centerX, jet1.centerY, rot1, color = Color.BLACK, speed = bulletSpeed, life = bulletLifespan)
                bullets1.add(bullet)
            }
            KeyCode.ENTER -> {
                val bullet = gamePane.spawnBullet(jet2.centerX, jet2.centerY, rot2, color = Color.WHITE, speed = bulletSpeed, life = bulletLifespan)
                bullets2.add(bullet)
            }
            KeyCode.ESCAPE -> {
                when (gameState) {
                    RUNNING -> gameState = PAUSED.apply()
                    PAUSED -> gameState = RUNNING.apply()
                }
            }
            KeyCode.R -> {
                when (gameState) {
                    JET_1_WON, JET_2_WON -> {
                        gamePane.reset(jet1, jet2, bullets1, bullets2)
                        rot1 = Math.toRadians(jet1InitRot)
                        rot2 = Math.toRadians(jet2InitRot)
                        initialize()
                    }
                }
            }
        }
    }

    @FXML
    fun onKeyReleased(e: KeyEvent) {
        when (e.code) {
            KeyCode.A -> rotLeft1 = false
            KeyCode.D -> rotRight1 = false
            KeyCode.LEFT -> rotLeft2 = false
            KeyCode.RIGHT -> rotRight2 = false
        }
    }

    @FXML
    fun initialize() {
        GameState.init(statusMsg, timer)

        gamePane.initialize(JetFighter.gameWidth, JetFighter.gameHeight)
        jet1Score.initialize(jet1ScorePosX, jet1ScorePosY, fontColor = Color.BLACK)
        jet2Score.initialize(jet2ScorePosX, jet2ScorePosY, fontColor = Color.WHITE)

        jet1 = gamePane.spawnJet(jet1InitPosX, jet1InitPosY, rot = rot1, speed = jetSpeed, label = jet1Score, image = Image(FileInputStream(jet1ImgPath)))
        jet2 = gamePane.spawnJet(jet2InitPosX, jet2InitPosY, rot = rot2, speed = jetSpeed, label = jet2Score, image = Image(FileInputStream(jet2ImgPath)))

        statusMsg.initialize(statusMsgPosX, statusMsgPosY, fontColor = Color.WHITE, visible = false)

        gameState = RUNNING.apply()
    }
}

// Extension methods
fun Pane.initialize(width: Double, height: Double) {
    minWidth = width
    minHeight = height
}

fun Pane.spawnJet(x: Double, y: Double, width: Double = 50.0, height: Double = 50.0, rot: Double, speed: Double, label: Label, image: Image): Jet {
    val jet = Jet(ImageView(image), x - width / 2, y - height / 2, rot, speed, label)
    with(jet.body) {
        fitHeight = height
        fitWidth = width
    }
    this.children.add(jet.body)
    return jet
}

fun Pane.spawnBullet(x: Double, y: Double, dir: Double, size: Double = 2.0, color: Color, speed: Double, life: Int): Bullet {
    val offsetX = cos(dir) * 25
    val offsetY = sin(dir) * 25
    val bullet = Bullet(Circle(x + offsetX, y + offsetY, size, color), dir, speed, life)
    this.children.add(bullet.body)
    bullet.body.toBack()
    return bullet
}

fun Pane.destroyBullet(bullet: Bullet) {
    this.children.remove(bullet.body)
}

fun Pane.reset(jet1: Jet, jet2: Jet, bullets1: MutableSet<Bullet>, bullets2: MutableSet<Bullet>) {
    children.remove(jet1.body)
    children.remove(jet2.body)
    bullets1.forEach { children.remove(it.body) }.also { bullets1.clear() }
    bullets2.forEach { children.remove(it.body) }.also { bullets2.clear() }
}

fun Label.initialize(x: Double, y: Double, text: String = "0", fontSize: Double = 70.0, fontColor: Color, visible: Boolean = true) {
    font = Font.font(fontSize)
    textFill = fontColor
    updateText(text, x, y)
    textAlignment = TextAlignment.CENTER
    isVisible = visible
    toFront()
}

fun Label.updateText(text: String, x: Double, y: Double) {
    this.text = text
    val t = Text(text).apply { font = Font.font(this@updateText.font.size) } // create Text object to get font dimensions
    layoutX = x - t.layoutBounds.width / 2
    layoutY = y - t.layoutBounds.height / 2
}
