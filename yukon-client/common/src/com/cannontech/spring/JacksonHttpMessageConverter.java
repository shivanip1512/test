package com.cannontech.spring;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.cannontech.clientutils.YukonLogManager;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class JacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static final Logger log = YukonLogManager.getLogger(JacksonHttpMessageConverter.class);

    public JacksonHttpMessageConverter() {
        super();
        objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        try {
            return super.readInternal(clazz, inputMessage);
        } catch (HttpMessageNotReadableException e) {
            log.error("Unable to convert json to type: " + clazz, e);
            throw e;
        }
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        try {
            super.writeInternal(object, outputMessage);
        } catch (HttpMessageNotWritableException e) {
            if (object == null) {
                log.error("Unable to convert null to json.", e);
            } else {
                log.error("Unable to convert type: " + object.getClass() + " to json.", e);
            }
            throw e;
        }
    }
}
