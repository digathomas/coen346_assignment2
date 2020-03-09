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

            FileWriter writer = new FileWriter("output.txt", false); // clears file and appends
            writer.write("Round Robin Scheduler: \n\n");
            int time = 1;
            int numProcess = tempArray.size();
            // List<Process> arrival = new ArrayList<Process>(); // max size: numProcess
            List<Process> ready = new ArrayList<Process>(); // max size: numProcess - numCpu
            // List<Process> veryready = new ArrayList<Process>();
            List<Process> running = new ArrayList<Process>(); // max size: 1, single-core
            // List<Process> waiting = new ArrayList<Process>(); // max size: numProcess
            List<Process> terminated = new ArrayList<Process>(); // max size: numProcess

            for (int i = 0; i < numProcess; i++) {
                ready.add(new Process(tempArray.get(i)[0], tempArray.get(i)[1], 1));
            }

            for (int i = 0; i < numProcess; i++) {
                move(ready, running);
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Started\n");

                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Resumed\n");
                // PAUSE THIS THREAD

                running.get(0).run();

                // RESUME THIS TREAD
                time = time + running.get(0).getAllowedTime();
                for (int j = 0; j < ready.size(); j++) {
                    ready.get(j).addWaitingTime(running.get(0).getAllowedTime());
                }
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Paused\n");

                if (running.get(0).isFinished()) {
                    writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Finished\n");
                    move(running, terminated);
                } else {
                    move(running, ready);
                }
            }

            writer.write("--------------\n");

            while (terminated.size() < numProcess) {
                // Find shortest remaining time through processes in ready queue
                if (ready.size() != 0) {
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

                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Resumed\n");
                // PAUSE THIS THREAD

                running.get(0).run();

                // RESUME THIS TREAD
                time = time + running.get(0).getAllowedTime();
                for (int j = 0; j < ready.size(); j++) {
                    ready.get(j).addWaitingTime(running.get(0).getAllowedTime());
                }
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Paused\n");

                if (running.get(0).isFinished()) {
                    writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Finished\n");
                    move(running, terminated);
                } else {
                    move(running, ready);
                }
            }

            // Print output.txt
            writer.write("--------------\n");
            writer.write("Waiting times: \n");
            for (int i = 0; i < terminated.size(); i++) {
                writer.write("Process " + terminated.get(i).getStartingTime() + ": "
                        + terminated.get(i).getWaitingTime() + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void move(List<Process> departure, List<Process> destination) {
        destination.add(departure.get(0));
        departure.remove(0);
    }

    public static void move(List<Process> departure, List<Process> destination, int index) {
        destination.add(departure.get(index));
        departure.remove(index);
    }
}