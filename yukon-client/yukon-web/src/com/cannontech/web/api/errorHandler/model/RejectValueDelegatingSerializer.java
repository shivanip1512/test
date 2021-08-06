package com.cannontech.web.api.errorHandler.model;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;

import com.cannontech.common.util.DateSerializer;
import com.cannontech.common.util.JavaDateSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * This serializer delegator choose the correct serializer based on passed value
 */
public class RejectValueDelegatingSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object type, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (type instanceof DateTime) {
            new DateSerializer().serialize((DateTime) type, gen, provider);
        }
        else if (type instanceof Date) {
            new JavaDateSerializer().serialize((Date) type, gen, provider);
        } else {
            gen.writeString(type.toString());
        }
    }
}
