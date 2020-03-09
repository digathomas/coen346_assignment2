public class Process extends Thread{

    private int startingTime;
    private int remainingTime;
    private int waitingTime;
    private int allowedTime;

    public Process(int newStartingTime, int newRemainingTime) {
        startingTime = newStartingTime;
        remainingTime = newRemainingTime;
        waitingTime = 0;
        allowedTime = (int) Math.ceil(remainingTime * 0.1); // rounded up for no 0
    }

    public int getStartingTime() {
        return startingTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getAllowedTime() {
        return allowedTime;
    }

    public Boolean isFinished() {
        if (remainingTime > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void addWaitingTime(int time) {
        waitingTime = waitingTime + time;
    }

    public void run() {
        this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // rounded up for no 0;
        remainingTime = remainingTime - allowedTime;
    }
}