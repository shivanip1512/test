package com.cannontech.support.service.impl;

import com.cannontech.support.service.SupportBundleWriter;

public abstract class AbstractSupportBundleWriter implements SupportBundleWriter {
    private String name = null;
    private boolean optional = false;

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
}
