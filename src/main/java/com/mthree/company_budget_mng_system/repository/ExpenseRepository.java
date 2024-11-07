package com.mthree.company_budget_mng_system.repository;

import com.mthree.company_budget_mng_system.model.CategoryType;
import com.mthree.company_budget_mng_system.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategoryType(CategoryType categoryType);
}
