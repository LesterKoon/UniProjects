package game.model

class Ball(var x: Double, var y: Double, var vX: Double, var vY: Double, val radius: Double) {

  def move(deltaTime: Double): Unit = {
    x += vX * deltaTime
    y += vY * deltaTime
  }

  def reverseXDirection(): Unit = {
    vX = -vX
  }

  def reverseYDirection(): Unit = {
    vY = -vY
  }
}