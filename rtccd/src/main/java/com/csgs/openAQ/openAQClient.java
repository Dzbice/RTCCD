package com.csgs.openAQ;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.csgs.Coordinates;

import io.github.cdimascio.dotenv.Dotenv;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


public class openAQClient {
    private Dotenv dotenv = Dotenv.load();
    

    private final String APIKEY =dotenv.get("OPENAQ_KEY");
    private final String BASEURL ="https://api.openaq.org/v3/locations?bbox=-76.403,36.760,-76.177,36.940";
    private final HttpClient client;

    public openAQClient(){
        client = HttpClient.newHttpClient();
    }

    public String findAll() throws IOException, InterruptedException{
       HttpRequest request =  HttpRequest.newBuilder()
            .uri(URI.create(BASEURL))
            .header("x-api-key", APIKEY)
            
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); 
        return response.body();
    }
    public void parselocationJson(String json) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        JsonNode resultsNode = rootNode.get("results");
        int sensorID =0;
        for (JsonNode locationNode : resultsNode) {
            int id  = locationNode.get("id").asInt();
            String city = locationNode.get("locality").asText();
            Coordinates coordinates = new Coordinates(
                locationNode.get("coordinates").get("latitude").asDouble(),
                locationNode.get("coordinates").get("longitude").asDouble()
            );
            for(JsonNode sensor: locationNode.get("sensors")){
                String paramName = sensor.get("parameter").get("name").asText();
                if(paramName.equals("pm25")){
                    sensorID = sensor.get("id").asInt();
                }
            }
        }
    }

    public String readings() throws IOException, InterruptedException{
       HttpRequest request =  HttpRequest.newBuilder()
            .uri(URI.create("https://api.openaq.org/v3/locations/494922/latest"))
            .header("x-api-key", APIKEY)
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); 
        return response.body();
    }
    
    

}