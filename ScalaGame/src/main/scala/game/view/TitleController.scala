package game.view

import game.Main
import game.Main.stage
import game.model.GameSettings.{canvasHeight, canvasWidth}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafxml.core.macros.sfxml
import javafx.{scene => jfxs}

@sfxml
class TitleController {

  def handleStartGame(action: ActionEvent): Unit = {
    val canvas = new Canvas(canvasWidth, canvasHeight)
    val Play = new Game(canvas)

    val gameScene = new Scene {
      content = canvas
    }

    stage = new PrimaryStage {
      title = "Brick Breaker"
      scene = gameScene
    }

    Play.startGame()
  }

  def handleInstructions(action: ActionEvent): Unit = {
    Main.showInstructions()
  }

  def handleSettings(action: ActionEvent): Unit = {
    Main.showSettings()
  }

  def handleExit(action: ActionEvent): Unit = {
    System.exit(0)
  }
}
