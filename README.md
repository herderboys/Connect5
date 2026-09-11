# Connect-5 engine

Java-based console game where a human player competes against an AI opponent to connect five pieces in a row. The engine is built with a focus on performance, utilizing bitwise operations for board state management and the Minimax algorithm with Alpha-Beta pruning for AI decision-making.

## Features

*   **Minimax AI with Alpha-Beta Pruning:** The AI calculates optimal moves by evaluating future game states up to a specified depth while pruning branch evaluations to save computational resources.
*   **Bit-Level Board Representation:** The grid state is stored efficiently within an array of 64-bit `long` integers. Each square uses exactly two bits (00 for empty, 01 for player 1, 10 for player 2) to optimize memory and performance via bit masking.
*   **Heuristic Board Evaluation:** The engine scores the board dynamically by checking piece alignments across horizontal, vertical, and both diagonal directions, assigning escalating point values for lengths of 2, 3, and 4 pieces.
*   **Interactive Console Gameplay:** Features a continuous game loop that prints the board state, handles turn switching, and accepts human input via standard console coordinates.

---

## Architecture & Class Overview

The project is structured into three distinct classes:

*   **`Game.java`:** The main entry point and controller. It manages the game loop, processes user input through a `Scanner`, and handles the visual rendering of the board to the console.
*   **`Brain.java`:** The AI engine and rules arbiter. It implements the Minimax algorithm, evaluates board scores based on current piece placements, and checks the grid for a 5-in-a-row win condition.
*   **`Board.java`:** The core data structure representing the physical game board. It utilizes bit shifting and masking to set, retrieve, and track legal moves across the grid.

---

## Usage

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.

### Running the Application
Compile the Java files and run the `main` method located in the `Game` class. By default, the application will initialize an 8x8 board size. 

```java
public static void main(String[] args) {
    Game game = new Game(8);
    game.start();
}
