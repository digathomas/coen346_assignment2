import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RoundRobinScheduler {

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
            List<Process> running = new ArrayList<Process>(); // max size of 1
            List<Process> ready = new ArrayList<Process>(); // max size of numProcess - numCpu
            // List<Process> waiting = new ArrayList<Process>(); // max size of numProcess
            List<Process> terminated = new ArrayList<Process>(); // max size of numProcess

            for (int i = 0; i < numProcess; i++) {
                ready.add(new Process(tempArray.get(i)[0], tempArray.get(i)[1], 1));
            }

            for (int i = 0; i < numProcess; i++) {
                move(ready, running);
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Started\n");

                //PAUSE THIS THREAD
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Resumed\n");
                running.get(0).run();
                time++;
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Paused\n");
                //RESUME THIS TREAD

                if (running.get(0).isFinished()) {
                    writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Finished\n");
                    move(running, terminated);
                } else {
                    move(running, ready);
                }
            }

            writer.write("\n");

            while (terminated.size() != numProcess) {
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

                //PAUSE THIS THREAD
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Resumed\n");
                running.get(0).run();
                time++;
                writer.write("Time " + time + ", Process " + running.get(0).getStartingTime() + ", Paused\n");
                //RESUME THIS TREAD

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