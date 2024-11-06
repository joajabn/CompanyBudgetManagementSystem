package com.mthree.company_budget_mng_system.exception;

public class CategoryThresholdExceededException extends RuntimeException {
    public CategoryThresholdExceededException(String message) {
        super(message);
    }
}
