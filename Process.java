public class Process {

    private int startingTime;
    private int remainingTime;
    private int waitingTime;
    private int allowedTime;

    public Process(int newStartingTime, int newRemainingTime, int newAllowedTime) {
        this.startingTime = newStartingTime;
        this.remainingTime = newRemainingTime;
        this.waitingTime = 0;
        this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // rounded up for no 0
    }

    public void setStartingTime(int newStartingTime) {
        this.startingTime = newStartingTime;
    }

    public void setRemainingTime(int newRemainingTime) {
        this.remainingTime = newRemainingTime;
    }

    public void setWaitingTime(int newWaitingTime) {
        this.waitingTime = newWaitingTime;
    }

    public void setAllowedTime() {
        this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // rounded up for no 0;
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

    public void run() {
        remainingTime--;
    }
}