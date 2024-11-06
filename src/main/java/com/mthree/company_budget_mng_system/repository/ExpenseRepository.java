package com.mthree.company_budget_mng_system.repository;

import com.mthree.company_budget_mng_system.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
