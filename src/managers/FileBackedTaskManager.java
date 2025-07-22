package managers;

import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epicId,startTime,duration\n");
            for (Task task : tasks.values()) {
                writer.write(toStringForFile(task) + "\n");
            }

            for (EpicTask epic : epics.values()) {
                writer.write(toStringForFile(epic) + "\n");
            }

            for (SubTask subtask : subtasks.values()) {
                writer.write(toStringForFile(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла: " + file.getName(), e);
        }
    }

    private String toStringForFile(Task task) {
        String epicId = "";
        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        String duration = task.getDuration() != null ? task.getDuration().toString() : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                TaskType.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                startTime,
                duration);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            int maxId = 0;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);

                if (task instanceof SubTask) {
                    manager.subtasks.put(task.getId(), (SubTask) task);
                    manager.getPrioritizedTasks().add((SubTask) task);
                } else if (task instanceof EpicTask) {
                    manager.epics.put(task.getId(), (EpicTask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                    manager.getPrioritizedTasks().add(task);
                }
                maxId = Math.max(maxId, task.getId());
            }
            manager.setNewId(maxId + 1);
            for (SubTask subtask : manager.subtasks.values()) {
                int epicId = subtask.getEpicId();
                EpicTask epic = manager.epics.get(epicId);
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + file.getName(), e);
        }
        return manager;
    }

    private static Task fromString(String line) {
        String[] fields = line.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        String epicIdStr = fields.length > 5 ? fields[5] : "";
        String startTimeStr = fields.length > 6 ? fields[6] : "";
        String durationStr = fields.length > 7 ? fields[7] : "";

        LocalDateTime startTime = startTimeStr.isEmpty() ? null : LocalDateTime.parse(startTimeStr);
        Duration duration = durationStr.isEmpty() ? Duration.ZERO : Duration.parse(durationStr);
        Task taskFromFile;

        switch (type) {
            case TASK -> {
                taskFromFile = new Task(id, name, description, status);
            }
            case EPIC -> {
                taskFromFile = new EpicTask(id, name, description, status);
            }
            case SUBTASK -> {
                if (epicIdStr.isEmpty()) {
                    throw new IllegalArgumentException("Не хватает поля epicId в строке: " + line);
                }
                int epicId = Integer.parseInt(epicIdStr);
                taskFromFile = new SubTask(id, name, description, status, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }

        taskFromFile.setStartTime(startTime);
        taskFromFile.setDuration(duration);

        return taskFromFile;
    }

    @Override
    public Task addNewTask(Task task) {
        Task result = super.addNewTask(task);
        save();
        return result;
    }

    @Override
    public EpicTask addNewEpic(EpicTask epic) {
        EpicTask result = super.addNewEpic(epic);
        save();
        return result;
    }

    @Override
    public SubTask addNewSubtask(SubTask subtask) {
        SubTask result = super.addNewSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(EpicTask newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubTask(SubTask newSubtask) {
        super.updateSubTask(newSubtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void clearAllSubTasks() {
        super.clearAllSubTasks();
        save();
    }

}
