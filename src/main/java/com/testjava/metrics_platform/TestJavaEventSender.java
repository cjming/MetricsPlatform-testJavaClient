package com.testjava.metrics_platform;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

    private final Gson gson = new Gson();

    @Override
    public void sendEvents(String baseUri, Collection<Event> events) throws IOException {
        URL url = isValid(baseUri) ? new URL(baseUri) : new URL(DestinationEventService.LOCAL.getBaseUri());

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.connect();

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), UTF_8))) {
                for (Event event : events) {
                    gson.toJson(event, writer);
                }
                System.out.println("Posted data to local event logging!");
            }

            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8))) {
                    br.lines().forEach(System.out::print);
                    System.out.println();
                }
            } else {
                throw new IOException("Error sending events, status code=" + status);
            }
        } finally {
            if (connection != null) connection.disconnect();
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
