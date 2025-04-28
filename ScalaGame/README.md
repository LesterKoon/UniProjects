# Scala Brick-Breaker / Breakout Game
This is a simple implementation of the classic brick-breaker game for an Object-Oriented Programming assignment built using Scala, ScalaFX and Scene Builder.

## 1. Preview of the Game

> Complete Game Preview

<iframe width="560" height="315" src="https://www.youtube.com/embed/Db2dNVfpId4?si=ee-OicQ32YE0Z-oE" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

> Preview of the Game Interface

![main](https://github.com/LesterKoon/UniProjects/assets/77951315/ba743fb5-4338-4116-b14c-d5e1b11e7784)
![game1](https://github.com/LesterKoon/UniProjects/assets/77951315/4d17f1a9-0b11-44a0-b8db-4347b6023301)
![game2](https://github.com/LesterKoon/UniProjects/assets/77951315/a9ed7452-8e86-42a3-95ef-0361dff9f93c)

## 2. UML Diagram

> Flowchart
This shows the general flow of the program from start to end. It shows that u are able to pick to either Start Game, Instructions, Settings or Exit. You can return to the Title Screen from the Instructions and Settings page, and end the program through exit. Selecting Start Game will initiate the game until reaching game over and finally exiting the program.

![Flowchart](https://github.com/LesterKoon/UniProjects/assets/77951315/05a2f8c8-3691-40b3-a9d3-d2605f68cf04)

## 3. Features

- The flow of the project is also linear, which means that users are able to fully explore the whole program every time without getting lost and forced to exit the program.
- The game has customizable aspects. The game allows you to change both the “paddle size” and “ball speed” in the settings page, freely. This adds an extra dimension and an additional degree of complexity to what is generally a simple game.
- Each object of the game is also in their separate classes with their own methods. This allows the project to be easily built-on and modified by other developer with minimal effort as all classes and methods used in the project are well-defined. This also adds a degree of isolation to the program, which make testing and debugging classes and methods easier as the error can be easily identified without disturbing other classes.

## 4. Future Imporovements

- The “Brick” class and its related case classes has a method called “collisionEffect” which allows effects such as sound and animation to be added to the game when the ball collides with a solid brick.
-	The game main concept is utilizing physics for the ball. The current program uses an animation timer to update the game’s object and redraw them every frame. This makes the physics of the game, primarily the ball not consistent and unrealistic at times, especially at the boundaries and corners of the objects.
-	The score in the game is currently not a useful feature, as it only displays the current game’s score and resets every time the program is run. Thus, the game is lacking a leaderboard function that will prompt the user for their names and display their score on the leaderboard once the game is ended. However, with the current game function this is difficult to achieve as there are multiple combination of “paddle size” and “ball size”.
