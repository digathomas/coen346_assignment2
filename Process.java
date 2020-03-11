public class Process implements Runnable {

    private Boolean cpuAccess;
    private double startingTime;
    private double remainingTime;
    private double starvingTime;
    private double waitingTime;
    private double allowedTime;

    public Process(double newStartingTime, double newRemainingTime) {
        this.cpuAccess = false;
        this.startingTime = newStartingTime;
        this.remainingTime = newRemainingTime;
        this.starvingTime = 0;
        this.waitingTime = 0;
        //this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // TA instruction: to not use ints like in the example
        this.allowedTime = this.remainingTime * 0.1; // 10% of remaining time
    }

    public Boolean getCpuAccess() {
        return this.cpuAccess;
    }

    public double getStartingTime() {
        return this.startingTime;
    }

    public double getRemainingTime() {
        return this.remainingTime;
    }

    public double getStarvingTime() {
        return this.starvingTime;
    }

    public double getWaitingTime() {
        return this.waitingTime;
    }

    public double getAllowedTime() {
        return this.allowedTime;
    }

    public Boolean isFinished() {
        if (this.remainingTime > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void addWaitingTime(double time) {
        this.waitingTime += time;
        this.starvingTime += time;
    }

    public void run() {

        // Lock cpu access
        this.cpuAccess = true;

        // Reset starving time
        this.starvingTime = 0;

        // Compute allowed time
        //this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // TA instruction: to not use ints like in the example
        if (this.remainingTime < 0.5) {
            // Prevent too much overhead with the context switches
            // Prevent from infinite loop.
            this.allowedTime = this.remainingTime; // Finish this process
        } 
        else {
            this.allowedTime = this.remainingTime * 0.1; // 10% of remaining time
        }

        // Check if process finishes before allowed time
        if (this.remainingTime < this.allowedTime) {
            this.allowedTime = this.remainingTime;
        }

        // Update remaining time
        this.remainingTime -= this.allowedTime;

        for(int i = 0; i < 25; i++){
            System.out.print(i + ". PROCESS\n");
        }

        // Unlock cpu access
        this.cpuAccess = false;
    }
}