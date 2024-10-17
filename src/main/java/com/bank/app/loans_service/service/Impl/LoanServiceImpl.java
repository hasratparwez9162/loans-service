package com.bank.app.loans_service.service.Impl;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;
import com.bank.app.loans_service.repo.LoansRepository;
import com.bank.app.loans_service.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    @Autowired
    private LoansRepository loanRepository;

    @Override
    public Loan issueLoan(Loan loan) {
        logger.info("Issuing loan for userId: {}", loan.getUserId());
        loan.setLoanNumber(generateLoanNumber());
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(loan.getTenureMonths()));
        loan.setLoanStatus("ACTIVE");


        // Set interest rate based on loan type if interest rate is null
        if (loan.getInterestRate() == null) {
            switch (loan.getLoanType()) {
                case PERSONAL:
                    loan.setInterestRate(new BigDecimal("12.0")); // Example interest rate for personal loan
                    break;
                case HOME:
                    loan.setInterestRate(new BigDecimal("8.5")); // Example interest rate for home loan
                    break;
                case AUTO:
                    loan.setInterestRate(new BigDecimal("10.0")); // Example interest rate for auto loan
                    break;
                default:
                    throw new IllegalArgumentException("Unknown loan type: " + loan.getLoanType());
            }
        }

        BigDecimal totalInterest = (loan.getLoanAmount().multiply(loan.getInterestRate()).multiply(BigDecimal.valueOf(loan.getTenureMonths())))
                .divide(BigDecimal.valueOf(12 * 100), BigDecimal.ROUND_HALF_UP);
        loan.setRemainingBalance(loan.getLoanAmount().add(totalInterest));

        Loan newLoan = loanRepository.save(loan);
        logger.info("Loan issued successfully with loanId: {}", newLoan.getId());
        return newLoan;
    }

    @Override
    public List<Loan> getLoansByUserId(Long userId) {
        logger.info("Retrieving loans for userId: {}", userId);
        List<Loan> loans = loanRepository.findByUserId(userId);
        logger.info("Loans retrieved successfully for userId: {}", userId);
        return loans;
    }

    @Override
    public Loan updateLoanStatus(Long loanId, String newStatus) {
        logger.info("Updating loan status for loanId: {} to status: {}", loanId, newStatus);
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null) {
            loan.setLoanStatus(newStatus);
            loanRepository.save(loan);
            logger.info("Loan status updated successfully for loanId: {}", loanId);
        } else {
            logger.warn("Loan not found for loanId: {}", loanId);
        }
        return loan;
    }

    @Override
    public Loan repayLoan(Long loanId, BigDecimal paymentAmount) {
        logger.info("Repaying loan for loanId: {} with amount: {}", loanId, paymentAmount);
        Loan loan = loanRepository.findById(loanId).orElse(null);
        if (loan != null && loan.getRemainingBalance().compareTo(BigDecimal.ZERO) > 0) {
            loan.setRemainingBalance(loan.getRemainingBalance().subtract(paymentAmount));
            if (loan.getRemainingBalance().compareTo(BigDecimal.ZERO) <= 0) {
                loan.setLoanStatus("CLOSED");
            }
            loanRepository.save(loan);
            logger.info("Loan repaid successfully for loanId: {}", loanId);
        } else {
            logger.warn("Loan not found or already repaid for loanId: {}", loanId);
        }
        return loan;
    }

    @Override
    public Loan updateLoan(Loan inputLoan) throws ResourceNotFoundException {
        logger.info("Updating loan details for loanId: {}", inputLoan.getId());
        Loan existingLoan = loanRepository.findById(inputLoan.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + inputLoan.getId()));

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

        Loan updatedLoan = loanRepository.save(existingLoan);
        logger.info("Loan details updated successfully for loanId: {}", inputLoan.getId());
        return updatedLoan;
    }

    private String generateLoanNumber() {
        Random random = new Random();
        int randomNumber = 100000000 + random.nextInt(900000000);
        return "LN" + randomNumber;
    }
}