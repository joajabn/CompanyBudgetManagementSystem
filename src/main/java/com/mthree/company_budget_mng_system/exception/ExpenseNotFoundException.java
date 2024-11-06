package com.mthree.company_budget_mng_system.exception;

public class ExpenseNotFoundException extends RuntimeException {
        public ExpenseNotFoundException(String message) {
            super(message);
        }
}
