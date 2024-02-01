package demo.consumer;

import demo.event.DemoInboundEvent;
import demo.service.DemoService;
import demo.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DemoConsumerTest {

    private DemoService serviceMock;
    private DemoConsumer consumer;

    @BeforeEach
    public void setUp() {
        serviceMock = mock(DemoService.class);
        consumer = new DemoConsumer(serviceMock);
    }

    /**
     * Ensure that the JSON message is successfully passed on to the service.
     */
    @Test
    public void testListen_Success() {
        DemoInboundEvent testPayload = TestEventData.buildDemoInboundEvent();

        consumer.listen(testPayload);

        verify(serviceMock, times(1)).process(testPayload);
    }

    /**
     * If an exception is thrown, an error is logged but the processing completes successfully.
     *
     * This ensures the consumer offsets are updated so that the message is not redelivered.
     */
    @Test
    public void testListen_ServiceThrowsException() {
        DemoInboundEvent testPayload = TestEventData.buildDemoInboundEvent();

        doThrow(new RuntimeException("Service failure")).when(serviceMock).process(testPayload);

        consumer.listen(testPayload);

        verify(serviceMock, times(1)).process(testPayload);
    }
}
