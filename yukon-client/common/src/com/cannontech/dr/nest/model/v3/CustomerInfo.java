package com.cannontech.dr.nest.model.v3;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CustomerInfo {
    private String customerId;
    private String accountNumber;
    private String name;
    private PostalAddress serviceAddress;
    private String email;
    private int deviceCount;
    private List<String> deviceIds;
    private String enrollTime;
    private String approveTime;
    private String groupId;
    private EnrollmentSource enrollmentSource;
    private EnrollmentState enrollmentState;
    private List<ProgramType> programType;
    private String rejectionReason;
    private String rejectionNotes;
    
    public CustomerInfo() {
        
    }
    
    @JsonCreator
    public CustomerInfo(String customerId, String accountNumber, String name, PostalAddress serviceAddress,
            String email, int deviceCount, List<String> deviceIds, String enrollTime, String approveTime,
            String groupId, EnrollmentSource enrollmentSource, EnrollmentState enrollmentState,
            List<ProgramType> programType, String rejectionReason, String rejectionNotes) {
        super();
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.name = name;
        this.serviceAddress = serviceAddress;
        this.email = email;
        this.deviceCount = deviceCount;
        this.deviceIds = deviceIds;
        this.enrollTime = enrollTime;
        this.approveTime = approveTime;
        this.groupId = groupId;
        this.enrollmentSource = enrollmentSource;
        this.enrollmentState = enrollmentState;
        this.programType = programType;
        this.rejectionReason = rejectionReason;
        this.rejectionNotes = rejectionNotes;
    }
  
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PostalAddress getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(PostalAddress serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String getEnrollTime() {
        return enrollTime;
    }

    public void setEnrollTime(String enrollTime) {
        this.enrollTime = enrollTime;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public EnrollmentSource getEnrollmentSource() {
        return enrollmentSource;
    }

    public void setEnrollmentSource(EnrollmentSource enrollmentSource) {
        this.enrollmentSource = enrollmentSource;
    }

    public EnrollmentState getEnrollmentState() {
        return enrollmentState;
    }

    public void setEnrollmentState(EnrollmentState enrollmentState) {
        this.enrollmentState = enrollmentState;
    }

    public List<ProgramType> getProgramType() {
        return programType;
    }

    public void setProgramType(List<ProgramType> programType) {
        this.programType = programType;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionNotes() {
        return rejectionNotes;
    }

    public void setRejectionNotes(String rejectionNotes) {
        this.rejectionNotes = rejectionNotes;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}
