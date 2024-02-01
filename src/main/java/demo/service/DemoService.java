package demo.service;

import demo.event.DemoInboundEvent;
import demo.event.DemoOutboundEvent;
import demo.producer.DemoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemoService {

    @Autowired
    private final DemoProducer kafkaDemoProducer;

    public void process(DemoInboundEvent inboundEvent) {
        DemoOutboundEvent outboundEvent = DemoOutboundEvent.builder()
                .data("Processed: " + inboundEvent.getData())
                .build();
        kafkaDemoProducer.sendMessage(outboundEvent);
    }
}
