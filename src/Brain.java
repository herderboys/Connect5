public class Brain {

    private final Board board;
    private final int aiPlayer = 1;
    private final int humanPlayer = 2;

    public Brain(Board board) {
        this.board = board;
    }

    // min max algorithm
    public int minimax(int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (!board.hasLegalMovesLeft()) {
            return 0; // draw!
        }

        long winner = checkWinner();

        if (winner > 0) {
            if (winner == aiPlayer) {
                return 100000000;
            }
            return -100000000;
        }
        if (depth == 0) {
            return evaluateBoard(); // placeholder
        }

        // maximizing player
        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            int[] legalMoves = board.getLegalMoves();

            for (int legalMove : legalMoves) {

                // unpacking 2d index out of 1d index
                int row = legalMove / board.getSize();
                int col = legalMove % board.getSize();

                board.setPiece(row, col, aiPlayer);

                int eval = minimax(depth - 1, alpha, beta, !isMaximizingPlayer);

                board.setPiece(row, col, 0);

                maxEval = Math.max(maxEval, eval);

                // ab pruning
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;

            // maximizing ai
        } else {
            int minEval = Integer.MAX_VALUE;
            int[] legalMoves = board.getLegalMoves();

            for (int legalMove : legalMoves) {

                // unpacking 2d index out of 1d index
                int row = legalMove / board.getSize();
                int col = legalMove % board.getSize();

                board.setPiece(row, col, humanPlayer);

                int eval = minimax(depth - 1, alpha, beta, !isMaximizingPlayer);

                board.setPiece(row, col, 0);

                minEval = Math.min(minEval, eval);

                // ab pruning
                beta = Math.min(beta, minEval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    // returns the index of the best move
    public int getBestMove(int depth) {

        int maxScore = Integer.MIN_VALUE;
        int maxScoreIndex = -1;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int[] legalMoves = board.getLegalMoves();

        for (int legalMove : legalMoves) {

            // unpacking 2d index out of 1d index
            int row = legalMove / board.getSize();
            int col = legalMove % board.getSize();

            board.setPiece(row, col, aiPlayer);

            int eval = minimax(depth - 1, alpha, beta, false);

            board.setPiece(row, col, 0);

            if (eval > maxScore) {
                maxScore = eval;
                maxScoreIndex = legalMove;
            }
        }
        return maxScoreIndex;
    }

    public int evaluateBoard() {
        int totalScore = 0;
        int boardSize = board.getSize();

        // all possible directions that we need to check
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board.getPiece(row, col) == 0) { continue; }

                for (int[] dir : directions) {
                    int dRow = dir[0];
                    int dCol = dir[1];

                    int aiLength = checkDirection(row, col, dRow, dCol, aiPlayer);
                    totalScore += getScoreForLength(aiLength);

                    int humanLength = checkDirection(row, col, dRow, dCol, humanPlayer);
                    totalScore -= getScoreForLength(humanLength);
                }
            }
        }
        return totalScore;
    }

    private int getScoreForLength(int length) {
        if (length == 2) { return 10; }
        if (length == 3) { return 100; }
        if (length == 4) { return 1000; }

        return 0;
    }

    // checks if we have a winner
    public long checkWinner() {
        int boardSize = board.getSize();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                long currentPiece = board.getPiece(row, col);

                // if square is empty, no way to win for anyone, move on
                if (currentPiece == 0) {
                    continue;
                }

                // checking all valid directions
                if (checkDirection(row, col, 0, 1, currentPiece) == 5 ||
                        checkDirection(row, col, 1, 0, currentPiece) == 5 ||
                        checkDirection(row, col, 1, 1, currentPiece) == 5 ||
                        checkDirection(row, col, -1, 1, currentPiece) == 5) {
                    return currentPiece;
                }
            }
        }
        return 0;
    }

    /*
    helper method to check 5 in a row for a certain direction.
    dRow is direction of row, dCol is direction of column
    0, 1 to move right (horizontal)
    1, 0 to move down (vertical)
    1, 1 to move down-right
    -1, 1 to move up-right

    returns amount of pieces in a row for a given player
     */

    public int checkDirection(int startRow, int startCol, int dRow, int dCol, long player) {

        int boardSize = board.getSize();

        // make sure we're not standing in middle of row, only counting complete rows
        int prevRow = startRow - dRow;
        int prevCol = startCol - dCol;

        if (prevRow >= 0 && prevCol >= 0 && prevRow < boardSize && prevCol < boardSize) {

            if (board.getPiece(prevRow, prevCol) == player) {
                return 0;
            }

        }
        int count = 0;
        int row = startRow;
        int col = startCol;

        while (true) {
            if (row < 0 || col < 0 || row >= boardSize || col >= boardSize) {
                return count;
            }

            if (board.getPiece(row, col) == player) {
                count++;
                row += dRow;
                col += dCol;
            } else {
                return count;
            }
        }
    }
}
