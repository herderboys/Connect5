public class Board {
    /*
    representing board pieces with two bits (00 = empty, 01 = player 1, 10 = player 2).
    we want a 15x15 grid, which is 225 squares. each square is 2 bits, so we need 450 bits.
    a long is 64 bits, and 64 x 8 = 512 which 450 fits into.
     */
    private final long[] board = new long[8];
    private final int size ; // how many pieces to a side
    private final int pieceLength;

    public Board(int size, int pieceLength) {
        this.size = size;
        this.pieceLength = pieceLength;
    }

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
            throw new IllegalArgumentException("Player must be either 1 (computer) or 2 (human). It can also be set to 0 to clear piece.");
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

    public long getPiece(int row, int col) {

        int arrayIndex = getInternalIndex(row, col);
        int bitOffset = (row * (size * pieceLength) + col * pieceLength) % 64;

        // shift bits at offset to the absolute right of long
        long ret = board[arrayIndex] >> bitOffset;

        // wipe remaining bits that are not part of first 2 (3L == 0b11)
        ret &= 3L;

        return ret;
    }

    public int getIndex(int row, int col) {
        return row * size + col;
    }

    public int getSize() {
        return size;
    }

    public boolean hasLegalMovesLeft() {
        return getLegalMoves().length > 0;
    }

    private int getInternalIndex(int row, int col) {
        // divide by 64 to get the absolute first bit of the long, since a long is 64 bits
        return (row * (size * pieceLength) + col * pieceLength) / 64;
    }

}
