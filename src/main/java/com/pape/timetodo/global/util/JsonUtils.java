package com.pape.timetodo.global.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 싱글톤 패턴으로 인한 인스턴스 받기
     * @return
     */
    public static ObjectMapper getInstance() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static <P> P stringToObject(String jsonData, Class<P> classType){
        P result = null;
        try {
            result = objectMapper.readValue(jsonData, classType);
        } catch (JsonProcessingException e) {
            log.error("",e);
        }
        return result;
    }

    public static String objectToString(Object value){

        String result = null;
        try {
            result = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("",e);
        }
        return result;
    }

    /**
     * filePath 경로에 지정된 JSON 파일을 읽어 List형태로 반환, JSON파일이 배열형태로 있어야 함
     * @param <P>
     * @param filePath
     * @param classType
     * @return
     */
    public static <P> List<P> readJsonFile(String filePath, Class<P> classType){        

        List<P> result = new ArrayList<P>();

        ClassPathResource resource = new ClassPathResource(filePath);

        try {
            JSONParser parser = new JSONParser();
            
            byte[] binaryData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String strJson = new String(binaryData, StandardCharsets.UTF_8);

            JSONArray dateArray = (JSONArray) parser.parse(strJson);

            for(Object obj : dateArray){
                P node = getInstance().convertValue(obj, classType);

                result.add(node);
            }
        } catch (IOException e) {
            log.error("",e);
            throw new AppException(ExceptionCode.FILE_ERROR);
        } catch (ParseException e) {            
            log.error("",e);
            throw new AppException(ExceptionCode.FILE_PARSING_ERROR);
        }

        return result;
    }

}
