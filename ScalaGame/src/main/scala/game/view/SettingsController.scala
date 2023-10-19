package game.view

import game.Main
import game.model.GameSettings
import scalafx.scene.control.Slider
import scalafxml.core.macros.sfxml

@sfxml
class SettingsController (private val paddleSlider: Slider, private val ballSlider: Slider) {

  paddleSlider.value.onChange { (_, _, newValue) =>
    GameSettings.paddleWidth = newValue.doubleValue()
  }

  ballSlider.value.onChange { (_, _, newValue) =>
    GameSettings.ballVelocity = newValue.doubleValue()
  }

  def handleReturn(): Unit = {
    Main.showTitle()
  }

}
