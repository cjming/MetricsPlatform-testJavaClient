package com.testjava.metrics_platform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.wikimedia.metrics_platform.EventSender;
import org.wikimedia.metrics_platform.config.DestinationEventService;
import org.wikimedia.metrics_platform.event.Event;

import com.google.gson.Gson;

public class TestJavaEventSender implements EventSender {
    @Override
    public void sendEvents(String baseUri, Collection<Event> events) throws MalformedURLException {
        URL url = isValid(baseUri) ? new URL(baseUri) : new URL(DestinationEventService.LOCAL.getBaseUri());

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            for (Event event : events) {
                String payload = new Gson().toJson(event);
                System.out.println(payload);
                byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
                try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
                    dos.write(postData, 0, postData.length);
                    System.out.println("Posted data to local event logging!");
                }
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder content = new StringBuilder();
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        content.append(inputLine.trim());
                    }
                    System.out.println(content);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValid(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
