package http;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import com.google.gson.Gson;

import managers.TaskManager;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TasksHandler(manager, new Gson()));
        server.createContext("/epics", new EpicsHandler(manager, new Gson()));
        server.createContext("/subtasks", new SubtasksHandler(manager, new Gson()));
        server.createContext("/history", new HistoryHandler(manager, new Gson()));
        server.createContext("/prioritized", new PrioritizedHandler(manager, new Gson()));


    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(1);
        System.out.println("HTTP-сервер остановлен.");
    }
}
