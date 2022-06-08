package com.cannontech.development.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cannontech.common.pao.PaoType;
import com.cannontech.user.YukonUserContext;

public class DemandResponseSetup implements Serializable {
    private static final long serialVersionUID = 1L;
    private int programId;
    private String templateName = "Test";
    private int scenarios = 1;
    private int controlAreas = 1;
    private int programs = 3;
    private int devices = 7;
    private List<PaoType> types;
    private String token;
    private boolean clean;
    private YukonUserContext userContext;
    
    public int getProgramId() {
        return programId;
    }
    public void setProgramId(int programId) {
        this.programId = programId;
    }
    public String getTemplateName() {
        return templateName;
    }
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    public int getScenarios() {
        return scenarios;
    }
    public void setScenarios(int scenarios) {
        this.scenarios = scenarios;
    }
    public int getControlAreas() {
        return controlAreas;
    }
    public void setControlAreas(int controlAreas) {
        this.controlAreas = controlAreas;
    }
    public int getPrograms() {
        return programs;
    }
    public void setPrograms(int programs) {
        this.programs = programs;
    }
    public int getDevices() {
        return devices;
    }
    public void setDevices(int devices) {
        this.devices = devices;
    }
    public List<PaoType> getTypes() {
        return types;
    }
    public void setTypes(List<PaoType> types) {
        this.types = types;
    }
    
    public boolean isClean() {
        return clean;
    }
    public void setClean(boolean clean) {
        this.clean = clean;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public YukonUserContext getUserContext() {
        return userContext;
    }
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}