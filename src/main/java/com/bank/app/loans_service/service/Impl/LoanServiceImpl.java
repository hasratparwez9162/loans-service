package com.bank.app.loans_service.service.Impl;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.repo.LoansRepository;
import com.bank.app.loans_service.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoansRepository loanRepository;

    @Override
    public Loan issueLoan(Loan loan) {
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(loan.getTenureMonths()));
        loan.setRemainingBalance(loan.getLoanAmount());
        loan.setLoanStatus("ACTIVE");

        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    @Override
    public Loan updateLoanStatus(Long loanId, String newStatus) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null) {
            loan.setLoanStatus(newStatus);
            loanRepository.save(loan);
        }
        return loan;
    }

    @Override
    public Loan repayLoan(Long loanId, BigDecimal paymentAmount) {
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null && loan.getRemainingBalance().compareTo(BigDecimal.ZERO) > 0) {
            loan.setRemainingBalance(loan.getRemainingBalance().subtract(paymentAmount));
            if (loan.getRemainingBalance().compareTo(BigDecimal.ZERO) <= 0) {
                loan.setLoanStatus("CLOSED");
            }
            loanRepository.save(loan);
        }
        return loan;
    }
}
