package deviceapi.exception;

public class DeviceAlreadyExistException extends RuntimeException {
    public DeviceAlreadyExistException(String message) {
        super(message);
    }
}
