package com.cannontech.dr.nest.model;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public enum NestFileType {

    PENDING(NestURLTypes.PENDING.getUrl(), "latest.csv", CsvSchema.builder()
        .addColumn("REF")
        .addColumn("YEAR")
        .addColumn("MONTH")
        .addColumn("DAY")
        .addColumn("NAME")
        .addColumn("EMAIL")
        .addColumn("SERVICE_ADDRESS")
        .addColumn("SERVICE_CITY")
        .addColumn("SERVICE_STATE")
        .addColumn("SERVICE_POSTAL_CODE")
        .addColumn("ACCOUNT_NUMBER")
        .addColumn("CONTRACT_APPROVED")
        .addColumn("PROGRAMS")
        .addColumn("NEST_SERIALS")
        .addColumn("APPROVED")
        .addColumn("ASSIGN_GROUP")
        .addColumn("GROUP")
        .addColumn("REJECTION_REASON")
        .addColumn("REJECTION_NOTES")
        .build()),
    EXISTING(NestURLTypes.EXISTING.getUrl(), "latest.csv", CsvSchema.builder()
        .addColumn("REF")
        .addColumn("YEAR")
        .addColumn("MONTH")
        .addColumn("DAY")
        .addColumn("NAME")
        .addColumn("EMAIL")
        .addColumn("SERVICE_ADDRESS")
        .addColumn("SERVICE_CITY")
        .addColumn("SERVICE_STATE")
        .addColumn("SERVICE_POSTAL_CODE")
        .addColumn("ACCOUNT_NUMBER")
        .addColumn("CONTRACT_APPROVED")
        .addColumn("PROGRAMS")
        .addColumn("WINTER_RHR")
        .addColumn("SUMMER_RHR")
        .addColumn("ASSIGN_GROUP")
        .addColumn("GROUP")
        .addColumn("DISSOLVE")
        .addColumn("DISSOLVE_REASON")
        .addColumn("DISSOLVE_NOTES")
        .build());
    
    private NestFileType(String url, String file, CsvSchema schema) {
        this.url = url;
        this.file = file;
        this.schema = schema;
    }
    
    private String url;
    private String file;
    private CsvSchema schema;
    
    public String getUrl() {
        return url;
    }
    
    public String getFile() {
        return file;
    }

    public CsvSchema getSchema() {
        return schema;
    }
}
