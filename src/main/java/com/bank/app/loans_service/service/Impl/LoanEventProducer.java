package com.bank.app.loans_service.service.Impl;

import com.bank.app.loans_service.entity.Loan;
import com.bank.core.entity.LoanNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoanEventProducer {
    private static final String TOPIC = "loan-service-topic";
    private static final Logger logger = LoggerFactory.getLogger(LoanEventProducer.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLoanIssuedMessage(Loan loan) {
        logger.info("Sending loan issued message for loanId: {}", loan.getId());
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan, loanNotification);
        kafkaTemplate.send(TOPIC, "loan_issued", loanNotification);
        logger.info("Loan issued message sent for loanId: {}", loan.getId());
    }

    public void sendLoanStatusUpdatedMessage(Loan loan) {
        logger.info("Sending loan status updated message for loanId: {}", loan.getId());
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan, loanNotification);
        kafkaTemplate.send(TOPIC, "loan_status_updated", loanNotification);
        logger.info("Loan status updated message sent for loanId: {}", loan.getId());
    }

    public void sendLoanRepaidMessage(Loan loan) {
        logger.info("Sending loan repaid message for loanId: {}", loan.getId());
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan, loanNotification);
        kafkaTemplate.send(TOPIC, "loan_repaid", loanNotification);
        logger.info("Loan repaid message sent for loanId: {}", loan.getId());
    }

    public void sendLoanUpdatedMessage(Loan loan) {
        logger.info("Sending loan updated message for loanId: {}", loan.getId());
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan, loanNotification);
        kafkaTemplate.send(TOPIC, "loan_details_updated", loanNotification);
        logger.info("Loan updated message sent for loanId: {}", loan.getId());
    }
}