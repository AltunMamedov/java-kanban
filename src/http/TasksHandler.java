package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, query);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, query);
                    break;
                default:
                    exchange.sendResponseHeaders(405, 0);
                    exchange.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendServerError(exchange, "Ошибка на сервере: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<Task> tasks = manager.getAllTasks();
            String json = gson.toJson(tasks);
            sendText(exchange, json);
        } else {
            int id = parseId(query);
            Task task = manager.getTaskById(id);
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange, "Задача с id=" + id + " не найдена.");
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0 || manager.getTaskById(task.getId()) == null) {
            manager.addNewTask(task);
            sendResponse(exchange, 201, gson.toJson(task));  // Created
        } else {
            manager.updateTask(task);
            sendResponse(exchange, 201, gson.toJson(task));  // Updated
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            manager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены.");
        } else {
            int id = parseId(query);
            Task task = manager.getTaskById(id);
            if (task != null) {
                manager.deleteTaskById(id);
                sendText(exchange, "Задача с id=" + id + " удалена.");
            } else {
                sendNotFound(exchange, "Задача с id=" + id + " не найдена.");
            }
        }
    }

    private Integer parseId(String query) {
        try {
            String[] parts = query.split("=");
            if (parts.length == 2) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }
}
