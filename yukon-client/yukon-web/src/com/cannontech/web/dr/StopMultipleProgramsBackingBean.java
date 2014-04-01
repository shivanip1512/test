/**
 * 
 */
package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.FactoryUtils;
import org.apache.commons.collections4.list.LazyList;

public class StopMultipleProgramsBackingBean extends StopProgramBackingBeanBase {
    private Integer controlAreaId;
    private Integer scenarioId;
    private List<ProgramStopInfo> programStopInfo =
        LazyList.lazyList(new ArrayList<ProgramStopInfo>(), FactoryUtils.instantiateFactory(ProgramStopInfo.class));

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
