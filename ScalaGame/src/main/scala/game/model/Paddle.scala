package game.model

import scalafx.scene.input.{KeyCode, KeyEvent}

class Paddle(canvasWidth: Double, canvasHeight: Double, val paddleWidth: Double, val paddleHeight: Double) {
  var paddleX: Double = (canvasWidth - paddleWidth) / 2
  var paddleY: Double = canvasHeight - 50 // Adjust the paddle's Y position as needed

  val step = 30

  def handleKeyPressed(e: KeyEvent): Unit = {
    // Move the paddle with arrow keys
    e.code match {
      case KeyCode.Left => paddleX = Math.max(paddleX - step, 0)
      case KeyCode.Right => paddleX = Math.min(paddleX + step, canvasWidth - paddleWidth)
      case _ =>
    }
  }
}
