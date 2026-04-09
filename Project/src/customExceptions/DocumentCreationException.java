package customExceptions;

public class DocumentCreationException extends RuntimeException {
    public DocumentCreationException(String message) {
        super(message);
    }
}
