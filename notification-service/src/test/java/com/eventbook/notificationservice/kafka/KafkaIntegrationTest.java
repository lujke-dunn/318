package com.eventbook.notificationservice.kafka;

import com.eventbook.notificationservice.infrastructure.kafka.NotificationProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class KafkaIntegrationTest {

    @Autowired
    private NotificationProducer producer;

    @Test
    public void testSendMessage() throws InterruptedException {
        String topic = "event-topic";
        String message = "{\"userId\":\"user123\",\"eventId\":\"event456\",\"eventName\":\"Test Concert\"}";
        
        producer.sendMessage(topic, message);
        
        // Add assertions or verifications here
        // You might want to add a way to verify that your consumers processed the message
        Thread.sleep(1000); // Give some time for the message to be processed
    }
}
