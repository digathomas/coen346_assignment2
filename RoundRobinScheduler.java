import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RoundRobinScheduler extends Thread {

    public static void main(String[] args) {
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

            // Print input.txt
            System.out.print("input.txt\n");
            for (int i = 0; i < tempArray.size(); i++) {
                System.out.print(tempArray.get(i)[0] + "\t");
                System.out.print(tempArray.get(i)[1] + "\n");
            }

            // Create all queues
            FileWriter writer = new FileWriter("output.txt", false); // clears file and appends
            writer.write("Round Robin Scheduler: \n\n");
            int time = 1;
            int numProcess = tempArray.size();
            List<Process> arrival = new ArrayList<Process>(); // max size: numProcess
            List<Process> ready = new ArrayList<Process>(); // max size: numProcess - numCpu
            // List<Process> veryready = new ArrayList<Process>();
            List<Process> running = new ArrayList<Process>(); // max size: 1, single-core
            // List<Process> waiting = new ArrayList<Process>(); // max size: numProcess
            List<Process> terminated = new ArrayList<Process>(); // max size: numProcess

            // Create all processes
            for (int i = 0; i < numProcess; i++) {
                arrival.add(new Process(tempArray.get(i)[0], tempArray.get(i)[1]));
            }

            while (terminated.size() < numProcess) {

                // Check for arrivals
                if (arrival.size() != 0) {
                    int indexSoonestStartingTime = 0;
                    int soonestStartingTime = arrival.get(indexSoonestStartingTime).getStartingTime();
                    for (int i = 0; i < arrival.size(); i++) {
                        if (arrival.get(i).getStartingTime() < soonestStartingTime) {
                            soonestStartingTime = arrival.get(i).getStartingTime();
                            indexSoonestStartingTime = i;
                        }
                    }
                    move(arrival, running, indexSoonestStartingTime);
                    writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Started\n");
                }

                // Check for ready processes
                // Find shortest remaining time through processes in ready queue
                else if (ready.size() != 0) {
                    int indexShortestRemainingTime = 0;
                    int shortestRemainingTime = ready.get(indexShortestRemainingTime).getRemainingTime();
                    for (int i = 0; i < ready.size(); i++) {
                        if (ready.get(i).getRemainingTime() < shortestRemainingTime) {
                            shortestRemainingTime = ready.get(i).getRemainingTime();
                            indexShortestRemainingTime = i;
                        }
                    }
                    move(ready, running, indexShortestRemainingTime);
                }

                // Resumed process
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Resumed\n");

                // PAUSE THIS THREAD
                running.get(0).run();
                // RESUME THIS TREAD

                // Manage time of scheduler and processes
                time = time + running.get(0).getAllowedTime();
                for (int j = 0; j < arrival.size(); j++) {
                    arrival.get(j).addWaitingTime(running.get(0).getAllowedTime());
                }
                for (int j = 0; j < ready.size(); j++) {
                    ready.get(j).addWaitingTime(running.get(0).getAllowedTime());
                }

                // Paused process
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Paused\n");

                // Finished process
                if (running.get(0).isFinished()) {
                    writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Finished\n");
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
                int soonestStartingTime = terminated.get(indexSoonestStartingTime).getStartingTime();
                for (int i = 0; i < terminated.size(); i++) {
                    if (terminated.get(i).getStartingTime() < soonestStartingTime) {
                        soonestStartingTime = terminated.get(i).getStartingTime();
                        indexSoonestStartingTime = i;
                    }
                }
                writer.write("Process " + terminated.get(indexSoonestStartingTime).getStartingTime() + ": "
                        + terminated.get(indexSoonestStartingTime).getWaitingTime() + "\n");
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