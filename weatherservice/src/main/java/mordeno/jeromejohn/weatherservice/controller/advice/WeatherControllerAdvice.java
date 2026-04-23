package mordeno.jeromejohn.weatherservice.controller.advice;

import mordeno.jeromejohn.weatherservice.controller.WeatherControllerV1;
import mordeno.jeromejohn.weatherservice.dto.ErrorResponse;
import mordeno.jeromejohn.weatherservice.exceptions.WeatherProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = WeatherControllerV1.class)
public class WeatherControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(WeatherControllerAdvice.class);

    @ExceptionHandler(WeatherProviderException.class)
    public ResponseEntity<ErrorResponse> handleException(WeatherProviderException ex) {
        log.error("WeatherProviderException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(503, "No data available");
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException occurred: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(400, "Invalid request parameter");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
