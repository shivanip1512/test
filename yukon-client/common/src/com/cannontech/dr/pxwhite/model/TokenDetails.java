package com.cannontech.dr.pxwhite.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information about a PX White token, including expiration info, and info for the user who owns the token.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TokenDetails {
    private final String id;
    private final String expirationDate;
    private final String email;
    private final String displayName;
    private final String jobTitle;
    private final String department;
    private final String phoneNumber;
    private final String created;
    private final String createdBy;
    private final String modified;
    private final String modifiedBy;
    
    @JsonCreator
    public TokenDetails(@JsonProperty("id") String id,
                        @JsonProperty("expiration_date") String expirationDate,
                        @JsonProperty("email") String email,
                        @JsonProperty("display_name") String displayName,
                        @JsonProperty("job_title") String jobTitle,
                        @JsonProperty("department") String department,
                        @JsonProperty("phone_number") String phoneNumber, 
                        @JsonProperty("created") String created,
                        @JsonProperty("created_by") String createdBy,
                        @JsonProperty("modified") String modified,
                        @JsonProperty("modified_by") String modifiedBy) {
        this.id = id;
        this.expirationDate = expirationDate;
        this.email = email;
        this.displayName = displayName;
        this.jobTitle = jobTitle;
        this.department = department;
        this.phoneNumber = phoneNumber;
        this.created = created;
        this.createdBy = createdBy;
        this.modified = modified;
        this.modifiedBy = modifiedBy;
    }

    public String getId() {
        return id;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getDepartment() {
        return department;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModified() {
        return modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }
    
}
