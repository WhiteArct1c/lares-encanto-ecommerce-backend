package com.laresencanto.laresencantorestapi.service;

import com.laresencanto.laresencantorestapi.dto.response.ResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.dashboard.DashboardSummaryResponseDTO;
import com.laresencanto.laresencantorestapi.dto.response.dashboard.SalesByCategoryResponseDTO;
import com.laresencanto.laresencantorestapi.repository.CustomerRepository;
import com.laresencanto.laresencantorestapi.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AdminDashboardService {

    private static final List<String> FINISHED_ORDER_STATUSES = List.of(
            "ENTREGUE",
            "TROCA CONCLUÍDA",
            "DEVOLUÇÃO CONCLUÍDA"
    );

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public AdminDashboardService(OrderRepository orderRepository,
                                 CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public ResponseDTO<DashboardSummaryResponseDTO> getSummary(LocalDate referenceDate) {
        LocalDate ref = referenceDate != null ? referenceDate : LocalDate.now();

        LocalDateTime monthStart = ref.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        LocalDateTime dayStart = ref.atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        Double currentMonthSalesTotal = orderRepository
                .sumTotalPriceByStatusNamesAndCreatedAtBetween(
                        FINISHED_ORDER_STATUSES, monthStart, monthEnd);

        long currentMonthRegisteredUsers = customerRepository
                .countByCreatedAtBetween(monthStart, monthEnd);

        Long todaySalesCount = orderRepository
                .countByStatusNamesAndCreatedAtBetween(
                        FINISHED_ORDER_STATUSES, dayStart, dayEnd);

        DashboardSummaryResponseDTO dto = new DashboardSummaryResponseDTO(
                currentMonthSalesTotal != null ? currentMonthSalesTotal : 0.0,
                currentMonthRegisteredUsers,
                todaySalesCount != null ? todaySalesCount : 0L
        );

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Resumo do dashboard carregado com sucesso.",
                List.of(dto)
        );
    }

    public ResponseDTO<SalesByCategoryResponseDTO> getSalesByCategory(
            LocalDate startDate,
            LocalDate endDate,
            List<Integer> categoryIds) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        boolean categoryIdsIsNull = (categoryIds == null || categoryIds.isEmpty());
        List<Integer> idsParam = categoryIdsIsNull ? Collections.emptyList() : categoryIds;

        List<Object[]> rows = orderRepository.findSalesByCategoryAndPeriod(
                start,
                end,
                FINISHED_ORDER_STATUSES,
                idsParam,
                categoryIdsIsNull
        );

        Map<String, List<SalesByCategoryResponseDTO.CategorySalesDTO>> categoriesByMonth = new LinkedHashMap<>();
        Map<String, Integer> yearByKey = new HashMap<>();
        Map<String, Integer> monthByKey = new HashMap<>();

        for (Object[] row : rows) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            Integer categoryId = ((Number) row[2]).intValue();
            String categoryName = (String) row[3];
            Double totalSalesAmount = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
            Long totalOrders = row[5] != null ? ((Number) row[5]).longValue() : 0L;

            String key = year + "-" + month;

            categoriesByMonth
                    .computeIfAbsent(key, k -> new ArrayList<>())
                    .add(new SalesByCategoryResponseDTO.CategorySalesDTO(
                            categoryId,
                            categoryName,
                            totalSalesAmount,
                            totalOrders
                    ));

            yearByKey.putIfAbsent(key, year);
            monthByKey.putIfAbsent(key, month);
        }

        List<SalesByCategoryResponseDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<SalesByCategoryResponseDTO.CategorySalesDTO>> entry : categoriesByMonth.entrySet()) {
            String key = entry.getKey();
            Integer year = yearByKey.get(key);
            Integer month = monthByKey.get(key);

            String monthLabel = buildMonthLabel(year, month);

            result.add(new SalesByCategoryResponseDTO(
                    year,
                    month,
                    monthLabel,
                    entry.getValue()
            ));
        }

        return new ResponseDTO<>(
                HttpStatus.OK.toString(),
                "Vendas por categoria carregadas com sucesso.",
                result
        );
    }

    private String buildMonthLabel(Integer year, Integer month) {
        Month m = Month.of(month);
        String monthName = m.getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")).toUpperCase();
        return monthName + "/" + year;
    }
}


