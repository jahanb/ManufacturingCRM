package com.manufacturing.controller;

import com.manufacturing.model.Sales;
import com.manufacturing.model.Salesperson;
import com.manufacturing.service.SalesService;
import com.manufacturing.service.SalespersonService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Component
@ViewScoped
public class GraphBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private SalesService salesService;

    @Autowired
    private SalespersonService salespersonService;

    private List<Salesperson> allSalespersons;
    private Salesperson selectedSalesperson;
    private YearMonth selectedMonth;

    private int totalSales;
    private double totalRevenue;

    // Chart data for manual rendering
    private List<DaySalesData> chartData;
    private List<Sales> filteredSalesList;

    @PostConstruct
    public void init() {
        allSalespersons = salespersonService.findAll();
        selectedMonth = YearMonth.now();

        if (!allSalespersons.isEmpty()) {
            selectedSalesperson = allSalespersons.get(0);
            generateChart();
        }
    }

    public void generateChart() {
        if (selectedSalesperson == null || selectedMonth == null) {
            return;
        }

        // Get all sales for this salesperson
        List<Sales> allSales = salesService.findAll();

        // Filter by selected salesperson and month
        LocalDateTime startOfMonth = selectedMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = selectedMonth.atEndOfMonth().atTime(23, 59, 59);

        filteredSalesList = allSales.stream()
                .filter(sale -> sale.getSalesperson() != null &&
                        sale.getSalesperson().getId().equals(selectedSalesperson.getId()))
                .filter(sale -> sale.getSaleDate() != null &&
                        !sale.getSaleDate().isBefore(startOfMonth) &&
                        !sale.getSaleDate().isAfter(endOfMonth))
                .sorted(Comparator.comparing(Sales::getSaleDate))
                .collect(Collectors.toList());

        // Count sales per day
        Map<Integer, Long> salesPerDay = filteredSalesList.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getSaleDate().getDayOfMonth(),
                        Collectors.counting()
                ));

        // Calculate totals
        totalSales = filteredSalesList.size();
        totalRevenue = filteredSalesList.stream()
                .mapToDouble(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : 0.0)
                .sum();

        // Prepare chart data
        chartData = new ArrayList<>();
        int daysInMonth = selectedMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            Long count = salesPerDay.getOrDefault(day, 0L);
            chartData.add(new DaySalesData(day, count.intValue()));
        }
    }

    public void onSalespersonChange() {
        generateChart();
    }

    public void onMonthChange() {
        generateChart();
    }

    public String getChartTitle() {
        if (selectedSalesperson != null && selectedMonth != null) {
            return "Daily Sales Report - " + selectedSalesperson.getFirstName() + " " +
                    selectedSalesperson.getLastName() + " (" + selectedMonth.toString() + ")";
        }
        return "Daily Sales Report";
    }

    public int getMaxSales() {
        if (chartData == null || chartData.isEmpty()) {
            return 10;
        }
        return chartData.stream()
                .mapToInt(DaySalesData::getSalesCount)
                .max()
                .orElse(10);
    }

    // Inner class for chart data
    public static class DaySalesData {
        private int day;
        private int salesCount;

        public DaySalesData(int day, int salesCount) {
            this.day = day;
            this.salesCount = salesCount;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getSalesCount() {
            return salesCount;
        }

        public void setSalesCount(int salesCount) {
            this.salesCount = salesCount;
        }

        public int getBarHeight() {
            return salesCount * 30; // 30px per sale for visual representation
        }
    }

    // Getters and Setters
    public List<Salesperson> getAllSalespersons() {
        return allSalespersons;
    }

    public void setAllSalespersons(List<Salesperson> allSalespersons) {
        this.allSalespersons = allSalespersons;
    }

    public Salesperson getSelectedSalesperson() {
        return selectedSalesperson;
    }

    public void setSelectedSalesperson(Salesperson selectedSalesperson) {
        this.selectedSalesperson = selectedSalesperson;
    }

    public YearMonth getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(YearMonth selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public List<DaySalesData> getChartData() {
        return chartData;
    }

    public void setChartData(List<DaySalesData> chartData) {
        this.chartData = chartData;
    }

    public List<Sales> getFilteredSalesList() {
        return filteredSalesList;
    }

    public void setFilteredSalesList(List<Sales> filteredSalesList) {
        this.filteredSalesList = filteredSalesList;
    }
}