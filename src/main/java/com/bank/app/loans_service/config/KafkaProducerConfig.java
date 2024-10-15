package com.bank.app.loans_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerConfig.class);

    @Bean
    public NewTopic createTopic() {
        logger.info("Creating new Kafka topic: loan-service-topic");
        return new NewTopic("loan-service-topic", 3, (short) 1);
    }

    @Bean
    public Map<String, Object> producerConfig() {
        logger.info("Configuring Kafka producer properties");
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        logger.info("Kafka producer properties configured: {}", props);
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        logger.info("Creating Kafka ProducerFactory");
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        logger.info("Creating KafkaTemplate");
        return new KafkaTemplate<>(producerFactory());
    }
}