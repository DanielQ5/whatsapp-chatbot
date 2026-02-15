package com.chatbot.whatsapp_chatbot.insurance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    private static final String BASE_URL = "https://api.weatherapi.com/v1/current.json";

    private final Gson gson = new Gson();

    public String getWeather(String city) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = UriComponentsBuilder
                    .fromHttpUrl(BASE_URL)
                    .queryParam("key", apiKey)
                    .queryParam("q", city)
                    .queryParam("aqi", "no")
                    .toUriString();

            System.out.println("Making HTTP GET call to: " + url);

            String jsonResponse = restTemplate.getForObject(url, String.class);

            System.out.println("Response received:" + jsonResponse);

            return parseWeatherResponseWithGson(jsonResponse, city);

        } catch (HttpClientErrorException e) {
            System.err.println("HTTP Error: " + e.getStatusCode());
            System.err.println("Error Message: " + e.getMessage());
            return "Error: Could not fetch weather. " + e.getMessage();


        } catch (Exception e) {
            System.err.println("Error fetching weather: " + e.getMessage());
            return "Error " + e.getMessage();
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
            System.err.println("Error parsing JSON: " + e.getMessage());
            return "Error: Could not parse weather data. " + e.getMessage();
        }
    }
}
