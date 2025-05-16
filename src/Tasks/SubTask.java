package Tasks;


public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status); /*Добавлен status,
        т.к. изменен конструктор родительского класса*/
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    // удален метод setEpicId(int epicId){}
    @Override
    public String toString() {
        return "SubTask{id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}