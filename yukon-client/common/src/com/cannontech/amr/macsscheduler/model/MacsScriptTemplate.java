package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.Set;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.google.common.collect.ImmutableSet;

public enum MacsScriptTemplate {
    NO_TEMPLATE(ScriptTemplateTypes.NO_TEMPLATE_SCRIPT, ScriptTemplateTypes.NO_TEMPLATE_SCRIPT_STRING),
    METER_READ(ScriptTemplateTypes.METER_READ_SCRIPT, ScriptTemplateTypes.METER_READ_SCRIPT_STRING),
    METER_READ_RETRY(ScriptTemplateTypes.METER_READ_RETRY_SCRIPT, ScriptTemplateTypes.METER_READ_RETRY_SCRIPT_STRING),
    IED_360_370_METER_READ(ScriptTemplateTypes.IED_360_370_METER_READ_SCRIPT, ScriptTemplateTypes.IED_360_370_METER_READ_SCRIPT_STRING),
    IED_360_370_METER_READ_RETRY(ScriptTemplateTypes.IED_360_370_METER_READ_RETRY_SCRIPT, ScriptTemplateTypes.IED_360_370_METER_READ_RETRY_SCRIPT_STRING),
    IED_400_METER_READ(ScriptTemplateTypes.IED_400_METER_READ_SCRIPT, ScriptTemplateTypes.IED_400_METER_READ_SCRIPT_STRING),
    IED_400_METER_READ_RETRY(ScriptTemplateTypes.IED_400_METER_READ_RETRY_SCRIPT, ScriptTemplateTypes.IED_400_METER_READ_RETRY_SCRIPT_STRING),
    OUTAGE_METER_READ(ScriptTemplateTypes.OUTAGE_METER_READ_SCRIPT, ScriptTemplateTypes.OUTAGE_METER_READ_STRING),
    VOLTAGE_METER_READ(ScriptTemplateTypes.VOLTAGE_METER_READ_SCRIPT, ScriptTemplateTypes.VOLTAGE_METER_READ_STRING);
    
    private static Set<MacsScriptTemplate> metering =
        ImmutableSet.of(METER_READ, METER_READ_RETRY, IED_360_370_METER_READ, IED_360_370_METER_READ_RETRY,
            OUTAGE_METER_READ, VOLTAGE_METER_READ, IED_400_METER_READ, IED_400_METER_READ);
    private static Set<MacsScriptTemplate> retry =
        ImmutableSet.of(METER_READ_RETRY, IED_360_370_METER_READ_RETRY, IED_400_METER_READ_RETRY);
    private static Set<MacsScriptTemplate> ied300 = ImmutableSet.of(IED_360_370_METER_READ_RETRY, IED_360_370_METER_READ);
    private static Set<MacsScriptTemplate> ied400 = ImmutableSet.of(IED_400_METER_READ_RETRY, IED_400_METER_READ);
    
    private int id;
    private String description;
    
    MacsScriptTemplate(int id, String description){
        this.id = id;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    } 
    
    public static MacsScriptTemplate getTemplate(int id) {
        return Arrays.stream(MacsScriptTemplate.values())
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Could not find template=" + id));
    }
    
    public boolean isRetry() {
        return retry.contains(this);
    }

    public boolean hasBilling() {
        return isMetering();
    }

    public boolean hasNotification() {
        return isMetering();
    }

    public boolean isMetering() {
        return metering.contains(this);
    }

    public boolean isIed() {
        return isIed300() || isIed400();
    }

    public boolean isIed300() {
        return ied300.contains(this);
    }

    public boolean isIed400() {
        return ied400.contains(this);
    }
    
    public boolean isNoTemplateSelected(){
        return this == NO_TEMPLATE;
    }
    
    public static Set<MacsScriptTemplate> getRetryTypes() {
        return retry;
    }
    
    public static Set<MacsScriptTemplate> getIed300Types() {
        return ied300;
    }
    
    public static Set<MacsScriptTemplate> getIed400Types() {
        return ied400;
    }
}
