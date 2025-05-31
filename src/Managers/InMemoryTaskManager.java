package Managers;

import Tasks.EpicTask;
import Tasks.Status;
import Tasks.SubTask;
import Tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();
    private final HistoryManager historyManager;

    private int newId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public ArrayList<EpicTask> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear(); //добавлено удаление всех подзадач
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearAllSubTasks() {
        subtasks.clear();
        for (EpicTask epic : epics.values()) {
            epic.deleteAllSubTasks(); // очищение внутренних хранилищ
            updateEpicStatus(epic.getId()); // обновление статуса Эпика
        }
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epicTask = epics.get(id);
        if (epicTask != null) {
            historyManager.add(epicTask);
        }
        return epicTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subtasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public EpicTask addNewEpic(EpicTask epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task addNewTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask addNewSubtask(SubTask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);

            EpicTask epic = epics.get(epicId);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
            return subtask;
        }
        return null;
    }

    @Override
    public void updateEpic(EpicTask newEpic) {
        EpicTask existingEpic = epics.get(newEpic.getId());
        if (existingEpic != null) {
            existingEpic.setName(newEpic.getName());
            existingEpic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask newSubtask) {
        SubTask existingSubtask = subtasks.get(newSubtask.getId());
        if (existingSubtask != null &&
                existingSubtask.getEpicId() == newSubtask.getEpicId()) {

            existingSubtask.setName(newSubtask.getName());
            existingSubtask.setDescription(newSubtask.getDescription());
            existingSubtask.setStatus(newSubtask.getStatus());

            updateEpicStatus(existingSubtask.getEpicId());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        EpicTask epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskId()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        SubTask subtask = subtasks.remove(id);
        if (subtask != null) {
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.deleteSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
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

    @Override
    public Task getAnyTaskById(int id) {
        if (tasks.containsKey(id)) return tasks.get(id);
        if (subtasks.containsKey(id)) return subtasks.get(id);
        return epics.get(id);
    }

    private int generateId() {
        return newId++;
    }

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
}
