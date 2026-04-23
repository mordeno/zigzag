# Weather Service API

## Description
The Weather Service API is a Spring Boot application that provides weather data by integrating with two external weather providers: WeatherStack and OpenWeatherMap. 
The service fetches unified weather details such as wind speed and temperature for a given location. 
It uses a failover mechanism to ensure data availability, switching to a secondary provider if the primary provider fails.

## Features
- Fetch weather data from WeatherStack and OpenWeatherMap.
- Failover mechanism to ensure reliability, for a set duration.
- Unified response format for weather data.
- Caching support for up to 3 seconds to handle subsequent requests.

## How to Run Locally
### 1. Extract the project and navigate to root directory.

### 2. Configure API Keys
**[IMPORTANT]** Update the `application.yml` file with your API keys for WeatherStack and OpenWeatherMap:
```yaml
weather:
  providers:
    primary:
      name: weatherstack
      base-url: http://api.weatherstack.com
      access-key: YOUR_WEATHERSTACK_API_KEY
....

    failover:
      name: openweathermap
      base-url: http://api.openweathermap.org/data/2.5
      access-key: YOUR_OPENWEATHERMAP_API_KEY
```

### 3. Build and Run the Application
Use the following commands to build and run the application:
```bash
./mvnw clean install
./mvnw spring-boot:run
```
The application will start on `http://localhost:8080`.

### 4. Run using the jar file
Alternatively, you can run the jar file inside the target directory:
```bash
java -jar target/weatherservice-1.0.jar
```

## How to Access the API
### 1. Fetch Weather Data
Send a `GET` request to the following endpoint:
```
GET /v1/weather?city={city}
```
Example Request:
```
curl -X GET "http://localhost:8080/v1/weather?city=Melbourne"
```
NOTE: 'Melbourne' is hard-coded.

Example Response:
```json
{
  "wind_speed": 10,
  "temperature_degrees": 20
}
```
### 2. Error Handling
If both providers fail and the failover cache already expired, the API will return a `503 Service Unavailable` status with the following response:
```json
{
  "statusCode": 503,
  "message": "No data available"
}
```
## Dependencies
- **Spring Boot Starter Web:** For building RESTful APIs.
- **Spring Boot Starter Cache:** For caching weather data.
- **Caffeine:** Cache provider.
- **Spring Boot Starter Test:** For testing the application.

## Improvements and Challenges
- Caffeine cache familiarity - I don't have much experience with Caffeine cache, 
so I had to spend some time understanding how it works and how to configure it properly for the use case.
- Handling other errors - For this task, I just handled specific errors. If I had more time I would handle
other errors that the external API may return.
- Failover Cache/Data - The data returned if both providers failed can be served from Redis or DB instead
of the long-lived cache.

