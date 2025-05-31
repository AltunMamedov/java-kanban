package Tasks;

import java.util.ArrayList;


public class EpicTask extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        if (this.getId() == id) {
            return;
        }
        subtaskIds.add(id);
    }

    public void deleteSubtaskId(int id) {
        subtaskIds.remove((Integer) id);
    }

    public void deleteAllSubTasks() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "EpicTask{" + "id=" + getId() + ", name='" + getName() + '\'' + ", description='" + getDescription() + '\'' + ", status=" + getStatus() + ", subtaskIds=" + subtaskIds + '}';
    }
}
