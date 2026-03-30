package com.csgs.geocode;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.csgs.Coordinates;

import io.github.cdimascio.dotenv.Dotenv;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class geoclient {

    private Dotenv dotenv = Dotenv.load();
    private final String MAP_KEY = dotenv.get("GEO_KEY");
    private final HttpClient client;

    public geoclient() {
        client = HttpClient.newHttpClient();
    }

    public String findAll(String city) throws IOException, InterruptedException {
        String encodedCity = URLDecoder.decode(city, StandardCharsets.UTF_8);
        String url = "http://api.openweathermap.org/geo/1.0/direct?q=" 
            + encodedCity + "&limit=1&appid=" + MAP_KEY;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public Coordinates getCoordinates(String city) throws IOException, InterruptedException {
        String json = findAll(city);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        if (!root.isArray() || root.size() == 0) {
            return null;
        }

        JsonNode first = root.get(0);

        double lat = first.get("lat").asDouble();
        double lon = first.get("lon").asDouble();

        return new Coordinates(lat, lon);
    }
}