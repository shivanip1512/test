package com.cannontech.dr.nest.model.v3;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerEnrollment {
    private String customerId;
    private EnrollmentState enrollmentState;
    private String groupId;
    private RejectionReason rejectionReason;
    private String rejectionNote;
    
    public CustomerEnrollment() {
    }
    
    @JsonCreator
    public CustomerEnrollment(@JsonProperty("customerId") String customerId, @JsonProperty("enrollmentState") EnrollmentState enrollmentState, 
                              @JsonProperty("groupId") String groupId, @JsonProperty("rejectionReason") RejectionReason rejectionReason, 
                              @JsonProperty("rejectionNote") String rejectionNote) {
        super();
        this.customerId = customerId;
        this.enrollmentState = enrollmentState;
        this.groupId = groupId;
        this.rejectionReason = rejectionReason;
        this.rejectionNote = rejectionNote;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public EnrollmentState getEnrollmentState() {
        return enrollmentState;
    }
    public void setEnrollmentState(EnrollmentState enrollmentState) {
        this.enrollmentState = enrollmentState;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }
    public void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    public String getRejectionNote() {
        return rejectionNote;
    }
    public void setRejectionNote(String rejectionNote) {
        this.rejectionNote = rejectionNote;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
