package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudServiceAccountDetailV1 implements Serializable {
    private String serviceAccountId;
    private String name;
    private Boolean isActive;
    private String userType;
    private String adopterId;
    private List<EatonCloudSecretV1> secrets;
    private String createdTime;
    private String createdByUser;
    private String modifiedTime;
    private String modifiedByUser;
    
    public EatonCloudServiceAccountDetailV1(@JsonProperty("id") String serviceAccountId, @JsonProperty("name") String name,
            @JsonProperty("is_active") Boolean isActive, @JsonProperty("user_type") String userType,
            @JsonProperty("service_account_adopterid") String adopterId,
            @JsonProperty("secrets") List<EatonCloudSecretV1> secrets,
            @JsonProperty("created") String createdTime,
            @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") String modifiedTime,
            @JsonProperty("modified_by") String modifiedByUser) {
        this.serviceAccountId = serviceAccountId;
        this.name = name;
        this.isActive = isActive;
        this.userType = userType;
        this.adopterId = adopterId;
        this.secrets = secrets;
        this.createdTime = createdTime;
        this.createdByUser = createdByUser;
        this.modifiedTime = modifiedTime;
        this.modifiedByUser = modifiedByUser;
    }

    @JsonProperty("id")
    public String getServiceAccountId() {
        return serviceAccountId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("is_active")
    public Boolean getIsActive() {
        return isActive;
    }

    @JsonProperty("user_type")
    public String getUserType() {
        return userType;
    }
    @JsonProperty("service_account_adopterid")
    public String getAdopterId() {
        return adopterId;
    }

    @JsonProperty("secrets")
    public List<EatonCloudSecretV1> getSecrets() {
        return secrets;
    }

    @JsonProperty("created")
    public String getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("created_by")
    public String getCreatedByUser() {
        return createdByUser;
    }

    @JsonProperty("modified")
    public String getModifiedTime() {
        return modifiedTime;
    }

    @JsonProperty("modified_by")
    public String getModifiedByUser() {
        return modifiedByUser;
    }
}
