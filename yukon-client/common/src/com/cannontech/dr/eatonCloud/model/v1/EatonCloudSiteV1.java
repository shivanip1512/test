package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EatonCloudSiteV1 implements Serializable {
    private String siteGuid;
    private String name;
    private String description;
    private String contact;
    private String email;
    private String phone;
    private String createdTime;
    private String createdByUser;
    private String modifiedTime;
    private String modifiedByUser;

    @JsonCreator
    public EatonCloudSiteV1(@JsonProperty("id") String siteGuid,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description, 
            @JsonProperty("contact") String contact,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone,
            @JsonProperty("created") String createdTime, 
            @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") String modifiedTime,
            @JsonProperty("modified_by") String modifiedByUser) {
        this.siteGuid = siteGuid;
        this.name = name;
        this.description = description;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.createdTime = createdTime;
        this.createdByUser = createdByUser;
        this.modifiedTime = modifiedTime;
        this.modifiedByUser = modifiedByUser;
    }

    @JsonProperty("id")
    public String getSiteGuid() {
        return siteGuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("contact")
    public String getContact() {
        return contact;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("phone") 
    public String getPhone() {
        return phone;
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
