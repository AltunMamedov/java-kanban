package Tasks;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {
    @Test
    void shouldNotAddItselfAsSubtask() {
        EpicTask epic = new EpicTask("Эпик", "Описание");
        int epicId = epic.getId();

        epic.addSubtaskId(epicId);

        List<Integer> subtasks = epic.getSubtaskId();

        assertFalse(subtasks.contains(epicId), "Эпик не должен содержать сам себя в виде подзадачи");
    }

    @Test
    void shouldAddSubtaskId() {
        EpicTask epic = new EpicTask("Эпик", "Описание");
        epic.addSubtaskId(10);
        List<Integer> subtasks = epic.getSubtaskId();
        assertEquals(1, subtasks.size(), "Ожидается один ID в списке подзадач");
        assertTrue(subtasks.contains(10), "Список должен содержать ID 10");
    }

    @Test
    void shouldGetSubtaskId() {
        EpicTask epic = new EpicTask("Эпик", "Описание");
        epic.addSubtaskId(5);
        epic.addSubtaskId(7);
        epic.addSubtaskId(11);
        List<Integer> subtasks = epic.getSubtaskId();
        assertEquals(3, subtasks.size(), "Ожидается 3 подзадачи");
        assertTrue(subtasks.contains(5), "Список должен содержать ID 5");
        assertTrue(subtasks.contains(7), "Список должен содержать ID 7");
        assertTrue(subtasks.contains(11), "Список должен содержать ID 11");
    }

    @Test
    void shouldDeleteSubtaskId() {
        EpicTask epic = new EpicTask("Эпик", "Описание");
        epic.addSubtaskId(5);
        epic.addSubtaskId(7);
        epic.addSubtaskId(11);
        epic.deleteSubtaskId(5);
        List<Integer> subtasks = epic.getSubtaskId();
        assertFalse(subtasks.contains(5));
    }

    @Test
    void shouldDeleteAllSubTasks() {
        EpicTask epic = new EpicTask("Эпик", "Описание");
        epic.addSubtaskId(5);
        epic.addSubtaskId(7);
        epic.addSubtaskId(11);
        epic.deleteAllSubTasks();
        List<Integer> subtasks = epic.getSubtaskId();
        assertEquals(0, subtasks.size(), "Ожидается 0 подзадач");
        assertTrue(subtasks.isEmpty(), "Список подзадач должен быть пустым");
    }
}