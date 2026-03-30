package com.csgs.openAQ;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AQhandle implements HttpHandler{
    private final openAQClient client = new openAQClient();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            List<AQIData> data = client.parseLocationJson(client.findAll());

            // manually build JSON string from AQIData objects
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < data.size(); i++) {
                AQIData d = data.get(i);
                json.append("{")
                    .append("\"location\":\"").append(d.getLocation()).append("\",")
                    .append("\"lat\":").append(d.getCoordinates().getLatitude()).append(",")
                    .append("\"lon\":").append(d.getCoordinates().getLongitude()).append(",")
                    .append("\"pm25\":").append(d.getPm25()).append(",")
                    .append("\"aqi\":").append(d.getAQI())
                    .append("}");
                if (i < data.size() - 1) json.append(",");
            }
            json.append("]");

            // CORS header so your frontend can call it
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, json.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(json.toString().getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }

}
