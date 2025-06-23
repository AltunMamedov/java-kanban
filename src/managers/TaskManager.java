package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.util.List;


public interface TaskManager {
    // Для эпиков
    List<EpicTask> getAllEpics();

    // Для подзадач
    List<SubTask> getAllSubtasks();

    // Для обычных задач
    List<Task> getAllTasks();

    // Для эпиков
    void deleteAllEpics();

    // Для обычных задач
    void deleteAllTasks();

    // Для подзадач
    void clearAllSubTasks();

    // Для эпиков
    EpicTask getEpicById(int id);

    // Для обычных задач
    Task getTaskById(int id);

    // Для подзадач
    SubTask getSubTaskById(int id);

    // Для эпиков
    EpicTask addNewEpic(EpicTask epic);

    // Для обычных задач
    Task addNewTask(Task task);

    // Для подзадач
    SubTask addNewSubtask(SubTask subtask);

    // Для эпиков
    void updateEpic(EpicTask newEpic);

    // Для обычных задач
    void updateTask(Task task);

    // Для подзадач
    void updateSubTask(SubTask newSubtask);

    // Для эпиков
    void deleteEpicById(int id);

    // Для обычных задач
    void deleteTaskById(int id);

    // Для подзадач
    void deleteSubtaskById(int id);

    List<SubTask> getSubtasksByEpicId(int epicId);

    Task getAnyTaskById(int id);

    List<Task> getHistory();

}
