package com.smallv.stock.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface CompanyService {

    public void stockLoad(int baseDate);
    public void updateCompany();
    public void companyInfoLoad();
}
