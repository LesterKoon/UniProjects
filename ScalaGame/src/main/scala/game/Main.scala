package game

import javafx.scene.image.Image
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object Main extends JFXApp {

  // transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  // initialize the loader object.
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  // Load root layout from fxml file.
  loader.load();
  // retrieve the root component BorderPane from the FXML
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  // apply the stylesheets to the app
  val cssResource = getClass.getResource("view/theme.css")
  roots.stylesheets = List(cssResource.toExternalForm)


  stage = new PrimaryStage {
    title = "Brick Breaker"
    icons += new Image(getClass.getResource("images/icon.png").toURI.toString)
    scene = new Scene {
      root = roots
    }
    resizable = false
  }

  def showTitle()= {
    val resource = getClass.getResource("view/Title.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]()
    this.roots.setCenter(roots)
  }

  def showInstructions() = {
    val resource = getClass.getResource("view/Instructions.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]()
    this.roots.setCenter(roots)
  }

  def showSettings() = {
    val resource = getClass.getResource("view/Settings.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]()
    this.roots.setCenter(roots)
  }

  showTitle()

}
