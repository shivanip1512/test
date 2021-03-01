package com.cannontech.common.util;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateDeserializer extends JsonDeserializer<DateTime> {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy");

    @Override
    public DateTime deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
            throws IOException, JsonProcessingException {
        return dateFormatter.parseDateTime(paramJsonParser.getValueAsString());
    }

}
