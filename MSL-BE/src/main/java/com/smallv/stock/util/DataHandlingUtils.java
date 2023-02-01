package com.smallv.stock.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataHandlingUtils {

    private final TrustAllCertsUtil trustAllCertsUtil;

    @Value("${public-data-service-key.fsc}")
    private String serviceKey;

    /*
        - URL 결합기 -
        도메인 + 서비스 키 + 한 페이지 결과 수 + 페이지번호 + 응답형식 + 기준일자
    */
    /* FSC : Financial Service Commission 금융위원회 */
    public String combineUrlByFSC(String targetDomain, String numOfRaws, String pageNo, LocalDate baseDt){

        StringBuilder urlBuilder = new StringBuilder(targetDomain);
        urlBuilder.append(
                        "?serviceKey=" + serviceKey +
                        "&numOfRows=" + numOfRaws +
                        "&pageNo="+ pageNo +
                        "&basDt=" + baseDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                        "&resultType=json"
                    );

        return String.valueOf(urlBuilder);
    }

    /*
        오픈 API 연결-조회
    */
    public JsonObject getDataByURL(String combineUrl) {

        HttpURLConnection conn = null;
        URL url;
        BufferedReader br = null;

        trustAllCertsUtil.run();

        try {
            url = new URL(combineUrl);
            conn = (HttpURLConnection) url.openConnection();

            if(Optional.ofNullable(conn.getInputStream()).isPresent()){
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                }else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                }
            }
            JsonObject jsonObject = JsonParser.parseString(br.readLine()).getAsJsonObject();

            br.close();
            conn.disconnect();

            return jsonObject;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
