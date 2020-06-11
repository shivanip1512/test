package com.cannontech.common.trend.model;

import java.io.IOException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy").withZoneUTC();

    @Override
    public LocalDate deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
            throws IOException, JsonProcessingException {
        return dateFormatter.parseLocalDate(paramJsonParser.getValueAsString());
    }

}
