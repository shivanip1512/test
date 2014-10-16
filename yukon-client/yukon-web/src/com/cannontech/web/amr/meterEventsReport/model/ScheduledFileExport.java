package com.cannontech.web.amr.meterEventsReport.model;


public class ScheduledFileExport {

    private String scheduleName;
    private String exportFileName;
    private boolean appendDateToFileName;
    private String timestampPatternField;
    private boolean overrideFileExtension;
    private String exportFileExtension;
    private boolean includeExportCopy;
    private String exportPath;
    private String notificationEmailAddresses;
    private boolean sendEmail;

    public String getScheduleName() {
        return scheduleName;
    }
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }
    public String getExportFileName() {
        return exportFileName;
    }
    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }
    public boolean isAppendDateToFileName() {
        return appendDateToFileName;
    }
    public void setAppendDateToFileName(boolean appendDateToFileName) {
        this.appendDateToFileName = appendDateToFileName;
    }
    public String getTimestampPatternField() {
        return timestampPatternField;
    }
    public void setTimestampPatternField(String timestampPatternField) {
        this.timestampPatternField = timestampPatternField;
    }
    public boolean isOverrideFileExtension() {
        return overrideFileExtension;
    }
    public void setOverrideFileExtension(boolean overrideFileExtension) {
        this.overrideFileExtension = overrideFileExtension;
    }
    public String getExportFileExtension() {
        return exportFileExtension;
    }
    public void setExportFileExtension(String exportFileExtension) {
        this.exportFileExtension = exportFileExtension;
    }
    public boolean isIncludeExportCopy() {
        return includeExportCopy;
    }
    public void setIncludeExportCopy(boolean includeExportCopy) {
        this.includeExportCopy = includeExportCopy;
    }
    public String getExportPath() {
        return exportPath;
    }
    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }
    public String getNotificationEmailAddresses() {
        return notificationEmailAddresses;
    }
    public void setNotificationEmailAddresses(String notificationEmailAddresses) {
        this.notificationEmailAddresses = notificationEmailAddresses;
    }
    
    public boolean isSendEmail() {
        return sendEmail;
    }
    
    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}
