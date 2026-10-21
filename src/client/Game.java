package client;

import shared.Board;
import shared.Brain;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// class for clients to initiate a game
public class Game {
    private final Board board;
    private final Brain brain;
    private final Scanner scanner;
    private final int boardSize;
    private int playerNumber = 0;

    // initializing necessary game components
    public Game(int boardSize) {
        this.board = new Board(boardSize, 2);
        this.brain = new Brain(board);
        this.scanner = new Scanner(System.in);
        this.boardSize = board.getSize();
    }

    // starting a game
    public void start() throws IOException {
        System.out.println("Connect-5 engine initialized.");
        System.out.println("Do you wish to play with another player? (Y/N)");

        String answer = scanner.nextLine().trim();

        if (answer.equalsIgnoreCase("Y")) {
            // player wants to play against other human
            startMultiplayerGame();

        } else if (answer.equalsIgnoreCase("N")) {
            // player wants to play against AI
            startSingleplayerGame();
        }
    }

    // player chooses multiplayer, this loop gets run
    public void startMultiplayerGame() throws IOException {

        int port = 8000;
        Socket socket = new Socket("localhost", port);

        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        System.out.println("Type CREATE if you wish to create a new room.\nType JOIN if you wish to join another player's room.");


        while (true) {

            String answer = scanner.nextLine().trim();

            boolean validConnection = answer.equalsIgnoreCase("CREATE") || answer.equalsIgnoreCase("JOIN");
            if (!validConnection) {
                System.out.println("Not a valid statement. Try again.");
                continue;
            }
            if (answer.equalsIgnoreCase("CREATE")) {
                playerNumber = 1;

                outputStream.writeUTF("CREATE"); // 1
                String roomCode = inputStream.readUTF(); // 1

                System.out.println("Room created with room code " + roomCode);
                break;

            } else if (answer.equalsIgnoreCase("JOIN")) {
                playerNumber = 2;

                outputStream.writeUTF("JOIN"); // 1
                System.out.println("Please enter room code of game you wish to join.");

                String roomCode = scanner.nextLine();
                outputStream.writeUTF(roomCode); // 2
                break;
            }
        }

        // player 2 joined confirmation (found in Server.java)
        String joinConfirmation = inputStream.readUTF(); // 1 or 2 depending on if join or create

        System.out.println(joinConfirmation);

        // if that's not sent, game cannot be joined
        if (!joinConfirmation.equals("Player 2 joined.")) {
            socket.close();
            return;
        }

        while (true) {

            // initializing board state for this turn
            int boardLength = board.getBoard().length;
            long[] boardState = new long[boardLength];
            for (int i = 0; i < boardLength; i++) {
                boardState[i] = inputStream.readLong();
            }
            board.setBoard(boardState);

            printBoard();

            // check if someone has won
            long winner = board.checkWinner();

            // if winner or draw
            if (winner != 0 || board.hasNoLegalMovesLeft()) {
                System.out.println(inputStream.readUTF()); // prints who is the winner
                break;

            } else {
                int turn = inputStream.readInt();

                // pretty much the same as single player
                if (turn == playerNumber) {

                    // loop to contain turn if moves are illegal
                    while (true) {
                        System.out.println("Your turn. Enter row and column (e.g. '3, 5'): \n");

                        System.out.println("Row: ");
                        int row = scanner.nextInt();
                        System.out.println();

                        System.out.println("Column: ");
                        int col = scanner.nextInt();
                        System.out.println();

                        // local bounds checks, that way gamesession doesn't have to deal with them
                        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
                            System.out.println("Can't place on index out of bounds.");
                            continue;
                        }

                        if (board.getPiece(row, col) != 0) {
                            System.out.println("Index already occupied.");
                            continue;
                        }

                        outputStream.writeInt(row);
                        outputStream.writeInt(col);
                        break;
                    }
                } else {
                    System.out.println("Waiting for other player to make their move...");
                }
            }

        }
        socket.close();
    }

    // player chooses singleplayer, this loop gets run (with minimax)
    public void startSingleplayerGame() {
        System.out.println("Playing against AI.");

        int currentPlayer = 2; // human starts

        while (true) {
            printBoard();
            long winner = board.checkWinner();

            if (board.hasNoLegalMovesLeft()) {
                System.out.println("Draw!");
                break;
            }

            if (winner > 0) {
                if (winner == 1) {
                    System.out.println("You lose!");
                } else {
                    System.out.println("You win!");
                }
                break;
            }
            if (currentPlayer == 2) {
                System.out.println("Your turn. Enter row and column (e.g., '3, 5'): \n");

                System.out.print("Row: ");
                int row = scanner.nextInt();
                System.out.println();

                System.out.print("Column: ");
                int col = scanner.nextInt();
                System.out.println();

                if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
                    System.out.println("Can't place on index out of bounds.");
                    continue;
                }

                if (board.getPiece(row, col) != 0) {
                    System.out.println("Index already occupied.");
                    continue;
                }

                board.setPiece(row, col, currentPlayer);
            } else {
                System.out.println("AI is thinking...\n");
                int bestMoveIndex = brain.getBestMove(6);

                int row = bestMoveIndex / boardSize;
                int col = bestMoveIndex % boardSize;

                board.setPiece(row, col, currentPlayer);
            }
            if (currentPlayer == 1) {
                currentPlayer = 2;
            } else {
                currentPlayer = 1;
            }
        }
    }

    // method for translating board state to a printable terminal output board readable by humans
    private void printBoard() {
        System.out.print("   ");
        for (int col = 0; col < board.getSize(); col++) {
            System.out.printf("%2d ", col);
        }
        System.out.println();

        for (int row = 0; row < board.getSize(); row++) {
            System.out.printf("%2d ", row);

            for (int col = 0; col < board.getSize(); col++) {
                long piece = board.getPiece(row, col);
                if (piece == 0) System.out.print(" . ");
                else if (piece == 1) System.out.print(" X "); // ai / player 1
                else if (piece == 2) System.out.print(" O "); // human / player 2
            }
            System.out.println();
        }
        System.out.println();
        if (playerNumber == 0) {
            System.out.println("AI = X, You = O.\n");
        } else {
            System.out.println("Player 1 = X, Player 2 = O");
        }
    }

    // main method, instantiates a new game with an 8x8 layout
    public static void main(String[] args) throws IOException {
        Game game = new Game(8);
        game.start();
    }
}
