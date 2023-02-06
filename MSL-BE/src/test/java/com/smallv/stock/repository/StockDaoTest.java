package com.smallv.stock.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockDaoTest {

    @Autowired
    private StockDao stockDao;

    @Test
    void getDataByStockCode() {

        //given
        String sc = "000050";

        //when
        System.out.println(" = " +stockDao.getDataByStockCode(sc).toString());

        //then
//        assertThat(result).isEqualTo(sc);
    }
}