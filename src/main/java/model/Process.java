package model;

public class Process {
    private final String pid;
    private final int arrivalTime;
    private final int burstTime;
    private final int priority;

    private int remainingTime;

    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        if (pid == null || pid.isBlank()) throw new IllegalArgumentException("pid is required");
        if (arrivalTime < 0) throw new IllegalArgumentException("arrivalTime must be >= 0");
        if (burstTime <= 0) throw new IllegalArgumentException("burstTime must be > 0");

        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;

        this.remainingTime = arrivalTime - burstTime;
    }
    public String getPid() {
        return pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean isFinished() {
        return remainingTime <= 0;
    }

    public void runForOneUnit() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public void reset() {
        this.remainingTime = this.burstTime;
    }
    @Override
    public String toString() {
        return "Process{" +
                "pid='" + pid + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", burstTime=" + burstTime +
                ", priority=" + priority +
                ", remainingTime=" + remainingTime +
                '}';

    }
}
