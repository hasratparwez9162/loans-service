package com.bank.app.loans_service.service.Impl;


import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;
import com.bank.app.loans_service.repo.LoansRepository;
import com.bank.app.loans_service.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoansRepository loanRepository;

    @Override
    public Loan issueLoan(Loan loan) {
        // Set the basic loan details
        loan.setLoanNumber(generateLoanNumber());
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(loan.getTenureMonths()));
        loan.setLoanStatus("ACTIVE");
        // Calculate total interest
        BigDecimal totalInterest = (loan.getLoanAmount().multiply(loan.getInterestRate()).multiply(BigDecimal.valueOf(loan.getTenureMonths())))
                .divide(BigDecimal.valueOf(12 * 100), BigDecimal.ROUND_HALF_UP);
        // Set remaining balance to the loan (initial amount plus interest)
        loan.setRemainingBalance(loan.getLoanAmount().add(totalInterest));
        // Save the loan (this will also save the associated EMISchedule entries)
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



    @Override
    public Loan updateLoan(Loan inputLoan) throws ResourceNotFoundException {
        // Retrieve the existing loan from the repository by its ID
        Loan existingLoan = loanRepository.findById(inputLoan.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + inputLoan.getId()));

        // Update only the fields provided in the input
        if (inputLoan.getUserId() != null) {
            existingLoan.setUserId(inputLoan.getUserId());
        }
        if (inputLoan.getLoanAmount() != null) {
            existingLoan.setLoanAmount(inputLoan.getLoanAmount());
        }
        if (inputLoan.getLoanNumber() != null) {
            existingLoan.setLoanNumber(inputLoan.getLoanNumber());
        }
        if (inputLoan.getLoanType() != null) {
            existingLoan.setLoanType(inputLoan.getLoanType());
        }
        if (inputLoan.getInterestRate() != null) {
            existingLoan.setInterestRate(inputLoan.getInterestRate());
        }
        if (inputLoan.getTenureMonths() != 0) {
            existingLoan.setTenureMonths(inputLoan.getTenureMonths());
        }
        if (inputLoan.getStartDate() != null) {
            existingLoan.setStartDate(inputLoan.getStartDate());
        }
        if (inputLoan.getEndDate() != null) {
            existingLoan.setEndDate(inputLoan.getEndDate());
        }
        if (inputLoan.getRemainingBalance() != null) {
            existingLoan.setRemainingBalance(inputLoan.getRemainingBalance());
        }
        if (inputLoan.getLoanStatus() != null) {
            existingLoan.setLoanStatus(inputLoan.getLoanStatus());
        }

        return loanRepository.save(existingLoan);
    }

    private String generateLoanNumber() {
        Random random = new Random();
        int randomNumber = 100000000 + random.nextInt(900000000); // Generate a 9-digit number
        return "LN" + randomNumber;
    }
}
