package com.cannontech.common.pao.service.providers.fields;

import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.util.CtiUtilities;

public class YukonPaObjectFields implements PaoTemplatePart {
    private String name;
    private String description = CtiUtilities.STRING_DASH_LINE;
    private boolean disabled = false;
    private String statistics = CtiUtilities.STRING_DASH_LINE;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    public String getStatistics() {
        return statistics;
    }
    
    public void setStatistics(String statistics) {
        this.statistics = statistics;
    }
    
}
