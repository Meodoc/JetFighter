package com.example.jetfighter

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.FileInputStream

// Jet image: https://www.pngegg.com/de/png-zxkyn/download

class JetFighter : Application() {
    companion object {
        const val gameWidth = 320.0 * 2.5
        const val gameHeight = 240.0 * 3
        const val winPoints = 20

        val jetBlackPath = JetFighter::class.java.getResource("jet_black.png")?.toExternalForm()
        val jetWhitePath = JetFighter::class.java.getResource("jet_white.png")?.toExternalForm()
    }

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(JetFighter::class.java.getResource("game-view.fxml"))
        val scene = Scene(fxmlLoader.load(), gameWidth, gameHeight)

        scene.root.requestFocus()
        stage.title = "JetFighter!"
        stage.isResizable = false
        stage.icons.add(Image(jetBlackPath))
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(JetFighter::class.java)
}