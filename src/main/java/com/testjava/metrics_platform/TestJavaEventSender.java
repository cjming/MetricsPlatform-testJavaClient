package com.testjava.metrics_platform;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.wikimedia.metrics_platform.EventSender;
import org.wikimedia.metrics_platform.config.DestinationEventService;
import org.wikimedia.metrics_platform.event.Event;

import com.google.gson.Gson;

public class TestJavaEventSender implements EventSender {
    @Override
    public void sendEvents(String baseUri, Collection<Event> events) {
        try {
            URL url = new URL(DestinationEventService.LOCAL.getBaseUri());
            Event event = events.iterator().next();
            String payload = new Gson().toJson(event);
            System.out.println(payload);
            byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
            HttpURLConnection connection;

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.write(postData, 0, postData.length);
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
                    br.close();
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
