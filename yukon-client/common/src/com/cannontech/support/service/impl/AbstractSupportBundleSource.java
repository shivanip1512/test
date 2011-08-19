package com.cannontech.support.service.impl;

import com.cannontech.support.service.SupportBundleSource;

public abstract class AbstractSupportBundleSource implements SupportBundleSource {

    private String sourceName = null;
    private boolean optional = false;

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

}
