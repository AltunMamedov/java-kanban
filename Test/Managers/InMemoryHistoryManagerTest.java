package Managers;

import Tasks.Status;
import Tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void shouldAddTasksAndReturnHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.IN_PROGRESS);


        List<Task> emptyHistory = historyManager.getHistory();
        assertTrue(emptyHistory.isEmpty(), "История должна быть пустой в начале");


        historyManager.add(task1);
        historyManager.add(task2);


        List<Task> history = historyManager.getHistory();


        assertEquals(2, history.size(), "В истории должно быть 2 задачи");


        assertSame(task1, history.get(0), "Первая задача должна совпадать с добавленной");
        assertSame(task2, history.get(1), "Вторая задача должна совпадать с добавленной");
    }
}