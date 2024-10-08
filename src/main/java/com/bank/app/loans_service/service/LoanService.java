package com.bank.app.loans_service.service;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {

    Loan issueLoan(Loan loan);

    List<Loan> getLoansByUserId(Long userId);

    Loan updateLoanStatus(Long loanId, String newStatus);

    Loan repayLoan(Long loanId, BigDecimal paymentAmount);

    Loan updateLoan(Loan loan) throws ResourceNotFoundException;


}

