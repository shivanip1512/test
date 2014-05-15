package com.cannontech.common.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.Joiner;

public interface JsonSerializers {

    class EcobeeDate extends JsonSerializer<Instant> {
        private static final DateTimeFormatter drDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        
        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = drDateFormatter.print(date);
            jsonGenerator.writeString(dateString);
        }
    }

    class EcobeeTime extends JsonSerializer<Instant> {
        private static final DateTimeFormatter drDateFormatter = DateTimeFormat.forPattern("HH:mm:ss");

        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = drDateFormatter.print(date);
            jsonGenerator.writeString(dateString);
        }
    }

    class EcobeeRuntimeReportRow extends JsonDeserializer<RuntimeReportRow> {
        private static final DateTimeFormatter ecobeeDateTimeFormatter = 
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();

        @Override
        public RuntimeReportRow deserialize(JsonParser parser,
                                  DeserializationContext context)
                throws IOException, JsonProcessingException {
            // https://www.ecobee.com/home/developer/api/documentation/v1/operations/get-runtime-report.shtml
            // "2010-01-01,00:00:00,heatOff,70,92,..."
            String str = parser.getValueAsString();
            String[] split = str.split(",", -1);
            Instant date = ecobeeDateTimeFormatter.parseDateTime(split[0] + " " + split[1]).toInstant();
            // order comes from deviceReadColumns in EcobeeCommunicationServiceImpl
            String eventName = split[2];
            Float indoorTemp = StringUtils.isEmpty(split[3]) ? null : Float.parseFloat(split[3]);
            Float outdoorTemp = StringUtils.isEmpty(split[4]) ? null : Float.parseFloat(split[4]);
            Float coolSetPoint = StringUtils.isEmpty(split[5]) ? null : Float.parseFloat(split[5]);
            Float heatSetPoint = StringUtils.isEmpty(split[6]) ? null : Float.parseFloat(split[6]);
            Integer coolRuntime = StringUtils.isEmpty(split[7]) ? null : Integer.parseInt(split[7]);
            Integer heatRuntime = StringUtils.isEmpty(split[8]) ? null : Integer.parseInt(split[8]);

            return new RuntimeReportRow(date, eventName, indoorTemp, outdoorTemp, coolSetPoint, heatSetPoint, 
                                        coolRuntime, heatRuntime);
        }
    }

    class Csv extends JsonSerializer<Iterable<String>> {
        @Override
        public void serialize(Iterable<String> strings, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(Joiner.on(",").join(strings));
        }
    }
}
