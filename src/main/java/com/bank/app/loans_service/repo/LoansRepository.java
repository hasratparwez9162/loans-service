package com.bank.app.loans_service.repo;

import com.bank.app.loans_service.entity.Loans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoansRepository extends JpaRepository<Loans,Long> {
}
