package com.bank.app.loans_service.controller;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;
import com.bank.app.loans_service.service.Impl.LoanEventProducer;
import com.bank.app.loans_service.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("loans")
public class LoansController {

        @Autowired
        private LoanService loanService;
        @Autowired
        LoanEventProducer loanEventProducer;

        @PostMapping("/issue")
        public ResponseEntity<Loan> issueLoan(@RequestBody Loan loan) {
            Loan newLoan = loanService.issueLoan(loan);
            // Send message after loan issue.
            loanEventProducer.sendLoanIssuedMessage(newLoan);
            return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable Long userId) {
            List<Loan> loans = loanService.getLoansByUserId(userId);
            return new ResponseEntity<>(loans, HttpStatus.OK);
        }

        @PutMapping("/status/{loanId}")
        public ResponseEntity<Loan> updateLoanStatus(@PathVariable Long loanId, @RequestParam String status) {
            Loan updatedLoan = loanService.updateLoanStatus(loanId, status);
            // Send message after update loan
            loanEventProducer.sendLoanStatusUpdatedMessage(updatedLoan);
            return new ResponseEntity<>(updatedLoan, HttpStatus.OK);
        }

        @PutMapping("/repay/{loanId}")
        public ResponseEntity<Loan> repayLoan(@PathVariable Long loanId, @RequestParam BigDecimal amount) {
            Loan repaidLoan = loanService.repayLoan(loanId, amount);
            // Send confirmation message for repay
            loanEventProducer.sendLoanRepaidMessage(repaidLoan);
            return new ResponseEntity<>(repaidLoan, HttpStatus.OK);
        }
        @PutMapping("/update/loan")
        public ResponseEntity<Loan> updateLoanDetails(@RequestBody Loan loan) throws ResourceNotFoundException {
            Loan updatedLoan = loanService.updateLoan(loan);
            // Send message with Update details of loan
            loanEventProducer.sendLoanUpdatedMessage(updatedLoan);
            return new ResponseEntity<>(updatedLoan,HttpStatus.OK);
        }

}
