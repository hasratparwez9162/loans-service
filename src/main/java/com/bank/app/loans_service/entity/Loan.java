package com.bank.app.loans_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;  // Foreign key to User
    @Column(name = "amount")
    private BigDecimal loanAmount;
    @Column(unique = true)
    private String loanNumber;
    @Enumerated(EnumType.STRING)
    private LoanType loanType;
    private BigDecimal interestRate;
    private int tenureMonths;  // Tenure of the loan in months
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal remainingBalance;
    private String loanStatus;  // e.g., ACTIVE, CLOSED, DEFAULTED


}
