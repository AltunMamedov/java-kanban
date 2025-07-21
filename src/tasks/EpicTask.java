package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class EpicTask extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration = Duration.ZERO;

    public EpicTask(String name, String description) {
        super(name, description, Status.NEW);
    }

    public EpicTask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskIds;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public void addSubtaskId(int id) {
        if (this.getId() == id) {
            return;
        }
        subtaskIds.add(id);
    }

    public void deleteSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void deleteAllSubTasks() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,,%s,%s,%s",
                getId(),
                TaskType.EPIC,
                getName(),
                getStatus(),
                getDescription(),
                startTime,
                duration,
                endTime
        );
    }

}
