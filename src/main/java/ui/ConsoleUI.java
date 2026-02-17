package ui;

import algorithms.*;
import model.Process;
import model.ScheduleResult;

import ui.PlantUMLGanttExporter;
import java.nio.file.Path;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    public void run() {
        Scanner scanner = new Scanner(System.in);

        int n = readInt(scanner, "Enter the number of processes: ", 1, 10_000);

        List<Process> processes = new ArrayList<>();
        System.out.println("Enter the information of each process below ..");

        for (int i = 1; i <= n; i++) {
            System.out.println("Process " + i + ":");
            int arrival = readInt(scanner, "Arrival Time: ", 0, 1_000_000);
            int burst = readInt(scanner, "Burst Time: ", 1, 1_000_000);
            int priority = readInt(scanner, "Priority: ", Integer.MIN_VALUE, Integer.MAX_VALUE);

            processes.add(new Process("P" + i, arrival, burst, priority));
        }

        System.out.println();
        System.out.println("Select CPU Scheduling Algorithm: ");
        System.out.println("1. First-Come, First-Served (FCFS)");
        System.out.println("2. Shortest Process Next (SPN)");
        System.out.println("3. Shortest Remaining Time (SRT)");
        System.out.println("4. Round Robin (RR)");
        System.out.println("5. Priority Scheduling");

        int choice = readInt(scanner, "Enter your choice (1-5): ", 1, 5);

        ScheduleResult result;
        switch (choice) {
            case 1 -> result = new FCFSScheduler().schedule(processes);
            case 2 -> result = new SPNScheduler().schedule(processes);
            case 3 -> result = new SRTScheduler().schedule(processes);
            case 4 -> {
                int quantum = readInt(scanner, "Enter time quantum: ", 1, 1_000_000);
                result = new RRScheduler().schedule(processes, quantum);
            }
            case 5 -> result = new PriorityScheduler().schedule(processes);
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        }
        try {
            PlantUMLGanttExporter.export(result, java.nio.file.Path.of("out", "schedule.puml"));
            System.out.println("PlantUML file written to out/schedule.puml");
        } catch (Exception e) {
            System.out.println("PlantUML export failed: " + e.getMessage());
        }


        System.out.println();
        printResult(result);
    }

    private void printResult(ScheduleResult result) {
        System.out.println("Gantt Chart Visualization:");

        result.getTimeline().forEach(s -> System.out.println(" " + s));

        printAsciiGantt(result);

        System.out.println("Performance Metrics:");
        System.out.printf("Average Waiting Time: %.2f%n", result.getAvgWaitingTime());
        System.out.printf("Average Turnaround Time: %.2f%n", result.getAvgTurnaroundTime());
        System.out.printf("Average Response Time: %.2f%n", result.getAvgResponseTime());
    }

    private void printAsciiGantt(ScheduleResult result) {
        var slices = result.getTimeline();

        if (slices.isEmpty()) {
            System.out.println("(no timeline)");
            return;
        }

        System.out.println("\nGantt Chart (ASCII):");

        // top bar
        StringBuilder bar = new StringBuilder();
        for (var s : slices) {
            bar.append("| ")
                    .append(center(s.getPid(), 4))
                    .append(" ");
        }
        bar.append("|");
        System.out.println(bar);

        // time row
        StringBuilder times = new StringBuilder();
        times.append(slices.get(0).getStart());

        for (var s : slices) {
            times.append("      "); // spacing matches cell width
            times.append(s.getEnd());
        }

        System.out.println(times);
    }

    private String center(String text, int width) {
        if (text.length() >= width) return text;
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }



    private int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
