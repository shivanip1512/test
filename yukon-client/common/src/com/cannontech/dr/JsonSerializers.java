package com.cannontech.dr;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.dr.ecobee.message.EcobeeJobStatus;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.ConnectionStatus;
import com.cannontech.dr.honeywellWifi.azure.event.EquipmentStatus;
import com.cannontech.dr.honeywellWifi.azure.event.EventPhase;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public interface JsonSerializers {
    public static final DateTimeFormatter COMBINED_DATE_TIME = 
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
    
    public static final DateTimeFormatter HONEYWELL_DATE_TIME = 
            ISODateTimeFormat.dateTimeNoMillis(); //yyyy-MM-dd'T'HH:mm:ssZZ
    
    public static final DateTimeFormatter HONEYWELL_DATE_TIME_WITH_MILLIS = 
            ISODateTimeFormat.dateTime(); //yyyy-MM-dd'T'HH:mm:ss.SSSZZ
    
    public static final DateTimeFormatter HONEYWELL_WRAPPER_DATE_TIME = 
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC();
    
    public static final DateTimeFormatter PX_WHITE_ERROR_MESSAGE_DATE_TIME = 
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZ");
    
    class TO_DATE_PX_WHITE_ERROR extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = PX_WHITE_ERROR_MESSAGE_DATE_TIME.print(date);
            jsonGenerator.writeString(dateString);
        }
    }
    
    class FROM_DATE_PX_WHITE_ERROR extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            return PX_WHITE_ERROR_MESSAGE_DATE_TIME.parseDateTime(paramJsonParser.getValueAsString()).toInstant();
        }
    }
    
    class TO_DURATION extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(Long.toString(duration.getStandardSeconds()));
        }
    }
    
    class FROM_DURATION extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) 
                throws IOException, JsonProcessingException {
            Double durationSeconds = jsonParser.getValueAsDouble();
            if (durationSeconds == null) {
                return null;
            }
            //This is potentially losing a fraction of a second converting double to long, but I don't think we care.
            return Duration.standardSeconds(durationSeconds.longValue());
        }
    }
    
    class TO_TEMPERATURE_UNIT extends JsonSerializer<TemperatureUnit> {
        @Override
        public void serialize(TemperatureUnit unit, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(unit.getLetter());
        }
    }
    
    class FROM_TEMPERATURE_UNIT extends JsonDeserializer<TemperatureUnit> {
        @Override
        public TemperatureUnit deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) 
                throws IOException, JsonProcessingException {
            return TemperatureUnit.fromAbbreviation(jsonParser.getValueAsString());
        }
    }
    
    class TO_HONEYWELL_MESSAGE_TYPE extends JsonSerializer<HoneywellWifiDataType> {
        @Override
        public void serialize(HoneywellWifiDataType type, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(type.getJsonString());
        }
    }
    
    class FROM_HONEYWELL_MESSAGE_TYPE extends JsonDeserializer<HoneywellWifiDataType> {
        @Override
        public HoneywellWifiDataType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) 
                throws IOException, JsonProcessingException {
            Optional<HoneywellWifiDataType> optionalType = HoneywellWifiDataType.forJsonString(jsonParser.getValueAsString());
            return optionalType.orElse(HoneywellWifiDataType.UNKNOWN);
        }
    }
    
    class TO_DATE_HONEYWELL extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = HONEYWELL_DATE_TIME.print(date);
            jsonGenerator.writeString(dateString);
        }
    }

    class FROM_DATE_HONEYWELL extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            Instant result;
            try {
                //first, try to parse without millis
                result = HONEYWELL_DATE_TIME.parseDateTime(paramJsonParser.getValueAsString()).toInstant();
            } catch (IllegalArgumentException e) {
                //try to parse with millis
                result = HONEYWELL_DATE_TIME_WITH_MILLIS.parseDateTime(paramJsonParser.getValueAsString()).toInstant();
            }
            return result;
        }
    }
    
    class TO_DATE_HONEYWELL_WRAPPER extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = HONEYWELL_WRAPPER_DATE_TIME.print(date);
            jsonGenerator.writeString(dateString);
        }
    }

    class FROM_DATE_HONEYWELL_WRAPPER extends JsonDeserializer<Instant> {
        @Override
        public Instant deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            
            String stringValue = paramJsonParser.getValueAsString();
            //If the date includes fractional seconds, strip them off
            if (stringValue.length() > 19) {
                stringValue = stringValue.substring(0, 19);
            }
            return HONEYWELL_WRAPPER_DATE_TIME.parseDateTime(stringValue).toInstant();
        }
    }
    
    class FROM_EQUIPMENT_STATUS extends JsonDeserializer<EquipmentStatus> {
        @Override
        public EquipmentStatus deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            return EquipmentStatus.of(paramJsonParser.getValueAsString());
        }
    }
    
    class TO_EQUIPMENT_STATUS extends JsonSerializer<EquipmentStatus> {
        @Override
        public void serialize(EquipmentStatus status, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(status.getJsonString());
        }
    }
    
    class FROM_CONNECTION_STATUS extends JsonDeserializer<ConnectionStatus> {
        @Override
        public ConnectionStatus deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            return ConnectionStatus.of(paramJsonParser.getValueAsString());
        }
    }
    
    class TO_CONNECTION_STATUS extends JsonSerializer<ConnectionStatus> {
        @Override
        public void serialize(ConnectionStatus status, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(status.getJsonString());
        }
    }
    
    class FROM_PHASE extends JsonDeserializer<EventPhase> {
        @Override
        public EventPhase deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            return EventPhase.of(paramJsonParser.getValueAsString());
        }
    }
    
    class TO_PHASE extends JsonSerializer<EventPhase> {
        @Override
        public void serialize(EventPhase phase, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(phase.getJsonString());
        }
    }
    
    class TO_DATE extends JsonSerializer<Instant> {
        private static final DateTimeFormatter ecobeeDateFormatter =
            DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = ecobeeDateFormatter.print(date);
            jsonGenerator.writeString(dateString);
        }
    }

    class FROM_DATE extends JsonDeserializer<Instant> {
        private static final DateTimeFormatter ecobeeDateFormatter =
            DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

        @Override
        public Instant deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            return ecobeeDateFormatter.parseDateTime(paramJsonParser.getValueAsString()).toInstant();
        }
    }

    class TO_TIME extends JsonSerializer<Instant> {
        private static final DateTimeFormatter ecobeeTimeFormatter =
            DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC();

        @Override
        public void serialize(Instant date, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            String dateString = ecobeeTimeFormatter.print(date);
            jsonGenerator.writeString(dateString);
        }
    }

    class FROM_TIME extends JsonDeserializer<Instant> {
        private static final DateTimeFormatter ecobeeTimeFormatter =
            DateTimeFormat.forPattern("HH:mm:ss").withZoneUTC();

        @Override
        public Instant deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
                throws IOException, JsonProcessingException {
            return ecobeeTimeFormatter.parseDateTime(paramJsonParser.getValueAsString()).toInstant();
        }
    }

    class TO_BASIC_CSV extends JsonSerializer<Collection<String>> {
        @Override
        public void serialize(Collection<String> strings, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(Joiner.on(",").join(strings));
        }
    }

    class FROM_BASIC_CSV extends JsonDeserializer<Collection<String>> {
        @Override
        public Collection<String> deserialize(JsonParser jsonParser,
                DeserializationContext paramDeserializationContext) throws IOException, JsonProcessingException {
            return Lists.newArrayList(jsonParser.getValueAsString().split(","));
        }
    }

    class TO_SELECTION_TYPE extends JsonSerializer<SelectionType> {
        @Override
        public void serialize(SelectionType selectionType, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(selectionType.getEcobeeString());
        }
    }

    class FROM_SELECTION_TYPE extends JsonDeserializer<SelectionType> {
        @Override
        public SelectionType deserialize(JsonParser paramJsonParser,
                DeserializationContext paramDeserializationContext) throws IOException, JsonProcessingException {
            return SelectionType.fromEcobeeString(paramJsonParser.getValueAsString());
        }
    }

    class TO_JOB_STATUS extends JsonSerializer<EcobeeJobStatus> {
        @Override
        public void serialize(EcobeeJobStatus status, JsonGenerator jsonGenerator, SerializerProvider notUsed)
                throws IOException, JsonProcessingException {
            jsonGenerator.writeString(status.getEcobeeStatusString());
        }
    }

    class FROM_JOB_STATUS extends JsonDeserializer<EcobeeJobStatus> {
        @Override
        public EcobeeJobStatus deserialize(JsonParser paramJsonParser,
                DeserializationContext paramDeserializationContext) throws IOException, JsonProcessingException {
            return EcobeeJobStatus.fromEcobeeStatusString(paramJsonParser.getValueAsString());
        }
    }
}
