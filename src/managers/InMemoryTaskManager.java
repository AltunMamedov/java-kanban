package managers;

import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import java.util.TreeSet;
import java.util.Comparator;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epics = new HashMap<>();
    private final HistoryManager historyManager;
    Comparator<Task> comparator = Comparator
            .comparing(Task::getStartTime)
            .thenComparing(Task::getId);
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    private int newId = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public void setNewId(int newId) {
        this.newId = newId;
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
        for (EpicTask epic : epics.values()) {
            for (int subTaskId : epic.getSubtaskId()) {
                SubTask subTask = subtasks.remove(subTaskId);
                if (subTask != null) {
                    prioritizedTasks.remove(subTask);
                    historyManager.remove(subTaskId);
                }
            }
            historyManager.remove(epic.getId());
        }
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }


    @Override
    public void clearAllSubTasks() {
        for (SubTask subTask : subtasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        subtasks.clear();
        for (EpicTask epic : epics.values()) {
            epic.deleteAllSubTasks();
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
        }
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
        if (hasIntersection(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        task.setId(generateId());
        getTasks().put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public SubTask addNewSubtask(SubTask subtask) {
        if (hasIntersection(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей.");
        }
        int epicId = subtask.getEpicId();
        if (getEpics().containsKey(epicId)) {
            subtask.setId(generateId());
            getSubtasks().put(subtask.getId(), subtask);

            EpicTask epic = getEpics().get(epicId);
            epic.addSubtaskId(subtask.getId());

            updateEpicStatus(epic.getId());
            addToPrioritizedTasks(subtask);
            updateEpicTimeFields(epic);

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
        Task oldTask = getTasks().get(task.getId());
        if (oldTask != null) {
            removeFromPrioritizedTasks(oldTask);
            if (hasIntersection(task)) {
                addToPrioritizedTasks(oldTask); // возвращаем старую обратно
                throw new IllegalArgumentException("Обновленная задача пересекается по времени с другой задачей.");
            }
            getTasks().put(task.getId(), task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void updateSubTask(SubTask newSubtask) {
        SubTask existingSubtask = getSubtasks().get(newSubtask.getId());
        if (existingSubtask != null && existingSubtask.getEpicId() == newSubtask.getEpicId()) {
            removeFromPrioritizedTasks(existingSubtask);
            if (hasIntersection(newSubtask)) {
                addToPrioritizedTasks(existingSubtask);
                throw new IllegalArgumentException("Обновленная подзадача пересекается по времени с другой задачей.");
            }
            existingSubtask.setName(newSubtask.getName());
            existingSubtask.setDescription(newSubtask.getDescription());
            existingSubtask.setStatus(newSubtask.getStatus());
            existingSubtask.setStartTime(newSubtask.getStartTime());
            existingSubtask.setDuration(newSubtask.getDuration());
            addToPrioritizedTasks(existingSubtask);
            updateEpicStatus(existingSubtask.getEpicId());
            EpicTask epic = getEpics().get(existingSubtask.getEpicId());
            updateEpicTimeFields(epic);
        }
    }


    @Override
    public void deleteEpicById(int id) {
        EpicTask epic = getEpics().remove(id);
        if (epic == null) return;
        for (int subtaskId : new ArrayList<>(epic.getSubtaskId())) {
            SubTask subTask = subtasks.remove(subtaskId);
            if (subTask != null) {
                prioritizedTasks.remove(subTask);
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        SubTask subtask = getSubtasks().get(id);
        if (subtask != null) {
            removeFromPrioritizedTasks(subtask);
            getSubtasks().remove(id);
            EpicTask epic = getEpics().get(subtask.getEpicId());
            if (epic != null) {
                epic.deleteSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTimeFields(epic);
            }

            historyManager.remove(id);
        }
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTaskOverlapping(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }
        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.getStartTime() == null || prioritizedTask.getEndTime() == null) {
                continue;
            }
            boolean overlaps = task.getStartTime().isBefore(prioritizedTask.getEndTime()) &&
                    task.getEndTime().isAfter(prioritizedTask.getStartTime());
            if (overlaps) {
                return true;
            }
        }
        return false;
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    private boolean isIntersecting(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return !(task1.getEndTime().isBefore(task2.getStartTime()) ||
                task1.getStartTime().isAfter(task2.getEndTime()));
    }

    private boolean hasIntersection(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> isIntersecting(existingTask, newTask));
    }

    private void updateEpicTimeFields(EpicTask epic) {
        List<SubTask> subtasks = getSubtasksByEpicId(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }
        LocalDateTime start = null;
        Duration totalDuration = Duration.ZERO;
        for (SubTask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (start == null || subtask.getStartTime().isBefore(start)) {
                    start = subtask.getStartTime();
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        epic.setStartTime(start);
        epic.setDuration(totalDuration);
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
