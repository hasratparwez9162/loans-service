package com.bank.app.loans_service.service.Impl;

import com.bank.app.loans_service.entity.Loan;
import com.bank.core.entity.LoanNotification;
import com.bank.core.entity.LoanType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class LoanEventProducer {
    private static final String TOPIC = "loan-service-topic";



    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    public void sendLoanIssuedMessage(Loan loan)  {
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan,loanNotification);
        kafkaTemplate.send(TOPIC,"loan_issued", loanNotification);
    }

    public void sendLoanStatusUpdatedMessage(Loan loan)  {
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan,loanNotification);
        kafkaTemplate.send(TOPIC, "loan_status_updated",loanNotification);
    }

    public void sendLoanRepaidMessage(Loan loan)  {
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan,loanNotification);
        kafkaTemplate.send(TOPIC,"loan_repaid", loanNotification);
    }

    public void sendLoanUpdatedMessage(Loan loan)  {
        LoanNotification loanNotification = new LoanNotification();
        BeanUtils.copyProperties(loan,loanNotification);
        kafkaTemplate.send(TOPIC,"loan_details_updated", loanNotification);
    }
}
