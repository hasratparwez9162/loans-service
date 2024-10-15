package com.bank.app.loans_service.controller;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;
import com.bank.app.loans_service.service.Impl.LoanEventProducer;
import com.bank.app.loans_service.service.LoanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("loans")
@Api(value = "Loans Management System")
public class LoansController {

    private static final Logger logger = LoggerFactory.getLogger(LoansController.class);

    @Autowired
    private LoanService loanService;
    @Autowired
    LoanEventProducer loanEventProducer;

    @PostMapping("/issue")
    @ApiOperation(value = "Issue a new loan")
    public ResponseEntity<Loan> issueLoan(@RequestBody Loan loan) throws JsonProcessingException {
        logger.info("Issuing a new loan for userId: {}", loan.getUserId());
        Loan newLoan = loanService.issueLoan(loan);
        loanEventProducer.sendLoanIssuedMessage(newLoan);
        logger.info("Loan issued successfully with loanId: {}", newLoan.getId());
        return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "Retrieve loans by user ID")
    public ResponseEntity<List<Loan>> getLoansByUserId(@ApiParam(value = "ID of the user to retrieve loans", required = true) @PathVariable Long userId) {
        logger.info("Retrieving loans for userId: {}", userId);
        List<Loan> loans = loanService.getLoansByUserId(userId);
        logger.info("Loans retrieved successfully for userId: {}", userId);
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @PutMapping("/status/{loanId}")
    @ApiOperation(value = "Update loan status")
    public ResponseEntity<Loan> updateLoanStatus(@ApiParam(value = "ID of the loan to update", required = true) @PathVariable Long loanId,
                                                 @ApiParam(value = "New status of the loan", required = true) @RequestParam String status) throws JsonProcessingException {
        logger.info("Updating loan status for loanId: {} to status: {}", loanId, status);
        Loan updatedLoan = loanService.updateLoanStatus(loanId, status);
        loanEventProducer.sendLoanStatusUpdatedMessage(updatedLoan);
        logger.info("Loan status updated successfully for loanId: {}", loanId);
        return new ResponseEntity<>(updatedLoan, HttpStatus.OK);
    }

    @PutMapping("/repay/{loanId}")
    @ApiOperation(value = "Repay loan")
    public ResponseEntity<Loan> repayLoan(@ApiParam(value = "ID of the loan to repay", required = true) @PathVariable Long loanId,
                                          @ApiParam(value = "Amount to repay", required = true) @RequestParam BigDecimal amount) throws JsonProcessingException {
        logger.info("Repaying loan for loanId: {} with amount: {}", loanId, amount);
        Loan repaidLoan = loanService.repayLoan(loanId, amount);
        loanEventProducer.sendLoanRepaidMessage(repaidLoan);
        logger.info("Loan repaid successfully for loanId: {}", loanId);
        return new ResponseEntity<>(repaidLoan, HttpStatus.OK);
    }

    @PutMapping("/update/loan")
    @ApiOperation(value = "Update loan details")
    public ResponseEntity<Loan> updateLoanDetails(@RequestBody Loan loan) throws ResourceNotFoundException, JsonProcessingException {
        logger.info("Updating loan details for loanId: {}", loan.getId());
        Loan updatedLoan = loanService.updateLoan(loan);
        loanEventProducer.sendLoanUpdatedMessage(updatedLoan);
        logger.info("Loan details updated successfully for loanId: {}", loan.getId());
        return new ResponseEntity<>(updatedLoan, HttpStatus.OK);
    }
}