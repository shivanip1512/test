package com.cannontech.common.dr.setup;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.LMGearDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.constraint.service.ProgramConstraintService;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class LMServiceHelper {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private LMGearDao lmGearDao;
    @Autowired private LoadProgramSetupService loadProgramService;
    @Autowired private ProgramConstraintService programConstraintService;

    public List<LMDto> getGearsforModel(Integer programId, List<LMDto> gears) {
        List<LiteGear> allGears = Lists.newArrayList();
        allGears.addAll(lmGearDao.getAllLiteGears(programId));
        
        Integer gearNumber = (gears != null && gears.size() == 1) ? gears.get(0).getId() : null;
        if(gearNumber != null) {
            return allGears.stream()
                           .filter(liteGear -> liteGear.getGearNumber() == gearNumber.intValue())
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
        return new LMDto(liteGear.getGearNumber(), liteGear.getGearName());
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

    /**
     * Return a list of abbreviated PAO name corresponding to paoId
     */
    public String getAbbreviatedPaoNames(List<Integer> paoIds) {
        Map<Integer, LiteYukonPAObject> paoMaps = dbCache.getAllPaosMap();
        List<String> paoNameList = paoIds.stream()
                                         .filter(id -> paoMaps.containsKey(id))
                                         .map(id -> paoMaps.get(id).getPaoName())
                                         .collect(Collectors.toList());
        return StringUtils.abbreviate(String.join(", ", paoNameList), 2000);
    }
}
