
/**
 * Java. Firsf comers (+Second comers)
 *
 * @Autor Victor Panin
 * @Version April 20, 2020
 */

import javax.swing.*;

public class Main {
    private static View view;

    public static void main(String[] args) {
        FirstComers game = new FirstComers(5, 1);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view = new View(game);
            }
        });
    }
}
