package shared;

public class Brain {

    private final Board board;
    private final int aiPlayer = 1;
    private final int humanPlayer = 2;

    public Brain(Board board) {
        this.board = board;
    }

    // min max algorithm
    public int minimax(int depth, int alpha, int beta, boolean isMaximizingPlayer) {

        long winner = board.checkWinner();

        // returns a massive score if someone wins to always make it choose outcomes with wins
        if (winner > 0) {
            if (winner == aiPlayer) {
                return 100000000 + depth; // + depth so it prioritizes faster wins
            }
            return -100000000 - depth; // - depth to punish early losses
        }

        if (board.hasNoLegalMovesLeft()) {
            return 0; // draw!
        }

        // nobody won yet, guess who is doing better in game
        if (depth == 0) {
            return evaluateBoard();
        }

        // maximizing player, ai turn
        if (isMaximizingPlayer) {

            int maxEval = Integer.MIN_VALUE;
            int[] legalMoves = board.getLegalMoves();

            for (int legalMove : legalMoves) {

                // unpacking 2d index out of 1d index
                int row = legalMove / board.getSize();
                int col = legalMove % board.getSize();

                // simulate ai move
                board.setPiece(row, col, aiPlayer);

                // recursive call to minimax, false because human turn (= opposite of current's turn)
                int eval = minimax(depth - 1, alpha, beta, !isMaximizingPlayer);

                // undo sim
                board.setPiece(row, col, 0);

                // keeping track of best score
                maxEval = Math.max(maxEval, eval);

                // ab pruning
                // updates guaranteed minimum ai score
                alpha = Math.max(alpha, maxEval);

                // if human (beta) found move that results in lower score than ai (alpha) found,
                // human will never allow game to reach this state. therefore cancel branch
                if (beta <= alpha) {
                    break;
                }
            }
            // push the best ai score up recursion tree
            return maxEval;

            // minimizing player, human turn
        } else {
            int minEval = Integer.MAX_VALUE;
            int[] legalMoves = board.getLegalMoves();

            for (int legalMove : legalMoves) {

                // unpacking 2d index out of 1d index
                int row = legalMove / board.getSize();
                int col = legalMove % board.getSize();

                // simulate human move
                board.setPiece(row, col, humanPlayer);

                // recursive call to minimax, false because ai turn (= opposite of current's turn)
                int eval = minimax(depth - 1, alpha, beta, !isMaximizingPlayer);

                // undo sim
                board.setPiece(row, col, 0);

                // keeping track of worst (best for human) score
                minEval = Math.min(minEval, eval);

                // ab pruning
                // updates the guaranteed max human score
                beta = Math.min(beta, minEval);

                // same as before, ai found a move with higher score than what
                // human found here. cancel branch because ai will never allow this state
                if (beta <= alpha) {
                    break;
                }
            }
            // push worst case scenario up tree
            return minEval;
        }
    }

    // returns the 1D index of the best move, this is where minimaxing is actually applied
    public int getBestMove(int depth) {

        int maxScore = Integer.MIN_VALUE;
        int maxScoreIndex = -1;

        // minimum score AI can guarantee so far
        int alpha = Integer.MIN_VALUE;

        // maximum score human can guarantee so far
        int beta = Integer.MAX_VALUE;

        int[] legalMoves = board.getLegalMoves();

        for (int legalMove : legalMoves) {

            // unpacking 2d index out of 1d index
            int row = legalMove / board.getSize();
            int col = legalMove % board.getSize();

            // simulating move
            board.setPiece(row, col, aiPlayer);

            // checking what score this move would give with minimax
            int eval = minimax(depth - 1, alpha, beta, false);

            // undoing the move, this is just a sim
            board.setPiece(row, col, 0);

            // check if simulated move gave better result than previous best
            if (eval > maxScore) {
                maxScore = eval;
                maxScoreIndex = legalMove;
            }
            // now we know we can get at least maxScore, alpha is updated to this
            alpha = maxScore;
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

                    int aiLength = board.checkDirection(row, col, dRow, dCol, aiPlayer);
                    totalScore += getScoreForLength(aiLength);

                    int humanLength = board.checkDirection(row, col, dRow, dCol, humanPlayer);
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

}
