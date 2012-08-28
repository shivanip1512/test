package com.cannontech.web.stars.dr.operator.inventory.model;

import java.util.List;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Completable;
import com.google.common.collect.Lists;

public class ControlAuditResult implements Completable {
    
    private String auditId;
    
    private AuditSettings settings;
    
    private InventoryCollection controlled;
    private InventoryCollection uncontrolled;
    private InventoryCollection unknown;
    private InventoryCollection unsupported;
    
    private List<AuditRow> controlledRows = Lists.newArrayList();
    private List<AuditRow> uncontrolledRows = Lists.newArrayList();
    private List<AuditRow> unknownRows = Lists.newArrayList();
    private List<AuditRow> unsupportedRows = Lists.newArrayList();
    
    public String getAuditId() {
        return auditId;
    }
    
    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public AuditSettings getSettings() {
        return settings;
    }

    public void setSettings(AuditSettings settings) {
        this.settings = settings;
    }

    public InventoryCollection getControlled() {
        return controlled;
    }

    public void setControlled(InventoryCollection controlled) {
        this.controlled = controlled;
    }

    public InventoryCollection getUncontrolled() {
        return uncontrolled;
    }

    public void setUncontrolled(InventoryCollection uncontrolled) {
        this.uncontrolled = uncontrolled;
    }

    public InventoryCollection getUnknown() {
        return unknown;
    }

    public void setUnknown(InventoryCollection unknown) {
        this.unknown = unknown;
    }

    public InventoryCollection getUnsupported() {
        return unsupported;
    }

    public void setUnsupported(InventoryCollection unsupported) {
        this.unsupported = unsupported;
    }

    public List<AuditRow> getControlledRows() {
        return controlledRows;
    }

    public void setControlledRows(List<AuditRow> controlledRows) {
        this.controlledRows = controlledRows;
    }

    public List<AuditRow> getUncontrolledRows() {
        return uncontrolledRows;
    }

    public void setUncontrolledRows(List<AuditRow> uncontrolledRows) {
        this.uncontrolledRows = uncontrolledRows;
    }

    public List<AuditRow> getUnknownRows() {
        return unknownRows;
    }

    public void setUnknownRows(List<AuditRow> unknownRows) {
        this.unknownRows = unknownRows;
    }

    public List<AuditRow> getUnsupportedRows() {
        return unsupportedRows;
    }

    public void setUnsupportedRows(List<AuditRow> unsupportedRows) {
        this.unsupportedRows = unsupportedRows;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
    
    public SearchResult<AuditRow> getControlledPaged() {
        return SearchResult.pageBasedForWholeList(1, 10, controlledRows);
    }
    
    public SearchResult<AuditRow> getUncontrolledPaged() {
        return SearchResult.pageBasedForWholeList(1, 10, uncontrolledRows);
    }
    
    public SearchResult<AuditRow> getUnknownPaged() {
        return SearchResult.pageBasedForWholeList(1, 10, unknownRows);
    }
    
    public SearchResult<AuditRow> getUnsupportedPaged() {
        return SearchResult.pageBasedForWholeList(1, 10, unsupportedRows);
    }
    
}