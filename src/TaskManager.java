import Tasks.EpicTask;
import Tasks.Status;
import Tasks.SubTask;
import Tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int newId = 1;

    // TODO 0. Уникальный идентификационный номер задачи
    private int generateId() {
        return newId++;
    }

    // TODO 1. Возможность хранить задачи всех типов.
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();

    // TODO 2. Методы для каждого из типа задач (Задача/Эпик/Подзадача):
    // TODO  a. Получение списка всех задач.
    // Для эпиков
    public ArrayList<EpicTask> getAllEpics() {

        return new ArrayList<>(epics.values());
    }

    // Для подзадач
    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Для обычных задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // TODO  b. Удаление всех задач.
    // Для эпиков
    public void deleteAllEpics() {
        epics.clear();
    }

    // Для обычных задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Для подзадач
    public void deleteAllSubTasks() {
        subtasks.clear();
    }

    //TODO  c. Получение по идентификатору.

    // Для эпиков
    public EpicTask getEpicById(int id) {
        return epics.get(id);
    }

    // Для обычных задач
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Для подзадач
    public SubTask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    //TODO d. Создание.

    // Для эпиков
    public EpicTask addNewEpic(EpicTask epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    // Для обычных задач
    public Task addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    // Для подзадач
    public SubTask addNewSubtask(SubTask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        EpicTask epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
        }
        return subtask;
    }

    //TODO  e. Обновление.

    // Для эпиков
    public void updateEpic(EpicTask epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }
    }

    // Для обычных задач
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    // Для подзадач
    public void updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    //TODO  f. Удаление по идентификатору.

    // Для эпиков
    public void deleteEpicById(int id) {
        EpicTask epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    // Для обычных задач
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // Для подзадач
    public void deleteSubtaskById(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask != null) {
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskId().remove(Integer.valueOf(id));
                updateEpicStatus(epic.getId());
            }
        }
    }


    // TODO 3. Дополнительные методы:
    // TODO a. Получение списка всех подзадач определённого эпика.

    public ArrayList<SubTask> getSubtasksByEpicId(int epicId) {
        EpicTask epic = epics.get(epicId);
        ArrayList<SubTask> result = new ArrayList<>();

        if (epic == null) {
            return result;
        }

        for (int subtaskId : epic.getSubtaskId()) {
            SubTask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }


    public Task getAnyTaskById(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        if (subtasks.containsKey(id)) return subtasks.get(id);
        return epics.get(id);
    }


    // TODO 4. Управление статусами

    // Для эпиков

    private void updateEpicStatus(int epicId) {
        EpicTask epic = epics.get(epicId);
        if (epic == null) return;

        ArrayList<SubTask> subtasks = getSubtasksByEpicId(epicId);
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (SubTask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) allDone = false;
            if (subtask.getStatus() != Status.NEW) allNew = false;
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    // Для обычных задач
    public void updateTaskStatus(int taskId, Status newStatus) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setStatus(newStatus);
            tasks.put(taskId, task);
        }
    }

    // Для подзадач
    public void updateSubTaskStatus(int subtaskId, Status newStatus) {
        SubTask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            subtask.setStatus(newStatus);
            subtasks.put(subtaskId, subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }
}