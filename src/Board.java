public class Board {
    /*
    representing board pieces with two bits (00 = empty, 01 = player 1, 10 = player 2).
    we want a 15x15 grid, which is 225 squares. each square is 2 bits, so we need 450 bits.
    a long is 64 bits, and 64 x 8 = 512 which 450 fits into.
     */
    private final long[] board = new long[8];
    private final int BOARD_HEIGHT = 15;
    private final int PIECE_LENGTH = 2;

    /*
    choosing to set pieces using bit manipulation to optimize performance
    ( and also because I thought it was really fun in CPROG :) )
     */
    public void setPiece(int row, int col, int player) {
        if (player < 0 || player > 2) {
            throw new IllegalArgumentException("Player must be either 1, 2 or 0 to clear piece.");
        }

        int arrayIndex = getIndex(row, col);
        // where exactly is the leading bit of the piece?
        int bitOffset = (row * (BOARD_HEIGHT * PIECE_LENGTH) + col * PIECE_LENGTH) % 64;

        // make a mask clearing bits we want to overwrite
        long clearMask = ~(3L << bitOffset);

        // clear bits for board
        board[arrayIndex] &= clearMask;

        // prepare mask for which player to apply to piece
        long pieceMask = ((long)player) << bitOffset;

        // place player mask into correct piece
        board[arrayIndex] |= pieceMask;
    }

    public int getIndex(int row, int col) {
        // divide by 64 to get the absolute first bit of the long, since a long is 64 bits
        return (row * (BOARD_HEIGHT * PIECE_LENGTH) + col * PIECE_LENGTH) / 64;
    }
}
