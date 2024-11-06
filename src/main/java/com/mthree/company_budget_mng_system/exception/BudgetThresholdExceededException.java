package com.mthree.company_budget_mng_system.exception;

public class BudgetThresholdExceededException extends RuntimeException {
    public BudgetThresholdExceededException(String message) {
        super(message);
    }
}
