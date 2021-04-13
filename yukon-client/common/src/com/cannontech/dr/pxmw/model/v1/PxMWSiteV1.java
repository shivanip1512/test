package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWSiteV1 implements Serializable {
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
    private List<PxMWSiteDeviceV1> devices;

    @JsonCreator
    public PxMWSiteV1(@JsonProperty("id") String siteGuid,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description, 
            @JsonProperty("contact") String contact,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone,
            @JsonProperty("created") String createdTime, 
            @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") String modifiedTime,
            @JsonProperty("modified_by") String modifiedByUser,
            @JsonProperty("devices") List<PxMWSiteDeviceV1> devices) {
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
        this.devices = devices;
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
    
    @JsonProperty("devices")
    public List<PxMWSiteDeviceV1> getDevices() {
        return devices;
    }
}
