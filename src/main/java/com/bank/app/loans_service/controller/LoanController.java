package com.bank.app.loans_service.controller;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;
import com.bank.app.loans_service.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/loan")
@Tag(name = "Loan Controller", description = "Loan Management System")
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Issue a new loan.
     * @param loan The loan details.
     * @return A response entity with the newly created loan.
     */
    @PostMapping("/issue")
    @Operation(summary = "Issue a new loan", description = "Issue a new loan", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Loan issued successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Loan> issueLoan(@RequestBody Loan loan) {
        logger.info("Issuing new loan for user: {}", loan.getUserId());
        try {
            Loan newLoan = loanService.issueLoan(loan);
            logger.info("Loan issued successfully with loanId: {}", newLoan.getId());
            return new ResponseEntity<>(newLoan, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error issuing loan for user: {}", loan.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get loans by user ID.
     * @param userId The ID of the user.
     * @return A response entity with a list of loans.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get loans by user ID", description = "Get loans by user ID", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loans fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No loans found")
    })
    public ResponseEntity<List<Loan>> getLoansByUserId(@PathVariable Long userId) {
        logger.info("Fetching loans for user ID: {}", userId);
        List<Loan> loans = loanService.getLoansByUserId(userId);
        if (loans == null || loans.isEmpty()) {
            logger.warn("No loans found for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        logger.info("Loans fetched successfully for user ID: {}", userId);
        return ResponseEntity.ok(loans);
    }

    /**
     * Update loan status.
     * @param loanId The ID of the loan.
     * @param newStatus The new status of the loan.
     * @return A response entity with the updated loan.
     */
    @PutMapping("/status/{loanId}")
    @Operation(summary = "Update loan status", description = "Update loan status", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan status updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Loan> updateLoanStatus(@PathVariable Long loanId, @RequestParam String newStatus) {
        logger.info("Updating loan status for loanId: {} to status: {}", loanId, newStatus);
        try {
            Loan updatedLoan = loanService.updateLoanStatus(loanId, newStatus);
            if (updatedLoan == null) {
                logger.warn("Loan not found for loanId: {}", loanId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Loan status updated successfully for loanId: {}", loanId);
            return ResponseEntity.ok(updatedLoan);
        } catch (Exception e) {
            logger.error("Error updating loan status for loanId: {}", loanId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Repay a loan.
     * @param loanId The ID of the loan.
     * @param paymentAmount The amount to be paid.
     * @return A response entity with the updated loan.
     */
    @PutMapping("/repay/{loanId}")
    @Operation(summary = "Repay a loan", description = "Repay a loan", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan repaid successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Loan> repayLoan(@PathVariable Long loanId, @RequestParam BigDecimal paymentAmount) {
        logger.info("Repaying loan for loanId: {} with amount: {}", loanId, paymentAmount);
        try {
            Loan repaidLoan = loanService.repayLoan(loanId, paymentAmount);
            if (repaidLoan == null) {
                logger.warn("Loan not found or already repaid for loanId: {}", loanId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logger.info("Loan repaid successfully for loanId: {}", loanId);
            return ResponseEntity.ok(repaidLoan);
        } catch (Exception e) {
            logger.error("Error repaying loan for loanId: {}", loanId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Update loan details.
     * @param loan The loan details.
     * @return A response entity with the updated loan.
     */
    @PutMapping("/update")
    @Operation(summary = "Update loan details", description = "Update loan details", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Loan updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Loan not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Loan> updateLoan(@RequestBody Loan loan) {
        logger.info("Updating loan details for loanId: {}", loan.getId());
        try {
            Loan updatedLoan = loanService.updateLoan(loan);
            logger.info("Loan details updated successfully for loanId: {}", loan.getId());
            return ResponseEntity.ok(updatedLoan);
        } catch (ResourceNotFoundException e) {
            logger.error("Loan not found with id: {}", loan.getId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Error updating loan details for loanId: {}", loan.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}