package deviceapi.exception.handler;

import deviceapi.message.APIMessage;
import deviceapi.exception.DeviceAlreadyExistException;
import deviceapi.exception.DeviceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<APIMessage> catchDeviceNotFoundException(DeviceNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new APIMessage(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<APIMessage> catchDeviceAlreadyExistException(DeviceAlreadyExistException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new APIMessage(HttpStatus.CONFLICT.value(), e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIMessage> handleMessageNotReadableException(
            HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new APIMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
