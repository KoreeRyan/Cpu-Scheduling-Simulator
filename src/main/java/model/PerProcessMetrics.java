package model;

public class PerProcessMetrics {
    private final int startTime;
    private final int completionTime;
    private final int turnaroundTime;
    private final int waitingTime;
    private final int responseTime;

    public PerProcessMetrics(int startTime, int completionTime, int turnaroundTime, int waitingTime, int responseTime) {
        this.startTime = startTime;
        this.completionTime = completionTime;
        this.turnaroundTime = turnaroundTime;
        this.waitingTime = waitingTime;
        this.responseTime = responseTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getResponseTime() {
        return responseTime;
    }
}
