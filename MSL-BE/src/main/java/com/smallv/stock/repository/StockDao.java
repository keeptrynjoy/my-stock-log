package com.smallv.stock.repository;


import com.smallv.stock.dto.StockDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockDao {
    public void insertStock(StockDto stock);
    public void updateStockOnlyCrno(StockDto stock);
    public StockDto getDataByStockCode(String stockCode);
}
