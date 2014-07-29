package com.cannontech.dr.scenario.model;

import com.cannontech.user.YukonUserContext;

public class ScenarioShowActionField extends ScenarioBackingFieldBase {

    @Override
    public String getFieldName() {
        return "SHOW_ACTION";
    }
    
    @Override
    public Object getScenarioValue(Scenario scenario, YukonUserContext userContext) {
        
        if (scenario == null) {
            return "unknown";
        }

        // Check manual active
        boolean hasPrograms = scenario.isHasPrograms();

        if (!hasPrograms) {
            return "hasNoPrograms";
        } else {
            return "enabled";
        }
    }
    
    @Override
    protected boolean handlesNull() {
        return true;
    }
}
