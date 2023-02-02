package com.smallv.stock.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class FileReaderUtils {

    public JsonObject convertXMLToJson() {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        try {
            br = new BufferedReader(new FileReader("src/main/resources/static/CORPCODE.xml"));

            String line;
            while ((line=br.readLine())!=null){
                sb.append(line);
            }

            JSONObject jsonSimpleObj = XML.toJSONObject(sb.toString());
            JsonObject asJsonObj = JsonParser.parseString(jsonSimpleObj.toString()).getAsJsonObject();

            br.close();

            return asJsonObj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}