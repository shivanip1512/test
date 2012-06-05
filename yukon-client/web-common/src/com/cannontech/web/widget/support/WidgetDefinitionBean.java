package com.cannontech.web.widget.support;

import java.util.Set;

public interface WidgetDefinitionBean {

    public String getShortName();
    public Set<WidgetInput> getInputs();
    public boolean isHasIdentity();
    public Object getActionTarget();
}