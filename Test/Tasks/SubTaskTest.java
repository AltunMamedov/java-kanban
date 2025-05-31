package Tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        int wrongEpicId = 1;
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.NEW, wrongEpicId);
        assertNotEquals(subTask.getId(), subTask.getEpicId(),
                "Подзадача не может быть своим же эпиком (id и epicId не должны совпадать)");
    }

}