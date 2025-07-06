package managers;

import tasks.*;

import java.io.*;

@SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocType"})
public class FileBackedTaskManager extends InMemoryTaskManager {
    @SuppressWarnings("checkstyle:Indentation")
    private final File file;

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocMethod"})
    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @SuppressWarnings("checkstyle:Indentation")
    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : tasks.values()) {
                writer.write(task.toString() + "\n");
            }

            for (EpicTask epic : epics.values()) {
                writer.write(epic.toString() + "\n");
            }

            for (SubTask subtask : subtasks.values()) {
                writer.write(subtask.toString() + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла: " + file.getName(), e);
        }
    }

    @SuppressWarnings("checkstyle:Indentation")
    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);

                if (task instanceof SubTask) {
                    manager.subtasks.put(task.getId(), (SubTask) task);
                } else if (task instanceof EpicTask) {
                    manager.epics.put(task.getId(), (EpicTask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + file.getName(), e);
        }

        return manager;

    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingSwitchDefault"})
    private static Task fromString(String line) {
        String[] fields = line.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        Task taskFromFile = null;

        switch (type) {
            case TASK -> taskFromFile = new Task(id, name, description, status);
            case EPIC -> taskFromFile = new EpicTask(id, name, description, status);
            case SUBTASK -> {
                if (fields.length <= 5) {
                    throw new IllegalArgumentException("Не хватает поля epicId в строке: " + line);
                }
                int epicId = Integer.parseInt(fields[5]);
                taskFromFile = new SubTask(id, name, description, status, epicId);
            }


        }

        return taskFromFile;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public Task addNewTask(Task task) {
        Task result = super.addNewTask(task);
        save();
        return result;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public EpicTask addNewEpic(EpicTask epic) {
        EpicTask result = super.addNewEpic(epic);
        save();
        return result;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public SubTask addNewSubtask(SubTask subtask) {
        SubTask result = super.addNewSubtask(subtask);
        save();
        return result;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void updateEpic(EpicTask newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void updateSubTask(SubTask newSubtask) {
        super.updateSubTask(newSubtask);
        save();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }


}
