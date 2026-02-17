package algorithms;

import model.PerProcessMetrics;
import model.Process;
import model.ScheduleResult;
import model.ScheduleSlice;

import java.util.*;

public class RRScheduler {

    public ScheduleResult schedule(List<Process> input, int quantum) {
        if (quantum <= 0) throw new IllegalArgumentException("quantum must be positive");

        List<Process> all = new ArrayList<>();
        for (Process p : input) {
            all.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));
        }

        all.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparing(Process::getPid));

        Queue<Process> ready = new ArrayDeque<>();
        List<ScheduleSlice> timeline = new ArrayList<>();

        Map<String, Integer> firstStart = new HashMap<>();
        Map<String, Integer> completion = new HashMap<>();

        int n = all.size();
        int time = 0;
        int i = 0;

        while (completion.size() < n) {
            while (i < n && all.get(i).getArrivalTime() <= time) {
                ready.add(all.get(i));
                i++;
            }

            if (ready.isEmpty()) {
                if (i < n) {
                    int nextArrival = all.get(i).getArrivalTime();
                    addOrExtendSlice(timeline, "IDLE", time, nextArrival);
                    time = nextArrival;
                    continue;
                } else {
                    break;
                }
            }

            Process p = ready.poll();

            firstStart.putIfAbsent(p.getPid(), time);

            int runFor = Math.min(quantum, p.getRemainingTime());
            int start = time;
            int end = time + runFor;

            addOrExtendSlice(timeline, p.getPid(), start, end);

            for (int t = 0; t < runFor; t++) {
                p.runForOneUnit();
            }

            time = end;

            while (i < n && all.get(i).getArrivalTime() <= time) {
                ready.add(all.get(i));
                i++;
            }

            if (p.isFinished()) {
                completion.putIfAbsent(p.getPid(), time);
            } else {
                ready.add(p);
            }
        }

        Map<String, PerProcessMetrics> metrics = new LinkedHashMap<>();
        double sumWT = 0, sumTAT = 0, sumRT = 0;

        List<Process> byPid = new ArrayList<>(all);
        byPid.sort(Comparator.comparing(Process::getPid));

        for (Process p : byPid) {
            String pid = p.getPid();

            Integer ctObj = completion.get(pid);
            if (ctObj == null) throw new IllegalStateException("Missing completion time for " + pid);
            int ct = ctObj;

            int start = firstStart.getOrDefault(pid, p.getArrivalTime());
            int tat = ct - p.getArrivalTime();
            int wt = tat - p.getBurstTime();
            int rt = start - p.getArrivalTime();

            metrics.put(pid, new PerProcessMetrics(start, ct, tat, wt, rt));

            sumWT += wt;
            sumTAT += tat;
            sumRT += rt;
        }

        return new ScheduleResult(timeline, metrics, sumWT /n, sumTAT / n,  sumRT / n);

    }

    private static void addOrExtendSlice(List<ScheduleSlice> timeline, String pid, int start, int end) {
        if (start == end) return;

        if (timeline.isEmpty()) {
            timeline.add(new ScheduleSlice(pid, start, end));
            return;
        }
        ScheduleSlice last =  timeline.get(timeline.size() - 1);
        if (last.getPid().equals(pid) && last.getEnd() == start) {
            timeline.set(timeline.size() - 1, new ScheduleSlice(pid, last.getStart(), end));
        } else {
            timeline.add(new ScheduleSlice(pid, start, end));
        }
    }
}
