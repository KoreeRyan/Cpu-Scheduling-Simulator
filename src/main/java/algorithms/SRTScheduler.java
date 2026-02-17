package algorithms;

import model.PerProcessMetrics;
import model.Process;
import model.ScheduleResult;
import model.ScheduleSlice;

import java.util.*;

public class SRTScheduler {

    public ScheduleResult schedule(List<Process> input) {
        List<Process> all = new ArrayList<>();
        for (Process p : input) {
            all.add(new Process(p.getPid(), p.getArrivalTime(), p.getBurstTime(), p.getPriority()));
        }

        all.sort(Comparator.comparingInt(Process::getArrivalTime).thenComparing(Process::getPid));

        List<ScheduleSlice> timeline = new ArrayList<>();
        Map<String, Integer> firstStart = new HashMap<>();
        Map<String, Integer> completion = new HashMap<>();

        int n = all.size();
        int time = 0;

        while (completion.size() < n) {
            Process chosen = null;
            for (Process p : all) {
                if (p.getArrivalTime() <= time && !p.isFinished()) {
                    if (chosen == null) {
                        chosen = p;
                    } else {
                        int pr = p.getRemainingTime();
                        int cr = chosen.getRemainingTime();
                        if (pr < cr) {
                            chosen = p;
                        } else if (pr == cr) {
                            if (p.getArrivalTime() < chosen.getArrivalTime()) {
                                chosen = p;
                            } else if (p.getArrivalTime() == chosen.getArrivalTime()
                                    && p.getPid().compareTo(chosen.getPid()) < 0) {
                                chosen = p;
                            }
                        }
                    }
                }
            }

            if (chosen == null) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : all) {
                    if (!p.isFinished()) {
                        nextArrival = Math.min(nextArrival, p.getArrivalTime());
                    }
                }

                if (nextArrival == Integer.MAX_VALUE) {
                    System.out.println("=== DEBUG: end-state check ===");
                    System.out.println("time=" + time + " completion.size=" + completion.size() + " n=" + n);

                    for (Process p : all) {
                        System.out.println(
                                p.getPid()
                                        + " finished=" + p.isFinished()
                                        + " remaining=" + p.getRemainingTime()
                                        + " recorded=" + completion.containsKey(p.getPid())
                        );
                    }

                    throw new IllegalStateException(
                            "All processes finished but completion map is missing entries."
                    );
                }



                addOrExtendSlice(timeline, "IDLE", time, nextArrival);
                time = nextArrival;
                continue;
            }


            firstStart.putIfAbsent(chosen.getPid(), time);

            addOrExtendSlice(timeline, chosen.getPid(), time, time + 1);
            chosen.runForOneUnit();
            time++;


            if (chosen.isFinished()) {
                completion.putIfAbsent(chosen.getPid(), time);
            }
        }

        Map<String, PerProcessMetrics> metrics = new LinkedHashMap<>();
        double sumWT = 0, sumTAT = 0, sumRT = 0;

        List<Process> byPid = new ArrayList<>(all);
        byPid.sort(Comparator.comparing(Process::getPid));

        for (Process p : byPid) {
            String pid = p.getPid();

            Integer ctObj = completion.get(pid);
            if (ctObj == null) {
                throw new IllegalStateException("Missing completion time for " + pid);
            }

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

        return new ScheduleResult(
                timeline,
                metrics,
                sumWT / n,
                sumTAT / n,
                sumRT / n
        );
    }


    private static void addOrExtendSlice(List<ScheduleSlice> timeline, String pid, int start, int end) {
        if (timeline.isEmpty()) {
            timeline.add(new ScheduleSlice(pid, start, end));
            return;
        }

        ScheduleSlice last = timeline.get(timeline.size() - 1);
        if (last.getPid().equals(pid) && last.getEnd() == start) {
            timeline.set(timeline.size() - 1, new ScheduleSlice(pid, last.getStart(), end));
        } else {
            timeline.add(new ScheduleSlice(pid, start, end));
        }
    }
}
