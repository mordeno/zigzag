package mordeno.jeromejohn.weatherservice.controller;

import mordeno.jeromejohn.weatherservice.dto.WeatherDetails;
import mordeno.jeromejohn.weatherservice.exceptions.WeatherProviderException;
import mordeno.jeromejohn.weatherservice.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class WeatherControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    private WeatherDetails weatherDetails;

    @BeforeEach
    void setUp() {
        weatherDetails = new WeatherDetails(10, 30);
    }

    @Test
    public void GetWeather_ValidRequest_Returns200() throws Exception {
        when(weatherService.getWeather("city")).thenReturn(weatherDetails);

        mockMvc.perform(get("/v1/weather")
                        .param("city", "city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wind_speed").value(10))
                .andExpect(jsonPath("$.temperature_degrees").value(30));
    }

    @Test
    public void GetWeather_AnyException_Returns503() throws Exception {
        when(weatherService.getWeather("city")).thenThrow(new WeatherProviderException("Provider error"));

        mockMvc.perform(get("/v1/weather")
                        .param("city", "city"))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.statusCode").value(503))
                .andExpect(jsonPath("$.message").value("No data available"));
    }
}
