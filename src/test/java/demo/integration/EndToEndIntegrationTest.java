package demo.integration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import demo.DemoConfiguration;
import demo.event.DemoInboundEvent;
import demo.event.DemoOutboundEvent;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static demo.integration.EndToEndIntegrationTest.DEMO_INBOUND_TEST_TOPIC;
import static demo.integration.EndToEndIntegrationTest.DEMO_OUTBOUND_TEST_TOPIC;
import static demo.util.TestEventData.buildDemoInboundEvent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@SpringBootTest(classes = { DemoConfiguration.class } )
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(controlledShutdown = true, topics = { DEMO_INBOUND_TEST_TOPIC, DEMO_OUTBOUND_TEST_TOPIC })
public class EndToEndIntegrationTest {

    protected final static String DEMO_INBOUND_TEST_TOPIC = "demo-inbound-topic";
    protected final static String DEMO_OUTBOUND_TEST_TOPIC = "demo-outbound-topic";

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTestListener testReceiver;

    @Configuration
    static class TestConfig {

        /**
         * The test listener.
         */
        @Bean
        public KafkaTestListener testReceiver() {
            return new KafkaTestListener();
        }
    }

    /**
     * Use this receiver to consume messages from the outbound topic.
     */
    public static class KafkaTestListener {
        AtomicInteger counter = new AtomicInteger(0);

        @KafkaListener(groupId = "KafkaIntegrationTest", topics = DEMO_OUTBOUND_TEST_TOPIC, autoStartup = "true")
        void receive(@Payload final DemoOutboundEvent event) {
            log.debug("KafkaTestListener - Received message: outbound data: " + event.getData());
            assertThat(event.getData(), notNullValue());
            counter.incrementAndGet();
        }
    }

    @BeforeEach
    public void setUp() {
        // Wait until the partitions are assigned.
        registry.getListenerContainers().stream().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
        testReceiver.counter.set(0);
    }

    /**
     * Send in a single event and ensure an outbound event is emitted.  Assert the outbound event is as expected.
     */
    @Test
    public void testSuccess_SingleEvent() throws Exception {
        UUID payloadId = UUID.randomUUID();
        DemoInboundEvent inboundPayload = buildDemoInboundEvent();
        kafkaTemplate.send(DEMO_INBOUND_TEST_TOPIC, inboundPayload).get();

        Awaitility.await().atMost(1, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testReceiver.counter::get, equalTo(1));

        assertThat(testReceiver.counter.get(), equalTo(1));
    }

    /**
     * Send in multiple events and ensure an outbound event is emitted for each.
     */
    @Test
    public void testSuccess_MultipleEvents() throws Exception {
        int totalMessages = 10;
        for (int i=0; i<totalMessages; i++) {
            UUID payloadId = UUID.randomUUID();
            DemoInboundEvent inboundPayload = buildDemoInboundEvent();
            kafkaTemplate.send(DEMO_INBOUND_TEST_TOPIC, inboundPayload).get();
        }
        Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(testReceiver.counter::get, equalTo(totalMessages));
    }
}
