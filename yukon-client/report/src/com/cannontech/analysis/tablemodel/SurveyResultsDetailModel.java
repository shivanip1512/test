package com.cannontech.analysis.tablemodel;

import java.util.Date;

public class SurveyResultsDetailModel extends
        SurveyResultsModelBase<SurveyResultsDetailModel.ModelRow> {
    // inputs
    String accountNumber;
    String deviceSerialNumber;

    // member variables
    private static String title = "Survey Results Detail Report";

    public static class ModelRow {
        public String accountNumber;
        public String serialNumber;
        public String altTrackingNumber;
        public String reason;
        public Date scheduledDate;
        public Date startDate;
        public Date endDate;
        public String loadProgram;
        public String gear;
    }

    public void doLoadData() {
        // rows
        ModelRow row = new ModelRow();
        row.reason = "because";
        row.accountNumber = "" + surveyId;
        row.loadProgram = "load program here";
        row.gear = "gear here";
        data.add(row);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }
}
