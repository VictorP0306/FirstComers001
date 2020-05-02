public class Exchange {
    private volatile boolean stopSolution = false;
    private volatile boolean solutionFound = false;
    private volatile boolean thereIsNoDecision = false;
    private volatile int progress = 0;
    private FirstComers game;
    private int countSolution = 1;
    private Desk desk;

    Exchange(FirstComers game, int countSolution) {
        this.game = game;
        this.countSolution = countSolution;
    }

    public boolean isStopSolution() {
        return stopSolution;
    }
    public void setStopSolution(boolean stopSolution) {
        this.stopSolution = stopSolution;
    }
    public boolean isSolutionFound() {
        return solutionFound;
    }
    public void setSolutionFound(boolean solutionFound) {
        this.solutionFound = solutionFound;
    }
    public boolean isThereIsNoDecision() {
        return thereIsNoDecision;
    }
    public void setThereIsNoDecision(boolean thereIsNoDecision) {
        this.thereIsNoDecision = thereIsNoDecision;
    }
    public boolean getStopSolution() {
        return solutionFound || thereIsNoDecision;
    }
    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public FirstComers getGame() {
        return game;
    }
    public int getCountSolution() {
        return countSolution;
    }
    public Desk getDesk() {
        return desk;
    }
    public void setDesk(Desk desk) {
        this.desk = desk;
    }
}
