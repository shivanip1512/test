package com.cannontech.dr.edgeDr;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeDrWebhookRequest implements Serializable{
    String token;
    String name;
    String type;
    String payload;
    String errorMsg;

    public EdgeDrWebhookRequest(@JsonProperty("token") String token, @JsonProperty("name") String name,
            @JsonProperty("type") String type, @JsonProperty("payload") String payload,
            @JsonProperty("errorMsg") String errorMsg) {
        this.token = token;
        this.name = name;
        this.type = type;
        this.payload = payload;
        this.errorMsg = errorMsg;
    }

    @JsonProperty("token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("payload")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @JsonProperty("errorMsg")
    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "EdgeDrWebhookRequest [token=" + token + ", name=" + name + ", type=" + type + ", payload=" + payload
                + ", errorMsg=" + errorMsg + "]";
    }

}
