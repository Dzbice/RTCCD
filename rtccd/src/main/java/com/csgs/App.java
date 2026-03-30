package com.csgs;

import java.net.InetSocketAddress;

import com.csgs.firms.firmsHandle;
import com.csgs.geocode.GeocodeHandler;
import com.csgs.openAQ.AQhandle;
import com.sun.net.httpserver.HttpServer;

public class App {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/aqi", new AQhandle());
        server.createContext("/api/firms",new firmsHandle());
        server.createContext("/api/geocode", new GeocodeHandler());
        server.start();
    }
}
