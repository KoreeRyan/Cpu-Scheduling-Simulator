package model;

import java.util.List;
import java.util.Map;

public class ScheduleResult {
    private final List<ScheduleSlice> timeline;
    private final Map<String, PerProcessMetrics> metricsByPid;

    private final double avgWaitingTime;
    private final double avgTurnaroundTime;
    private final double avgResponseTime;

    public ScheduleResult(List<ScheduleSlice> timeline,
                          Map<String, PerProcessMetrics> metricsByPid,
                          double avgWaitingTime,
                          double avgTurnaroundTime,
                          double avgResponseTime) {
        this.timeline = timeline;
        this.metricsByPid = metricsByPid;
        this.avgWaitingTime = avgWaitingTime;
        this.avgTurnaroundTime = avgTurnaroundTime;
        this.avgResponseTime = avgResponseTime;
    }

    public List<ScheduleSlice> getTimeline() {
        return timeline;
    }

    public Map<String, PerProcessMetrics> getMetricsByPid() {
        return metricsByPid;
    }

    public double getAvgWaitingTime() {
        return avgWaitingTime;
    }

    public double getAvgTurnaroundTime() {
        return avgTurnaroundTime;
    }

    public double getAvgResponseTime() {
        return avgResponseTime;
    }
}
