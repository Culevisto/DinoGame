🦖 Dino Runner — JavaFX Game

A modern remake of the Google Chrome "No Internet" dinosaur game, created fully in JavaFX using object-oriented programming, event handling, and animation algorithms.

🎮 Overview

This is a 2D endless runner game where the player controls a dinosaur that automatically runs and must jump over cactuses while clouds move dynamically in the background.

Built entirely using JavaFX animation, custom graphics, and procedural generation.

🧠 Data Structures Used
Data Structure	Purpose
ArrayList<Group>	Stores and manages all cactus obstacles.
ArrayList<Group>	Manages all procedural clouds in the background.
Primitive variables (double, boolean, etc.)	Used for motion, physics, and timing.
Label objects	Display UI elements such as score and messages.
⚙️ Algorithms Implemented

Game Loop (AnimationTimer) — core algorithm that updates positions every frame.

Gravity and Jump Simulation — uses velocity and acceleration formulas:

velocityY += GRAVITY;
dinoY += velocityY;


Collision Detection — checks intersection between dinosaur and cactuses using:

if (dino.getBoundsInParent().intersects(obstacle.getBoundsInParent())) gameOver();


Procedural Cloud Generation — randomizes cloud position, size, and opacity for realism.

Dynamic Difficulty Scaling — increases game speed and spawn rate as score grows.

Scoring System — increments score smoothly over time using delta-time.

🧩 Input & Output

Input:

Keyboard: SPACE → jump, R → restart

Mouse click → jump or restart

Output:

On-screen score counter

“Game Over” message

Optional: game can be extended to log scores into a .txt file (future improvement)

🚀 How to Run

Clone the repo:

git clone https://github.com/yourusername/DinoGame.git


Open in IntelliJ IDEA or any Java IDE.

Ensure JavaFX 21+ is added as dependency (if using Maven).

Run DinoGame.java.

🧩 What I Learned

Implementing game loops and understanding frame-based updates.

Handling real-time animations and physics in JavaFX.

Using OOP principles (encapsulation, modular design).

Creating procedural visual effects (clouds) using only shapes.

Integrating Git & GitHub for project version control.

🛠️ Improvements & Future Ideas

Add sound effects for jumps and collisions.

Implement a score saving system with file I/O (scores.txt).

Add day/night cycle (background color changes).

Mobile-friendly version with touch support.

🎥 Video Explanation

▶️ Watch here: [YouTube Link or local video file]

In the video (10–15 minutes):

Explain your data structures (ArrayList, Label, etc.)

Explain algorithms (AnimationTimer, gravity, collision)

Show live gameplay

Describe what you learned

Mention possible improvements
