package managers;

import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    @SuppressWarnings("checkstyle:Indentation")
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epics = new HashMap<>();
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
        return new ArrayList<>(getEpics().values());
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(getSubtasks().values());
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(getTasks().values());
    }

    @Override
    public void deleteAllEpics() {
        for (EpicTask epic : getEpics().values()) {
            historyManager.remove(epic.getId());
        }
        for (SubTask subtask : getSubtasks().values()) {
            historyManager.remove(subtask.getId());
        }
        getEpics().clear();
        getSubtasks().clear();
    }


    @Override
    public void deleteAllTasks() {
        for (Task task : getTasks().values()) {
            historyManager.remove(task.getId());
        }
        getTasks().clear();
    }


    @Override
    public void clearAllSubTasks() {
        for (SubTask subTask : getSubtasks().values()) {
            historyManager.remove(subTask.getId());
        }
        getSubtasks().clear();

    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epicTask = getEpics().get(id);
        if (epicTask != null) {
            historyManager.add(epicTask);
        }
        return epicTask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = getTasks().get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = getSubtasks().get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public EpicTask addNewEpic(EpicTask epic) {
        epic.setId(generateId());
        getEpics().put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task addNewTask(Task task) {
        task.setId(generateId());
        getTasks().put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask addNewSubtask(SubTask subtask) {
        int epicId = subtask.getEpicId();
        if (getEpics().containsKey(epicId)) {
            subtask.setId(generateId());
            getSubtasks().put(subtask.getId(), subtask);

            EpicTask epic = getEpics().get(epicId);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
            return subtask;
        }
        return null;
    }

    @Override
    public void updateEpic(EpicTask newEpic) {
        EpicTask existingEpic = getEpics().get(newEpic.getId());
        if (existingEpic != null) {
            existingEpic.setName(newEpic.getName());
            existingEpic.setDescription(newEpic.getDescription());
        }
    }

    @Override
    public void updateTask(Task task) {
        if (getTasks().containsKey(task.getId())) {
            getTasks().put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask newSubtask) {
        SubTask existingSubtask = getSubtasks().get(newSubtask.getId());
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
        EpicTask epic = getEpics().remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskId()) {
                getSubtasks().remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        getTasks().remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        SubTask subtask = getSubtasks().remove(id);
        if (subtask != null) {
            EpicTask epic = getEpics().get(subtask.getEpicId());
            if (epic != null) {
                epic.deleteSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
        }
        historyManager.remove(id);
    }

    @Override
    public ArrayList<SubTask> getSubtasksByEpicId(int epicId) {
        EpicTask epic = getEpics().get(epicId);
        ArrayList<SubTask> result = new ArrayList<>();

        if (epic == null) {
            return result;
        }

        for (int subtaskId : epic.getSubtaskId()) {
            SubTask subtask = getSubtasks().get(subtaskId);
            if (subtask != null) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public Task getAnyTaskById(int id) {
        if (getTasks().containsKey(id)) return getTasks().get(id);
        if (getSubtasks().containsKey(id)) return getSubtasks().get(id);
        return getEpics().get(id);
    }

    private int generateId() {
        return newId++;
    }

    private void updateEpicStatus(int epicId) {
        EpicTask epic = getEpics().get(epicId);
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

    protected HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    protected HashMap<Integer, SubTask> getSubtasks() {
        return subtasks;
    }

    protected HashMap<Integer, EpicTask> getEpics() {
        return epics;
    }
}
