package com.cannontech.dr.loadgroup.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.ControlScenarioBase;
import com.cannontech.common.dr.setup.ControlScenarioProgram;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.loadgroup.service.ControlScenarioService;
import com.google.common.collect.Lists;

public class DrSetupHelper {

    @Autowired private LMGearDao lmGearDao;
    @Autowired private ControlScenarioService controlScenarioService;

    public List<LMDto> getGearsforModel(Integer programId, boolean isViewMode) {
        List<LiteGear> allGears = Lists.newArrayList();
        allGears.addAll(lmGearDao.getAllLiteGears());
        List<LMDto> gears = Lists.newArrayList();
        allGears.stream().forEach(liteGear -> {
            if (!isViewMode && liteGear.getOwnerID() == programId) {
                LMDto gear = new LMDto();
                gear.setId(liteGear.getGearID());
                gear.setName(liteGear.getGearName());
                gears.add(gear);
            } else if (isViewMode && liteGear.getOwnerID() == programId && liteGear.getGearNumber() == 1) {
                LMDto gear = new LMDto();
                gear.setId(liteGear.getGearID());
                gear.setName(liteGear.getGearName());
                gears.add(gear);
            }
        });
        return gears;
    }

    public void validateProgramsAndGear(ControlScenarioBase controlScenarioBase) {
        List<ControlScenarioProgram> validPrograms = controlScenarioService.getAvailablePrograms();
        controlScenarioBase.getAllPrograms().stream().forEach(program -> {
            Optional<ControlScenarioProgram> controlScenarioProgram = validPrograms.stream().filter(
                prg -> prg.getProgramId().compareTo(program.getProgramId()) == 0).findFirst();
            if (controlScenarioProgram.isEmpty()) {
                throw new NotFoundException("Program ID : " + program.getProgramId() + " invalid");
            } else {
                List<LMDto> validGears = controlScenarioProgram.get().getGears();
                Optional<LMDto> gear = validGears.stream().filter(
                    gr -> gr.getId().compareTo(program.getGears().get(0).getId()) == 0).findFirst();
                if (gear.isEmpty()) {
                    throw new NotFoundException("Gear ID : " + program.getGears().get(0).getId() + " invalid");
                }
            }
        });
    }
}
