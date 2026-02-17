package algorithms;

import model.PerProcessMetrics;
import model.Process;
import model.ScheduleResult;
import model.ScheduleSlice;

import java.util.*;

public class PriorityScheduler {

    public ScheduleResult schedule(List<Process> input) {
        List<Process> remaining = new ArrayList<>();
        for (Process p : input) {
            remaining.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));
        }

        List<Process> ready = new ArrayList<>();
        List<ScheduleSlice> timeline = new ArrayList<>();
        Map<String, PerProcessMetrics> metrics = new LinkedHashMap<>();

        int time = 0;
        int n = remaining.size();

        while (metrics.size() < n) {

            Iterator<Process> it = remaining.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.getArrivalTime() <= time) {
                    ready.add(p);
                    it.remove();
                }
            }

            if (ready.isEmpty()) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : remaining) {
                    nextArrival = Math.min(nextArrival, p.getArrivalTime());
                }

                timeline.add(new ScheduleSlice("IDLE", time, nextArrival));

                time = nextArrival;
                continue;
            }

            //Pick the highest priority (lowest number wins)
            //Tie-break: arrival, then pid
            Process chosen = ready.get(0);
            for (Process p : ready) {
                if (p.getPriority() <= chosen.getPriority()) {
                    chosen = p;
                } else if (p.getPriority() == chosen.getPriority()) {
                    if (p.getArrivalTime() < chosen.getArrivalTime()) {
                        chosen = p;
                    } else if (p.getArrivalTime() == chosen.getArrivalTime()
                            && p.getPid().compareTo(chosen.getPid())< 0) {
                        chosen = p;
                    }
                }
            }

            //Run to completion
            int start = time;
            int end = time + chosen.getBurstTime();
            timeline.add(new ScheduleSlice(chosen.getPid(), start, end));

            //Metrics
            int completionTime = end;
            int turnaroundTime = completionTime - chosen.getArrivalTime();
            int waitingTime = turnaroundTime - chosen.getBurstTime();
            int responseTime = start - chosen.getArrivalTime();

            metrics.put(chosen.getPid(),
                    new PerProcessMetrics(start, completionTime, turnaroundTime, waitingTime, responseTime));

            ready.remove(chosen);
            time = end;

        }

        double avgWT = metrics.values().stream().mapToInt(PerProcessMetrics::getWaitingTime).average().orElse(0.0);
        double avgTAT = metrics.values().stream().mapToInt(PerProcessMetrics::getTurnaroundTime).average().orElse(0.0);
        double avgRT = metrics.values().stream().mapToInt(PerProcessMetrics::getResponseTime).average().orElse(0.0);

        return new ScheduleResult(timeline, metrics, avgWT, avgTAT, avgRT);
    }
}
