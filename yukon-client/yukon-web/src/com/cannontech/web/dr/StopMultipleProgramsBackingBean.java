/**
 * 
 */
package com.cannontech.web.dr;

import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class StopMultipleProgramsBackingBean extends StopProgramBackingBeanBase {
    private Integer controlAreaId;
    private Integer scenarioId;
    private List<ProgramStopInfo> programStopInfo =
        LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(ProgramStopInfo.class));

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

    public List<ProgramStopInfo> getProgramStopInfo() {
        return programStopInfo;
    }

    public void setProgramStopInfo(List<ProgramStopInfo> programStopInfo) {
        this.programStopInfo = programStopInfo;
    }
}