package game.view

import game.Main.stage
import game.model.GameSettings._
import game.model._
import scalafx.Includes._
import scalafx.animation.AnimationTimer
import scalafx.application.JFXApp
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, Text}

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Game(val canvas: Canvas){

  val paddle: Paddle = new Paddle(canvasWidth, canvasHeight, paddleWidth, paddleHeight)

  var score: Int = 0

  var bricks: ListBuffer[Brick] = ListBuffer()

  def generateBricks(): Unit = {
    val rows = 6 // Adjust the number of rows
    val bricksPerRow = 7 // Adjust the number of bricks per row
    val totalWidth = bricksPerRow * brickWidth
    val totalSpacingX = (canvasWidth - totalWidth) / (bricksPerRow + 1) // Calculate the total spacing between bricks and canvas boundaries
    val spacingX = totalSpacingX // Use the same spacing for horizontal alignment

    val topMargin = 45

    val totalHeight = rows * brickHeight
    val totalSpacingY = (canvasHeight - totalHeight) / (rows + 1) // Calculate the total spacing between bricks vertically
    val spacingY = totalSpacingY / 6 // Use the same spacing for vertical alignment

    for {
      row <- 0 until rows
      col <- 0 until bricksPerRow
    } {
      val x = spacingX + col * (brickWidth + spacingX) // Add spacing between bricks horizontally
      val y = topMargin + spacingY + row * (brickHeight + spacingY) // Add spacing between bricks vertically
      if (Math.random() < 0.05) {
        bricks += SolidBrick(x, y)
      } else {
        bricks += BreakableBrick(x, y)
      }
    }
  }

  def checkCollision(ballX: Double, ballY: Double, ballVX: Double, ballVY: Double): (Double, Double, Double, Double) = {
    var newBallX = ballX
    var newBallY = ballY
    var newBallVX = ballVX
    var newBallVY = ballVY

    // Collision with window boundaries
    if (ballX - ballRadius < 0 || ballX + ballRadius > canvasWidth) {
      newBallVX = -ballVX
    }

    if (ballY - ballRadius < 0) {
      newBallVY = -ballVY
    }

    if (ballY + ballRadius > canvasHeight) {
      // Ball goes below the screen, reset the ball
      newBallX = canvasWidth / 2
      newBallY = canvasHeight / 2
      newBallVX = 0.0
      newBallVY = 0.0

      score-=5
    }

    // Collision with the paddle
    if (ballY + ballRadius >= paddle.paddleY && ballX >= paddle.paddleX && ballX <= paddle.paddleX + paddle.paddleWidth) {
      val paddleCenterX = paddle.paddleX + paddle.paddleWidth / 2

      // Calculate the normalized distance from the ball's center to the center of the paddle
      val distanceFromCenterX = (ballX - paddleCenterX) / (paddle.paddleWidth / 2)

      // Adjust the ball's X velocity based on the collision point on the paddle
      newBallVX = ballVX + 1 * distanceFromCenterX

      // Reverse ball direction based on the collision with the top of the paddle
      newBallVY = -ballVY
      score +=5
    }

    // Collision with bricks
    val ballNextX = ballX + ballVX
    val ballNextY = ballY + ballVY

    for (brick <- bricks) {
      val brickBoundingBox = BrickBoundingBox(brick.x, brick.y, brickWidth, brickHeight)

      if (brickBoundingBox.isCollision(ballNextX, ballNextY, ballRadius)) {
        // Calculate the collision normal based on the relative position of the ball to the brick
        val brickCenterX = brick.x + brickWidth / 2
        val brickCenterY = brick.y + brickHeight / 2
        val dx = ballNextX - brickCenterX
        val dy = ballNextY - brickCenterY

        val normalX = if (Math.abs(dx) > brickWidth / 2) Math.signum(dx) else 0
        val normalY = if (Math.abs(dy) > brickHeight / 2) Math.signum(dy) else 0

        // Reverse ball direction based on the collision normal
        if (normalX != 0) newBallVX = -ballVX
        if (normalY != 0) newBallVY = -ballVY

        // Move the ball just outside the brick to avoid further collision in the same frame
        newBallX = ballNextX + normalX * ballRadius
        newBallY = ballNextY + normalY * ballRadius

        // Perform any additional collision effects specific to the brick type
        brick.collisionEffect(new Ball(newBallX, newBallY, newBallVX, newBallVY, ballRadius)) // Use 'new Ball' to create a Ball object

        // Remove the brick if it is breakable
        bricks = bricks.filterNot(_ == brick)
        score +=15

        return (newBallX, newBallY, newBallVX, newBallVY)
      }
    }
    (newBallX + newBallVX, newBallY + newBallVY, newBallVX, newBallVY)
  }

  def checkGameOver(): Boolean = {
    bricks.isEmpty
  }

  def draw(gc: GraphicsContext): Unit = {
    // Clear canvas
    gc.fill = Color.LightGrey
    gc.fillRect(0, 0, canvasWidth, canvasHeight)

    // Draw bricks
    gc.fill = Color.DeepSkyBlue
    for (brick <- bricks) {
      gc.fillRect(brick.x, brick.y, brickWidth, brickHeight)
    }

    // Draw paddle
    gc.fill = Color.DarkCyan
    gc.fillRect(paddle.paddleX, paddle.paddleY, paddleWidth, paddleHeight)

    // Draw ball
    gc.fill = Color.Red
    gc.fillOval(ballX - ballRadius, ballY - ballRadius, ballRadius * 2, ballRadius * 2)

    // Draw score at the top center
    gc.fill = Color.Black
    val fontSize = 24
    gc.font = Font.font(fontSize)
    val scoreText = s"Score: $score"

    val text = new Text(scoreText)
    text.setFont(gc.font)
    val scoreWidth = text.getLayoutBounds.getWidth

    gc.fillText(scoreText, (canvasWidth - scoreWidth) / 2, 30)

    // Check and draw "Game Over" message
    if (checkGameOver()) {
      gc.fill = Color.Black
      val fontSize = 50
      gc.font = Font.font(fontSize)
      gc.fillText("Game Over!!!", canvasWidth / 2 - 150, canvasHeight / 2)
    }
  }

  def gameLoop(now: Long, lastTime: Long): Unit = {
    if (lastTime != 0) {
      val deltaTime: Double = (now - lastTime) / 100.0

      if (!checkGameOver()) { // Only update the game if it's not over
        val (newBallX, newBallY, newBallVX, newBallVY) = checkCollision(ballX, ballY, ballVX, ballVY)

        ballX = newBallX
        ballY = newBallY
        ballVX = newBallVX
        ballVY = newBallVY

        ballX += ballVX * deltaTime
        ballY += ballVY * deltaTime

        if (ballY - ballRadius > canvasHeight) {
          ballX = canvasWidth / 2
          ballY = canvasHeight / 2
          ballVX = 0.0
          ballVY = 0.0
        }
      }
    }

    draw(gc)
  }

  var lastTime: Long = 0
  val animationTimer = AnimationTimer { time =>
    val now: Long = System.currentTimeMillis() // Capture the current time
    gameLoop(now, lastTime) // Call your gameLoop method here
    lastTime = now
  }

  val gc = canvas.graphicsContext2D

  canvas.setFocusTraversable(true) // Allow the canvas to receive focus and key events

  canvas.onKeyPressed = (e: KeyEvent) => {
    // Launch the ball
    val launchAnglesDegrees: List[Double] = List(45, 50, 130, 135)

    // Inside the KeyEvent handler for launching the ball:
    if (e.code == KeyCode.Space && ballVX == 0 && ballVY == 0) {
      val randomIndex = Random.nextInt(launchAnglesDegrees.length) // Randomly select an index from the list
      val launchAngleDegrees = launchAnglesDegrees(randomIndex) // Get the randomly selected launch angle in degrees

      val launchAngle = Math.toRadians(launchAngleDegrees) // Convert the launch angle from degrees to radians
      ballVX = ballVelocity * Math.cos(launchAngle) // Calculate X velocity based on angle and constant ballVelocity
      ballVY = -ballVelocity * Math.sin(launchAngle) // Calculate Y velocity based on angle and constant ballVelocity (negative Y direction for upwards)
    }

    // Call the paddle's handleKeyPressed method for left and right arrow keys
    else if (e.code == KeyCode.Left || e.code == KeyCode.Right) {
      paddle.handleKeyPressed(e)
    }
  }

  def startGame(): Unit = {
    bricks.clear() // Clear the list before generating new bricks
    generateBricks()

    val sceneProperty = new scalafx.beans.property.ObjectProperty[scalafx.scene.Scene]
    sceneProperty.value = new scalafx.scene.Scene {
      content = canvas
    }

    stage = new JFXApp.PrimaryStage {
      title = "Brick Breaker"
      scene = sceneProperty.value // Use the value property to get the actual Scene object
      resizable = false
    }

    animationTimer.start()

  }

}


