package com.cannontech.web.stars.dr.operator.model;

import com.cannontech.web.bulk.util.BulkFileUpload;

public class AccountImportData {
    private String email;
    private BulkFileUpload accountFile;
    private BulkFileUpload hardwareFile;
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public BulkFileUpload getAccountFile() {
        return accountFile;
    }

    public void setAccountFile(BulkFileUpload accountFile) {
        this.accountFile = accountFile;
    }

    public BulkFileUpload getHardwareFile() {
        return hardwareFile;
    }

    public void setHardwareFile(BulkFileUpload hardwareFile) {
        this.hardwareFile = hardwareFile;
    }
}