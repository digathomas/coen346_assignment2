import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class RoundRobinScheduler implements Runnable {

    public static void main(String[] args) {
        try {
            System.out.print("Start Runnable Thread RoundRobinScheduler\n");
            Thread robin = new Thread(new RoundRobinScheduler(), "t1");
            robin.start();
            robin.join();
            System.out.print("End Runnable Thread RoundRobinScheduler\n");
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void run() {
        try {
            // Read input.txt
            Scanner inFile = new Scanner(new File("input.txt"));

            List<int[]> tempArray = new ArrayList<int[]>();
            while (inFile.hasNextLine()) {
                int temp[] = new int[2];
                temp[0] = inFile.nextInt();
                temp[1] = inFile.nextInt();

                tempArray.add(temp);
            }
            inFile.close();

            // Create all queues
            FileWriter writer = new FileWriter("output.txt", false); // clears file and appends
            writer.write("Round Robin Scheduler: \n\n");
            DecimalFormat df0 = new DecimalFormat("0"); // Display 0 decimal places in output.txt
            DecimalFormat df2 = new DecimalFormat("00"); // Display 2 decimal places in output.txt
            double time = 1;
            Semaphore cpuAccess = new Semaphore(1); // 1 available permit to be aquired
            int numProcess = tempArray.size();
            List<Process> arrival = new ArrayList<Process>(); // max size: numProcess // new key word can't be used
            List<Process> ready = new ArrayList<Process>(); // max size: numProcess - numCpu
            // List<Process> veryready = new ArrayList<Process>();
            List<Process> running = new ArrayList<Process>(); // max size: 1, single-core
            // List<Process> waiting = new ArrayList<Process>(); // max size: numProcess //
            // not used because no I/O
            List<Process> terminated = new ArrayList<Process>(); // max size: numProcess

            // Create all processes
            for (int i = 0; i < numProcess; i++) {
                arrival.add(new Process(tempArray.get(i)[0], tempArray.get(i)[1], cpuAccess));
            }

            while (terminated.size() < numProcess) {

                // Check for arrivals
                if (arrival.size() != 0) {
                    int indexSoonestStartingTime = 0;
                    double soonestStartingTime = arrival.get(indexSoonestStartingTime).getStartingTime();
                    for (int i = 0; i < arrival.size(); i++) {
                        double tempSoonestStartingTime = arrival.get(i).getStartingTime();
                        if (tempSoonestStartingTime < soonestStartingTime) {
                            soonestStartingTime = tempSoonestStartingTime;
                            indexSoonestStartingTime = i;
                        }
                    }
                    move(arrival, running, indexSoonestStartingTime);
                    // Changed instruction format to clearer table format
                    writer.write("[Time " + df2.format(time) + "]\t\t[Process "
                            + df0.format(running.get(0).getStartingTime()) + "]\t[Started]\t[Remaining "
                            + df2.format(running.get(0).getRemainingTime()) + "]\t[Starved "
                            + df2.format(running.get(0).getStarvingTime()) + "]\t\t---- start ----\n");
                }

                // Check for ready processes
                // Find shortest remaining time through processes in ready queue with it's
                // starvation time in mind
                // https://softwareengineering.stackexchange.com/questions/324742/how-to-properly-deal-with-starvation
                else if (ready.size() != 0) {
                    int indexNextPriority = 0;
                    // These constants depend on the context, vary them to the needed use case
                    // A bigger constant gives more importance to starvation and less importance to
                    // shortest remaining time

                    // Controls starvation's weight against remaining time, grows linearly
                    double starvationConstant = 0.001;
                    // Controls starvation's increase in weight as process gets older in ready
                    // queue, grows exponentinally
                    double starvationBase = 5;

                    double nextPriority = starvationConstant
                            * Math.pow(starvationBase, ready.get(indexNextPriority).getStarvingTime())
                            + (1 / ready.get(indexNextPriority).getRemainingTime());

                    for (int i = 0; i < ready.size(); i++) {
                        double tempNextPriority = starvationConstant
                                * Math.pow(starvationBase, ready.get(i).getStarvingTime())
                                + (1 / ready.get(i).getRemainingTime());
                        if (tempNextPriority > nextPriority) {
                            nextPriority = tempNextPriority;
                            indexNextPriority = i;
                        }
                    }
                    move(ready, running, indexNextPriority);
                }

                // Resumed process
                writer.write(
                        "[Time " + df2.format(time) + "]\t\t[Process " + df0.format(running.get(0).getStartingTime())
                                + "]\t[Resumed]\t[Remaining " + df2.format(running.get(0).getRemainingTime())
                                + "]\t[Starved " + df2.format(running.get(0).getStarvingTime()) + "]\n");

                // Run current process
                running.get(0).run();

                // Manage time of scheduler and processes
                time = time + running.get(0).getAllowedTime();
                for (int j = 0; j < arrival.size(); j++) {
                    arrival.get(j).addWaitingTime(running.get(0).getAllowedTime());
                }
                for (int j = 0; j < ready.size(); j++) {
                    ready.get(j).addWaitingTime(running.get(0).getAllowedTime());
                }

                // Paused process
                writer.write(
                        "[Time " + df2.format(time) + "]\t\t[Process " + df0.format(running.get(0).getStartingTime())
                                + "]\t[Paused]\t[Remaining " + df2.format(running.get(0).getRemainingTime())
                                + "]\t[Starved " + df2.format(running.get(0).getStarvingTime()) + "]\n");

                // Finished process
                if (running.get(0).isFinished()) {
                    writer.write("[Time " + df2.format(time) + "]\t\t[Process "
                            + df0.format(running.get(0).getStartingTime()) + "]\t[Finished]\t[Remaining "
                            + df2.format(running.get(0).getRemainingTime()) + "]\t[Starved "
                            + df2.format(running.get(0).getStarvingTime()) + "]\t\t---- end ----\n");
                    move(running, terminated);
                }
                // Interrupted process
                else {
                    move(running, ready);
                }

                // Check running queue size
                if (running.size() != 0) {
                    writer.write("\n");
                }
            }

            // Print output.txt
            writer.write("--------------\n");
            writer.write("Waiting times: \n");
            while (terminated.size() != 0) {
                int indexSoonestStartingTime = 0;
                double soonestStartingTime = terminated.get(indexSoonestStartingTime).getStartingTime();
                for (int i = 0; i < terminated.size(); i++) {
                    double tempSoonestStartingTime = terminated.get(i).getStartingTime();
                    if (tempSoonestStartingTime < soonestStartingTime) {
                        soonestStartingTime = tempSoonestStartingTime;
                        indexSoonestStartingTime = i;
                    }
                }
                writer.write("Process " + df0.format(terminated.get(indexSoonestStartingTime).getStartingTime()) + ": "
                        + df2.format(terminated.get(indexSoonestStartingTime).getWaitingTime()) + "\n");
                terminated.remove(indexSoonestStartingTime);
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Move front of queue to back of queue
    public static void move(List<Process> departure, List<Process> destination) {
        destination.add(departure.get(0));
        departure.remove(0);
    }

    // Move at index of queue to back of queue
    public static void move(List<Process> departure, List<Process> destination, int index) {
        destination.add(departure.get(index));
        departure.remove(index);
    }
}