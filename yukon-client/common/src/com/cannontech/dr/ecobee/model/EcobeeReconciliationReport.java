package com.cannontech.dr.ecobee.model;

import java.util.Collection;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * This report describes any discrepancies between Yukon's structure of ecobee groups and thermostats, and the ecobee
 * portal's structure of management sets and thermostats.
 * @see EcobeeDiscrepancy and its subclasses
 */
public class EcobeeReconciliationReport {
    private final Integer reportId;
    private final Instant reportDate;
    private final Multimap<EcobeeDiscrepancyCategory, EcobeeDiscrepancy> errors = ArrayListMultimap.create();

    public EcobeeReconciliationReport(Integer reportId, Instant reportDate, Iterable<EcobeeDiscrepancy> errors) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        
        for (EcobeeDiscrepancy error : errors) {
            this.errors.put(error.getErrorType().getCategory(), error);
        }
    }
    
    public EcobeeReconciliationReport(Iterable<EcobeeDiscrepancy> errors) {
        this(null, null, errors);
    }
    
    public int getReportId() {
        return reportId;
    }

    public Collection<EcobeeDiscrepancy> getErrors() {
        return errors.values();
    }
    
    public int getErrorNumberByCategory(EcobeeDiscrepancyCategory category) {
        return errors.get(category).size();
    }

    public Instant getReportDate() {
        return reportDate;
    }
}
