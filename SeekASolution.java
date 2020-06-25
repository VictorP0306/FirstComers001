public class SeekASolution implements Runnable {
    private Exchange exchange;
    private FirstComers game;
    private int size;
    private int spaceCount;
    private int letterCount;
    private int solutionCount;
    private Desk desk;

    public SeekASolution(Exchange exchange) {
        setExchange(exchange);
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
        game = exchange.getGame();
        size = game.getSize();
        spaceCount = game.getSpaceCount();
        letterCount = size - spaceCount;
    }

    @Override
    public void run() {

        Logger logger = Logger.getInstance();
        logger.write("Start: SeekASolution.run()");

        exchange.setSolutionFound(false);
        exchange.setThereIsNoDecision(false);
        desk = new Desk(size, spaceCount);
        solutionCount = 0;
        Command command = Command.Check;
        int row = 0;
        int col = 0;
        int pos = 0;
        int letter = 0;
        int ss = size * size;
        boolean state = false;
        int progress = 0;

        //Calculate
        loop:
        while (true) {

            progress++;
            if (progress >= 1000) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                synchronized (exchange) {
                    if (exchange.isStopSolution()) {
                        return;
                    }
                    exchange.setProgress(exchange.getProgress() + 1);
                    progress = 0;
                }
            }

//            logger.write("command="+command.toString());

            switch (command) {

                case Check: {
                    state = check(row, col, letter);
                    if (state) {
                        command = Command.NextCell;
                    } else {
                        command = Command.NextLetter;
                    }
//                    logger.write("row="+row+" col="+col+" letter="+letter+" state="+state+" command="+command.toString());
                    break;
                }

                case NextLetter: {
                    letter++;
                    if (letter <= letterCount) {
                        command = Command.Check;
                    } else {
                        command = Command.PreviousCell;
                    }
//                    logger.write("row="+row+" col="+col+" letter="+letter+" state="+state+" command="+command.toString());
                    break;
                }

                case NextCell: {
                    pos++;
                    row = pos / size;
                    col = pos % size;
                    if (pos < ss) {
                        letter = 0;
                        command = Command.Check;
                    } else {
                        //end desk
//                        if (state) {
                            //Solution!!!
                            ++solutionCount;
//                            logger.write(desk.toString());
                            if (exchange.getCountSolution() == 1) {
                                synchronized (exchange) {
                                    exchange.setSolutionFound(true);
                                    exchange.setDesk(desk);
                                }
//                                exchange = null;
//                                logger.write("solutionCount="+solutionCount+" getCountSolution="+exchange.getCountSolution()+" new desk. return");
                                return;
                            } else {
                                if (solutionCount == 1) {
                                    //next
                                    command = Command.PreviousCell;
                                } else {
                                    //Not solution
                                    synchronized (exchange) {
                                        exchange.setThereIsNoDecision(true);
                                    }
//                                    exchange = null;
//                                    logger.write("solutionCount="+solutionCount+" return");
                                    return;
                                }
                            }
//                        } else {
//                            command = Command.PreviousCell;
//                        }
                    }
//                    logger.write("row="+row+" col="+col+" letter="+letter+" command="+command.toString());
                    break;
                }

                case PreviousCell: {
                    pos--;
                    row = pos / size;
                    col = pos % size;
                    if (pos >= 0) {
                        letter = desk.getCell(row, col);
                        command = Command.NextLetter;
                    } else {
                        //Not solution
                        synchronized (exchange) {
                            if (solutionCount == 1) {
                                exchange.setSolutionFound(true);
//                                logger.write("setSolutionFound=true");
                            } else {
                                exchange.setThereIsNoDecision(true);
//                                logger.write("setThereIsNoDecision=true");
                            }
                        }
//                        exchange = null;
                        return;
                    }
                    break;
                }
                default: {
//                    System.out.println("Что за команда: " + command + "?");
                    synchronized (exchange) {
                        exchange.setThereIsNoDecision(true);
                    }
//                    exchange = null;
                    return;
                }
            }
        }
    }

    boolean check(int row, int col, int letter) {
        desk.setCell(row, col, letter);
        // Проверка сверху
        int cnt = letter == 0 ? spaceCount : 1;
        int h = game.getHeader(Desk.SIDEUP, col);
        boolean checkH = h > 0;
        for (int line = 0; line <= row; line++) {
            int l = desk.getCell(line, col);
            //Повторение
            if (line < row) {
                if (l == letter) {
                    if (--cnt <= 0) {
                        return false;
                    }
                }
            }
            //Равенство заголовку
            if (checkH) {
                if (l > 0) {
                    if (l == h) {
                        checkH = false;
                    } else {
                        return false;
                    }
                }
            }
        }

        //Проверка слева
        cnt = letter == 0 ? spaceCount : 1;
        h = game.getHeader(Desk.SIDELEFT, row);
        checkH = h > 0;
        for (int pos = 0; pos <= col; pos++) {
            int l = desk.getCell(row, pos);
            if (pos < col) {
                if (l == letter) {
                    if (--cnt <= 0) {
                        return false;
                    }
                }
            }
            if (checkH) {
                if (l > 0) {
                    if (l == h) {
                        checkH = false;
                    } else {
                        return false;
                    }
                }
            }
        }

        //Проверка снизу
        if (row == size - 1) {
            h = game.getHeader(Desk.SIDEDOWN, col);
            checkH = h > 0;
            if (checkH) {
                for (int line = size - 1; line >= 0; line--) {
                    int l = desk.getCell(line, col);
                    //Равенство заголовку
                    if (l > 0) {
                        if (l == h) {
                            break;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }

        //Проверка справа
        if (col == size - 1) {
            h = game.getHeader(Desk.SIDERIGHT, row);
            checkH = h > 0;
            if (checkH) {
                for (int pos = size - 1; pos >= 0; pos--) {
                    int l = desk.getCell(row, pos);
                    //Равенство заголовку
                    if (l > 0) {
                        if (l == h) {
                            break;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}

enum Command {
    NextLetter,
    NextCell,
    PreviousCell,
    Check
}