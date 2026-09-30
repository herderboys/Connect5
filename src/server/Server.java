package server;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

// server to hold all game sessions and players
public class Server {
    private static final ConcurrentHashMap<String, GameSession> sessions = new ConcurrentHashMap<>();

    // empty constructor, not instantiated
    public Server() {
    }

    // starting server method
    private static void startServer() {
        // local host
        int port = 8000;

        try (ServerSocket serversocket = new ServerSocket(port)) {
            System.out.println("Server running on port " + port + ".");

            while (true) {
                Socket clientSocket = serversocket.accept();

                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

                String request = inputStream.readUTF(); // 1

                if (request.startsWith("CREATE")) {

                    String roomCode = generateRoomCode(6);
                    GameSession gameSession = new GameSession(roomCode, clientSocket);
                    sessions.put(roomCode, gameSession);

                    // just give room code as output, client handles console print
                    outputStream.writeUTF(roomCode); // 1
                    System.out.println("Player 1 connected.");

                } else if (request.startsWith("JOIN")) {
                    // e.g. "JOIN abcdef", will give abcdef
                    String roomCode = inputStream.readUTF(); // 2
                    GameSession gameSession = sessions.get(roomCode);

                    if (gameSession != null && !gameSession.isFull()) {
                        gameSession.setPlayer2(clientSocket);

                        // need to create an output stream for player 1 to transmit messages to both players
                        DataOutputStream player1OutStream = new DataOutputStream(gameSession.getPlayer1Socket().getOutputStream());
                        player1OutStream.writeUTF("Player 2 joined."); // 2
                        outputStream.writeUTF("Player 2 joined."); // 1
                        System.out.println("Player 2 connected.");

                        new Thread(gameSession).start();
                        System.out.println("Game session started.");
                    }

                } else {
                    outputStream.writeUTF("ERROR. Could not join room. Either room doesn't exist or room is full."); // 1/2 depending on if player is 1 or 2
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // helper method to generate a random room code
    private static String generateRoomCode(int len) {
        StringBuilder ret = new StringBuilder();
        Random random = new Random();
        char c = 0;

        for (int i = 0; i < len; i++) {
            c = (char) (random.nextInt(26) + 'a');
            ret.append(c);
        }
        return ret.toString();
    }

    // main method, only calls startServer()
    public static void main(String[] args) {
        startServer();

    }
}
