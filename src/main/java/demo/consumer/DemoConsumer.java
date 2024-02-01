package demo.consumer;

import demo.event.DemoInboundEvent;
import demo.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DemoConsumer {

    final DemoService demoService;

    @KafkaListener(
            topics = "demo-inbound-topic",
            groupId = "demo-consumer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload DemoInboundEvent inboundEvent) {
        log.info("Received message - payload: " + inboundEvent.getData());
        try {
            demoService.process(inboundEvent);
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
        }
    }
}
