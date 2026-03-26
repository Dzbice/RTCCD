package com.csgs;
import com.csgs.firms.firmsClient;
public class App {
    public static void main(String[] args) throws Exception {
        firmsClient client = new firmsClient();
        //client.parseJson(client.findAll());
        System.out.println(client.findAll());
    }
}
