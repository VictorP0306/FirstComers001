import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class View extends JFrame {
    private FirstComers game;
    private DesktopButton[][] btns;
    private int firstOrSecond;
    private int gameSize;
    private int gameSpaceCount;
    private final int maxSizeDesk = 11;
    private boolean isEditHeader;
    private boolean isBuild;
    private boolean isSolution;
    private final View mainForm;
    private final JMenuBar mainMenu;
    private JPanel workPanel;
    private Settings settingsForm = null;
    private JPopupMenu popupMenu;
    private int selectRow = -1;
    private int selectCol = -1;
    private final Color bgColor;

    public View(FirstComers game) {
        this.game = game;
        setFirstOrSecond();
        this.gameSize = game.getSize();
        this.gameSpaceCount = game.getSpaceCount();
        this.isEditHeader = false;
        this.isBuild = false;
        this.isSolution = false;
        this.mainForm = this;

        setTitle("Первые встречные");
        setSize(500, 546);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setVisible(true);

        this.mainMenu = new JMenuBar();
        createMenu();
        createPopupmenu();
        createWorkPanel();
        bgColor = btns[0][0].getBackground(); //getForeground();

        init();

        revalidate();
    }

    public void newSet(Map<String, Integer> args) {
        for (Map.Entry<String, Integer> entry : args.entrySet()) {
            if (entry.getKey().toUpperCase().equals("GAME")) {
                firstOrSecond = entry.getValue();
            } else if (entry.getKey().toUpperCase().equals("SIZE")) {
                gameSize = entry.getValue();
            } else if (entry.getKey().toUpperCase().equals("SPACE")) {
                gameSpaceCount = entry.getValue();
            }
        }

        if (firstOrSecond == 0) {
            game = new FirstComers(gameSize, gameSpaceCount);
        } else {
            game = new SecondComers(gameSize, gameSpaceCount);
        }

        init();
    }
    public String getS(int key) {
        String result = " ";
        if (key == -1) {
            result = "-";
        } else if (key > 0 && key < 9) {
            result = Character.toString((char) ('A'+(key-1)));
        } else {
            result = " ";
        }
        return result;
    }

    private void init() {

        for (int row = 0; row < maxSizeDesk; row++) {
            for (int col = 0; col < maxSizeDesk; col++) {
                boolean isVisible = true;
                if (row > gameSize + 1 || col > gameSize + 1) {
                    isVisible = false;
                } else if (row == 0 && col == 0) {
                    isVisible = false;
                } else if (row == gameSize + 1 && col == 0) {
                    isVisible = false;
                } else if (row == 0 && col == gameSize + 1) {
                    isVisible = false;
                } else if (row == gameSize + 1 && col == gameSize + 1) {
                    isVisible = false;
                }
                JButton btn = btns[row][col];
                btn.setVisible(isVisible);

                boolean isEnable = true;
                if (row == 0 || col == 0 || row == gameSize + 1 || col == gameSize + 1) {
                    isEnable = false;
                }
                btn.setEnabled(isEnable);
            }
        }
        isBuild = false;
        isSolution = false;
        setNoEdit();
        showWorkSpace();
    }
    private void showWorkSpace() {
        //header
        for (int i = 1; i <= gameSize; i++) {
            btns[0][i].setEnabled(isEditHeader); // up
            btns[0][i].setText(getS(game.getHeader(Desk.SIDEUP, i-1)));
            btns[gameSize+1][i].setEnabled(isEditHeader); // down
            btns[gameSize+1][i].setText(getS(game.getHeader(Desk.SIDEDOWN, i-1)));
            btns[i][0].setEnabled(isEditHeader); // left
            btns[i][0].setText(getS(game.getHeader(Desk.SIDELEFT, i-1)));
            btns[i][gameSize+1].setEnabled(isEditHeader); // right
            btns[i][gameSize+1].setText(getS(game.getHeader(Desk.SIDERIGHT, i-1)));
        }
        //workSpace
        for (int row = 1; row <= gameSize; row++) {
            for (int col = 1; col <= gameSize; col++) {
                btns[row][col].setEnabled( ! isEditHeader);
                btns[row][col].setText(getS(game.getDesk(row-1, col-1)));
            }
        }
        //popup menu
        int letterCount = gameSize - gameSpaceCount;
        MenuElement[] mE = popupMenu.getSubElements();
        for (int i = 0; i < mE.length; i++) {
            mE[i].getComponent().setVisible((i - 2) < letterCount);
        }
    }
    private void createMenu() {
        setJMenuBar(mainMenu);
        createFileMenu();
        createEditMenu();

        //==========
        //Помощь
        JMenu menuHelp = new JMenu("Помощь");
        mainMenu.add(menuHelp);
        menuHelp.setMnemonic('о');
        //О программе
        JMenuItem helpAbout = new JMenuItem("О программе");
        menuHelp.add(helpAbout);
        helpAbout.setMnemonic('О');
        helpAbout.addActionListener(new AboutAction(this));
    }
    private void createFileMenu() {
        //==========
        JMenu menuFile = new JMenu("Файл");
        mainMenu.add(menuFile);
        menuFile.setMnemonic('Ф');
        //Новый
        JMenuItem fileNew = new JMenuItem("Новый");
        menuFile.add(fileNew);
        fileNew.setMnemonic('Н');
        fileNew.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        fileNew.setIcon(new ImageIcon("src/res/new.png"));
        fileNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSet(new HashMap<>());
                init();
            }
        });
        //Открыть
        JMenuItem fileOpen = new JMenuItem("Открыть");
        menuFile.add(fileOpen);
        fileOpen.setMnemonic('О');
        fileOpen.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        fileOpen.setIcon(new ImageIcon("src/res/open.png"));
        fileOpen.addActionListener(new OpenAction());
        //Сохранить
        JMenuItem fileSave = new JMenuItem("Сохранить");
        menuFile.add(fileSave);
        fileSave.setMnemonic('С');
        fileSave.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        fileSave.setIcon(new ImageIcon("src/res/save.png"));
        fileSave.addActionListener(new SaveAction());
        //---------
        menuFile.addSeparator();
        //Выход
        JMenuItem fileExit = new JMenuItem("Выход");
        menuFile.add(fileExit);
        fileExit.setMnemonic('В');
        fileExit.setIcon(new ImageIcon("src/res/exit.png"));
        fileExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
    private void createEditMenu() {
        //==========
        //Правка
        JMenu menuEdit = new JMenu("Правка");
        mainMenu.add(menuEdit);
        menuEdit.setMnemonic('П');
        //Отмена
        JMenuItem editUndo = new JMenuItem("Отмена");
        menuEdit.add(editUndo);
        editUndo.setMnemonic('О');
        editUndo.setIcon(new ImageIcon("src/res/cancel.png"));
        editUndo.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
        editUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSolution || isBuild || isEditHeader) { return; }
                game.undo();
                showWorkSpace();
            }
        });
        //----------
        menuEdit.addSeparator();
        //----------
        //Редактировать
        JMenuItem editEdit = new JMenuItem("Редактировать");
        menuEdit.add(editEdit);
        editEdit.setMnemonic('Р');
        editEdit.setIcon(new ImageIcon("src/res/reply.png"));
        editEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isBuild || isSolution) return;
                if (! isEditHeader) {
                    isEditHeader = true;
                }
                showWorkSpace();
            }
        });
        //Завершить редактирование
        JMenuItem editStop = new JMenuItem("Завершить редактирование");
        menuEdit.add(editStop);
        editStop.setMnemonic('З');
        editStop.setIcon(new ImageIcon("src/res/stop.png"));
        editStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isBuild || isSolution) return;
                if (isEditHeader) {
                    isEditHeader = false;
                }
                showWorkSpace();
            }
        });
        //----------
        menuEdit.addSeparator();
        //Создать
        JMenuItem editBuild = new JMenuItem("Создать");
        menuEdit.add(editBuild);
        editBuild.setEnabled(false);
        //Решить
        JMenuItem editSolution = new JMenuItem("Решить");
        menuEdit.add(editSolution);
        editSolution.setEnabled(false);
        //----------
        menuEdit.addSeparator();
        //Настройка
        JMenuItem editSettings = new JMenuItem("Настройки");
        menuEdit.add(editSettings);
        editSettings.setMnemonic('Н');
        editSettings.setIcon(new ImageIcon("src/res/options.png"));
        editSettings.addActionListener(new SettingsAction());
    }
    private void setNoEdit() {
        selectRow = -1;
        selectCol = -1;
        isEditHeader = false;
    }
    private void createPopupmenu() {
        popupMenu = new JPopupMenu();
        PuMenuItem mi;
        for (int i = -1; i < 9; i++) {
            mi = new PuMenuItem(i);
            String t = getS(i);
            mi.setText(t);
            popupMenu.add(mi);
            if (i > 0) {
                mi.setMnemonic(t.charAt(0));
            }
        }
        popupMenu.getInvoker();
    }
    private void createWorkPanel() {
        GridBagLayout gbl = new GridBagLayout();
        workPanel = new JPanel(gbl);
        add(workPanel, BorderLayout.CENTER);
        JButton dButton;
        btns = new DesktopButton[11][11];
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                dButton = new DesktopButton(" ", row, col);
                workPanel.add(dButton, newConstrains(row, col));
            }
        }
    }
    private GridBagConstraints newConstrains(int row, int col) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.gridy = row;
        gbc.gridx = col;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        return gbc;
    }
    private void setKeyPUM(ActionEvent e, int key) {
        if (selectRow == -1 || selectCol == -1) { return; }

        if (selectRow == 0) {
            game.setHeader(Desk.SIDEUP, selectCol-1, key);
        } else if (selectRow == gameSize + 1) {
            game.setHeader(Desk.SIDEDOWN, selectCol-1, key);
        } else if (selectCol == 0) {
            game.setHeader(Desk.SIDELEFT, selectRow-1, key);
        } else if (selectCol == gameSize + 1) {
            game.setHeader(Desk.SIDERIGHT, selectRow-1, key);
        } else {
            game.setDesk(selectRow-1, selectCol-1, key);
        }
        btns[selectRow][selectCol].setText(getS(key));

        if (checkError(selectRow-1, selectCol-1) == 2) {
            if (checkFinish()) {
                if (! checkError()) {
                    win();
                }
            }
        }

        selectRow = -1;
        selectCol = -1;
    }
    private boolean checkError() {
        boolean error = false;
        for (int row = 0; row < gameSize; row++) {
            for (int col = 0; col < gameSize; col++) {
                error = error || checkError(row, col) == 1;
            }
        }
        return error;
    }
    private int checkError(int row, int col) {
        int result = 0;
        final int error = 1;
        final int checkAll = 2;
        int value = game.getDesk(row, col);
        if (value == 0) {
            btns[row+1][col+1].setBackground(bgColor);
            return result;
        }
        int countRow = value == -1 ? gameSpaceCount : 1;
        int countCol = countRow;
        int countAllRow = gameSize - gameSpaceCount;
        int countAllCol = countAllRow;
        for (int i = 0; i < gameSize; i++) {
            int v = game.getDesk(i, col);
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
            v = game.getDesk(row, i);
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
            btns[row+1][col+1].setBackground(Color.RED);
            return error;
        }

        if (countAllRow == 0 && countAllCol == 0) {
            result = checkAll;
        }

        if (value > 0) {
            loopA:
            for (int side = 0; side < 4; side++) {
                int number = game.getNumber(side, row, col);
                int depth = game.getDepth(side, row, col);
                int h = game.getHeader(side, side <= 1 ? col : row);
                if (h <= 0) {
                    continue;
                }
                if (result == checkAll) {
                    // все цифры присутствуют надо проверить первую и проверить на финиш
                    // жесткая проверка
                    for (int i = 0; i < 1 + gameSpaceCount; i++) {
                        int v = game.getLine(side, number, i);
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
                    for (int i = 0; i < 1 + gameSpaceCount; i++) {
                        int v = game.getLine(side, number, i);
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
        btns[row+1][col+1].setBackground(result == error ? Color.RED : bgColor);
        return result;
    }
    private void setFirstOrSecond() {
        this.firstOrSecond = 0;
    }
    private boolean checkFinish() {
        int allCount = gameSize * (gameSize - gameSpaceCount);
        for (int row = 0; row < gameSize; row++) {
            for (int col = 0; col < gameSize; col++) {
                int value = game.getDesk(row, col);
                if (value > 0) { allCount--; }
            }
        }
        if (allCount == 0) {
            return true;
        }
        return false;
    }
    private void win() {
        JOptionPane.showMessageDialog(mainForm,
                "ПОБЕДА !!!",
                "Первые встречные",
                JOptionPane.WARNING_MESSAGE);
    }

    class DesktopButton extends JButton {
        private final int row;
        private final int col;

        DesktopButton(String title, int row, int col) {
            this.row = row;
            this.col = col;
            setText(title);
            btns[row][col] = this;

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectRow = row;
                    selectCol = col;
                    DesktopButton btn = btns[row][col];
                    popupMenu.show(btn, btn.getWidth() / 2, btn.getHeight() / 2);
                }
            });
        }
    }
    class PuMenuItem extends JMenuItem {
        private int key;
        public PuMenuItem (int key) {
            this.key = key;
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setKeyPUM(e, key);
                }
            });
        }
    }
    class AboutAction implements ActionListener {
        JFrame mainForm;

        public AboutAction(JFrame mainForm) {
            this.mainForm = mainForm;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(mainForm,
                    new String[]{"Первые встречные",
                            "(C) Панин Виктор 2020 г."},
                    "О программе",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    class SettingsAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (settingsForm == null) {
                settingsForm = new Settings(mainForm);
            }

            HashMap<String, Integer> args = new HashMap<String, Integer>();
            args.put("Game", firstOrSecond);
            args.put("Size", gameSize);
            args.put("Space", gameSpaceCount);
            settingsForm.setArgs(args);

            settingsForm.setVisible(true);
        }
    }
    class OpenAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isBuild || isSolution) return;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select file");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text (*.txt)", "TXT");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(mainForm);
            if (result == JFileChooser.APPROVE_OPTION) {
//                    mainObject.OpenFile(fileChooser.getSelectedFile().getAbsolutePath());
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                char[] buffer = new char[120]; // 9 * 9 + 9 * 4
                int countRead = 0;
                try {
                    FileReader in = new FileReader(fileName);
                    countRead = in.read(buffer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

                if (buffer[0] == 'F') {
//                        openFirstGames(buffer, countRead);
                    if (countRead < 3 ) {
                        System.out.println("Error file format");
                        return;
                    }
                    int size = Integer.parseInt(Character.toString(buffer[1]));
                    int numCount = Integer.parseInt(Character.toString(buffer[2]));
                    if (countRead == 3 + size * 4 || countRead == 3 + size * 4 + size * size) {
                    } else {
                        System.out.println("Error file format");
                        return;
                    }
                    if (numCount == size - 1 || numCount == size - 2) {
                    } else {
                        System.out.println("Error file format");
                        return;
                    }
//                    game = new FirstComers(size, size - numCount);
                    Map<String, Integer> args = new HashMap<>();
                    args.put("Game", 0);
                    args.put("Size", size);
                    args.put("Space", size - numCount);
                    newSet(args);
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < size; j++) {
                            int index = 3 + i * size + j;
                            int v = Integer.parseInt(Character.toString(buffer[index]));
                            game.setHeader(i, j, v);
                        }
                    }
                    if (countRead == 3 + size * 4 + size * size) {
                        int offset = 3 + size * 4;
                        for (int row = 0; row < size; row++) {
                            for (int col = 0; col < size; col++) {
                                int index = offset + row * size + col;
                                int v = Integer.parseInt(Character.toString(buffer[index]));
                                game.setDesk(row, col, v, false);
                            }
                        }
                    }
                    firstOrSecond = 0;
                    gameSize = size;
                    gameSpaceCount = size - numCount;
                    isBuild = false;
                    isSolution = false;
                    init();
                } else {
                    //            openSecondCounter(buffer);
                    System.out.println("Error file format");
                    return;
                }
            }
        }
    }
    class SaveAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isSolution || isBuild) return;
            boolean flagEdit = isEditHeader;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select file");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text (*.txt)", "TXT");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(mainForm);
            if (result == JFileChooser.APPROVE_OPTION) {
//                    mainObject.SaveFile(fileChooser.getSelectedFile().getAbsolutePath());
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                try {
                    FileWriter out = new FileWriter(fileName);
                    out.write("F");
                    out.write(Integer.toString(gameSize));
                    out.write(Integer.toString(gameSize - gameSpaceCount));
                    for (int side = 0; side < 4; side++) {
                        for (int i = 0; i < gameSize; i++) {
                            out.write(Integer.toString(game.getHeader(side, i)));
                        }
                    }
                    boolean flagIsEmpty = true;
                    for (int row = 0; row < gameSize; row++) {
                        for (int col = 0; col < gameSize; col++) {
                            if (game.getDesk(row, col) != 0) {
                                flagIsEmpty = false;
                                break;
                            }
                        }
                    }
                    if (!flagIsEmpty) {
                        for (int row = 0; row < gameSize; row++) {
                            for (int col = 0; col < gameSize; col++) {
                                out.write(Integer.toString(game.getDesk(row, col)));
                            }
                        }
                    }
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
            if (flagEdit) {
                isEditHeader = false;
                showWorkSpace();
            }
        }
    }
}
