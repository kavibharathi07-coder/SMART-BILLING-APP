package com.supermarket.service;

import com.supermarket.dao.ReportDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final ReportDAO dao =
            new ReportDAO();

    public Map<String, Object> dailySales(
            LocalDate date
    ) throws Exception {

        return dao.getDailySales(date);
    }

    public List<Map<String, Object>> billerSales()
            throws Exception {

        return dao.getBillerSales();
    }

    public List<Map<String, Object>> lowStockReport(
            int limit
    ) throws Exception {

        return dao.getLowStock(limit);
    }

    public List<Map<String, Object>> auditLogs()
            throws Exception {

        return dao.getAuditLogs();
    }
}