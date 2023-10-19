package game.view

import game.Main
import scalafxml.core.macros.sfxml

@sfxml
class InstructionsController {

  def handleReturn(): Unit = {
    Main.showTitle()
  }

}
