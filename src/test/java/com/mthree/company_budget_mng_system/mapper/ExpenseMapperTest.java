package com.mthree.company_budget_mng_system.mapper;

import com.mthree.company_budget_mng_system.dto.ExpenseDTO;
import com.mthree.company_budget_mng_system.model.CategoryType;
import com.mthree.company_budget_mng_system.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExpenseMapperTest {
    private ExpenseMapper expenseMapper;

    @BeforeEach
    public void setUp() {
        expenseMapper = Mappers.getMapper(ExpenseMapper.class);
    }

    @Test
    void testExpenseMappingToDTO() {
        // Create an Expense entity
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setDescription("Team Lunch");
        expense.setAmount(new BigDecimal("200.50"));
        expense.setDate(LocalDate.of(2024,11,06));
        expense.setCategoryType(CategoryType.HR); // Assuming CategoryType is an enum

        // Map the Expense entity to an ExpenseDTO
        ExpenseDTO expenseDTO = expenseMapper.map(expense);

        // Assert that the ExpenseDTO is not null and contains correct values
        assertNotNull(expenseDTO);
        assertEquals(expense.getId(), expenseDTO.getId());
        assertEquals(expense.getDescription(), expenseDTO.getDescription());
        assertEquals(expense.getAmount(), expenseDTO.getAmount());
        assertEquals(expense.getCategoryType(), expenseDTO.getCategoryType()); // Checking enum value
        assertEquals(expense.getDate(), expenseDTO.getDate());
    }

    @Test
    void testExpenseMappingFromDTO() {
        // Create an ExpenseDTO object
        ExpenseDTO expenseDTO = ExpenseDTO.builder()
                .id(1L)
                .description("Conference Fee")
                .amount(new BigDecimal("150.75"))
                .date(LocalDate.of(2024,5,6))
                .categoryType(CategoryType.MARKETING) // Assuming the category is passed as a string
                .build();

        // Map the ExpenseDTO to an Expense entity
        Expense expense = expenseMapper.map(expenseDTO);

        // Assert that the Expense entity is not null and contains correct values
        assertNotNull(expense);
        assertEquals(expenseDTO.getId(), expense.getId());
        assertEquals(expenseDTO.getDescription(), expense.getDescription());
        assertEquals(expenseDTO.getAmount(), expense.getAmount());
        assertEquals(expenseDTO.getCategoryType(), expense.getCategoryType()); // Enum comparison
        assertEquals(expenseDTO.getDate(), expense.getDate());
    }

    @Test
    void testMappingListOfExpensesToDTO() {
        // Create a list of Expense entities
        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setDescription("Team Lunch");
        expense1.setAmount(new BigDecimal("200.50"));
        expense1.setDate(LocalDate.of(2024,5,6));
        expense1.setCategoryType(CategoryType.HR);

        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setDescription("Conference Fee");
        expense2.setAmount(new BigDecimal("150.75"));
        expense2.setDate(LocalDate.of(2024,5,6));
        expense2.setCategoryType(CategoryType.MARKETING);

        // Add expenses to a list
        List<Expense> expenses = List.of(expense1, expense2);

        // Map the list of Expense entities to a list of ExpenseDTOs
        List<ExpenseDTO> expenseDTOs = expenseMapper.mapToDtoList(expenses);

        // Assert that the mapped list is not null and contains the correct number of items
        assertNotNull(expenseDTOs);
        assertEquals(2, expenseDTOs.size());

        // Verify each DTO has the correct values
        assertEquals(expense1.getId(), expenseDTOs.get(0).getId());
        assertEquals(expense1.getDescription(), expenseDTOs.get(0).getDescription());
        assertEquals(expense1.getAmount(), expenseDTOs.get(0).getAmount());
        assertEquals(expense1.getCategoryType(), expenseDTOs.get(0).getCategoryType()); // Enum comparison

        assertEquals(expense2.getId(), expenseDTOs.get(1).getId());
        assertEquals(expense2.getDescription(), expenseDTOs.get(1).getDescription());
        assertEquals(expense2.getAmount(), expenseDTOs.get(1).getAmount());
        assertEquals(expense2.getCategoryType(), expenseDTOs.get(1).getCategoryType()); // Enum comparison
    }

    @Test
    void testMappingListOfExpenseDTOsToEntities() {
        // Create a list of ExpenseDTOs
        ExpenseDTO expenseDTO1 = ExpenseDTO.builder()
                .id(1L)
                .description("Team Lunch")
                .amount(new BigDecimal("200.50"))
                .date(LocalDate.of(2024,5,6))
                .categoryType(CategoryType.HR)
                .build();

        ExpenseDTO expenseDTO2 = ExpenseDTO.builder()
                .id(2L)
                .description("Conference Fee")
                .amount(new BigDecimal("150.75"))
                .date(LocalDate.of(2024,11,6))
                .categoryType(CategoryType.MARKETING)
                .build();

        List<ExpenseDTO> expenseDTOS = List.of(expenseDTO1, expenseDTO2);

        // Map the list of ExpenseDTOs to a list of Expense entities
        List<Expense> expenses = expenseMapper.mapToEntityList(expenseDTOS);

        // Assert that the mapped list is not null and contains the correct number of items
        assertNotNull(expenses);
        assertEquals(2, expenses.size());

        // Verify each entity has the correct values
        assertEquals(expenseDTO1.getId(), expenses.get(0).getId());
        assertEquals(expenseDTO1.getDescription(), expenses.get(0).getDescription());
        assertEquals(expenseDTO1.getAmount(), expenses.get(0).getAmount());
        assertEquals(expenseDTO1.getCategoryType(), expenses.get(0).getCategoryType());

        assertEquals(expenseDTO2.getId(), expenses.get(1).getId());
        assertEquals(expenseDTO2.getDescription(), expenses.get(1).getDescription());
        assertEquals(expenseDTO2.getAmount(), expenses.get(1).getAmount());
        assertEquals(expenseDTO2.getCategoryType(), expenses.get(1).getCategoryType());
    }
}