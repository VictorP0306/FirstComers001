import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Settings extends JDialog {
    private View parent;
    private JDialog settingsForm;
    private int firstOrSecond;
    private int gameSize;
    private int gameSpaceCount;
    private JComboBox<Integer> cb;
    private JSpinner sp, spSpace;

    public Settings(View parent) throws HeadlessException {
        this.parent = parent;
        this.settingsForm = this;

        setTitle("Настройки");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setModal(true);

        JPanel mainPanel = BoxLayoutUtils.createVerticalPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        add(mainPanel, BorderLayout.CENTER);

        JPanel gamePanel = BoxLayoutUtils.createHorizontalPanel();
        mainPanel.add(gamePanel);
        JLabel lGame = new JLabel("Игра");
        gamePanel.add(lGame);
        gamePanel.add(BoxLayoutUtils.createHorizontalStrut(12));
        String[] items = {"Первые встречные", "Вторые встречные"};
        cb = new JComboBox(items);
        gamePanel.add(cb);
        cb.setSelectedIndex(0);
        cb.setEnabled(false);
        mainPanel.add(BoxLayoutUtils.createVerticalStrut(12));

        JPanel sizePanel = BoxLayoutUtils.createHorizontalPanel();
        mainPanel.add(sizePanel);
        JLabel sizeLabel = new JLabel("Размер");
        sizePanel.add(sizeLabel);
        sizePanel.add(BoxLayoutUtils.createHorizontalStrut(12));
        SpinnerNumberModel spm = new SpinnerNumberModel(5, 5, 9, 1);
        sp = new JSpinner(spm);
        sizePanel.add(sp);
        mainPanel.add(BoxLayoutUtils.createVerticalStrut(12));

        JPanel spacePanel = BoxLayoutUtils.createHorizontalPanel();
        mainPanel.add(spacePanel);
        JLabel spaceLabel = new JLabel("Пустых клеток");
        spacePanel.add(spaceLabel);
        spacePanel.add(BoxLayoutUtils.createHorizontalStrut(12));
        SpinnerNumberModel spmSpace = new SpinnerNumberModel(1, 1, 2, 1);
        spSpace = new JSpinner(spmSpace);
        spacePanel.add(spSpace);
        mainPanel.add(BoxLayoutUtils.createVerticalStrut(12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        mainPanel.add(btnPanel);
        JPanel grid = new JPanel( new GridLayout(1,2,5,0) );
        btnPanel.add(grid);
        JButton btnOk = new JButton("Ок");
        grid.add(btnOk);
        btnOk.addActionListener(new ReturnArgs());
        JButton btnCancel = new JButton("Отмена");
        grid.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsForm.setVisible(false);
            }
        });

        BoxLayoutUtils.setGroupAlignmentX(
            Component.LEFT_ALIGNMENT,
            lGame, sizeLabel, spaceLabel);
        BoxLayoutUtils.setGroupAlignmentY(
            Component.CENTER_ALIGNMENT,
            cb, sp, spSpace);
        GUITools.makeSameSize(lGame, sizeLabel, spaceLabel);
        GUITools.fixTextFieldSize(cb);
        GUITools.fixTextFieldSize(sp);
        GUITools.fixTextFieldSize(spSpace);

        pack();
        setResizable(false);
    }

    public void setArgs(Map<String, Integer> args) {
        if (args == null) {
            return;
        }
        for (Map.Entry<String, Integer> entry : args.entrySet()) {
            if (entry.getKey().equals("Game")) {
                firstOrSecond = entry.getValue();
                cb.setSelectedIndex(firstOrSecond);
            } else if (entry.getKey().equals("Size")) {
                gameSize = entry.getValue();
                sp.setValue(gameSize);
            } else if (entry.getKey().equals("Space")) {
                gameSpaceCount = entry.getValue();
                spSpace.setValue(gameSpaceCount);
            }
        }
    }

    class ReturnArgs implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (firstOrSecond != cb.getSelectedIndex() ||
                    gameSize != (int) sp.getValue() ||
                    gameSpaceCount != (int) spSpace.getValue()) {
                Map<String, Integer> args = new HashMap<>();
                args.put("Game", cb.getSelectedIndex());
                args.put("Size", (int) sp.getValue());
                args.put("Space", (int) spSpace.getValue());
                parent.newSet(args);
            }
            settingsForm.setVisible(false);
        }
    }
}