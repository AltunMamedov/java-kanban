package Managers;

import Tasks.EpicTask;
import Tasks.SubTask;
import Tasks.Task;

import java.util.ArrayList;


public interface TaskManager {
    // Для эпиков
    ArrayList<EpicTask> getAllEpics();

    // Для подзадач
    ArrayList<SubTask> getAllSubtasks();

    // Для обычных задач
    ArrayList<Task> getAllTasks();

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

    ArrayList<SubTask> getSubtasksByEpicId(int epicId);

    Task getAnyTaskById(int id);

    ArrayList<Task> getHistory();

}
