package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.EpicTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
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
            List<EpicTask> epics = manager.getAllEpics();
            sendText(exchange, gson.toJson(epics));
        } else {
            int id = parseId(query);
            EpicTask epic = manager.getEpicById(id);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найден.");
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        EpicTask epic = gson.fromJson(body, EpicTask.class);

        if (epic.getId() == 0 || manager.getEpicById(epic.getId()) == null) {
            manager.addNewEpic(epic);
        } else {
            manager.updateEpic(epic);
        }

        sendText(exchange, "Эпик сохранён: " + gson.toJson(epic));
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            manager.deleteAllEpics();
            sendText(exchange, "Все эпики удалены.");
        } else {
            int id = parseId(query);
            EpicTask epic = manager.getEpicById(id);
            if (epic != null) {
                manager.deleteEpicById(id);
                sendText(exchange, "Эпик с id=" + id + " удалён.");
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найден.");
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
