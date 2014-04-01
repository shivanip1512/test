/**
 * 
 */
package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.list.LazyList;

public class StartMultipleProgramsBackingBean extends StartProgramBackingBeanBase {
    private Integer controlAreaId;
    private Integer scenarioId;
    private List<ProgramStartInfo> programStartInfo =
        LazyList.lazyList(new ArrayList<ProgramStartInfo>(), FactoryUtils.instantiateFactory(ProgramStartInfo.class));

    public Integer getControlAreaId() {
        return controlAreaId;
    }

    public void setControlAreaId(Integer controlAreaId) {
        this.controlAreaId = controlAreaId;
    }

    public Integer getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Integer scenarioId) {
        this.scenarioId = scenarioId;
    }

    public List<ProgramStartInfo> getProgramStartInfo() {
        return programStartInfo;
    }

    public void setProgramStartInfo(List<ProgramStartInfo> programStartInfo) {
        this.programStartInfo = programStartInfo;
    }
}
