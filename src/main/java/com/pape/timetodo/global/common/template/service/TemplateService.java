package com.pape.timetodo.global.common.template.service;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.pape.timetodo.global.common.template.model.TemplabeBaseModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    /**
     * Thymeleaf HTML 파일을 데이터 바인딩하여 String으로 변환
     *
     * @param <T>
     * @param pdfDto
     * @return
     */
    public <T extends TemplabeBaseModel> String getHtmlToString(T templateDto) {

        Map<String, Object> param = templateDto.getParam();

        // Thymeleaf 방식 html에 입힐 데이터 바인딩
        Context context = new Context();

        for (Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();

            context.setVariable(key, param.get(key));
        }

        templateDto.init();

        // 앞 뒤 prefix, suffix는 yml에 정의해놓음
        // html에 바인딩할 데이터 넣고 파싱이후 String형식으로 뽑아옴
        String htmlContent = templateEngine.process(templateDto.getTemplate(), context);

        return htmlContent;
    }

}   
