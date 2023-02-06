package com.smallv.stock.service;

public interface StockService {

    public void saveStockInfo(int baseDate);
    public void saveCorpCode();
    public void saveFinanceInfo(String requestStCd, int currentYear, String requestQr);
}
