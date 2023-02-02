package com.smallv.stock.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.json.XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class UrlHandlingUtils {

    private final TrustAllCertsUtil trustAllCertsUtil;

    @Value("${public-data-service-key.fsc}")
    private String fscServiceKey;
    @Value("${public-data-service-key.fss}")
    private String fssServiceKey;

    /*
        - URL 결합기 -
        도메인 + 서비스 키 + 한 페이지 결과 수 + 페이지번호 + 응답형식 + 기준일자
    */
    /* FSC : Financial Service Commission 금융위원회 */
    public String combineURLForFSC(String targetDomain, String numOfRaws, String pageNo, LocalDate baseDt){

        StringBuilder urlBuilder = new StringBuilder(targetDomain);
        urlBuilder.append(
                        "?serviceKey=" + fscServiceKey +
                        "&numOfRows=" + numOfRaws +
                        "&pageNo="+ pageNo +
                        "&basDt=" + baseDt.format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                        "&resultType=json"
                    );

        return String.valueOf(urlBuilder);
    }

    /*
        - URL 결합기 -
        도메인 + 서비스 키
    */
    /* FSS : Financial Supervisory Service 금융감독원  */
    public String combineURLForFSS(String targetDomain){

        StringBuilder urlBuilder = new StringBuilder(targetDomain);
        urlBuilder.append(
                "?crtfc_key=" + fssServiceKey
        );

        return String.valueOf(urlBuilder);
    }
    
    /*
        오픈 API 연결-조회
    */
    public JsonObject getDataByURL(String combineUrl,String convertType) {

        HttpURLConnection conn = null;
        URL url;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

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

            String line;
            while ((line=br.readLine())!=null){
                sb.append(line);
            }
            System.out.println(sb.toString());
            JsonObject asJsonObj;

            if(convertType.equalsIgnoreCase("xml")){
                JSONObject jsonSimpleObj = XML.toJSONObject(sb.toString());
                asJsonObj = JsonParser.parseString(jsonSimpleObj.toString()).getAsJsonObject();
            } else {
                asJsonObj = JsonParser.parseString(sb.toString()).getAsJsonObject();
            }

            br.close();
            conn.disconnect();

            return asJsonObj;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
