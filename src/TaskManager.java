import Tasks.EpicTask;
import Tasks.Status;
import Tasks.SubTask;
import Tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int newId = 1;


    private int generateId() {
        return newId++;
    }


    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();


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


    // Для эпиков
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear(); //добавлено удаление всех подзадач
    }

    // Для обычных задач
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Для подзадач
    public void clearAllSubTasks() {
        subtasks.clear();
        for (EpicTask epic : epics.values()) {
            epic.deleteAllSubTasks(); // очищение внутренних хранилищ
            updateEpicStatus(epic.getId()); // обновление статуса Эпика
        }

    }


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
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) { // Проверяем существование эпика
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);

            EpicTask epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
            return subtask;
        }
        return null;
    }

    // Для эпиков
    public void updateEpic(EpicTask newEpic) {
        EpicTask existingEpic = epics.get(newEpic.getId());
        if (existingEpic != null) {
            // Обновляем только название и описание
            existingEpic.setName(newEpic.getName());
            existingEpic.setDescription(newEpic.getDescription());
        }
    }

    // Для обычных задач
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    // Для подзадач
    public void updateSubTask(SubTask newSubtask) {
        SubTask existingSubtask = subtasks.get(newSubtask.getId());
        if (existingSubtask != null &&
                existingSubtask.getEpicId() == newSubtask.getEpicId()) {

            existingSubtask.setName(newSubtask.getName());
            existingSubtask.setDescription(newSubtask.getDescription());
            existingSubtask.setStatus(newSubtask.getStatus()); // обновляем статус подзадачи

            updateEpicStatus(existingSubtask.getEpicId());
        }
    }


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
                epic.deleteSubtaskId(id); // Используем метод из EpicTask
                updateEpicStatus(epic.getId());
            }
        }
    }


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

    // Удалены обновления статусов для обычных задач и для подзадач
}