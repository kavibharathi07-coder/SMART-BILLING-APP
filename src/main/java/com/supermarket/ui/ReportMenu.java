package com.supermarket.ui;

import com.supermarket.service.ReportService;
import com.supermarket.util.ConsoleUtil;
import com.supermarket.util.InputUtil;

import java.time.LocalDate;
import java.util.Map;

public class ReportMenu {

    private final ReportService service;

    public ReportMenu(
            ReportService service
    ) {

        this.service = service;
    }

    public void show() {

        while (true) {

            ConsoleUtil.header(
                    "ADMIN REPORTS"
            );

            System.out.println(
                    "1. Daily Sales"
            );

            System.out.println(
                    "2. Biller Sales"
            );

            System.out.println(
                    "3. Low Stock"
            );

            System.out.println(
                    "4. Audit Logs"
            );

            System.out.println(
                    "5. Back"
            );

            String choice =
                    InputUtil.readRequired(
                            "Enter choice: "
                    );

            try {

                switch (choice) {

                    case "1":
                        dailySales();
                        break;

                    case "2":
                        billerSales();
                        break;

                    case "3":
                        lowStock();
                        break;

                    case "4":
                        showAuditLogs();
                        break;

                    case "5":
                        return;

                    default:
                        System.out.println(
                                "Invalid choice."
                        );
                }

            } catch (Exception e) {

                System.out.println(
                        "Report failed: "
                                + e.getMessage()
                );
            }

            ConsoleUtil.pause();
        }
    }

    private void dailySales()
            throws Exception {

        String input =
                InputUtil.readLine(
                        "Date (YYYY-MM-DD) or ENTER for today: "
                );

        LocalDate date =
                input.isBlank()
                        ? LocalDate.now()
                        : LocalDate.parse(input);

        Map<String, Object> m =
                service.dailySales(date);

        ConsoleUtil.header(
                "DAILY SALES - "
                        + date
        );

        System.out.println(
                "Total Bills    : "
                        + m.getOrDefault(
                                "total_bills",
                                0
                        )
        );

        System.out.println(
                "Subtotal       : "
                        + ConsoleUtil.money(
                                ((Number)
                                        m.getOrDefault(
                                                "subtotal",
                                                0
                                        ))
                                        .doubleValue()
                        )
        );

        System.out.println(
                "Discount       : "
                        + ConsoleUtil.money(
                                ((Number)
                                        m.getOrDefault(
                                                "discount",
                                                0
                                        ))
                                        .doubleValue()
                        )
        );

        System.out.println(
                "Tax            : "
                        + ConsoleUtil.money(
                                ((Number)
                                        m.getOrDefault(
                                                "tax",
                                                0
                                        ))
                                        .doubleValue()
                        )
        );

        System.out.println(
                "Total Sales    : "
                        + ConsoleUtil.money(
                                ((Number)
                                        m.getOrDefault(
                                                "total_sales",
                                                0
                                        ))
                                        .doubleValue()
                        )
        );

        System.out.println(
                "Cash Collected : "
                        + ConsoleUtil.money(
                                ((Number)
                                        m.getOrDefault(
                                                "cash_collected",
                                                0
                                        ))
                                        .doubleValue()
                        )
        );
    }

    private void billerSales()
            throws Exception {

        ConsoleUtil.header(
                "BILLER SALES"
        );

        System.out.printf(
                "%-20s %10s %15s%n",
                "BILLER",
                "BILLS",
                "SALES"
        );

        for (
                Map<String, Object> m :
                service.billerSales()
        ) {

            System.out.printf(
                    "%-20s %10s %15s%n",
                    m.get("biller"),
                    m.get("bills"),
                    ConsoleUtil.money(
                            ((Number)
                                    m.get("sales"))
                                    .doubleValue()
                    )
            );
        }
    }

    private void lowStock()
            throws Exception {

        int limit =
                InputUtil.readPositiveInt(
                        "Maximum stock to display: "
                );

        ConsoleUtil.header(
                "LOW STOCK"
        );

        System.out.printf(
                "%-6s %-28s %8s %12s%n",
                "ID",
                "PRODUCT",
                "STOCK",
                "REORDER"
        );

        for (
                Map<String, Object> m :
                service.lowStockReport(limit)
        ) {

            System.out.printf(
                    "%-6s %-28s %8s %12s%n",
                    m.get("product_id"),
                    m.get("product_name"),
                    m.get("stock_qty"),
                    m.get("reorder_level")
            );
        }
    }

    public void showAuditLogs()
            throws Exception {

        ConsoleUtil.header(
                "AUDIT LOGS"
        );

        System.out.printf(
                "%-5s %-7s %-18s %-55s%n",
                "ID",
                "USER",
                "ACTION",
                "DESCRIPTION"
        );

        for (
                Map<String, Object> m :
                service.auditLogs()
        ) {

            System.out.printf(
                    "%-5s %-7s %-18s %-55s%n",
                    m.get("audit_id"),
                    m.get("user_id"),
                    m.get("action_type"),
                    m.get("description")
            );
        }
    }
}