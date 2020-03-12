import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Process implements Runnable {

    private double startingTime;
    private double remainingTime;
    private double starvingTime;
    private double waitingTime;
    private double allowedTime;
    private Semaphore cpuAccess;
    private Thread thread;

    public Process(double newStartingTime, double newRemainingTime, Semaphore newCpuAccess) {
        this.startingTime = newStartingTime;
        this.remainingTime = newRemainingTime;
        this.starvingTime = 0;
        this.waitingTime = 0;
        this.cpuAccess = newCpuAccess;
        this.setAllowedTimeRounded();
        this.thread = new Thread(new Runnable() {
            public void run() {
                // Lock cpu access
                while (!cpuAccess.tryAcquire()) {
                }
                ;
                System.out.print("Thread execution\n");
                // Unlock cpu access
                cpuAccess.release();
            }
        });
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

    private void setAllowedTime() {
        if (this.remainingTime < 0.5) {
            // Prevent too much overhead with the context switches
            // Prevent from infinite loop.
            this.allowedTime = this.remainingTime; // Finish this process
        } else {
            this.allowedTime = this.remainingTime * 0.1; // 10% of remaining time
        }
    }

    private void setAllowedTimeRounded() {
        this.allowedTime = (int) Math.ceil(remainingTime * 0.1); // TA instruction: to not use ints such as the example
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
        // Reset starving time
        this.starvingTime = 0;

        // Compute allowed time
        this.setAllowedTimeRounded();

        // Check if process finishes before allowed time
        if (this.remainingTime < this.allowedTime) {
            this.allowedTime = this.remainingTime;
        }

        // Update remaining time
        this.remainingTime -= this.allowedTime;

        // Unlock Cpu access
        this.cpuAccess.release();

        // Resume and Pause thread
        this.runThread();

        // Lock scheduler until thread finishes
        while (!this.cpuAccess.tryAcquire()) {
        }
        ;
    }

    void runThread() {
        try {
            // thread.start() // Instantiating the thread once
            thread.run(); // Continues execution of thread where it left off
            TimeUnit.SECONDS.sleep((long) this.getAllowedTime()); // Allows the thread to run for it's allowed time
            Thread.sleep(100); // Pauses thread if not finished yet
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}