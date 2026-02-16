package model;

public class ScheduleSlice {
    private final String pid;
    private final int start;
    private final int end;

    public ScheduleSlice(String pid, int start, int end) {
        if (pid == null || pid.isBlank()) throw new IllegalArgumentException("pid is required");
        if (start < 0) throw new IllegalArgumentException("start must be >= 0");
        if (end < start) throw new IllegalArgumentException("end must be >= start");
        this.pid = pid;
        this.start = start;
        this.end = end;
    }

    public String getPid() {
        return pid;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }

    @Override
    public String toString() {
        return pid + "[" + start + "->" + end + "]";
    }
}
