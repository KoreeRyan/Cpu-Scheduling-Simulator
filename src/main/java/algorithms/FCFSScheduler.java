package algorithms;

import model.PerProcessMetrics;
import model.Process;
import model.ScheduleResult;
import model.ScheduleSlice;

import java.util.*;

public class FCFSScheduler {

    public ScheduleResult schedule(List<Process> input) {
        //Copy + reset
        List<Process> processes = new ArrayList<>();
        for (Process p : input) {
            Process copy = new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority());
            processes.add(copy);
        }

        processes.sort(Comparator
                .comparingInt(Process::getArrivalTime)
                .thenComparing(Process::getPid));

        List<ScheduleSlice> timeline = new ArrayList<>();
        Map<String, PerProcessMetrics> metrics = new LinkedHashMap<>();

        int time = 0;

        for (Process p : processes) {
            //If CPU is idle before this process arrives, jump time
            if (time < p.getArrivalTime()) {
                timeline.add(new ScheduleSlice("IDLE", time, p.getArrivalTime()));
                time = p.getArrivalTime();
            }

            int start = time;
            int end = time + p.getBurstTime();

            timeline.add(new ScheduleSlice(p.getPid(), start, end));

            int completionTime = end;
            int turnaroundTime = completionTime - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBurstTime();
            int responseTime = start - p.getArrivalTime();

            metrics.put(p.getPid(),
                    new PerProcessMetrics(start, completionTime, turnaroundTime, waitingTime, responseTime));

            time = end;
        }

        //averages
        double avgWT = metrics.values().stream().mapToInt(PerProcessMetrics::getWaitingTime).average().orElse(0.0);
        double avgTAT = metrics.values().stream().mapToInt(PerProcessMetrics::getTurnaroundTime).average().orElse(0.0);
        double avgRT = metrics.values().stream().mapToInt(PerProcessMetrics::getResponseTime).average().orElse(0.0);

        return new ScheduleResult(timeline, metrics, avgWT, avgTAT, avgRT);
    }
}
