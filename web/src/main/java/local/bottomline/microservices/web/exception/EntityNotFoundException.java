package local.bottomline.microservices.web.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
