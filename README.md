# Loan Service

The **Loan Service** is a microservice that handles loan-related operations in the banking application. It manages issuing, updating, repaying, and retrieving loans for users.

### Table of Contents

* Features
* Technologies
* Architecture
* Endpoints
* Setup Instructions
* Usage

### Features

* Issue a new loan to a user.
* Update loan status (e.g., mark as CLOSED).
* Repay loans by specifying the amount.
* Retrieve loans by loan ID or user ID.
* Supports different loan types (e.g., PERSONAL, HOME, AUTO).

### Technologies

* Spring Boot (v3.1.0)
* Java (v17)
* Hibernate for ORM
* MySQL as the database
* Lombok for cleaner code
* Spring Data JPA

### Architecture

This service follows the microservices architecture and connects to other services like the User Service for managing loans related to a particular user.

### Entity

##### Loan:

** Attributes:

* id: Unique identifier for the loan.
* loanNumber: Auto-generated loan number.
* loanType: Enum (PERSONAL, HOME, AUTO).
* principalAmount: Total loan amount.
* interestRate: Interest rate for the loan.
* balanceAmount: Remaining balance for the loan.
* loanStatus: Enum for loan status (e.g., ACTIVE, CLOSED).
* userId: Foreign key linking to the user.
* startDate: Date the loan was issued.
* endDate: Expected date for loan repayment.

### Endpoints

1. Issue a New Loan <br> URL: /loans/issue <br> Method: POST <br> Request Body:


    {
    "loanType": "HOME",
    "principalAmount": 200000,
    "interestRate": 3.5,
    "userId": 1
    }
Response:
    
    {
    "id": 1,
    "loanNumber": "LN123456789",
    "loanType": "HOME",
    "principalAmount": 200000,
    "interestRate": 3.5,
    "balanceAmount": 200000,
    "loanStatus": "ACTIVE",
    "startDate": "2023-09-01",
    "endDate": "2033-09-01",
    "userId": 1
    }
2. Retrieve Loans by User ID <br> URL: /loans/user/{userId} <br> Method: GET <br> Path Variable: userId (Long) <br>

Response:

    
    [
    {
    "id": 1,
    "loanNumber": "LN123456789",
    "loanType": "HOME",
    "principalAmount": 200000,
    "interestRate": 3.5,
    "balanceAmount": 180000,
    "loanStatus": "ACTIVE",
    "startDate": "2023-09-01",
    "endDate": "2033-09-01",
    "userId": 1
    }
    ]
3. Update Loan Status <br> URL: /loans/status/{loanId} <br> Method: PUT <br> Path Variable: loanId (Long) <br> Request Parameter: status (e.g., CLOSED) <br>

Response:

    
    {
    "id": 1,
    "loanNumber": "LN123456789",
    "loanType": "HOME",
    "principalAmount": 200000,
    "interestRate": 3.5,
    "balanceAmount": 0,
    "loanStatus": "CLOSED",
    "startDate": "2023-09-01",
    "endDate": "2033-09-01",
    "userId": 1
    }
4. Repay Loan <br> URL: /loans/repay/{loanId} <br> Method: PUT <br> Path Variable: loanId (Long) <br> Request Parameter: amount (BigDecimal) <br>

Response:

    
    {
    "id": 1,
    "loanNumber": "LN123456789",
    "loanType": "HOME",
    "principalAmount": 200000,
    "interestRate": 3.5,
    "balanceAmount": 150000,
    "loanStatus": "ACTIVE",
    "startDate": "2023-09-01",
    "endDate": "2033-09-01",
    "userId": 1
    }

### Setup Instructions

#### Prerequisites

* Java 17
* Maven
* MySQL

### Steps

Clone the repository:

    git clone https://github.com/hasratparwez9162/loans-service.git
    cd loans-service
#### Update the application.properties with your MySQL credentials:

    
    spring.datasource.url=jdbc:mysql://localhost:3306/[Enter Your Database Name] 
    spring.datasource.username=[Enter username]
    spring.datasource.password=[Enter password]
#### Update the application.properties with Eureka Configuration, JPA Properties, and port:


    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
    
    server.port=8084
    
    eureka.client.service-url.defaultZone=http://localhost:8761/eureka
### Run the application:

    
    mvn spring-boot:run
### Test the service using Postman or another API client.

### Usage

Issuing a New Loan:
Make a POST request to /loans/issue with the loan details.

Retrieve Loan Details:
Use GET requests to /loans/user/{userId} to retrieve loan details for a particular user.

Update Loan Status:
Use a PUT request to /loans/status/{loanId} with the new status (e.g., CLOSED).

Repay Loan:
Use a PUT request to /loans/repay/{loanId} with the amount to repay the loan balance.