package managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class TaskManagerWithTimeTest {
    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void shouldCalculateEpicTimeFieldsCorrectly() {
        EpicTask epic = manager.addNewEpic(new EpicTask("Epic", "Description"));

        SubTask sub1 = new SubTask("Sub1", "Desc", Status.NEW, epic.getId());
        sub1.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 0));
        sub1.setDuration(Duration.ofMinutes(60));
        manager.addNewSubtask(sub1);

        SubTask sub2 = new SubTask("Sub2", "Desc", Status.NEW, epic.getId());
        sub2.setStartTime(LocalDateTime.of(2025, 7, 20, 12, 0));
        sub2.setDuration(Duration.ofMinutes(30));
        manager.addNewSubtask(sub2);

        EpicTask updatedEpic = manager.getEpicById(epic.getId());
        assertNotNull(updatedEpic.getStartTime(), "Epic startTime не должен быть null");
        assertEquals(LocalDateTime.of(2025, 7, 20, 10, 0), updatedEpic.getStartTime());
        assertEquals(Duration.ofMinutes(90), updatedEpic.getDuration(), "Epic duration должен быть суммой подзадач");
    }


    @Test
    void shouldReturnPrioritizedTasksInCorrectOrder() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 20, 9, 0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.addNewTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 20, 8, 0));
        task2.setDuration(Duration.ofMinutes(45));
        manager.addNewTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();

        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }

    @Test
    void shouldDetectTaskTimeOverlap() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addNewTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 30)); // перекрывается с task1
        task2.setDuration(Duration.ofMinutes(30));

        boolean overlaps = manager.isTaskOverlapping(task2);
        assertTrue(overlaps, "Задачи пересекаются по времени");

        task2.setStartTime(LocalDateTime.of(2025, 7, 20, 11, 1)); // не пересекается
        overlaps = manager.isTaskOverlapping(task2);
        assertFalse(overlaps, "Задачи не пересекаются");
    }

    @Test
    void shouldNotAddTaskIfTimeOverlap() {
        Task task1 = new Task("Task1", "Desc", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2025, 7, 20, 9, 0));
        task1.setDuration(Duration.ofMinutes(60));
        manager.addNewTask(task1);

        Task task2 = new Task("Task2", "Desc", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2025, 7, 20, 9, 30)); // пересекается с task1
        task2.setDuration(Duration.ofMinutes(30));


        if (manager.isTaskOverlapping(task2)) {

            assertNull(manager.getTaskById(task2.getId()), "Задача не должна быть добавлена из-за пересечения");
        } else {
            manager.addNewTask(task2);
        }
    }

    @Test
    void shouldUpdateTaskAndMaintainPrioritizedTasks() {
        Task task = new Task("Task", "Desc", Status.NEW);
        task.setStartTime(LocalDateTime.of(2025, 7, 20, 9, 0));
        task.setDuration(Duration.ofMinutes(60));
        manager.addNewTask(task);

        Task updatedTask = new Task(task.getId(), "Task Updated", "New Desc", Status.IN_PROGRESS);
        updatedTask.setStartTime(LocalDateTime.of(2025, 7, 20, 10, 0)); // изменили время старта
        updatedTask.setDuration(Duration.ofMinutes(30));
        manager.updateTask(updatedTask);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(1, prioritized.size());
        assertEquals(updatedTask.getStartTime(), prioritized.get(0).getStartTime());
    }
}
