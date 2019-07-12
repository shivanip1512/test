package com.cannontech.common.dr.setup;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.google.common.collect.Lists;

public class LMServiceHelper {

    @Autowired private LMGearDao lmGearDao;
    @Autowired private LoadProgramSetupService loadProgramService;
    @Autowired private ProgramConstraintService programConstraintService;

    public List<LMDto> getGearsforModel(Integer programId, List<LMDto> gears) {
        List<LiteGear> allGears = Lists.newArrayList();
        allGears.addAll(lmGearDao.getAllLiteGears(programId));
        
        Integer gearId = (gears != null && gears.size() == 1) ? gears.get(0).getId() : null;
        if(gearId != null) {
            return allGears.stream()
                           .filter(liteGear -> liteGear.getGearID() == gearId.intValue())
                           .map(liteGear -> buildGear(liteGear))
                           .collect(Collectors.toList());
        }
        else {
            return allGears.stream()
                           .map(liteGear -> buildGear(liteGear))
                           .collect(Collectors.toList());
        }
    }

    private LMDto buildGear(LiteGear liteGear) {
        return new LMDto(liteGear.getGearID(), liteGear.getGearName());
    }
    
    public void validateProgramsAndGear(ControlScenario controlScenario) {
        List<ProgramDetails> validPrograms = loadProgramService.getAvailablePrograms();
        controlScenario.getAllPrograms().stream().forEach(program -> {
            ProgramDetails programDetails = validPrograms.stream()
                                                         .filter(prg -> prg.getProgramId().compareTo(program.getProgramId()) == 0)
                                                         .findFirst()
                                                         .orElseThrow(() -> new NotFoundException("Program Id not found"));

            programDetails.getGears().stream()
                                     .filter(gr -> gr.getId().compareTo(program.getGears().get(0).getId()) == 0)
                                     .findFirst()
                                     .orElseThrow(() -> new NotFoundException("Gear Id not found"));
        });
    }

    /**
     * Returns <code>Optional LMDto</code> object for Season Schedule for specified seasonScheduleId
     */
    public Optional<LMDto> getSeasonSchedule(Integer seasonScheduleId) {
        return programConstraintService.getSeasonSchedules().stream()
                                                            .filter(lmdto -> lmdto.getId().compareTo(seasonScheduleId) == 0)
                                                            .findFirst();
    }

    /**
     * Returns <code>Optional LMDto</code> object for Holiday Schedule for specified holidayScheduleId
     */
    public Optional<LMDto> getHolidaySchedule(Integer holidayScheduleId) {
        return programConstraintService.getHolidaySchedules().stream()
                                                             .filter(lmdto -> lmdto.getId().compareTo(holidayScheduleId) == 0)
                                                             .findFirst();
    }
}
