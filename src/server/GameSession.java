package server;

import shared.Board;

import java.io.*;
import java.net.Socket;

// class for one game session
// one game session = a game with 2 players and a room code
public class GameSession implements Runnable {
    private final int BOARD_SIZE = 8;
    private final int PIECE_LENGTH = 2;

    private String roomCode;
    private Socket player1Socket;
    private Socket player2Socket;
    private Board board;

    // intiantiates game session with room code and first player, initializes a board
    public GameSession(String roomCode, Socket player1Socket) {
        this.roomCode = roomCode;
        this.player1Socket = player1Socket;
        this.board = new Board(BOARD_SIZE, PIECE_LENGTH);
    }

    // setter for adding player 2
    public void setPlayer2(Socket player2) {
        player2Socket = player2;
    }

    // getting to gain access to player 1 (used in Server.java)
    public Socket getPlayer1Socket() {
        return player1Socket;
    }

    // method to check if session is full / if a new player is allowed to join
    public boolean isFull() {
        return player2Socket != null;
    }

    // commenting order of stream writes/reads to keep track of what comes when,
    // that's what the commented numbers mean
    // run method for starting gamesession thread
    @Override
    public void run() {
        int playerTurn = 1;

        try {

            DataOutputStream outputStream1 = new DataOutputStream(player1Socket.getOutputStream());
            DataOutputStream outputStream2 = new DataOutputStream(player2Socket.getOutputStream());

            DataInputStream inputStream1 = new DataInputStream(player1Socket.getInputStream());
            DataInputStream inputStream2 = new DataInputStream(player2Socket.getInputStream());

            while (true) {

                long[] boardState = board.getBoard();

                for (int i = 0; i < boardState.length; i++) {
                    outputStream1.writeLong(boardState[i]); // 1
                    outputStream2.writeLong(boardState[i]); // 1
                }

                long winner = board.checkWinner();

                if (winner != 0) {
                    outputStream1.writeUTF("Player " + winner + " wins!"); // 2
                    outputStream2.writeUTF("Player " + winner + " wins!"); // 2
                    break;

                    // draw
                } else if (board.hasNoLegalMovesLeft()) {
                    outputStream1.writeUTF("Draw!");
                    outputStream2.writeUTF("Draw!");
                    break;

                } else {

                    // sending player turn to both players so they can check
                    // themselves if they're allowed to play
                    outputStream1.writeInt(playerTurn); // 2
                    outputStream2.writeInt(playerTurn); // 2

                    int row = 0;
                    int col = 0;

                    DataInputStream activeInputStream;

                    if (playerTurn == 1) {
                        activeInputStream = inputStream1;

                    } else {
                        activeInputStream = inputStream2;
                    }

                    row = activeInputStream.readInt(); // 1
                    col = activeInputStream.readInt(); // 2

                    // bounds checks for placed pieces are locally handled

                    board.setPiece(row, col, playerTurn);

                    // if 1 has turn, 3 - 1 = 2. if 2 has turn, 3 - 2 = 1. Elegant!
                    playerTurn = 3 - playerTurn;

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
