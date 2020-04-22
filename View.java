import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class View extends JFrame {
    private FirstComers game;
    private JButton[] tBtns;
    private JButton[][] btns;
    private int gameSize;
    private int gameSpaceCount;
    private final int maxSizeDesk = 11;
    private boolean isEditHeader;
    private boolean isBuild;
    private boolean isSolution;

    public View(FirstComers game) {
        this.game = game;
        this.gameSize = game.getSize();
        this.gameSpaceCount = game.getSpaceCount();
        this.isEditHeader = false;
        this.isBuild = false;
        this.isSolution = false;

        setTitle("Первые встречные");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setDefaultLookAndFeelDecorated(true);
        setVisible(true);

        createMenu();
//        createMainPanel();
        createToolPanel();
        createWorkPanel();

        init();

        revalidate();
    }
    private void init() {
        int letterCount = gameSize - gameSpaceCount;
        for (int i = 0; i < 9; i++) {
            tBtns[i].setEnabled(i < letterCount);
        }

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
    }

    private void createMenu() {
        JMenuBar mainMenu = new JMenuBar();
        setJMenuBar(mainMenu);

        //==========
        JMenu menuFile = new JMenu("Файл");
        mainMenu.add(menuFile);
        //Новый
        JMenuItem fileNew = new JMenuItem("Новый");
        menuFile.add(fileNew);
        fileNew.setEnabled(false);
        //Открыть
        JMenuItem fileOpen = new JMenuItem("Открыть");
        menuFile.add(fileOpen);
        fileOpen.setEnabled(false);
        //Сохранить
        JMenuItem fileSave = new JMenuItem("Сохранить");
        menuFile.add(fileSave);
        fileSave.setEnabled(false);
        //---------
        menuFile.addSeparator();
        //Выход
        JMenuItem fileExit = new JMenuItem("Выход");
        menuFile.add(fileExit);
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
        //Отмена
        JMenuItem editUndo = new JMenuItem("Отмена");
        menuEdit.add(editUndo);
        editUndo.setEnabled(false);
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
        editSettings.setEnabled(false);

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
    private void createToolPanel() {
        JToolBar toolBar = new JToolBar();
        add(toolBar, BorderLayout.NORTH);
        tBtns = new JButton[12];
        ToolButton toolButton = new ToolButton("-", 9);
        toolBar.add(toolButton);
        for (int i = 0; i < 9; i++) {
            toolButton = new ToolButton(Character.toString((char) ('A'+i)), i);
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
        JPanel workPanel = new JPanel(new GridBagLayout());
        add(workPanel, BorderLayout.CENTER);
        JButton dButton;
        btns = new JButton[11][11];
        for (int row = 0; row < 11; row++) {
            for (int col = 0; col < 11; col++) {
                 dButton = new DesktopButton(" ", row, col);
                 workPanel.add(dButton, newConstrains(row, col));
            }
        }
    }
    private GridBagConstraints newConstrains(int row, int col) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = row;
        gbc.gridx = col;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        return gbc;
    }
    private void setHeader(int side, int index) {
        if (isBuild || isSolution || !isEditHeader) return;
    }
    private void setToolButton(int key) {
        if (isBuild || isSolution) return;
    }
    private void setDeskButton(int row, int col) {
        if (isBuild || isSolution || isEditHeader) return;
    }

    class ToolButton extends JButton {
        private final int key;

        public ToolButton(String text, int key) {
            super(text);
            this.key = key;
            tBtns[key] = this;
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
                    int newRow, newCol;
                    if (row == 0) {
                        setHeader(Desk.SIDEUP, col - 1);
                    } else if (col == 0) {
                        setHeader(Desk.SIDELEFT, row - 1);
                    } else if (row == gameSize + 1) {
                        setHeader(Desk.SIDEDOWN, col - 1);
                    } else if (col == gameSize + 1) {
                        setHeader(Desk.SIDERIGHT, row - 1);
                    } else {
                        setDeskButton(row - 1, col - 1);
                    }
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
                    new String[] {"Первые встречные",
                            "(C) Панин Виктор 2020 г."},
                    "О программе",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
