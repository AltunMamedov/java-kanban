package managers;

import org.junit.jupiter.api.*;
import tasks.Status;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {

        tempFile = File.createTempFile("task_manager_test", ".csv");

        tempFile.deleteOnExit();

        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void saveAndLoadEmptyManager_shouldBeEmpty() {

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что все списки пустые
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Tasks should be empty");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Epics should be empty");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Subtasks should be empty");
    }
    @Test
    void addTask_shouldSaveAndLoadTask() {
        Task task = new Task("Test task", "Description", Status.NEW);
        manager.addNewTask(task); // вызовет save() внутри

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(task.getName(), loadedManager.getAllTasks().get(0).getName());
    }

}
