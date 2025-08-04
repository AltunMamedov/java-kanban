package http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import http.HttpTaskServer;
import managers.InMemoryTaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private InMemoryTaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        manager = new InMemoryTaskManager();
        client = HttpClient.newHttpClient();
        int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                taskServer = new HttpTaskServer(manager);
                manager.deleteAllTasks();
                manager.deleteAllEpics();
                manager.clearAllSubTasks();
                taskServer.start();
                break;
            } catch (IOException e) {
                if (attempt == maxAttempts) throw e;
                System.out.println("Port 8080 in use, retrying... (Attempt " + attempt + " of " + maxAttempts + ")");
                Thread.sleep(1000);
            }
        }
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        if (taskServer != null) {
            taskServer.stop();
            Thread.sleep(500);
        }
    }


    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", tasks.Status.NEW);
        String taskJson = "{\"name\":\"Test Task\",\"description\":\"Test Description\",\"status\":\"NEW\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getAllTasks().size());
        assertEquals("Test Task", manager.getAllTasks().get(0).getName());
    }

    @Test
    void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не найдена"));
    }


    @Test
    void testAddEpic() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask("Test Epic", "Test Epic Description");
        String epicJson = "{\"name\":\"Test Epic\",\"description\":\"Test Epic Description\",\"status\":\"NEW\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getAllEpics().size());
        assertEquals("Test Epic", manager.getAllEpics().get(0).getName());
    }

    @Test
    void testGetNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не найден"));
    }


    @Test
    void testAddSubtask() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask("Test Epic", "Test Epic Description");
        manager.addNewEpic(epic);
        SubTask subtask = new SubTask("Test Subtask", "Test Subtask Description", tasks.Status.NEW, epic.getId());
        String subtaskJson = "{\"name\":\"Test Subtask\",\"description\":\"Test Subtask Description\",\"status\":\"NEW\",\"epicId\":" + epic.getId() + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getAllSubtasks().size());
        assertEquals("Test Subtask", manager.getAllSubtasks().get(0).getName());
    }

    @Test
    void testGetNonExistentSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("не найдена"));
    }
}