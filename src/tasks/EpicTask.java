package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;
    public EpicTask(String name, String description) {
        super(name, description, Status.NEW);
    }

    public EpicTask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskIds;
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        String startTimeStr = getStartTime() != null ? getStartTime().toString() : "";
        String durationStr = getDuration() != null ? getDuration().toString() : "";
        LocalDateTime endTime = getEndTime();
        String endTimeStr = endTime != null ? endTime.toString() : "";
        return String.format("%d,%s,%s,%s,%s,,%s,%s,%s",
                getId(),
                TaskType.EPIC,
                getName(),
                getStatus(),
                getDescription(),
                startTimeStr,
                durationStr,
                endTimeStr
        );
    }
}
