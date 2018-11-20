package com.cannontech.dr.nest.model.simulator;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public enum NestFileType {

    EXISTING(CsvSchema.builder()
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
    
    private NestFileType(CsvSchema schema) {
        this.schema = schema;
    }
    
    private CsvSchema schema;
    
    public CsvSchema getSchema() {
        return schema;
    }
}
