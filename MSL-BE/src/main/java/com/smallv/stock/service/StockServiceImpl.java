package com.smallv.stock.service;

import com.google.gson.*;
import com.smallv.stock.dto.FinanceDto;
import com.smallv.stock.dto.StockDto;
import com.smallv.stock.repository.FinanceDao;
import com.smallv.stock.repository.StockDao;
import com.smallv.stock.util.FileReaderUtils;
import com.smallv.stock.util.UrlHandlingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;

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
        } else {
            for(JsonElement j:asParsingArr){
                try {
                    stockObj = StockDto.builder()
                                .stockCode(extractJsonStr.apply(j,"srtnCd"))
                                .stockNm(extractJsonStr.apply(j,"itmsNm"))
                                .osCnt(extractJsonStr.apply(j,"lstgStCnt"))
                                .mrktDiv(extractJsonStr.apply(j,"mrktCtg"))
                                .build();

                    stockDao.insertStock(stockObj);
                } catch (DuplicateKeyException e){
                }
            }
            System.out.println("****완료****");
        }
    }

    @Override
    public void saveCorpCode() {
        StockDto stock;

        JsonArray asParsingArr = fileReaderUtils.convertXMLToJson()
                .get("result")
                .getAsJsonObject()
                .get("list")
                .getAsJsonArray();

        if(asParsingArr.isEmpty()){
            throw new IllegalStateException("조회된 기업이 없음. 데이터 확인 요망");
        } else {
            for(JsonElement j:asParsingArr){
                stock = StockDto.builder()
                        .stockCode(extractJsonStr.apply(j,"stock_code"))
                        .corpCode(extractJsonStr.apply(j,"corp_code"))
                        .build();

                if(stock.getStockCode().length()>=6){
                    stockDao.updateStockOnlyCrno(stock);
                }
            }
            System.out.println("****완료****");
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
        } else {
            for(JsonElement j:asParsingArr){
                try {
                    financeDto = FinanceDto.builder()
                            .stockCode(extractJsonStr.apply(j,"stock_code"))
                            .accountNm(extractJsonStr.apply(j,"account_nm"))
                            .bsnsYear(extractJsonStr.apply(j,"bsns_year"))
                            .qrDiv(requestQr)
                            .currentAddAmt(extractJsonStr.apply(j,"frmtrm_add_amount"))
                            .currentAmt(extractJsonStr.apply(j,"thstrm_amount"))
                            .currencyDiv(extractJsonStr.apply(j,"currency"))
                            .build();
                } catch (NullPointerException e) {
                    throw new RuntimeException(e);
                }
                 financeDao.insertFinance(financeDto);
            }
        }
    }

    private static JsonArray getFssDataFromJson(JsonObject resData){
        Optional<JsonElement> asParsingArr = Optional.ofNullable(resData.get("list"));

        asParsingArr.orElseThrow(()-> new IllegalStateException("요청 조건 확인 요망"));

        return asParsingArr.get().getAsJsonArray();
    }

    private static JsonArray getFscDataFromJson(JsonObject resData){

        Optional<JsonElement> asParsingArr = Optional.ofNullable(resData.get("response"));

        asParsingArr.orElseThrow(()-> new IllegalStateException("요청 조건 확인 요망"));

        asParsingArr.get().getAsJsonObject()
                .get("body").getAsJsonObject()
                .get("items").getAsJsonObject()
                .get("item").getAsJsonArray();

        return asParsingArr.get().getAsJsonArray();
    }

    BiFunction<JsonElement,String,String> extractJsonStr = (j,s) -> {
        String result="";
        try{
            result = j.getAsJsonObject().get(s).toString().replaceAll("[,\"]","");
        }catch (NullPointerException e) {
            result = "0";
        }
        return result;
    };


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