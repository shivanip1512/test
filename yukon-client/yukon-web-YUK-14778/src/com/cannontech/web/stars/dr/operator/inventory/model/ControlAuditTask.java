package com.cannontech.web.stars.dr.operator.inventory.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Failable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

public class ControlAuditTask extends CollectionBasedInventoryTask implements Failable {
    
    private AuditSettings settings;
    
    private List<AuditRow> controlled = new ArrayList<>();
    private List<AuditRow> uncontrolled = new ArrayList<>();
    private List<AuditRow> unknown = new ArrayList<>();
    private List<AuditRow> unsupported = new ArrayList<>();
    
    private Throwable error;
    
    public ControlAuditTask(AuditSettings settings, InventoryCollection collection, YukonUserContext userContext) {
        this.settings = settings;
        this.collection = collection;
        this.userContext = userContext;
    }
    
    public AuditSettings getSettings() {
        return settings;
    }
    
    public void setSettings(AuditSettings settings) {
        this.settings = settings;
    }
    
    public List<AuditRow> getControlled() {
        return controlled;
    }
    
    public void setControlled(List<AuditRow> controlled) {
        this.controlled = controlled;
    }
    
    public List<AuditRow> getUncontrolled() {
        return uncontrolled;
    }
    
    public void setUncontrolled(List<AuditRow> uncontrolled) {
        this.uncontrolled = uncontrolled;
    }
    
    public List<AuditRow> getUnknown() {
        return unknown;
    }
    
    public void setUnknownRows(List<AuditRow> unknown) {
        this.unknown = unknown;
    }
    
    public List<AuditRow> getUnsupported() {
        return unsupported;
    }
    
    public void setUnsupported(List<AuditRow> unsupported) {
        this.unsupported = unsupported;
    }
    
    @Override
    public Throwable getError() {
        return error;
    }
    
    public void setError(Throwable error) {
        this.error = error;
    }
    
    public SearchResults<AuditRow> getControlledPaged() {
        return SearchResults.pageBasedForWholeList(1, 10, controlled);
    }
    
    public SearchResults<AuditRow> getUncontrolledPaged() {
        return SearchResults.pageBasedForWholeList(1, 10, uncontrolled);
    }
    
    public SearchResults<AuditRow> getUnknownPaged() {
        return SearchResults.pageBasedForWholeList(1, 10, unknown);
    }
    
    public SearchResults<AuditRow> getUnsupportedPaged() {
        return SearchResults.pageBasedForWholeList(1, 10, unsupported);
    }
    
    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(key + "controlAudit.label");
    }
    
    @Override
    public void errorOccurred(Throwable t) {
        error = t;
    }
    
    @Override
    public boolean isErrorOccurred() {
        return error != null;
    }
}