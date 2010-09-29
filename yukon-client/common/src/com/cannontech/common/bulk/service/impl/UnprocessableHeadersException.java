package com.cannontech.common.bulk.service.impl;

import java.util.Set;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;

public class UnprocessableHeadersException extends RuntimeException {

    private Set<BulkFieldColumnHeader> badHeaders;
    
    public UnprocessableHeadersException(Set<BulkFieldColumnHeader> badHeaders) {
        this.badHeaders = badHeaders;
    }
    
    public Set<BulkFieldColumnHeader> getBadHeaders() {
        return badHeaders;
    }
}