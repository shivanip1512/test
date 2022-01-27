package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.joda.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudServiceAccountDetailV1 implements Serializable {
    private String serviceAccountId;
    private String name;
    private Boolean isActive;
    private String userType;
    private String adopterId;
    private List<EatonCloudSecretV1> secrets;
    private Date createdTime;
    private String createdByUser;
    private Date modifiedTime;
    private String modifiedByUser;
    
    public EatonCloudServiceAccountDetailV1(@JsonProperty("id") String serviceAccountId, @JsonProperty("name") String name,
            @JsonProperty("is_active") Boolean isActive, @JsonProperty("user_type") String userType,
            @JsonProperty("service_account_adopterid") String adopterId,
            @JsonProperty("secrets") List<EatonCloudSecretV1> secrets,
            @JsonProperty("created") Date createdTime,
            @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") Date modifiedTime,
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
    public Date getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("created_by")
    public String getCreatedByUser() {
        return createdByUser;
    }

    @JsonProperty("modified")
    public Date getModifiedTime() {
        return modifiedTime;
    }

    @JsonProperty("modified_by")
    public String getModifiedByUser() {
        return modifiedByUser;
    }
    
    public Instant getExpiryTime(int secretNumber) {
        return getSecrets().stream()
                .findFirst()
                .map(secret -> secret.getExpiryTime())
                .map(date -> new Instant(date))
                .orElse(null);
    }
}
