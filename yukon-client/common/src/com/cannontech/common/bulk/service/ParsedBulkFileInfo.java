package com.cannontech.common.bulk.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;

public abstract class ParsedBulkFileInfo {

    // INPUT INFO
    private BulkFileInfo bulkFileInfo;

    // member data
    private List<MessageSourceResolvable> errorResolvers = new ArrayList<MessageSourceResolvable>();
    private List<BulkFieldColumnHeader> updateBulkFieldColumnHeaders = new ArrayList<BulkFieldColumnHeader>();
    int lineCount = 0;


    // PUBLIC GETTERS
    //----------------------------------------------------------------------------------------------

    // ERRORS
    public List<MessageSourceResolvable> getErrorResolvers() {
        return errorResolvers;
    }
    
    // UPDATE FIELDS
    public List<BulkFieldColumnHeader> getUpdateBulkFieldColumnHeaders() {
        return updateBulkFieldColumnHeaders;
    }
    
    public void addError(MessageSourceResolvable messageSourceResolvable) {
        errorResolvers.add(messageSourceResolvable);
    }
    
    public boolean hasErrors() {
        return errorResolvers.size() > 0;
    }

    // UPDATE FIELDS
    public void addUpdateBulkFieldColumnHeaders(Collection<BulkFieldColumnHeader> headers) {
        updateBulkFieldColumnHeaders.addAll(headers);
    }

    public ParsedBulkFileInfo(BulkFileInfo bulkFileInfo) {
        this.bulkFileInfo = bulkFileInfo;
    }

    public BulkFileInfo getBulkFileInfo() {
        return bulkFileInfo;
    }

    public void setBulkFileInfo(BulkFileInfo bulkFileInfo) {
        this.bulkFileInfo = bulkFileInfo;
    }

    public int getLineCount() {
        return lineCount;
    }
    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

}
