package com.cannontech.dr.ecobee.model;

import java.util.Collection;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimap;

/**
 * This report describes any discrepancies between Yukon's structure of ecobee groups and thermostats, and the ecobee
 * portal's structure of management sets and thermostats.
 * @see EcobeeDiscrepancy and its subclasses
 */
public class EcobeeReconciliationReport {
    private final Integer reportId;
    private final Instant reportDate;
    private final Multimap<EcobeeDiscrepancyCategory, EcobeeDiscrepancy> errorsByCategory = ArrayListMultimap.create();
    private final Multimap<EcobeeDiscrepancyType, EcobeeDiscrepancy> errorsByType = ArrayListMultimap.create();
    private final Map<Integer, EcobeeDiscrepancy> errorsById;
    
    public EcobeeReconciliationReport(Integer reportId, Instant reportDate, Iterable<EcobeeDiscrepancy> errors) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        
        Builder<Integer, EcobeeDiscrepancy> errorsByIdBuilder = ImmutableMap.builder();
        for (EcobeeDiscrepancy error : errors) {
            errorsByCategory.put(error.getErrorType().getCategory(), error);
            errorsByType.put(error.getErrorType(), error);
            //only populate this map if the error has an id
            if (error.getErrorId() != null) {
                errorsByIdBuilder.put(error.getErrorId(), error);
            }
        }
        errorsById = errorsByIdBuilder.build();
    }
    
    public EcobeeReconciliationReport(Iterable<EcobeeDiscrepancy> errors) {
        this(null, null, errors);
    }
    
    public int getReportId() {
        return reportId;
    }
    
    public EcobeeDiscrepancy getError(int errorId) {
        return errorsById.get(errorId);
    }
    
    public Collection<EcobeeDiscrepancy> getErrors() {
        return errorsByCategory.values();
    }
    
    public Collection<EcobeeDiscrepancy> getErrors(EcobeeDiscrepancyCategory category) {
        return errorsByCategory.get(category);
    }
    
    public Collection<EcobeeDiscrepancy> getErrors(EcobeeDiscrepancyType type) {
        return errorsByType.get(type);
    }
    
    public int getErrorNumberByCategory(EcobeeDiscrepancyCategory category) {
        return errorsByCategory.get(category).size();
    }

    public Instant getReportDate() {
        return reportDate;
    }
}
