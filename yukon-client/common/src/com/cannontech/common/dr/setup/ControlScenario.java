package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

public class ControlScenario {

    @JsonIgnoreProperties(value={"id"}, allowGetters= true)
    private Integer id;
    private String name;
    @Valid private List<ProgramDetails> allPrograms;

    public ControlScenario() {
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

    public List<ProgramDetails> getAllPrograms() {
        return allPrograms;
    }

    public void setAllPrograms(List<ProgramDetails> allPrograms) {
        this.allPrograms = allPrograms;
    }

    public void buildModel(LMScenario controlScenario) {
        setId(controlScenario.getPAObjectID());
        setName(controlScenario.getPAOName());
        List<ProgramDetails> allPrograms = Lists.newArrayList();
        controlScenario.getAllThePrograms().forEach(program -> {
            ProgramDetails programDetails = new ProgramDetails();
            programDetails.setProgramId(program.getProgramID());
            programDetails.setStartOffsetInMinutes(program.getStartOffset() / 60);
            programDetails.setStopOffsetInMinutes(program.getStopOffset() / 60);
            LMDto gear = new LMDto();
            List<LMDto> gears = new ArrayList<>(1);
            gear.setId(program.getStartGear());
            gears.add(gear);
            programDetails.setGears(gears);
            allPrograms.add(programDetails);
        });
        setAllPrograms(allPrograms.stream()
                                  .sorted((p1, p2) -> p1.getStartOffsetInMinutes().compareTo(p2.getStartOffsetInMinutes()))
                                  .collect(Collectors.toList()));
    }

    public void buildDBPersistent(LMScenario controlScenario) {
        // Setting Control Scenario details
        controlScenario.setScenarioID(getId());
        controlScenario.setScenarioName(getName());
        controlScenario.getAllThePrograms().removeAllElements();

        // Setting Programs list
        if (CollectionUtils.isNotEmpty(getAllPrograms())) {
            getAllPrograms().forEach(program -> {
                LMControlScenarioProgram lmControlScenarioProgram = new LMControlScenarioProgram();
                lmControlScenarioProgram.setScenarioID(getId());
                lmControlScenarioProgram.setProgramID(program.getProgramId());
                lmControlScenarioProgram.setStartOffset(program.getStartOffsetInMinutes() * 60);
                lmControlScenarioProgram.setStopOffset(program.getStopOffsetInMinutes() * 60);
                lmControlScenarioProgram.setStartGear(program.getGears().get(0).getId());
                controlScenario.getAllThePrograms().add(lmControlScenarioProgram);
            });
        }
    }
}
