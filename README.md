# Company Budget Management System

This project is a **Company Budget Management System** designed to manage and track a company's annual budget, monitor expenses across various categories, and compare them against the allocated budget. It provides a REST API for managing budgets, categories, and expenses while offering features like expense warnings, budget summaries, and visual indicators.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [System Architecture](#system-architecture)
5. [API Endpoints](#api-endpoints)
6. [Setup Instructions](#setup-instructions)
7. [Testing](#testing)
8. [Contributing](#contributing)

## Project Overview
The Company Budget Management System allows users to:
- Set and track company budgets.
- Divide budgets into multiple categories (e.g., HR, IT, Marketing).
- Add expenses to categories and monitor them against the budget.
- Receive warnings when expenses exceed 90% of the allocated budget.
- Securely manage and access budget data through a RESTful API.

## Features
- **CRUD Operations for Budgets and Expenses**: Users can create, read, update, and delete budgets and expenses.
- **Budget and Expense Comparison**: Automatically compares budgeted amounts with actual expenses.
- **Category-Wise Expense Tracking**: Expenses are categorized into pre-defined types such as HR, IT, Marketing, etc.
- **Real-Time Alerts**: Alerts when the total expenses or a category’s expenses exceed 90% of the allocated budget.
- **Database**: Data is stored in a relational database (MySQL).

[//]: # (- **Secure Access Control**: Role-based access control using Spring Security &#40;users, managers, etc.&#41;.)

[//]: # (- **Database & Caching**: Data is stored in a relational database &#40;MySQL&#41;, and caching is implemented to improve performance.)

## Technologies Used
- **Backend Framework**: Spring Boot
- **Database**: MySQL
- **Security**: Spring Security
- **Mapping**: MapStruct
- **Testing**: JUnit, Spring Boot Test
- **Validation**: Hibernate Validator
- **Logging**: SLF4J

## System Architecture
The application follows a layered architecture:
1. **Controller Layer**: Exposes RESTful endpoints for budget and expense management.
2. **Service Layer**: Handles business logic, including budget validation, expense tracking, and alert generation.
3. **Repository Layer**: Interacts with the database to persist and retrieve budgets, categories, and expenses.

[//]: # (4. **Security Layer**: Manages user authentication and role-based authorization.)

## API Endpoints
### Budget Endpoints
- `POST /api/budgets`: Create a new budget.
- `GET /api/budgets`: Get all budgets.
- `GET /api/budgets/{id}`: Get budget details by ID.
- `PUT /api/budgets/{id}`: Update an existing budget.
- `DELETE /api/budgets/{id}`: Delete a budget.
- `GET /api/budgets/{budgetId}/expenses`: Get all expenses for a specific budget.
- `GET /api/budgets/{budgetId}/percentage-used`: Get the percentage of the budget used.
- `GET /api/budgets/{budgetId}/rest`: Get the remaining budget available.

### Expense Endpoints
- `POST /api/expenses`: Add a new expense.
- `GET /api/expenses`: Get all expenses.
- `GET /api/expenses/{id}`: Get expense details by ID.
- `PUT /api/expenses/{id}`: Update an expense.
- `DELETE /api/expenses/{id}`: Delete an expense.


### User Endpoints
- `POST /api/users`: Create a new user.
- `GET /api/users/{username}`: Get user details by username.
- `PUT /api/users/{username}`: Update an existing user.
- `DELETE /api/users/{username}`: Delete a user.
- `GET /api/users`: Get all users.


## Setup Instructions

### Prerequisites
- Java 17
- Maven
- MySQL database
- IDE like IntelliJ IDEA or Eclipse

### Steps to Run Locally
1. **Clone the repository**:
    ```bash
    git clone https://github.com/joajabn/CompanyBudgetManagementSystem.git
    ```

2. **Set up the database**:
    - Create a MySQL database.
    - Update the `application.properties` file with your database connection details.
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:8080/budget_management
    spring.datasource.username=root
    spring.datasource.password=password
    spring.jpa.hibernate.ddl-auto=update
    ```

3. **Install dependencies**:
    ```bash
    mvn clean install
    ```

4. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

5. **Access the application**:
    - The API should now be running at `http://localhost:8080`.


## Testing
The project includes tests for the core functionality such as:
- Budget validation.
- Expense creation and updates.

Run tests with:
```bash
mvn test
