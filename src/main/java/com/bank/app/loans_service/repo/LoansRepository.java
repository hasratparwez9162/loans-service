package com.bank.app.loans_service.repo;

import com.bank.app.loans_service.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoansRepository extends JpaRepository<Loan, Long> {

    /**
     * Retrieve loans by user ID.
     * @param userId The ID of the user.
     * @return A list of loans belonging to the user.
     */
    List<Loan> findByUserId(Long userId);
}