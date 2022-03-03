package com.cannontech.common.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.TypeNotSupportedException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JavaDateDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
    private static final Logger log = YukonLogManager.getLogger(JavaDateDeserializer.class);

    @Override
    public Date deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
            throws IOException, JsonProcessingException {
        Date date = null;

        try {
            dateFormatter.setLenient(false);
            date = dateFormatter.parse(paramJsonParser.getValueAsString());
        } catch (ParseException e) {

            log.error("Error while parsing date");
            throw new TypeNotSupportedException("Date format not supported");
        }
        return date;
    }

}
