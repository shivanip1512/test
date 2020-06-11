package com.cannontech.common.trend.model;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateSerializer extends JsonSerializer<DateTime> {
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider arg2)
        throws IOException, JsonProcessingException {

        gen.writeString(formatter.print(value));
    }
}
