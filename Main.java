
/**
 * Java. Firsf comers (+Second comers)
 *
 * @Autor Victor Panin
 * @Version April 20, 2020
 */

import javax.swing.*;

public class Main {
    private static FirstComers game;
    private static View view;

    public static void main(String[] args) {
        game = new FirstComers(5, 1);
        //view = new View(game);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view = new View(game);
            }
        });
    }
}
