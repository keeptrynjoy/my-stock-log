package com.smallv.stock.service;

import com.google.gson.*;
import com.smallv.stock.dto.FinanceDto;
import com.smallv.stock.dto.StockDto;
import com.smallv.stock.repository.FinanceDao;
import com.smallv.stock.repository.StockDao;
import com.smallv.stock.util.FileReaderUtils;
import com.smallv.stock.util.UrlHandlingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockDao stockDao;
    private final FinanceDao financeDao;
    private final UrlHandlingUtils UrlHandlingUtils;
    private final FileReaderUtils fileReaderUtils;


    @Override
    public void saveStockInfo(int baseDate)  {
        StockDto stockObj;

        String combineUrl = UrlHandlingUtils.combineURLForFSC(
                "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo",
                "10000",
                "1",
                LocalDate.now().minusDays(baseDate)
        );

        JsonArray asParsingArr = getFscDataFromJson(UrlHandlingUtils.getDataByURL(combineUrl,"json"));

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 종목이 없음. 기준일 확인 요망");
        }

        for(JsonElement j:asParsingArr){

            stockObj = StockDto.builder()
                        .stockCode(extractJsonEleToStr(j,"srtnCd"))
                        .stockNm(extractJsonEleToStr(j,"itmsNm"))
                        .osCnt(extractJsonEleToStr(j,"lstgStCnt"))
                        .mrktDiv(extractJsonEleToStr(j,"mrktCtg"))
                        .corpCode("0")
                        .build();

            stockDao.insertStock(stockObj);
        }

        saveCorpCode();
    }

    private void saveCorpCode() {
        StockDto stock;

        JsonArray asParsingArr = fileReaderUtils.convertXMLToJson().getAsJsonObject()
                .get("result").getAsJsonObject()
                .get("list").getAsJsonArray();

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 기업이 없음. 데이터 확인 요망");
        }

        for(JsonElement j:asParsingArr){


            stock = StockDto.builder()
                    .stockCode(extractJsonEleToStr(j,"stock_code"))
                    .corpCode(extractJsonEleToStr(j,"corp_code"))
                    .build();

            stockDao.updateStockOnlyCrno(stock);
        }
    }

    @Override
    public void saveFinanceInfo(String requestStCd, int currentYear, String requestQr) {

        FinanceDto financeDto;

        Optional<StockDto> storedCpCd = Optional.ofNullable(stockDao.getDataByStockCode(requestStCd));
        storedCpCd.orElseThrow(() -> new IllegalStateException("요청 종목코드 확인 요망"));
        Optional<ReportCode> reportCode =  Optional.ofNullable(ReportCode.valueOfQuarter(requestQr));
        reportCode.orElseThrow(()-> new IllegalStateException("요청 분기명 확인 요망"));

        String targetDomain = "https://opendart.fss.or.kr/api/fnlttSinglAcnt.json";
        String corpCode = storedCpCd.get().getCorpCode();
        String reprtCode = reportCode.get().label();
        
        String combineUrl = String.valueOf(UrlHandlingUtils.combineURLForFSS(targetDomain)
                .append("&corp_code="+ corpCode)
                .append("&bsns_year="+ currentYear)
                .append("&reprt_code=" + reprtCode));

        JsonArray asParsingArr = getFssDataFromJson(UrlHandlingUtils.getDataByURL(combineUrl,"json"));

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 종목이 없음. 기준일 확인 요망");
        }

        for(JsonElement j:asParsingArr){

                financeDto = FinanceDto.builder()
                        .stockCode(extractJsonEleToStr(j,"stock_code"))
                        .accountNm(extractJsonEleToStr(j,"account_nm"))
                        .bsnsYear(extractJsonEleToStr(j,"bsns_year"))
                        .qrDiv(requestQr)
                        .currentAddAmt(extractJsonEleToStr(j,"frmtrm_add_amount"))
                        .currentAmt(extractJsonEleToStr(j,"thstrm_amount"))
                        .currencyDiv(extractJsonEleToStr(j,"currency"))
                        .fsDiv(extractJsonEleToStr(j,"fs_div"))
                        .build();

             financeDao.insertFinance(financeDto);
        }

    }

    private JsonArray getFssDataFromJson(JsonElement resData){

        JsonArray asParsingJsonArr =  resData.getAsJsonObject()
                .get("list").getAsJsonArray();

        return asParsingJsonArr;
    }

    private JsonArray getFscDataFromJson(JsonElement resData){

        JsonArray asParsingJsonArr =  resData.getAsJsonObject()
                .get("response").getAsJsonObject()
                .get("body").getAsJsonObject()
                .get("items").getAsJsonObject()
                .get("item").getAsJsonArray();

        return asParsingJsonArr;
    }

    private String extractJsonEleToStr(JsonElement jsonElement, String target){

       JsonElement parsingEle = jsonElement.getAsJsonObject().get(target);

       if(parsingEle == null || parsingEle.isJsonNull() ){
           return "0";
       }
       return parsingEle.toString().replaceAll("[\",]","");
    }


    private enum ReportCode{
        FIRST_QUARTER("11013")
        ,SECOND_QUARTER("11012")
        ,THIRD_QUARTER("11014")
        ,FOURTH_QUARTER("11011")
        ;

        private final String label;

        ReportCode(String label){
            this.label = label;
        }

        public String label(){
            return label;
        }

        public static ReportCode valueOfQuarter(String quarter){
            return Arrays.stream(values())
                    .filter(value -> value.name().equals(quarter))
                    .findAny()
                    .orElse(null);
        }
    }
}