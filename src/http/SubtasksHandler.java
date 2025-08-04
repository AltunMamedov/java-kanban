package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
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
            sendServerError(exchange, "Ошибка на сервере: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<SubTask> subtasks = manager.getAllSubtasks();
            sendText(exchange, gson.toJson(subtasks));
        } else {

            if (query.startsWith("id=")) {
                int id = parseId(query);
                SubTask subtask = manager.getSubTaskById(id);
                if (subtask != null) {
                    sendText(exchange, gson.toJson(subtask));
                } else {
                    sendNotFound(exchange, "Подзадача с id=" + id + " не найдена.");
                }
                return;
            }
            if (query.startsWith("epicId=")) {
                int epicId = parseId(query);
                List<SubTask> subtasks = manager.getSubtasksByEpicId(epicId);
                sendText(exchange, gson.toJson(subtasks));
                return;
            }

            sendNotFound(exchange, "Неверный запрос: " + query);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        SubTask subtask = gson.fromJson(body, SubTask.class);

        if (subtask.getId() == 0 || manager.getSubTaskById(subtask.getId()) == null) {
            manager.addNewSubtask(subtask);
        } else {
            manager.updateSubTask(subtask);
        }

        sendText(exchange, "Подзадача сохранена: " + gson.toJson(subtask));
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            manager.clearAllSubTasks();
            sendText(exchange, "Все подзадачи удалены.");
        } else {
            int id = parseId(query);
            SubTask subtask = manager.getSubTaskById(id);
            if (subtask != null) {
                manager.deleteSubtaskById(id);
                sendText(exchange, "Подзадача с id=" + id + " удалена.");
            } else {
                sendNotFound(exchange, "Подзадача с id=" + id + " не найдена.");
            }
        }
    }

    private int parseId(String query) {
        try {
            return Integer.parseInt(query.split("=")[1]);
        } catch (Exception e) {
            return -1;
        }
    }
}
