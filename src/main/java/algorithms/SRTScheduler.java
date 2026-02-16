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


    }
}
