package com.smallv.stock.repository;

import com.smallv.stock.dto.FinanceDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FinanceDao {
    public void insertFinance(FinanceDto financeDto);
}
