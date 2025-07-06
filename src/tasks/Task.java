package tasks;

import java.util.Objects;

@SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocType"})
public class Task {
    @SuppressWarnings("checkstyle:Indentation")
    private int id;
    @SuppressWarnings("checkstyle:Indentation")
    private String name;
    @SuppressWarnings("checkstyle:Indentation")
    private String description;
    @SuppressWarnings("checkstyle:Indentation")
    private Status status;


    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocMethod"})
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocMethod"})
    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }


    @SuppressWarnings("checkstyle:Indentation")
    public int getId() {
        return id;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public String getName() {
        return name;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public Status getStatus() {
        return status;
    }


    @SuppressWarnings("checkstyle:Indentation")
    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public void setStatus(Status status) {
        this.status = status;
    }

    @SuppressWarnings("checkstyle:Indentation")
    public void setId(int id) {
        this.id = id;
    }


    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:NeedBraces"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,",
                getId(),
                TaskType.TASK,
                getName(),
                getStatus(),
                getDescription()
        );
    }
}
