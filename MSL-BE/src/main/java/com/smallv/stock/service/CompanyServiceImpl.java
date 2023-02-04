package com.smallv.stock.service;

import com.google.gson.*;
import com.smallv.stock.dto.CompanyDto;
import com.smallv.stock.repository.CompanyDao;
import com.smallv.stock.util.FileReaderUtils;
import com.smallv.stock.util.UrlHandlingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final CompanyDao companyDao;
    private final UrlHandlingUtils UrlHandlingUtils;
    private final FileReaderUtils fileReaderUtils;

    @Override
    public void stockLoad(int baseDate)  {
        CompanyDto company;
        Gson gson = new Gson();

        String combineUrl = UrlHandlingUtils.combineURLForFSC(
                "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo",
                "10000",
                "1",
                LocalDate.now().minusDays(baseDate)
        );

        JsonArray asParsingArr = getFscDataFromJson(UrlHandlingUtils.getDataByURL(combineUrl,"json"));

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 종목이 없음. 기준일 확인 요망");
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
    public void companyCrnoLoad() {
        CompanyDto company;

        JsonArray asParsingArr = fileReaderUtils.convertXMLToJson()
                .get("result")
                .getAsJsonObject()
                .get("list")
                .getAsJsonArray();

        BiFunction<JsonElement,String,String> extractedJsonStr =
                (j,s)-> j.getAsJsonObject().get(s).toString().replaceAll("\"","");

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 기업이 없음. 데이터 확인 요망");
        } else {
            for(JsonElement j:asParsingArr){
                company = CompanyDto.builder()
                        .srtnCd(extractedJsonStr.apply(j,"stock_code"))
                        .crno(extractedJsonStr.apply(j,"corp_code"))
                        .build();

                if(company.getSrtnCd().length()>=6){
                    companyDao.updateCompanyOnlyCrno(company);
                }
            }
            System.out.println("****완료****");
        }
    }


    @Override
    public void companyInfoLoad() {
//        CompanyDto company;
//        Gson gson = new Gson();
//
//        String combineUrl = dataHandlingUtils.combineUrlForFSC(
//                "https://apis.data.go.kr/1160100/service/GetCorpBasicInfoService/getCorpOutline",
//                "10000",
//                "1",
//                LocalDate.now().minusDays(2)
//        );
//
//        JsonObject resData = dataHandlingUtils.getDataByURL(combineUrl);
//
//        JsonArray asParsingArr = getDataItemsFromJson(resData);
//
//        if(asParsingArr.isEmpty()){
//            throw new IllegalStateException("조회된 기업이 없음. 기준일 확인 요망");
//        } else {
//            for(JsonElement j:asParsingArr){
//                company = CompanyDto.builder()
//                        .srtnCd(j.getAsJsonObject().get(""))
//                        .build();
//                try {
//                    companyDao.updateCompany(company);
//                } catch (DuplicateKeyException e){
//                }
//            }
//            System.out.println("****완료****");
//        }
    }


    private static JsonArray getFscDataFromJson(JsonObject resData){

        JsonArray asParsingArr = resData.get("response").getAsJsonObject()
                .get("body").getAsJsonObject()
                .get("items").getAsJsonObject()
                .get("item").getAsJsonArray();

        return asParsingArr;
    }
}