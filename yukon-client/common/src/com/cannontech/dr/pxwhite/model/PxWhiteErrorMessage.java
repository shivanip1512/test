package com.cannontech.dr.pxwhite.model;

import org.joda.time.Instant;

import com.cannontech.dr.JsonSerializers.FROM_DATE_PX_WHITE_ERROR;
import com.cannontech.dr.JsonSerializers.TO_DATE_PX_WHITE_ERROR;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * PX White puts this object in the body of an error response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteErrorMessage {
    private final String message;
    private final String errorId;
    private final Integer errorCode;
    private final Instant generatedTime;
    private final String requestedUri;
    
    @JsonCreator
    public PxWhiteErrorMessage(@JsonProperty("Message") String message,
                               @JsonProperty("ErrorId") String errorId,
                               @JsonProperty("ErrorCode") Integer errorCode,
                               @JsonProperty("GeneratedTime") @JsonDeserialize(using=FROM_DATE_PX_WHITE_ERROR.class) Instant generatedTime,
                               @JsonProperty("RequestedUri") String requestedUri) {
        
        this.message = message;
        this.errorId = errorId;
        this.errorCode = errorCode;
        this.generatedTime = generatedTime;
        this.requestedUri = requestedUri;
    }

    /**
     * This field is currently serialized as a String, but it is actually a mish-mash of plain ol' String and
     * escaped JSON. We could un-mangle and parse that JSON, if we find a need.
     */
    @JsonProperty("Message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("ErrorId")
    public String getErrorId() {
        return errorId;
    }

    @JsonProperty("ErrorCode")
    public Integer getErrorCode() {
        return errorCode;
    }

    @JsonProperty("GeneratedTime")
    @JsonSerialize(using=TO_DATE_PX_WHITE_ERROR.class)
    public Instant getGeneratedTime() {
        return generatedTime;
    }

    @JsonProperty("RequestedUri")
    public String getRequestedUri() {
        return requestedUri;
    }
}
