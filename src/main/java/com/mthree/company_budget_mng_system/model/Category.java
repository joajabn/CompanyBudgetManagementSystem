package com.mthree.company_budget_mng_system.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Budget budget;

    @OneToMany(mappedBy = "category")
    private List<Expense> expenses;
}
