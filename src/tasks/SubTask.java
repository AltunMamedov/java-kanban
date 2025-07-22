package tasks;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String startTimeStr = getStartTime() != null ? getStartTime().toString() : "";
        String durationStr = getDuration() != null ? getDuration().toString() : "";
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                getId(),
                TaskType.SUBTASK,
                getName(),
                getStatus(),
                getDescription(),
                getEpicId(),
                startTimeStr,
                durationStr
        );
    }

}