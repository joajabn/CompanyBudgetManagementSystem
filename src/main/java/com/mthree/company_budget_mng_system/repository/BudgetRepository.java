package com.mthree.company_budget_mng_system.repository;

import com.mthree.company_budget_mng_system.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    boolean existsByYear(Integer year);
}
