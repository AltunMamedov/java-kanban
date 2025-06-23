package managers;

import tasks.Status;
import tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldAddTasksAndReturnHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        task1.setId(1);
        task2.setId(2);

        List<Task> emptyHistory = historyManager.getHistory();
        assertTrue(emptyHistory.isEmpty(), "История должна быть пустой в начале");


        historyManager.add(task1);
        historyManager.add(task2);


        List<Task> history = historyManager.getHistory();


        assertEquals(2, history.size(), "В истории должно быть 2 задачи");


        assertSame(task1, history.get(0), "Первая задача должна совпадать с добавленной");
        assertSame(task2, history.get(1), "Вторая задача должна совпадать с добавленной");
    }

    @Test
    void shouldMoveExistingTaskToEndWhenAddedAgain() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);
        task2.setId(2);
        Task task3 = new Task("Задача 3", "Описание задачи 3", Status.NEW);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);


        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertSame(task1, history.get(0));
        assertSame(task3, history.get(1));
        assertSame(task2, history.get(2));
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);

        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "В истории должна остаться одна задача после удаления");
        assertSame(task2, history.get(0), "Оставшаяся задача должна быть task2");
    }

    @Test
    void shouldKeepOneExampleOfTaskWhenAddingSameTaskMultipleTimes() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task1.setId(1);

        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "В истории должна быть одна задача без дубликатов");
        assertSame(task1, history.get(0));
    }


}