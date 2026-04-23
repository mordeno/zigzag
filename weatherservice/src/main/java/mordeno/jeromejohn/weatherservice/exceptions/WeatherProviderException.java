package mordeno.jeromejohn.weatherservice.exceptions;

public class WeatherProviderException extends RuntimeException {
    public WeatherProviderException(String message) {
        super(message);
    }
}
