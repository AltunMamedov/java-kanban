package managers;

import tasks.EpicTask;
import tasks.Status;
import tasks.SubTask;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Создаем эпик
        EpicTask epic = new EpicTask("Организовать переезд", "Подготовка к новоселью");
        manager.addNewEpic(epic);
        System.out.println("Создан эпик: " + epic);

        // Создаем подзадачи
        SubTask subTask1 = new SubTask("Упаковать книги", "Коробки для книг", Status.NEW, epic.getId());
        subTask1 = manager.addNewSubtask(subTask1);

        SubTask subTask2 = new SubTask("Заказать грузовик", "Грузоподъемность 2 тонны", Status.NEW, epic.getId());
        subTask2 = manager.addNewSubtask(subTask2);

        // Проверяем статус эпика (NEW)
        System.out.println("\nСтатус эпика после создания подзадач: " + epic.getStatus());

        // Меняем статусы подзадач
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);
        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask2);

        // Проверяем статус эпика (IN_PROGRESS)
        System.out.println("Статус эпика после изменения подзадач: " + epic.getStatus());

        // Выводим все подзадачи эпика
        System.out.println("\nПодзадачи эпика:");
        manager.getSubtasksByEpicId(epic.getId()).forEach(System.out::println);
    }
}
