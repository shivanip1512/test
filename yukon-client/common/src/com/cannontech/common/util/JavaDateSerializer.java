package com.cannontech.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JavaDateSerializer extends JsonSerializer<Date> {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg) throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}
