package game.model

object GameSettings {
  val canvasWidth: Double = 600
  val canvasHeight: Double = 800

  var paddleWidth: Double = 100
  var paddleHeight: Double = 20
  var paddleX: Double = (canvasWidth - paddleWidth) / 2
  var paddleY: Double = canvasHeight - 50

  val ballRadius: Double = 12
  var ballX: Double = canvasWidth / 2
  var ballY: Double = canvasHeight / 2
  var ballVX: Double = 0.0
  var ballVY: Double = 0.0
  var ballVelocity = 5.0

  val brickWidth: Double = 70
  val brickHeight: Double = 20
}
