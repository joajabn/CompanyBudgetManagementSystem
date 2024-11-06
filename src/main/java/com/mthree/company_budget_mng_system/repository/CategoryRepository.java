package com.mthree.company_budget_mng_system.repository;

import com.mthree.company_budget_mng_system.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
