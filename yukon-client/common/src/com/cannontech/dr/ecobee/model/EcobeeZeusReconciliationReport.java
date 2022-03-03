package com.cannontech.dr.ecobee.model;

import java.util.Collection;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Multimap;

/**
 * This report describes any discrepancies between Yukon's structure of ecobee groups and thermostats, and the ecobee
 * portal's structure of management sets and thermostats.
 * @see EcobeeZeusDiscrepancy and its subclasses
 */
public class EcobeeZeusReconciliationReport {
    private final Integer reportId;
    private final Instant reportDate;
    private final Multimap<EcobeeZeusDiscrepancyCategory, EcobeeZeusDiscrepancy> errorsByCategory = ArrayListMultimap.create();
    private final Multimap<EcobeeZeusDiscrepancyType, EcobeeZeusDiscrepancy> errorsByType = ArrayListMultimap.create();
    private final Map<Integer, EcobeeZeusDiscrepancy> errorsById;
    
    public EcobeeZeusReconciliationReport(Integer reportId, Instant reportDate, Iterable<EcobeeZeusDiscrepancy> errors) {
        this.reportId = reportId;
        this.reportDate = reportDate;
        
        Builder<Integer, EcobeeZeusDiscrepancy> errorsByIdBuilder = ImmutableMap.builder();
        for (EcobeeZeusDiscrepancy error : errors) {
            errorsByCategory.put(error.getErrorType().getCategory(), error);
            errorsByType.put(error.getErrorType(), error);
            //only populate this map if the error has an id
            if (error.getErrorId() != null) {
                errorsByIdBuilder.put(error.getErrorId(), error);
            }
        }
        errorsById = errorsByIdBuilder.build();
    }
    
    public EcobeeZeusReconciliationReport(Iterable<EcobeeZeusDiscrepancy> errors) {
        this(null, null, errors);
    }
    
    public int getReportId() {
        return reportId;
    }
    
    public EcobeeZeusDiscrepancy getError(int errorId) {
        return errorsById.get(errorId);
    }
    
    public Collection<EcobeeZeusDiscrepancy> getErrors() {
        return errorsByCategory.values();
    }
    
    public Collection<EcobeeZeusDiscrepancy> getErrors(EcobeeZeusDiscrepancyCategory category) {
        return errorsByCategory.get(category);
    }
    
    public Collection<EcobeeZeusDiscrepancy> getErrors(EcobeeZeusDiscrepancyType type) {
        return errorsByType.get(type);
    }
    
    public int getErrorNumberByCategory(EcobeeZeusDiscrepancyCategory category) {
        return errorsByCategory.get(category).size();
    }

    public Instant getReportDate() {
        return reportDate;
    }
}
