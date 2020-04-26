import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private View mainForm;
    private JPanel workPanel;
    private Settings settingsForm = null;
    private JPopupMenu popupMenu;
    private int selectRow = -1;
    private int selectCol = -1;

    public View(FirstComers game) {
        this.game = game;
        this.firstOrSecond = 0;
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

        createMenu();
        createPopupmenu();
        createWorkPanel();

        init();

        revalidate();
    }

    public void newSet(Map<String, Integer> args) {
        for (Map.Entry<String, Integer> entry : args.entrySet()) {
            if (entry.getKey().equals("Game")) {
                firstOrSecond = entry.getValue();
            } else if (entry.getKey().equals("Size")) {
                gameSize = entry.getValue();
            } else if (entry.getKey().equals("Space")) {
                gameSpaceCount = entry.getValue();
            }
        }

        if (firstOrSecond == 0) {
            game = new FirstComers(gameSize, gameSpaceCount);
        } else {
            game = new FirstComers(gameSize, gameSpaceCount);
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
        isEditHeader = false;
        isBuild = false;
        isSolution = false;
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
        JMenuBar mainMenu = new JMenuBar();
        setJMenuBar(mainMenu);

        //==========
        JMenu menuFile = new JMenu("Файл");
        mainMenu.add(menuFile);
        menuFile.setMnemonic('Ф');
        //Новый
        JMenuItem fileNew = new JMenuItem("Новый");
        menuFile.add(fileNew);
        fileNew.setEnabled(false);
        //!!!
        //Открыть
        JMenuItem fileOpen = new JMenuItem("Открыть");
        menuFile.add(fileOpen);
        fileOpen.setEnabled(false);
        //Сохранить
        JMenuItem fileSave = new JMenuItem("Сохранить");
        menuFile.add(fileSave);
        fileSave.setEnabled(false);
        //!!!
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

        //==========
        //Правка
        JMenu menuEdit = new JMenu("Правка");
        mainMenu.add(menuEdit);
        menuEdit.setMnemonic('П');
        //Отмена
        JMenuItem editUndo = new JMenuItem("Отмена");
        menuEdit.add(editUndo);
        editUndo.setEnabled(false);
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
    private void createToolPanel() {
        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH);
//        tBtns = new JButton[12];
        ToolButton toolButton = new ToolButton("-", 9);
        toolBar.add(toolButton);
        for (int i = 0; i < 8; i++) {
            toolButton = new ToolButton(Character.toString((char) ('A' + i)), i);
            toolBar.add(toolButton);
        }
        toolBar.addSeparator();
        toolButton = new ToolButton("X", 10);
        toolBar.add(toolButton);
        toolBar.addSeparator();
        toolButton = new ToolButton("<-", 11);
        toolBar.add(toolButton);
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

    private void setToolButton(int key) {
        if (isBuild || isSolution) return;
     }

    private void setDeskButton(int row, int col) {
        if (isBuild || isSolution || isEditHeader) return;
    }
    private void setKeyPUM(ActionEvent e, int key) {
        if (selectRow == -1 || selectCol == -1) { return; }

        if (selectRow == 0) {
            game.setHeader(Desk.SIDEUP, selectCol, key);
        } else if (selectRow == gameSize + 1) {
            game.setHeader(Desk.SIDEDOWN, selectCol, key);
        } else if (selectCol == 0) {
            game.setHeader(Desk.SIDELEFT, selectRow, key);
        } else if (selectCol == gameSize + 1) {
            game.setHeader(Desk.SIDEDOWN, selectRow, key);
        }
        btns[selectRow][selectCol].setText(getS(key));

        selectRow = -1;
        selectCol = -1;
    }
    class ToolButton extends JToggleButton {
        private final int key;

        public ToolButton(String text, int key) {
            super(text);
            this.key = key;
//            tBtns[key] = this;
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setToolButton(key);
                }
            });
        }
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
}
