package com.csgs.firms;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.csgs.Coordinates;

import io.github.cdimascio.dotenv.Dotenv;

public class firmsClient {
    private Dotenv dotenv = Dotenv.load();
    private final String MAP_KEY = dotenv.get("FIRMS_KEY");
private final String BASE_URL =
    "https://firms.modaps.eosdis.nasa.gov/api/area/csv/"
    + MAP_KEY +
    "/MODIS_NRT/116.542969,-34.741612,146.777344,-17.811456/1";
    private final HttpClient client;

    public firmsClient(){
        client = HttpClient.newHttpClient();
    }

  public String findAll(Coordinates coords) throws IOException, InterruptedException {
    double[] bbox = coords.boundingBox();
    String url = "https://firms.modaps.eosdis.nasa.gov/api/area/csv/"
        + MAP_KEY
        + "/MODIS_NRT/"
        + bbox[1] + "," + bbox[0] + "," + bbox[3] + "," + bbox[2]
        + "/1";
    
    System.out.println("FIRMS URL: " + url); // debug
    
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(10)) // give up after 10 seconds
        .GET()
        .build();
    
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println("FIRMS status: " + response.statusCode());
    System.out.println("FIRMS raw: " + response.body().substring(0, Math.min(200, response.body().length())));
    return response.body();
}

    public String findAll(double[] bbox) throws IOException, InterruptedException {
    String url = "https://firms.modaps.eosdis.nasa.gov/api/area/csv/"
        + MAP_KEY +
        "/MODIS_NRT/"
        + bbox[1] + "," + bbox[0] + "," + bbox[3] + "," + bbox[2]
        + "/1";
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("x-api-key", MAP_KEY)
        .GET()
        .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
}

    public List<Coordinates> dataParse(String data){
        String[] lines = data.split("\n");
        List<Coordinates> coordsList = new ArrayList<>();
        int x = 0;
        for (String line : lines) {
            if (x == 0) {
                x++;
                continue;
            }
         //   System.out.println(line);
            x++;
            String[] parts = line.split(",");
            if (parts.length < 2) continue; 
               try {
            double lat = Double.parseDouble(parts[0]);
            double lon = Double.parseDouble(parts[1]);
            coordsList.add(new Coordinates(lat, lon));
        } catch (NumberFormatException e) {
            continue; // skip unparseable lines
        }
        }
        return coordsList;
    }



    
}
