package com.example.keuangan.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.keuangan.dto.expense.ExpenseDto;
import com.example.keuangan.dto.income.IncomeDto;
import com.example.keuangan.dto.installment.InstallmentDto;
import com.example.keuangan.dto.report.SummaryResponseDto;
import com.example.keuangan.entity.Expense;
import com.example.keuangan.entity.Income;
import com.example.keuangan.entity.Installment;
import com.example.keuangan.entity.User;
import com.example.keuangan.repository.ExpenseRepository;
import com.example.keuangan.repository.IncomeRepository;
import com.example.keuangan.repository.InstallmentRepository;
import com.example.keuangan.repository.UserRepository;

@Service
public class FinanceService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public IncomeDto addIncome(String email, IncomeDto dto) {
        User user = getUserByEmail(email);
        Income income = new Income();
        income.setUser(user);
        income.setSumberPemasukan(dto.getSumberPemasukan());
        income.setNominal(dto.getNominal());
        income.setTanggal(dto.getTanggal());
        income.setKeterangan(dto.getKeterangan());
        income = incomeRepository.save(income);
        return mapToDto(income);
    }

    public List<IncomeDto> getIncomes(String email) {
        User user = getUserByEmail(email);
        return incomeRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDto addExpense(String email, ExpenseDto dto) {
        User user = getUserByEmail(email);
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(dto.getCategory());
        expense.setNominal(dto.getNominal());
        expense.setTanggal(dto.getTanggal());
        expense.setKeterangan(dto.getKeterangan());
        expense = expenseRepository.save(expense);
        return mapToDto(expense);
    }

    public List<ExpenseDto> getExpenses(String email) {
        User user = getUserByEmail(email);
        return expenseRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public InstallmentDto addInstallment(String email, InstallmentDto dto) {
        User user = getUserByEmail(email);
        Installment installment = new Installment();
        installment.setUser(user);
        installment.setNamaCicilan(dto.getNamaCicilan());
        installment.setTotalCicilan(dto.getTotalCicilan());
        installment.setCicilanBulanan(dto.getCicilanBulanan());
        installment.setTenor(dto.getTenor());
        installment.setSisaCicilan(dto.getSisaCicilan());
        installment.setStatus("AKTIF");
        installment = installmentRepository.save(installment);
        return mapToDto(installment);
    }

    public List<InstallmentDto> getInstallments(String email) {
        User user = getUserByEmail(email);
        return installmentRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void payInstallment(Long id, BigDecimal amount) {
        Installment installment = installmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Installment not found"));

        BigDecimal newBalance = installment.getSisaCicilan().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            newBalance = BigDecimal.ZERO;
        }
        installment.setSisaCicilan(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            installment.setStatus("LUNAS");
        }

        installmentRepository.save(installment);
    }

    public SummaryResponseDto getMonthlySummary(String email, int month, int year) {
        User user = getUserByEmail(email);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Income> incomes = incomeRepository.findByUserIdAndTanggalBetween(user.getId(), start, end);
        List<Expense> expenses = expenseRepository.findByUserIdAndTanggalBetween(user.getId(), start, end);

        List<Installment> activeInstallments = installmentRepository.findByUserId(user.getId()).stream()
                .filter(i -> "AKTIF".equals(i.getStatus()))
                .collect(Collectors.toList());

        BigDecimal totalIncome = incomes.stream().map(Income::getNominal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenses.stream().map(Expense::getNominal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalInstallment = activeInstallments.stream().map(Installment::getCicilanBulanan)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense).subtract(totalInstallment);

        return new SummaryResponseDto(totalIncome, totalExpense, totalInstallment, balance,
                start.getMonth().name() + " " + year);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private IncomeDto mapToDto(Income income) {
        IncomeDto dto = new IncomeDto();
        dto.setId(income.getId());
        dto.setUserId(income.getUser().getId());
        dto.setSumberPemasukan(income.getSumberPemasukan());
        dto.setNominal(income.getNominal());
        dto.setTanggal(income.getTanggal());
        dto.setKeterangan(income.getKeterangan());
        return dto;
    }

    private ExpenseDto mapToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setUserId(expense.getUser().getId());
        dto.setCategory(expense.getCategory());
        dto.setNominal(expense.getNominal());
        dto.setTanggal(expense.getTanggal());
        dto.setKeterangan(expense.getKeterangan());
        return dto;
    }

    private InstallmentDto mapToDto(Installment installment) {
        InstallmentDto dto = new InstallmentDto();
        dto.setId(installment.getId());
        dto.setUserId(installment.getUser().getId());
        dto.setNamaCicilan(installment.getNamaCicilan());
        dto.setTotalCicilan(installment.getTotalCicilan());
        dto.setCicilanBulanan(installment.getCicilanBulanan());
        dto.setTenor(installment.getTenor());
        dto.setSisaCicilan(installment.getSisaCicilan());
        dto.setStatus(installment.getStatus());
        return dto;
    }
}
