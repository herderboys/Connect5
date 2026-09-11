# Connect-5 engine

Java-based console game where a human player competes against either an AI opponent or another human to connect five pieces in a row. The engine is built with a focus on performance, utilizing bitwise operations for board state management and the Minimax algorithm with Alpha-Beta pruning for AI decision-making. Multiplayer games are played over a TCP socket connection between a shared server and two clients.

## Features

*   **Minimax AI with Alpha-Beta Pruning:** The AI calculates optimal moves by evaluating future game states up to a specified depth while pruning branch evaluations to save computational resources.
*   **Bit-Level Board Representation:** The grid state is stored efficiently within an array of 64-bit `long` integers. Each square uses exactly two bits (00 for empty, 01 for player 1, 10 for player 2) to optimize memory and performance via bit masking.
*   **Heuristic Board Evaluation:** The engine scores the board dynamically by checking piece alignments across horizontal, vertical, and both diagonal directions, assigning escalating point values for lengths of 2, 3, and 4 pieces.
*   **Interactive Console Gameplay:** Features a continuous game loop that prints the board state, handles turn switching, and accepts human input via standard console coordinates.
*   **Local Multiplayer over TCP:** Players can host or join a game session over the network. A room-code system (`CREATE`/`JOIN`) pairs two clients together on the server, which then relays board state and enforces turn order between them.

---

## Architecture & Class Overview

The project is structured into three packages:

*   **`client/Game.java`:** The main entry point and controller. It manages the game loop, processes user input through a `Scanner`, renders the board to the console, and — for multiplayer — opens a socket to the server and exchanges board state/turns each round.
*   **`server/Server.java`:** Listens on port 8000 for incoming client connections. Handles `CREATE` requests by generating a room code and starting a new session, and `JOIN` requests by pairing a second client with an existing room.
*   **`server/GameSession.java`:** Runs one multiplayer match on its own thread. Owns the authoritative `Board` for that session, broadcasts board state and turn order to both connected sockets, and applies each player's move as it comes in.
*   **`shared/Brain.java`:** The AI engine and rules arbiter, used in single-player games. It implements the Minimax algorithm, evaluates board scores based on current piece placements, and checks the grid for a 5-in-a-row win condition.
*   **`shared/Board.java`:** The core data structure representing the physical game board, shared by both the client (single-player) and server (multiplayer). It utilizes bit shifting and masking to set, retrieve, and track legal moves across the grid.

---

## Usage

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.

### Running the Application

Compile all sources under `src/`:

```bash
javac -d out $(find src -name "*.java")
```

**Single-player (vs. AI):** run the client and answer `N` when asked to play with another player.

```bash
java -cp out client.Game
```

**Multiplayer:** first start the server, then run the client on each machine/terminal, answering `Y` and using `CREATE` on one client and `JOIN <room code>` on the other.

```bash
java -cp out server.Server
java -cp out client.Game   # player 1: CREATE
java -cp out client.Game   # player 2: JOIN <room code>
```

By default, the application initializes an 8x8 board.

```java
public static void main(String[] args) {
    Game game = new Game(8);
    game.start();
}
