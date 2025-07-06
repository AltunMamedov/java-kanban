package managers;

@SuppressWarnings({"checkstyle:Indentation", "checkstyle:WhitespaceAround", "checkstyle:MissingJavadocType"})
public class ManagerSaveException extends RuntimeException{
    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MissingJavadocMethod"})
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
