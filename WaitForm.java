import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitForm extends JDialog {
    private volatile Exchange exchange;
    private final Timer timer;
    private final JLabel label;
    private String textLabel;

    public WaitForm(JFrame parent, Exchange exchange) {
        super(parent);
        this.exchange = exchange;

        setTitle(exchange.getGame().getTitle());
        setSize(250, 150);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setModal(true);

        textLabel = exchange.getTextLabel();
        label = new JLabel(textLabel);
        add(label, BorderLayout.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        JButton button = new JButton("Прервать");
        add(button, BorderLayout.SOUTH);
        button.addActionListener(new ButtonAction());
        timer = new Timer(1000, new TimerAction());
        timer.setRepeats(true);
        timer.start();

        setVisible(true);
        revalidate();
    }
    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
        this.textLabel = exchange.getTextLabel();
        label.setText(this.textLabel);
        timer.start();
        setVisible(true);
    }

    class ButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (exchange) {
                exchange.setStopSolution(true);
            }
            timer.stop();
            setVisible(false);
        }
    }
    class TimerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            synchronized (exchange) {
                if (exchange.getStopSolution()) {
                    timer.stop();
                    setVisible(false);
                } else {
                    label.setText(textLabel + " - " + exchange.getProgress());
                }
            }
        }
    }
}
