/**
 * 
 */
package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

@SuppressWarnings("unchecked")
public class StartMultipleProgramsBackingBean extends StartProgramBackingBeanBase {
    private Integer controlAreaId;
    private Integer scenarioId;
    private List<ProgramStartInfo> programStartInfo =
        LazyList.decorate(Lists.newArrayList(), FactoryUtils.instantiateFactory(ProgramStartInfo.class));

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

    public void initDefaults(YukonUserContext userContext,
            List<DisplayablePao> programs,
            Map<Integer, ScenarioProgram> scenarioPrograms) {
        super.initDefaults(userContext);
        List<ProgramStartInfo> programStartInfo = new ArrayList<ProgramStartInfo>(programs.size());
        for (DisplayablePao program : programs) {
            int programId = program.getPaoIdentifier().getPaoId();
            int gearNumber = 1;
            if (scenarioPrograms != null) {
                ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);
                if (scenarioProgram != null) {
                    gearNumber = scenarioProgram.getStartGear();
                }
            }
            programStartInfo.add(new ProgramStartInfo(programId, gearNumber, true));
        }
        setProgramStartInfo(programStartInfo);
    }
}