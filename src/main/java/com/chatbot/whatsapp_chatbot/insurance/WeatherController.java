package com.chatbot.whatsapp_chatbot.insurance;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam String city) {
        System.out.println("Received request for city: " + city);

        // This calls the WeatherService, which makes the HTTP call
        String weatherInfo = weatherService.getWeather(city);

        return weatherInfo;
    }

    @GetMapping("/weather/test")
    public String testWeather() {
        // Hardcoded test with London
        return weatherService.getWeather("London");
    }
}

