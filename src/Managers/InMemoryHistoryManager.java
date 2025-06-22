package Managers;

import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    Node head;
    Node tail;
    HashMap<Integer, Node> nodeMap = new HashMap<>();

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;

        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        nodeMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }

        return tasks;

    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;

        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }

        node.next = null;
        node.prev = null;// Обнулим ссылки у node
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
        }
        nodeMap.remove(id);
    }


    @Override
    public void add(Task task) {
        Node existingNode = nodeMap.get(task.getId());
        if (existingNode != null) {
            removeNode(existingNode);
            nodeMap.remove(task.getId());
        }
        linkLast(task);
        nodeMap.put(task.getId(), tail);
    }


    @Override
    public List<Task> getHistory() {
        return getTasks();
    }


    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

}
