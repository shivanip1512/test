package com.cannontech.yukon.system.metrics.listener;

import java.lang.reflect.Type;

import org.joda.time.DateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateTimeDeserializer implements JsonDeserializer {
    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new DateTime(json.getAsJsonPrimitive().getAsString());
    }
}
