
public class CreatePazzle implements Runnable {

    private final Exchange exchange;
    private final FirstComers game;
    private int size;
    private int spaceCount;
    private int letterCount;
    private int[][] header;
    private boolean saveHeader;

    private SeekASolution solution;
    private FirstComers gameSolution;
    private Exchange exchangeSolution;

    public CreatePazzle(Exchange exchange) {
        this.exchange = exchange;
        this.game = exchange.getGame();
        this.size = game.getSize();
        this.spaceCount = game.getSpaceCount();
        this.letterCount = size - spaceCount;

        gameSolution = new FirstComers(size, spaceCount);
        exchangeSolution = new Exchange(gameSolution, 2, "");
        this.solution = new SeekASolution(exchangeSolution);
    }

    @Override
    public void run() {

//        Logger logger = Logger.getInstance();
//        logger.start();
//        logger.write("Start: CreatePazzle.run()", true);

        firstloop:
        while (true) {

//            logger.write("Fill: start");
            int[][] desk = new int[size][size];
            StringBuilder sb = null;
            for (int i = 0; i < size; i++) {
//            sb = new StringBuilder("");
                for (int j = 0; j <= letterCount - 1; j++) {
                    desk[i][(i + j) % size] = j + 1;
//                sb.append(""+desk[i][j]);
                }
//            logger.write(sb.toString());
            }
//        for (int row = 0; row < size; row++) {
//            sb = new StringBuilder("");
//            for (int col = 0; col < size; col++) {
//                sb.append(""+desk[row][col]);
//            }
//            logger.write(sb.toString());
//        }
//            logger.write("Fill: end");

//            logger.write("Exchange: start");
            for (int i = 0; i < size; i++) {
                int row = (int) (Math.random() * size);
                int col = (int) (Math.random() * size);
//            logger.write("i="+i+" row="+row+" col="+col);
                //обменять строки
                if (i != row) {
                    int[] di = desk[i];
                    desk[i] = desk[row];
                    desk[row] = di;
                }
                //обменять колонки
                if (i != col) {
                    for (int j = 0; j < size; j++) {
                        int v = desk[j][i];
                        desk[j][i] = desk[j][col];
                        desk[j][col] = v;
                    }
                }
            }
//            for (int row = 0; row < size; row++) {
//                sb = new StringBuilder("");
//                for (int col = 0; col < size; col++) {
//                    sb.append("" + desk[row][col]);
//                }
//                logger.write(sb.toString());
//            }
//            logger.write("Exchange: end");

//            logger.write("Header: start");
            for (int i = 0; i < size; i++) {
                //up
                for (int j = 0; j < 3; j++) {
                    int v = desk[j][i];
                    if (v != 0) {
                        gameSolution.setHeader(Desk.SIDEUP, i, v);
                        break;
                    }
                }
                //down
                for (int j = 0; j < 3; j++) {
                    int v = desk[size - j - 1][i];
                    if (v != 0) {
                        gameSolution.setHeader(Desk.SIDEDOWN, i, v);
                        break;
                    }
                }
                //left
                for (int j = 0; j < 3; j++) {
                    int v = desk[i][j];
                    if (v != 0) {
                        gameSolution.setHeader(Desk.SIDELEFT, i, v);
                        break;
                    }
                }
                //right
                for (int j = 0; j < 3; j++) {
                    int v = desk[i][size - j - 1];
                    if (v != 0) {
                        gameSolution.setHeader(Desk.SIDERIGHT, i, v);
                        break;
                    }
                }
            }
//            for (int side = 0; side < 4; side++) {
//                sb = new StringBuilder("");
//                for (int h = 0; h < size; h++) {
//                    sb.append("" + gameSolution.getHeader(side, h));
//                }
//                logger.write(sb.toString());
//            }
//            logger.write("Header: end");

//            logger.write("First check: start");
            exchangeSolution = new Exchange(gameSolution, 2, "");
            solution.setExchange(exchangeSolution);
            solution.run();
//            logger.write("First check: end");
            boolean resF = false;
            synchronized (exchangeSolution) {
                resF = exchangeSolution.isSolutionFound();
            }
//            logger.write("res=" + resF);
            if (!resF) { continue; }
            //end first test

            int progress = 0;
            header = new int[4][size];
            saveHeader = false;

            while (true) {

//                logger.write("Loop begin");

                progress++;
                if (progress >= 100) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    synchronized (exchange) {
                        if (exchange.isStopSolution()) {
                            break;
                        }
                        exchange.setProgress(exchange.getProgress() + 1);
                    }
                    progress = 0;
                }

                //Hide
//                logger.write("Hide: start");
                while (true) {
                    int v = (int) (Math.random() * (size << 2));
                    int side = v / size;
                    int h = v % size;
                    int l = gameSolution.getHeader(side, h);
//                    logger.write("hide? side=" + side + " h=" + h + " l=" + l);
                    if (l != 0) {
//                    logger.trace("hide!");
                        gameSolution.setHeader(side, h, 0);
                        break;
                    }
                }
                for (int side = 0; side < 4; side++) {
                    StringBuilder sB = new StringBuilder("");
                    for (int h = 0; h < size; h++) {
                        int l = gameSolution.getHeader(side, h);
                        sB.append(l);
                    }
//                    System.out.println(side+": "+sB.toString());
//                    logger.write(side+": "+sB.toString());
                }
//                logger.write("Hide: end");

                //Check
//                logger.write("Check: start");
                exchangeSolution = new Exchange(gameSolution, 2, "");
                solution.setExchange(exchangeSolution);
                solution.run();
//                logger.write("Check: analize");
                boolean res = false;
                synchronized (exchangeSolution) {
                    res = exchangeSolution.isSolutionFound();
                }
//                logger.write("res=" + res);
//                if (!res && !saveHeader) { break firstloop; }
                if (res) {
                    //Save header
//                    logger.write("Check: save header");
                    saveHeader = true;
                    for (int side = 0; side < 4; side++) {
                        StringBuilder s = new StringBuilder(Integer.toString(side)).append(" ");
                        for (int h = 0; h < size; h++) {
                            header[side][h] = gameSolution.getHeader(side, h);
                            s.append(header[side][h]);
                        }
//                        logger.write(s.toString());
                    }
                } else {
                    if (saveHeader) {
                        synchronized (exchange) {
                            //CopyHeader();
//                            logger.write("Check: solved find!");
                            for (int side = 0; side < 4; side++) {
                                StringBuilder s = new StringBuilder(Integer.toString(side)).append(" ");
                                for (int h = 0; h < size; h++) {
                                    game.setHeader(side, h, header[side][h]);
                                    s.append(header[side][h]);
                                }
//                                logger.write(s.toString());
                            }
                            exchange.setSolutionFound(true);
                        }
                    } else {
//                        exchange.setThereIsNoDecision(true);
//                        logger.write("Not solved! Restart");
                        break firstloop;
//                        continue;
                    }
//                    logger.write("Check: end", true);
//                    logger.close();
                    return;
                }
            }
        }
    }
}