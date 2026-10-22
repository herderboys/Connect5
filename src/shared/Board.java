package shared;

// game board stored as bits in a long[], with methods for placing pieces, legal moves and checking for a winner
public class Board {
    /*
    representing board pieces with two bits (00 = empty, 01 = player 1, 10 = player 2).
    we want a 15x15 grid, which is 225 squares. each square is 2 bits, so we need 450 bits.
    a long is 64 bits, and 64 x 8 = 512 which 450 fits into.
     */
    private long[] board = new long[8];
    private final int size ; // how many pieces to a side
    private final int pieceLength;

    // instantiating with a fixed side size, and a fixed piece length for placed pieces
    public Board(int size, int pieceLength) {
        this.size = size;
        this.pieceLength = pieceLength;
    }

    // checking which moves are legal to make
    public int[] getLegalMoves() {
        int emptyPlaces = 0;

        // loop once to get number of available moves
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (getPiece(row, col) == 0) {
                    emptyPlaces++;
                }
            }
        }

        // create array with place for all available moves
        int[] legalMoves = new int[emptyPlaces];

        // add index for available moves in array
        int legalMovesIndex = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (getPiece(row, col) == 0) {
                    legalMoves[legalMovesIndex] = getIndex(row, col);
                    legalMovesIndex++;
                }
            }
        }
        return legalMoves;
    }

    /*
    choosing to set pieces using bit manipulation to optimize performance
    ( and also because I thought it was really fun in CPROG :) )
     */
    public void setPiece(int row, int col, long player) {
        if (player < 0 || player > 2) {
            throw new IllegalArgumentException("Player must be either 1 (computer) or 2 (human). Set to 0 to clear piece.");
        }

        int arrayIndex = getInternalIndex(row, col);
        // where exactly is the leading bit of the piece?
        int bitOffset = (row * (size * pieceLength) + col * pieceLength) % 64;

        // make a mask clearing bits we want to overwrite
        long clearMask = ~(3L << bitOffset);

        // clear bits for board
        board[arrayIndex] &= clearMask;

        // prepare mask for which player to apply to piece
        long pieceMask = player << bitOffset;

        // place player mask into correct piece
        board[arrayIndex] |= pieceMask;
    }

    // returns value of piece at a specific index
    public long getPiece(int row, int col) {

        int arrayIndex = getInternalIndex(row, col);
        int bitOffset = (row * (size * pieceLength) + col * pieceLength) % 64;

        // shift bits at offset to the absolute right of long
        long ret = board[arrayIndex] >> bitOffset;

        // wipe remaining bits that are not part of first 2 (3L == 0b11)
        ret &= 3L;

        return ret;
    }

    // returns entire board state
    public long[] getBoard() {
        return board;
    }

    // getting index of piece, useful in loops
    public int getIndex(int row, int col) {
        return row * size + col;
    }

    // getter for size of board
    public int getSize() {
        return size;
    }

    // sets a new board to this board, completely overwrites current state
    public void setBoard(long[] newBoard) {
        this.board = newBoard;
    }

    // boolean to check if no moves are available, basically checking if a draw has occurred
    public boolean hasNoLegalMovesLeft() {
        return getLegalMoves().length == 0;
    }

    // checks if we have a winner
    public long checkWinner() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                long currentPiece = getPiece(row, col);

                // if square is empty, no way to win for anyone, move on
                if (currentPiece == 0) {
                    continue;
                }

                // checking all valid directions
                if (checkDirection(row, col, 0, 1, currentPiece) >= 5 ||
                        checkDirection(row, col, 1, 0, currentPiece) >= 5 ||
                        checkDirection(row, col, 1, 1, currentPiece) >= 5 ||
                        checkDirection(row, col, -1, 1, currentPiece) >= 5) {
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

        // make sure we're not standing in middle of row, only counting complete rows
        int prevRow = startRow - dRow;
        int prevCol = startCol - dCol;

        if (prevRow >= 0 && prevCol >= 0 && prevRow < size && prevCol < size) {

            if (getPiece(prevRow, prevCol) == player) {
                return 0;
            }

        }
        int count = 0;
        int row = startRow;
        int col = startCol;

        while (true) {
            if (row < 0 || col < 0 || row >= size || col >= size) {
                return count;
            }

            if (getPiece(row, col) == player) {
                count++;
                row += dRow;
                col += dCol;
            } else {
                return count;
            }
        }
    }

    // gets internal index of long array
    private int getInternalIndex(int row, int col) {
        // divide by 64 to get the absolute first bit of the long, since a long is 64 bits
        return (row * (size * pieceLength) + col * pieceLength) / 64;
    }

}
