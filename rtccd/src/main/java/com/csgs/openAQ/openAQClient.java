package com.csgs.openAQ;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.csgs.Coordinates;

import io.github.cdimascio.dotenv.Dotenv;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class openAQClient {

    private Dotenv dotenv = Dotenv.load();

    private final String APIKEY = dotenv.get("OPENAQ_KEY");
    private final String BASEURL = "https://api.openaq.org/v3/locations?bbox=-116.542969,-34.741612,146.777344,-17.811456";
    private final HttpClient client;

    public openAQClient() {
        client = HttpClient.newHttpClient();
    }

    public String findAll() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASEURL))
                .header("x-api-key", APIKEY)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String findAll(Coordinates coords) throws IOException, InterruptedException {
        double[] bbox = coords.boundingBox();
        String url = "https://api.openaq.org/v3/locations?bbox="
                + bbox[1] + "," + bbox[0] + "," + bbox[3] + "," + bbox[2];
        //System.out.println("URL: " + url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", APIKEY)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // bbox = {west, south, east, north}
    public String findAll(double[] bbox) throws IOException, InterruptedException {
        String url = "https://api.openaq.org/v3/locations?bbox="
                + bbox[0] + "," + bbox[1] + "," + bbox[2] + "," + bbox[3];
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", APIKEY)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public List<AQIData> parselocationJson(String json) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        JsonNode resultsNode = rootNode.get("results");
        List<AQIData> aqiList = new ArrayList<>();

        for (JsonNode locationNode : resultsNode) {
            int id = locationNode.get("id").asInt();
            String city = locationNode.get("locality").asText();
            Coordinates coordinates = new Coordinates(
                    locationNode.get("coordinates").get("latitude").asDouble(),
                    locationNode.get("coordinates").get("longitude").asDouble()
            );
            int pm25SensorId = -1;
            for (JsonNode sensor : locationNode.get("sensors")) {
                if (sensor.get("parameter").get("name").asText().equals("pm25")) {
                    pm25SensorId = sensor.get("id").asInt();
                    break;
                }
            }
            if (pm25SensorId == -1) {
                continue;
            }

            String latestJson = readings(id);
            double pm25 = parsePm25(latestJson, pm25SensorId);
            if (pm25 == -1) {
                continue;
            }
            aqiList.add(new AQIData(city, coordinates, pm25));

        }
        return aqiList;
    }

    public String readings(int locationId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openaq.org/v3/locations/" + locationId + "/latest"))
                .header("x-api-key", APIKEY)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public double parsePm25(String json, int pm25SensorId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode results = root.get("results");

        if (results == null || results.isEmpty()) {
            return -1; // guard against null/empty
        }
        for (JsonNode reading : results) {
            if (reading.get("sensorsId").asInt() == pm25SensorId) {
                return reading.get("value").asDouble();
            }
        }
        return -1;
    }

}
