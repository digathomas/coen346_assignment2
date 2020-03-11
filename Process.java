public class Process implements Runnable {

    private Boolean cpuAccess;
    private int startingTime;
    private int remainingTime;
    private int starvingTime;
    private int waitingTime;
    private int allowedTime;

    public Process(int newStartingTime, int newRemainingTime) {
        cpuAccess = false;
        startingTime = newStartingTime;
        remainingTime = newRemainingTime;
        starvingTime = 0;
        waitingTime = 0;
        allowedTime = (int) Math.ceil(remainingTime * 0.1); // rounded up for no 0
    }

    public Boolean getCpuAccess() {
        return cpuAccess;
    }

    public int getStartingTime() {
        return startingTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getStarvingTime() {
        return starvingTime;
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
        waitingTime += time;
        starvingTime += time;
    }

    public void run() {

        // Lock cpu access
        cpuAccess = true;

        // Reset starving time
        this.starvingTime = 0;

        // Compute allowed time
        this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // rounded up for no 0
        
        // Check if process finishes before allowed time
        if (this.remainingTime < this.allowedTime) {
            this.allowedTime = this.remainingTime;
        }

        // Update remaining time
        remainingTime -= allowedTime;

        // Unlock cpu access
        cpuAccess = false;
    }
}