package demo.service;

import demo.event.DemoInboundEvent;
import demo.event.DemoOutboundEvent;
import demo.producer.DemoProducer;
import demo.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DemoServiceTest {

    private DemoProducer mockKafkaDemoProducer;
    private DemoService service;

    @BeforeEach
    public void setUp() {
        mockKafkaDemoProducer = mock(DemoProducer.class);
        service = new DemoService(mockKafkaDemoProducer);
    }

    /**
     * Ensure the Kafka producer is called to emit a message.
     */
    @Test
    public void testProcess() {
        DemoInboundEvent testPayload = TestEventData.buildDemoInboundEvent();

        service.process(testPayload);

        verify(mockKafkaDemoProducer, times(1)).sendMessage(any(DemoOutboundEvent.class));
    }
}
