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

    public String getTitle() { return "Первые встречные"; }
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
    public int getNumber(int side, int row, int col) { return desk.getNumber(side, row, col); }
    public int getDepth(int side, int row, int col) { return desk.getDepth(side, row, col); }
    public int getLine(int side, int number, int depth) { return desk.getLine(side, number, depth); }
    public void undo() {
        if (history.isEmpty()) { return; }
        this.desk = history.pop();
    }

    private boolean checkError() {
        boolean error = false;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                error = error || checkError(row, col) == 1;
            }
        }
        return error;
    }
    private int checkError(int row, int col) {
        int result = 0;
        final int error = 1;
        final int checkAll = 2;
        int value = getDesk(row, col);
        if (value == 0) {
            return result;
        }
        int countRow = value == -1 ? spaceCount : 1;
        int countCol = countRow;
        int countAllRow = size - spaceCount;
        int countAllCol = countAllRow;
        for (int i = 0; i < size; i++) {
            int v = getDesk(i, col);
            if (v > 0) { countAllRow--; }
            // проверяем по вертикали есть ли такая цифра как в текущей клктке или количество пустых более gameSpaceCount
            if (i != row) {
                if (v == value) {
                    countRow--;
                    if (countRow <= 0) {
                        result = error;
                        break;
                    }
                }
            }
            v = getDesk(row, i);
            if (v > 0) { countAllCol--; }
            // проверяем по горизонтали
            if (i != col) {
                if (v == value) {
                    countCol--;
                    if (countCol <= 0) {
                        result = error;
                        break;
                    }
                }
            }
        }
        if (result == error) {
            return error;
        }

        if (countAllRow == 0 && countAllCol == 0) {
            result = checkAll;
        }

        if (value > 0) {
            loopA:
            for (int side = 0; side < 4; side++) {
                int number = getNumber(side, row, col);
                int depth = getDepth(side, row, col);
                int h = getHeader(side, side <= 1 ? col : row);
                if (h <= 0) {
                    continue;
                }
                if (result == checkAll) {
                    // все цифры присутствуют надо проверить первую и проверить на финиш
                    // жесткая проверка
                    for (int i = 0; i < 1 + spaceCount; i++) {
                        int v = getLine(side, number, i);
                        if (v <= 0) {
                            continue;
                        } else if (v == h) {
                            break;
                        } else {
                            result = error;
                            break loopA;
                        }
                    }
                } else {
                    // мягкая проверка (при встрече пустышки - двлее)
                    for (int i = 0; i < 1 + spaceCount; i++) {
                        int v = getLine(side, number, i);
                        if (v == 0) {
                            break;
                        } else if (v == -1) {
                            continue;
                        } else if (v == h) {
                            break;
                        } else {
                            result = error;
                            break loopA;
                        }
                    }
                }
            }
        }
        return result;
    }
    private boolean checkFinish() {
        int allCount = size * (size - spaceCount);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int value = getDesk(row, col);
                if (value > 0) { allCount--; }
            }
        }
        if (allCount == 0) {
            return true;
        }
        return false;
    }
}
