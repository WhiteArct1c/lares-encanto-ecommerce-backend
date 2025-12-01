package com.laresencanto.laresencantorestapi.controller;

import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.dashboard.DashboardSummaryResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.dashboard.SalesByCategoryResponseDTO;
import com.laresencanto.laresencantorestapi.service.AdminDashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public ResponseDTO<DashboardSummaryResponseDTO> getSummary(
            @RequestParam(name = "referenceDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate referenceDate) {
        return adminDashboardService.getSummary(referenceDate);
    }

    @GetMapping("/sales-by-category")
    public ResponseDTO<SalesByCategoryResponseDTO> getSalesByCategory(
            @RequestParam(name = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "categoryIds", required = false) List<Integer> categoryIds) {
        return adminDashboardService.getSalesByCategory(startDate, endDate, categoryIds);
    }
}


