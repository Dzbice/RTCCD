package com.csgs.geocode;

import com.csgs.Coordinates;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class GeocodeHandler implements HttpHandler {
    private final geoclient client = new geoclient();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            String city = query.replace("city=", "");

            Coordinates coords = client.getCoordinates(city);

            String json;
            if (coords == null) {
                json = "{\"error\":\"not found\"}";
            } else {
                json = "{\"lat\":" + coords.getLatitude() + ",\"lon\":" + coords.getLongitude() + "}";
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            byte[] bytes = json.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }
}