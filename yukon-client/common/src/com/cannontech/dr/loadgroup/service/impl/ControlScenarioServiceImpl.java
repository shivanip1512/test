package com.cannontech.dr.loadgroup.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.common.dr.setup.ControlScenarioBase;
import com.cannontech.common.dr.setup.ControlScenarioProgram;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.dr.loadgroup.service.ControlScenarioService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class ControlScenarioServiceImpl implements ControlScenarioService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DrSetupHelper drSetupHelper;
    @Autowired private IDatabaseCache dbCache;

    @Override
    public int create(ControlScenarioBase controlScenarioBase) {
        if (controlScenarioBase.getAllPrograms() != null) {
            drSetupHelper.validateProgramsAndGear(controlScenarioBase);
        }
        LMScenario lmScenario = getDBPersistent(controlScenarioBase);
        controlScenarioBase.buildDBPersistent(lmScenario);
        dbPersistentDao.performDBChange(lmScenario, TransactionType.INSERT);
        return lmScenario.getPAObjectID();
    }

    @Override
    public int update(int controlScenarioId, ControlScenarioBase controlScenarioBase) {
        if (controlScenarioBase.getAllPrograms() != null) {
            drSetupHelper.validateProgramsAndGear(controlScenarioBase);
        }
        controlScenarioBase.setId(controlScenarioId);
        LMScenario lmScenario = getDBPersistent(controlScenarioBase);
        controlScenarioBase.buildDBPersistent(lmScenario);
        dbPersistentDao.performDBChange(lmScenario, TransactionType.UPDATE);
        return lmScenario.getPAObjectID();
    }

    @Override
    public ControlScenarioBase retrieve(int controlScenarioId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(controlScenarioId);
        if (pao == null) {
            throw new NotFoundException("Control Scenario Id not found");
        }
        LMScenario lmScenario = (LMScenario) dbPersistentDao.retrieveDBPersistent(pao);
        ControlScenarioBase controlScenarioBase = new ControlScenarioBase();
        controlScenarioBase.buildModel(lmScenario);
        controlScenarioBase.getAllPrograms().stream().forEach(program -> {
            program.setGears(drSetupHelper.getGearsforModel(program.getProgramId(), true));
        });
        return controlScenarioBase;
    }

    @Override
    public List<ControlScenarioProgram> getAvailablePrograms() {
        List<ControlScenarioProgram> availablePrograms = Lists.newArrayList();
        List<LiteYukonPAObject> programs = dbCache.getAllLoadManagement();
        List<Integer> programIdsInControlArea = LMControlAreaProgram.getAllProgramsInControlAreas();
        try {
            programs.stream().forEach(liteYukonPAObject -> {
                Integer programId = liteYukonPAObject.getLiteID();
                programIdsInControlArea.stream().forEach(programIdInControlArea -> {
                    if (programIdInControlArea.compareTo(programId) == 0
                        && liteYukonPAObject.getPaoType().isDirectProgram()) {
                        ControlScenarioProgram controlScenarioProgram = new ControlScenarioProgram();
                        controlScenarioProgram.setProgramId(programId);
                        controlScenarioProgram.setGears(drSetupHelper.getGearsforModel(programId, false));
                        availablePrograms.add(controlScenarioProgram);
                    }
                });
            });
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return availablePrograms;
    }

    @Override
    public int delete(int controlScenarioId, String controlScenarioName) {
        Optional<LiteYukonPAObject> controlScenario =
            dbCache.getAllLMScenarios().stream().filter(scenario -> scenario.getLiteID() == controlScenarioId
                && scenario.getPaoName().equals(controlScenarioName)).findFirst();
        if (controlScenario.isEmpty()) {
            throw new NotFoundException("Id and Name combination not found");
        }

        YukonPAObject lmScenario = (YukonPAObject) LiteFactory.createDBPersistent(controlScenario.get());
        dbPersistentDao.performDBChange(lmScenario, TransactionType.DELETE);
        return lmScenario.getPAObjectID();

    }

    /**
     * Returns DB Persistent object
     */
    private LMScenario getDBPersistent(ControlScenarioBase controlScenarioBase) {
        LMScenario lmScenario = (LMScenario) LMFactory.createLoadManagement(PaoType.LM_SCENARIO);
        if (controlScenarioBase.getId() != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(controlScenarioBase.getId());
            lmScenario = (LMScenario) dbPersistentDao.retrieveDBPersistent(pao);
        }
        return lmScenario;
    }

}
