package com.csgs.firms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.csgs.Coordinates;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class firmsHandle implements HttpHandler {

    private final firmsClient client = new firmsClient();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {

            String query = exchange.getRequestURI().getQuery();
            double lat = 0;
            double lon = 0;

            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue[0].equals("lat")) {
                        lat = Double.parseDouble(keyValue[1]);
                    }
                    if (keyValue[0].equals("lon")) {
                        lon = Double.parseDouble(keyValue[1]);
                    }
                }
            }
            String raw = client.findAll(new Coordinates(lat, lon, 1)); // one call, larger bbox
            System.out.println("FIRMS raw: " + raw);
            List<Coordinates> coords = client.dataParse(raw); // reuse it
            System.out.println("Fires found: " + coords.size());

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < coords.size(); i++) {
                Coordinates c = coords.get(i);
                json.append("{")
                        .append("\"lat\":").append(c.getLatitude()).append(",")
                        .append("\"lon\":").append(c.getLongitude())
                        .append("}");
                if (i < coords.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            byte[] bytes = json.toString().getBytes();
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
