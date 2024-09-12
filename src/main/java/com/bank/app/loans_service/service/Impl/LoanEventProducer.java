package com.bank.app.loans_service.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class LoanEventProducer {
    private static final String TOPIC = "loan-service-topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLoanIssuedMessage(Object message) {
        kafkaTemplate.send(TOPIC, "loan_issued", message);
    }

    public void sendLoanStatusUpdatedMessage(Object message) {
        kafkaTemplate.send(TOPIC, "loan_status_updated", message);
    }

    public void sendLoanRepaidMessage(Object message) {
        kafkaTemplate.send(TOPIC, "loan_repaid", message);
    }

    public void sendLoanUpdatedMessage(Object message) {
        kafkaTemplate.send(TOPIC, "loan_details_updated", message);
    }
}
