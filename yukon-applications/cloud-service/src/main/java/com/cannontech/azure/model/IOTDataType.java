package com.cannontech.azure.model;

import com.google.gson.annotations.SerializedName;

public enum IOTDataType {
    @SerializedName("Telemetry")
    TELEMETRY,
    @SerializedName("Property")
    PROPERTY,
    @SerializedName("Both")
    BOTH;
}
