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
            return 0; // placeholder
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
            }
            return minEval;
        }
    }

    // player 1 is computer, player 2 is human
    public long checkWinner() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                long currentPiece = board.getPiece(row, col);

                // if square is empty, no way to win for anyone, move on
                if (currentPiece == 0) {
                    continue;
                }

                // checking all valid directions
                if (checkDirection(row, col, 0, 1, currentPiece, 5) ||
                        checkDirection(row, col, 1, 0, currentPiece, 5) ||
                        checkDirection(row, col, 1, 1, currentPiece, 5) ||
                        checkDirection(row, col, -1, 1, currentPiece, 5)) {
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

    returns true if there are n in a row for player, where n = length
     */

    public boolean checkDirection(int startRow, int startCol, int dRow, int dCol, long player, int length) {

        // -1 because starting piece counts as first piece
        int endRow = startRow + ((length - 1) * dRow);
        int endCol = startCol + ((length - 1) * dCol);

        if (endRow > board.getSize() - 1 || endRow < 0 || endCol < 0 || endCol > board.getSize() - 1) {
            return false;
        }

        int count = 0;
        int row = startRow;
        int col = startCol;

        while (count < length) {
            if (board.getPiece(row, col) == player) {
                count++;
            } else {
                return false;
            }
            row += dRow;
            col += dCol;
        }
        return count == length;
    }
}
