public class ThreadRun {
    public static void main(String[] args){

        Thread robin = new Thread(new RoundRobinScheduler(), "t1");
        System.out.print("Start Runnable Thread RoundRobinScheduler\n");
        robin.start();
        System.out.print("End Runnable Thread RoundRobinScheduler\n"); 
    }
}