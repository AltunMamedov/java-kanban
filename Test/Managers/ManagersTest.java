package Managers;

import Tasks.Status;
import Tasks.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldGetDefault() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер не должен быть null");
        Task task = new Task("Тестовая задача", "Описание задачи", Status.NEW);
        manager.addNewTask(task);
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Ожидается 1 задача в менеджере");
        assertEquals("Тестовая задача", tasks.get(0).getName());
    }
}