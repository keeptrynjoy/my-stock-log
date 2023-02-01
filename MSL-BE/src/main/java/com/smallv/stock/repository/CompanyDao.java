package com.smallv.stock.repository;


import com.smallv.stock.dto.CompanyDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyDao {
    public void insertCompany(CompanyDto company);
    public void updateCompany(CompanyDto company);
}
