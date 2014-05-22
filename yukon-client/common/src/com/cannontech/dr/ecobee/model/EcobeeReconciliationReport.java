package com.cannontech.dr.ecobee.model;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This report describes any discrepancies between Yukon's structure of ecobee groups and thermostats, and the ecobee
 * portal's structure of management sets and thermostats.
 * @see EcobeeDiscrepancy and its subclasses
 */
public class EcobeeReconciliationReport {
    private final Integer reportId;
    private final Instant reportDate;
    private final ImmutableMap<EcobeeDiscrepancyType, EcobeeDiscrepancy> errors;

    public EcobeeReconciliationReport(Integer reportId, Instant reportDate, Iterable<EcobeeDiscrepancy> errors) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        
        Builder<EcobeeDiscrepancyType, EcobeeDiscrepancy> builder = ImmutableMap.builder();
        for (EcobeeDiscrepancy error : errors) {
            builder.put(error.getErrorType(), error);
        }
        this.errors = builder.build();
    }
    
    public EcobeeReconciliationReport(Iterable<EcobeeDiscrepancy> errors) {
        this(null, null, errors);
    }
    
    public int getReportId() {
        return reportId;
    }

    public ImmutableMap<EcobeeDiscrepancyType, EcobeeDiscrepancy> getErrors() {
        return errors;
    }

    public Instant getReportDate() {
        return reportDate;
    }
}
