package com.testjava.metrics_platform;

import static com.testjava.metrics_platform.TestJavaClientMetadata.createTestJavaClientMetadata;
import static org.wikimedia.metrics_platform.config.StreamConfigFetcher.ANALYTICS_API_ENDPOINT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.wikimedia.metrics_platform.*;
import org.wikimedia.metrics_platform.config.*;
import org.wikimedia.metrics_platform.curation.CollectionCurationRules;
import org.wikimedia.metrics_platform.curation.CurationRules;
import org.wikimedia.metrics_platform.event.Event;

public class TestJava {
    private static final CurationFilter curationFilter = getCurationFilter();

    public static void main(String[] args) throws IOException {
        System.out.println("Testing the Java client:");
        submitEvent();
        System.out.println("Event submitted using the Java client :)");
    }

    public static void submitEvent() throws IOException {
        ClientMetadata testJavaClientMetadata = createTestJavaClientMetadata();
        Path pathStreamConfigs = Paths.get("../testJava/data/stream_configs.json");

        try (BufferedReader reader = Files.newBufferedReader(pathStreamConfigs)) {
            Map<String, StreamConfig> testStreamConfigs = getTestStreamConfigs(reader);

            SourceConfig sourceConfig = new SourceConfig(testStreamConfigs);
            AtomicReference<SourceConfig> sourceConfigRef = new AtomicReference<>();
            sourceConfigRef.set(sourceConfig);
            BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>(10);

            EventProcessor testJavaEventProcessor = getTestEventProcessor(
                    testJavaClientMetadata,
                    sourceConfigRef,
                    eventQueue
            );

            MetricsClient testJavaMetricsClient = getTestMetricsClient(
                    testJavaClientMetadata,
                    sourceConfigRef,
                    eventQueue
            );

            Event event = new Event("test/event/1.0.8", "test.stream", "test_java_event");
            Map<String, Object> customData = new HashMap<>();
            customData.put("action", "scroll");

            testJavaMetricsClient.dispatch(
                    "test_java_event",
                    customData
            );

            testJavaEventProcessor.sendEnqueuedEvents();

        }
    }

    private static Map<String, StreamConfig> getTestStreamConfigs(Reader reader) throws MalformedURLException {
        StreamConfigFetcher streamConfigFetcher = new StreamConfigFetcher(new URL(ANALYTICS_API_ENDPOINT));
        return streamConfigFetcher.parseConfig(reader);

    }

    private static MetricsClient getTestMetricsClient(
            ClientMetadata testClientMetadata,
            AtomicReference<SourceConfig> sourceConfigRef,
            BlockingQueue<Event> eventQueue
    ) {
        SessionController sessionController = new SessionController();
        SamplingController samplingController = new SamplingController(testClientMetadata, sessionController);

        return new MetricsClient(
                testClientMetadata,
                sessionController,
                samplingController,
                sourceConfigRef,
                eventQueue
        );
    }

    private static EventProcessor getTestEventProcessor(
            ClientMetadata testClientMetadata,
            AtomicReference<SourceConfig> sourceConfigRef,
            BlockingQueue<Event> eventQueue
    ) {
        ContextController contextController = new ContextController(testClientMetadata);
        CurationController curationController = new CurationController();

        return new EventProcessor(
                contextController,
                curationController,
                sourceConfigRef,
                new TestJavaEventSender(),
                eventQueue
        );
    }

    public static CurationFilter getCurationFilter() {
        return CurationFilter.builder()
                .pageTitleRules(CurationRules.<String>builder().isEquals("Test").build())
                .performerGroupsRules(
                        CollectionCurationRules.<String>builder()
                                .doesNotContain("sysop")
                                .containsAny(Arrays.asList("steward", "bureaucrat"))
                                .build()
                )
                .build();
    }
}