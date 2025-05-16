package Tasks;

import java.util.ArrayList;


public class EpicTask extends Task {
    private ArrayList<Integer> subtaskIds;

    public EpicTask(String name, String description) {
        super(name, description, Status.NEW); /*Из условия следует,
        что у EpicTask всегда при создании Status.NEW*/
        this.subtaskIds = new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId) {
        this.subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskId() {
        return new ArrayList<>(subtaskIds);
    }

    /*в этот класс нужно добавить два метода
1) удалит единичную подзадачу из хранилища subTasks
2) очистит все данные хранилища subTasks*/

    public void deleteSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void deleteAllSubTasks() {
        subtaskIds.clear();
    }

    //удален метод setSubtaskId(ArrayList<Integer> subtaskId) {}

    @Override
    public String toString() {
        return "EpicTask{id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtaskIds +
                '}';
    }
}