package com.smallv.stock.service;

import com.google.gson.*;
import com.smallv.stock.dto.CompanyDto;
import com.smallv.stock.repository.CompanyDao;
import com.smallv.stock.util.DataHandlingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final CompanyDao companyDao;
    private final DataHandlingUtils dataHandlingUtils;

    @Override
    public void stockLoad(int baseDate)  {
        CompanyDto company;
        Gson gson = new Gson();
        String combineUrl = dataHandlingUtils.combineUrlByFSC(
                "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo",
                "10000",
                "1",
                LocalDate.now().minusDays(baseDate)
                );

        JsonObject resData = dataHandlingUtils.getDataByURL(combineUrl);

        JsonArray asParsingArr = resData.get("response").getAsJsonObject()
                .get("body").getAsJsonObject()
                .get("items").getAsJsonObject()
                .get("item").getAsJsonArray();

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 종목이 없음. 기준일 조회");
        } else {
            for(JsonElement j:asParsingArr){
                company = gson.fromJson( j, CompanyDto.class);
                try {
                    companyDao.insertCompany(company);
                } catch (DuplicateKeyException e){
                }
            }
            System.out.println("****완료****");
        }
    }

    @Override
    public void updateCompany(){

    }

    @Override
    public void companyInfoLoad() {
    //StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1160100/service/GetCorpBasicInfoService/getCorpOutline");




    }

}