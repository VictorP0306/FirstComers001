public class FirstComers {
    private final int size;
    private final int spaceCount;
    private Desk desk;
    private int[][] header;

    public FirstComers(int size, int spaceCount) {
        this.size = size;
        this.spaceCount = spaceCount;
        desk = new Desk(size, spaceCount);
        header = new int[4][size];
    }
    public int getSize() {
        return size;
    }
    public int getSpaceCount() {
        return spaceCount;
    }
    public int getDesk(int row, int col) {
        return desk.getCell(row, col);
    }
    public int getHeader(int side, int h) {
        return header[side][h];
    }
    public void setHeader(int side, int h, int value) {
        header[side][h] = value;
    }
}
