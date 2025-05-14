package Tasks;

import java.util.ArrayList;
import java.util.Objects;

public class EpicTask extends Task {
    private ArrayList<Integer> subtaskId;

    public EpicTask(String name, String description) {
        super(name, description);
        this.subtaskId = new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId) {
        this.subtaskId.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }


}