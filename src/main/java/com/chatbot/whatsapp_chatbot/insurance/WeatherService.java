package com.chatbot.whatsapp_chatbot.insurance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;
    private static final String BASE_URL = "https://api.weatherapi.com/v1/current.json";

    private final Gson gson = new Gson();

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getWeather(String city) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(BASE_URL)
                    .queryParam("key", apiKey)
                    .queryParam("q", city)
                    .queryParam("aqi", "no")
                    .toUriString();

            logger.debug("Making HTTP GET call for city: {}", city);

            String jsonResponse = restTemplate.getForObject(url, String.class);

            logger.debug("Weather response received for city: {}", city);

            return parseWeatherResponseWithGson(jsonResponse, city);

        } catch (HttpClientErrorException e) {
            logger.error("HTTP error fetching weather for city {}: {}", city, e.getStatusCode());
            return "No se pudo obtener el clima en este momento. Intenta de nuevo mas tarde.";

        } catch (Exception e) {
            logger.error("Error fetching weather for city {}", city, e);
            return "No se pudo obtener el clima en este momento. Intenta de nuevo mas tarde.";
        }
    }

    private String parseWeatherResponseWithGson(String jsonResponse, String city) {
        try {
            JsonObject root = gson.fromJson(jsonResponse, JsonObject.class);

            JsonObject current = root.getAsJsonObject("current");
            JsonObject condition = current.getAsJsonObject("condition");

            // Extract values using proper JSON parsing
            double temp = current.get("temp_c").getAsDouble();
            double feelsLike = current.get("feelslike_c").getAsDouble();
            String conditionText = condition.get("text").getAsString();
            int humidity = current.get("humidity").getAsInt();

            // Optional: Get location info
            JsonObject location = root.getAsJsonObject("location");
            String locationName = location.get("name").getAsString();
            String country = location.get("country").getAsString();

            // Format the response
            return String.format(
                    "🌤️ Weather in %s, %s:\n" +
                            "Temperature: %.1f°C\n" +
                            "Feels like: %.1f°C\n" +
                            "Condition: %s\n" +
                            "Humidity: %d%%",
                    locationName, country, temp, feelsLike, conditionText, humidity
            );

        } catch (Exception e) {
            logger.error("Error parsing weather JSON for city {}", city, e);
            return "No se pudo procesar la informacion del clima.";
        }
    }
}
