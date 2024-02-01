package demo.util;

import demo.event.DemoInboundEvent;
import demo.event.DemoOutboundEvent;

public class TestEventData {

    public static String INBOUND_DATA = "inbound event data";
    public static String OUTBOUND_DATA = "outbound event data";

    public static DemoInboundEvent buildDemoInboundEvent() {
        return DemoInboundEvent.builder()
                .data(INBOUND_DATA)
                .build();
    }

    public static DemoOutboundEvent buildDemoOutboundEvent() {
        return DemoOutboundEvent.builder()
                .data(OUTBOUND_DATA)
                .build();
    }
}
