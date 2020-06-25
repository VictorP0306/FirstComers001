import java.util.Arrays;

public class Desk {
    public static final int SIDEUP = 0;
    public static final int SIDEDOWN = 1;
    public static final int SIDELEFT = 2;
    public static final int SIDERIGHT = 3;

    private final int size;
    private final int spaceCount;
    private int[][] desk;

    public Desk(int size, int spaceCount) {
        this.size = size;
        desk = new int[size][size];
        this.spaceCount = spaceCount;
    }
    public Desk(Desk oldDesk) {
        size = oldDesk.size;
        spaceCount = oldDesk.spaceCount;
        this.desk = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                this.desk[row][col] = oldDesk.desk[row][col];
            }
        }
    }

    public int getCell(int row, int col) {
//        return desk[row][col];

//        if (row >= size || col >= size) {
//            System.out.println("Error. row="+row+" col="+col);
//            return 0;
//        }
        int v = 0;
        try {
            v = desk[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("size="+size+" row="+row+" col="+col);
            e.printStackTrace();
        }
        return v;
    }
    public void setCell(int row, int col, int value) { desk[row][col] = value; }
    public int getLine(int side, int number, int depth) {
        int newRow = number;
        int newCol = depth;
        switch (side) {
            case SIDELEFT: { break; }
            case SIDERIGHT: { newCol = size - depth - 1; break; }
            case SIDEUP: { newRow = depth; newCol = number; break; }
            case SIDEDOWN: { newRow = size - depth - 1; newCol = number; break; }
            default:
                throw new IllegalStateException("Unexpected (side) value: " + side);
        }
        return desk[newRow][newCol];
    }
    public void setLine(int side, int row, int col, int value) {
        int newRow = row;
        int newCol = col;
        switch (side) {
            case SIDELEFT: { break; }
            case SIDERIGHT: { newCol = size - col - 1; break; }
            case SIDEUP: { newRow = col; newCol = row; break; }
            case SIDEDOWN: { newRow = col; newCol = size - row - 1; break; }
            default:
                throw new IllegalStateException("Unexpected (side) value: " + side);
        }
        desk[newRow][newCol] = value;
    }
    public int getNumber(int side, int row, int col) {
        int newRow = row;
        switch (side) {
            case SIDELEFT: { break; }
            case SIDERIGHT: { break; }
            case SIDEUP: { newRow = col; break; }
            case SIDEDOWN: { newRow = col; break; }
            default:
                throw new IllegalStateException("Unexpected (side) value: " + side);
        }
        return newRow;
    }
    public int getDepth(int side, int row, int col) {
        int newCol = col;
        switch (side) {
            case SIDELEFT: { break; }
            case SIDERIGHT: { newCol = size - col - 1; break; }
            case SIDEUP: { newCol = row; break; }
            case SIDEDOWN: { newCol = size - row - 1; break; }
            default:
                throw new IllegalStateException("Unexpected (side) value: " + side);
        }
        return newCol;

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Desk{" +
                "size=" + size +
                ", spaceCount=" + spaceCount + "}\n");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(desk[i][j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
