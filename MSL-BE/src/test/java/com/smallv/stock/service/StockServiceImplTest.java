package com.smallv.stock.service;

import com.smallv.stock.repository.FinanceDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class StockServiceImplTest {

    @InjectMocks
    StockServiceImpl stockService;

    @Mock
    FinanceDao financeDao;

    @Test
    void saveFinanceInfo() {

        //given
        String sc ="000050";
        int year = 2021;
        String qr = "FIRST_QUARTER";

        //when
        stockService.saveFinanceInfo(sc,year,qr);

        //then
    }
}