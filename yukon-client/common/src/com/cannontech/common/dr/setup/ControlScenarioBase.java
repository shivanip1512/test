package com.cannontech.common.dr.setup;

import java.util.List;
import java.util.Vector;

import javax.validation.Valid;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.google.common.collect.Lists;

public class ControlScenarioBase {

    private Integer id;
    private String name;
    @Valid private List<ControlScenarioProgram> allPrograms;

    public ControlScenarioBase() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ControlScenarioProgram> getAllPrograms() {
        return allPrograms;
    }

    public void setAllPrograms(List<ControlScenarioProgram> allPrograms) {
        this.allPrograms = allPrograms;
    }

    public void buildModel(LMScenario controlScenario) {
        setId(controlScenario.getPAObjectID());
        setName(controlScenario.getPAOName());
        List<ControlScenarioProgram> allPrograms = Lists.newArrayList();
        controlScenario.getAllThePrograms().forEach(program -> {
            ControlScenarioProgram controlScenarioProgram = new ControlScenarioProgram();
            controlScenarioProgram.setProgramId(program.getProgramID());
            controlScenarioProgram.setStartOffset(program.getStartOffset() / 60);
            controlScenarioProgram.setStopOffset(program.getStopOffset() / 60);
            allPrograms.add(controlScenarioProgram);
        });
        setAllPrograms(allPrograms);
    }

    public void buildDBPersistent(LMScenario controlScenario) {
        // Setting Control Scenario details
        controlScenario.setScenarioID(getId());
        controlScenario.setScenarioName(getName());
        Vector<LMControlScenarioProgram> progList = new Vector<>();

        // Setting Programs list
        getAllPrograms().forEach(program -> {
            LMControlScenarioProgram lmControlScenarioProgram = new LMControlScenarioProgram();
            lmControlScenarioProgram.setScenarioID(getId());
            lmControlScenarioProgram.setProgramID(program.getProgramId());
            lmControlScenarioProgram.setStartOffset(program.getStartOffset() * 60);
            lmControlScenarioProgram.setStopOffset(program.getStopOffset() * 60);
            lmControlScenarioProgram.setStartGear(program.getGears().get(0).getId());

            progList.add(lmControlScenarioProgram);
        });

        controlScenario.setAllThePrograms(progList);

    }
}
