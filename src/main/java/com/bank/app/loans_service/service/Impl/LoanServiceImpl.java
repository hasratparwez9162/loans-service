package com.bank.app.loans_service.service.Impl;

import com.bank.app.loans_service.entity.Loan;
import com.bank.app.loans_service.exception.ResourceNotFoundException;
import com.bank.app.loans_service.repo.LoansRepository;
import com.bank.app.loans_service.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoansRepository loanRepository;
    private final LoanEventProducer loanEventProducer;

    @Autowired
    public LoanServiceImpl(LoansRepository loanRepository, LoanEventProducer loanEventProducer) {
        this.loanRepository = loanRepository;
        this.loanEventProducer = loanEventProducer;
    }

    /**
     * Issues a new loan.
     * @param loan The loan details.
     * @return The newly created loan.
     */
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
                case HOME:
                    loan.setInterestRate(BigDecimal.valueOf(5.5));
                    break;
                case AUTO:
                    loan.setInterestRate(BigDecimal.valueOf(7.0));
                    break;
                case PERSONAL:
                    loan.setInterestRate(BigDecimal.valueOf(10.0));
                    break;
                default:
                    loan.setInterestRate(BigDecimal.valueOf(8.0));
            }
        }

        BigDecimal totalInterest = (loan.getLoanAmount().multiply(loan.getInterestRate()).multiply(BigDecimal.valueOf(loan.getTenureMonths())))
                .divide(BigDecimal.valueOf(12 * 100), BigDecimal.ROUND_HALF_UP);
        loan.setRemainingBalance(loan.getLoanAmount().add(totalInterest));

        Loan newLoan = loanRepository.save(loan);
        loanEventProducer.sendLoanIssuedMessage(newLoan);
        logger.info("Loan issued successfully with loanId: {}", newLoan.getId());
        return newLoan;
    }

    /**
     * Retrieves loans by user ID.
     * @param userId The ID of the user.
     * @return A list of loans belonging to the user.
     */
    @Override
    public List<Loan> getLoansByUserId(Long userId) {
        logger.info("Retrieving loans for userId: {}", userId);
        List<Loan> loans = loanRepository.findByUserId(userId);
        logger.info("Loans retrieved successfully for userId: {}", userId);
        return loans;
    }

    /**
     * Updates the status of a loan.
     * @param loanId The ID of the loan.
     * @param newStatus The new status of the loan.
     * @return The updated loan.
     */
    @Override
    public Loan updateLoanStatus(Long loanId, String newStatus) {
        logger.info("Updating loan status for loanId: {} to status: {}", loanId, newStatus);
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
        loan.setLoanStatus(newStatus);
        Loan updatedLoan = loanRepository.save(loan);
        loanEventProducer.sendLoanStatusUpdatedMessage(updatedLoan);
        logger.info("Loan status updated successfully for loanId: {}", loanId);
        return updatedLoan;
    }

    /**
     * Repays a loan.
     * @param loanId The ID of the loan.
     * @param paymentAmount The amount to be paid.
     * @return The updated loan.
     */
    @Override
    @Transactional
    public Loan repayLoan(Long loanId, BigDecimal paymentAmount) {
        logger.info("Repaying loan for loanId: {} with amount: {}", loanId, paymentAmount);
        Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
        if (loan.getRemainingBalance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newBalance = loan.getRemainingBalance().subtract(paymentAmount);
            loan.setRemainingBalance(newBalance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newBalance);
            Loan updatedLoan = loanRepository.save(loan);
            loanEventProducer.sendLoanRepaidMessage(updatedLoan);
            logger.info("Loan repaid successfully for loanId: {}", loanId);
            return updatedLoan;
        } else {
            logger.warn("Loan already repaid for loanId: {}", loanId);
            return loan;
        }
    }

    /**
     * Updates loan details.
     * @param inputLoan The loan details to be updated.
     * @return The updated loan.
     * @throws ResourceNotFoundException if the loan is not found.
     */
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
        loanEventProducer.sendLoanUpdatedMessage(updatedLoan);
        logger.info("Loan details updated successfully for loanId: {}", inputLoan.getId());
        return updatedLoan;
    }

    /**
     * Generates a unique loan number.
     * @return A randomly generated loan number.
     */
    private String generateLoanNumber() {
        Random random = new Random();
        int randomNumber = 100000000 + random.nextInt(900000000);
        return "LN" + randomNumber;
    }
}