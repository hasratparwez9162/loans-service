package com.bank.app.loans_service.service;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {

    /**
     * Issue a new loan.
     * @param loan The loan details.
     * @return The newly created loan.
     */
    Loan issueLoan(Loan loan);

    /**
     * Get loans by user ID.
     * @param userId The ID of the user.
     * @return A list of loans belonging to the user.
     */
    List<Loan> getLoansByUserId(Long userId);

    /**
     * Update loan status.
     * @param loanId The ID of the loan.
     * @param newStatus The new status of the loan.
     * @return The updated loan.
     */
    Loan updateLoanStatus(Long loanId, String newStatus);

    /**
     * Repay a loan.
     * @param loanId The ID of the loan.
     * @param paymentAmount The amount to be paid.
     * @return The updated loan.
     */
    Loan repayLoan(Long loanId, BigDecimal paymentAmount);

    /**
     * Update loan details.
     * @param loan The loan details.
     * @return The updated loan.
     * @throws ResourceNotFoundException if the loan is not found.
     */
    Loan updateLoan(Loan loan) throws ResourceNotFoundException;
}