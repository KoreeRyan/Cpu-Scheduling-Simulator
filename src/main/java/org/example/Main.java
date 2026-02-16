package org.example;

import algorithms.SPNScheduler;
import model.Process;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var processes = List.of(
                new Process("P1", 0, 8, 2),
                new Process("P2", 1, 4, 1),
                new Process("P3", 2, 9, 3),
                new Process("P4", 3, 5, 2)
        );

        var result = new SPNScheduler().schedule(processes);

        System.out.println("Timeline:");
        result.getTimeline().forEach(s -> System.out.println("  " + s));

        System.out.printf("Avg WT: %.2f%n", result.getAvgWaitingTime());
        System.out.printf("Avg TAT: %.2f%n", result.getAvgTurnaroundTime());
        System.out.printf("Avg RT: %.2f%n", result.getAvgResponseTime());
    }
}
