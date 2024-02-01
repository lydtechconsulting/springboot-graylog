package demo.producer;

import demo.event.DemoOutboundEvent;
import demo.properties.DemoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoProducer {
    @Autowired
    private final DemoProperties properties;

    @Autowired
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public SendResult<String, DemoOutboundEvent> sendMessage(DemoOutboundEvent event) {
        try {
            SendResult<String, DemoOutboundEvent> result = (SendResult) kafkaTemplate.send(properties.getOutboundTopic(), event).get();
            log.info("Emitted message with payload: " + event.getData());
            return result;
        } catch (Exception e) {
            String message = "Error sending message to topic " + properties.getOutboundTopic();
            log.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
