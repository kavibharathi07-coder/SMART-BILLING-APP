package com.supermarket.service;

import com.supermarket.dao.BillDAO;

import java.sql.SQLException;
import java.util.List;

public class HistoryService {

    private final BillDAO billDAO =
            new BillDAO();

    public List<String[]> getCustomerHistory(
            String cardId
    ) throws Exception {

        return billDAO.getCustomerHistory(
                cardId
        );
    }

    public List<String[]> getBillerHistory(
            int billerId
    ) throws Exception {

        return billDAO.getBillerHistory(
                billerId
        );
    }
}