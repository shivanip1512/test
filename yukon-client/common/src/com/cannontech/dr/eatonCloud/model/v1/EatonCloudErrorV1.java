package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EatonCloudErrorV1 implements Serializable {
    private List<String> invalidParameters;
    @JsonAlias({"message"})
    private String message;
    private String errorId;
    @JsonAlias({"statusCode"})
    private Integer errorCode;
    private String generatedTime;
    private Integer errorNumber;

    @JsonCreator
    public EatonCloudErrorV1(@JsonProperty("InvalidParameters") List<String> invalidParameters,
            @JsonProperty("Message") String message,
            @JsonProperty("ErrorId") String errorId,
            @JsonProperty("ErrorCode") Integer errorCode,
            @JsonProperty("GeneratedTime") String generatedTime,
            @JsonProperty("ErrorNumber") Integer errorNumber) {
        this.invalidParameters = invalidParameters;
        this.message =  message;
        this.errorId = errorId;
        this.errorCode = errorCode;
        this.generatedTime = generatedTime;
        this.errorNumber = errorNumber;
    }
    
    public EatonCloudErrorV1(Integer errorCode, String message) {
        this.message =  message;
        this.errorCode = errorCode;
    }

    @JsonProperty("InvalidParameters") 
    public List<String> getInvalidParameters() {
        return invalidParameters;
    }

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
    public String getGeneratedTime() {
        return generatedTime;
    }
    
    @JsonProperty("ErrorNumber")
    public Integer getErrorNumber() {
        return errorNumber;
    }
}
