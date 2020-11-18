package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWSiteV1 implements Serializable {
    private String siteGuid;
    private String adopterGuid;
    private String name;
    private String description;
    private String contact;
    private String email;
    private String phone;
    private List<PxMWSiteDeviceV1> devices;
    private String createdTime;
    private String createdByUser;
    private String modifiedTime;
    private String modifiedByUser;

    @JsonCreator
    public PxMWSiteV1(@JsonProperty("id") String siteGuid, @JsonProperty("adopterId") String adopterGuid,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description, @JsonProperty("contact") String contact,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone, @JsonProperty("devices") List<PxMWSiteDeviceV1> devices,
            @JsonProperty("created") String createdTime, @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") String modifiedTime,
            @JsonProperty("modified_by") String modifiedByUser) {
        this.siteGuid = siteGuid;
        this.adopterGuid = adopterGuid;
        this.name = name;
        this.description = description;
        this.contact = contact;
        this.email = email;
        this.phone = phone;
        this.devices = devices;
        this.createdTime = createdTime;
        this.createdByUser = createdByUser;
        this.modifiedTime = modifiedTime;
        this.modifiedByUser = modifiedByUser;
    }

    @JsonProperty("id")
    public String getSiteGuid() {
        return siteGuid;
    }

    @JsonProperty("adopterId")
    public String getAdopterGuid() {
        return adopterGuid;
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

    @JsonProperty("devices")
    public List<PxMWSiteDeviceV1> getDevices() {
        return devices;
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
