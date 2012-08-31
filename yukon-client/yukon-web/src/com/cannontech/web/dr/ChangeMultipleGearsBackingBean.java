/**
 * 
 */
package com.cannontech.web.dr;

import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class ChangeMultipleGearsBackingBean {
    private Integer controlAreaId;
    private Integer scenarioId;
    private List<ProgramGearChangeInfo> programGearChangeInfo =
        LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(ProgramGearChangeInfo.class));

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

    public List<ProgramGearChangeInfo> getProgramGearChangeInfo() {
        return programGearChangeInfo;
    }

    public void setProgramGearChangeInfo(List<ProgramGearChangeInfo> programGearChangeInfo) {
        this.programGearChangeInfo = programGearChangeInfo;
    }

}