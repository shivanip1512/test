package com.cannontech.dr.honeywellWifi;

import org.joda.time.Instant;

import com.cannontech.dr.JsonSerializers.FROM_DATE_HONEYWELL;
import com.cannontech.dr.JsonSerializers.FROM_HONEYWELL_MESSAGE_TYPE;
import com.cannontech.dr.JsonSerializers.TO_DATE_HONEYWELL;
import com.cannontech.dr.JsonSerializers.TO_HONEYWELL_MESSAGE_TYPE;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonIgnoreProperties(ignoreUnknown=true)
public class HoneywellWifiMessageWrapper {
    private final String identifier;
    private final HoneywellWifiDataType type;
    private final Instant date;
    private final String json;
    
    @JsonCreator
    public HoneywellWifiMessageWrapper(@JsonProperty("identifier") String identifier,
                                       @JsonDeserialize(using=FROM_HONEYWELL_MESSAGE_TYPE.class) @JsonProperty("type") HoneywellWifiDataType type,
                                       @JsonDeserialize(using=FROM_DATE_HONEYWELL.class) @JsonProperty("date") Instant date,
                                       @JsonProperty("json") String json) {
        this.identifier = identifier;
        this.type = type;
        this.date = date;
        this.json = json;
    }

    public String getIdentifier() {
        return identifier;
    }

    @JsonSerialize(using=TO_HONEYWELL_MESSAGE_TYPE.class)
    public HoneywellWifiDataType getType() {
        return type;
    }

    @JsonSerialize(using=TO_DATE_HONEYWELL.class)
    public Instant getDate() {
        return date;
    }

    public String getJson() {
        return json;
    }
}
