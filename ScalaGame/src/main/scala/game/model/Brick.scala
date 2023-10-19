package game.model

import game.model.GameSettings.{brickHeight, brickWidth}

class Brick(val x: Double, val y: Double, val brickWidth: Double, val brickHeight: Double) {
  def isCollision(ballX: Double, ballY: Double, ballRadius: Double): Boolean = {
    // Implement collision detection logic for the brick
    val brickBoundingBox = BrickBoundingBox(x, y, brickWidth, brickHeight)
    brickBoundingBox.isCollision(ballX, ballY, ballRadius)
  }

  def collisionEffect(ball: Ball): Unit = {
    // Implement collision effect logic for the brick
  }
}

case class BreakableBrick(override val x: Double, override val y: Double) extends Brick(x, y, brickWidth, brickHeight) {
  override def isCollision(ballX: Double, ballY: Double, ballRadius: Double): Boolean = {
    ballX + ballRadius > x && ballX - ballRadius < x + brickWidth &&
      ballY + ballRadius > y && ballY - ballRadius < y + brickHeight
  }

  override def collisionEffect(ball: Ball): Unit = {
    // Perform any desired effects when the ball collides with a breakable brick
  }
}

case class SolidBrick(override val x: Double, override val y: Double) extends Brick(x, y, brickWidth, brickHeight) {
  override def isCollision(ballX: Double, ballY: Double, ballRadius: Double): Boolean = {
    ballX + ballRadius > x && ballX - ballRadius < x + brickWidth &&
      ballY + ballRadius > y && ballY - ballRadius < y + brickHeight
  }

  override def collisionEffect(ball: Ball): Unit = {
    // Perform any desired effects when the ball collides with a solid brick
  }
}

case class BrickBoundingBox(left: Double, top: Double, width: Double, height: Double) {
  def right: Double = left + width

  def bottom: Double = top + height

  def isCollision(ballX: Double, ballY: Double, ballRadius: Double): Boolean = {
    // Check if the ball is within the rectangular bounding box
    if (ballX + ballRadius < left || ballX - ballRadius > right ||
      ballY + ballRadius < top || ballY - ballRadius > bottom) {
      return false
    }

    // Calculate the precise collision with the circle
    val brickCenterX = left + width / 2
    val brickCenterY = top + height / 2
    val dx = Math.abs(ballX - brickCenterX)
    val dy = Math.abs(ballY - brickCenterY)

    if (dx > width / 2 + ballRadius || dy > height / 2 + ballRadius) {
      return false
    }

    if (dx <= width / 2 || dy <= height / 2) {
      return true
    }

    val cornerDistSquared = (dx - width / 2) * (dx - width / 2) + (dy - height / 2) * (dy - height / 2)
    return cornerDistSquared <= ballRadius * ballRadius
  }
}