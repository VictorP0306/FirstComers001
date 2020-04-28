import java.util.ArrayList;
import java.util.Stack;

public class FirstComers {
    private final int size;
    private final int spaceCount;
    private Desk desk;
    private int[][] header;
    private Stack<Desk> history;

    public FirstComers(int size, int spaceCount) {
        this.size = size;
        this.spaceCount = spaceCount;
        desk = new Desk(size, spaceCount);
        header = new int[4][size];
        history = new Stack<>();
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
    public void setDesk(int row, int col, int value) {
        setDesk(row, col, value, true);
    }
    public void setDesk(int row, int col, int value, boolean saveHistory) {
        if (saveHistory) {
            history.push(new Desk(this.desk));
        }
        desk.setCell(row, col, value);
    }
    public void setHeader(int side, int h, int value) {
        header[side][h] = value;
    }
    public void undo() {
        if (history.isEmpty()) { return; }
        this.desk = history.pop();
    }
}
