package com.pape.timetodo.global.common.mail.model;

import com.pape.timetodo.global.common.template.model.TemplabeBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class CertMailTemplateModel extends TemplabeBaseModel{
    
    private String certNum;

    @Override
    public void init() {
        super.setTemplate("mail/cert_mail");
    }

    @Override
    public Map<String, Object> getParam() {
        Field[] fields = getClass().getDeclaredFields();

        HashMap<String, Object> result = new HashMap<>();

        try {
            for(Field field : fields){
                Object value = field.get(this);
                String key = field.getName();

                result.put(key, value);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("",e);
        }

        return result;
    }

}
