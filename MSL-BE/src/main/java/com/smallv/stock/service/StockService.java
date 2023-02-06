package com.smallv.stock.service;

public interface StockService {

    public void saveStockInfo(int baseDate);

    public void saveFinanceInfo(String requestStCd, int currentYear, String requestQr);
}
