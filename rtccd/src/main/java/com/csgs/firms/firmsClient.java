package com.csgs.firms;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import io.github.cdimascio.dotenv.Dotenv;

public class firmsClient {
    private Dotenv dotenv = Dotenv.load();
    private final String MAP_KEY = dotenv.get("FIRMS_KEY");
    private final String BASE_URL = "https://firms.modaps.eosdis.nasa.gov/api/area/csv/" + MAP_KEY + "/VIIRS_NOAA20_NRT/world/1";

    private final HttpClient client;

    public firmsClient(){
        client = HttpClient.newHttpClient();
    }

    public String findAll() throws IOException, InterruptedException{
       HttpRequest request =  HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    
}
