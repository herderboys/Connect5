import java.util.Scanner;

public class Game {
    private final Board board;
    private final Brain brain;
    private final Scanner scanner;
    private final int boardSize;

    public Game(int boardSize) {
        this.board = new Board(boardSize, 2);
        this.brain = new Brain(board);
        this.scanner = new Scanner(System.in);
        this.boardSize = board.getSize();
    }

    public void start() {
        System.out.println("Connect-5 engine initialized.");
        int currentPlayer = 2; // human starts

        while (true) {
            printBoard();
            long winner = brain.checkWinner();
            if (winner > 0) {
                if (winner == 1) {
                    System.out.println("You lose!");
                } else {
                    System.out.println("You win!");
                }
                break;
            }
            if (currentPlayer == 2) {
                System.out.println("Your turn. Enter row and column (e.g., '7, 7'): \n");

                System.out.print("Row: ");
                int row = scanner.nextInt();
                System.out.println();

                System.out.print("Column: ");
                int col = scanner.nextInt();
                System.out.println();

                board.setPiece(row, col, currentPlayer);
            } else {
                System.out.println("AI is thinking...");
                int bestMoveIndex = brain.getBestMove(5);

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
                else if (piece == 1) System.out.print(" X "); // ai
                else if (piece == 2) System.out.print(" O "); // human
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("AI = X, You = O.\n");
    }

    public static void main(String[] args) {
        Game game = new Game(8);
        game.start();
    }
}
