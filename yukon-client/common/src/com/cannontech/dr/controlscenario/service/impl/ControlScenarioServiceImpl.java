package com.cannontech.dr.controlscenario.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.dr.setup.ControlScenario;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMServiceHelper;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.dr.setup.service.LMSetupService;
import com.cannontech.yukon.IDatabaseCache;

public class ControlScenarioServiceImpl implements LMSetupService <ControlScenario, LMCopy> {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private LMServiceHelper lmServiceHelper;
    @Autowired private IDatabaseCache dbCache;

    @Override
    @Transactional
    public int create(ControlScenario controlScenario) {
        if (CollectionUtils.isNotEmpty(controlScenario.getAllPrograms())) {
            lmServiceHelper.validateProgramsAndGear(controlScenario);
        }
        LMScenario lmScenario = getDBPersistent(controlScenario);
        controlScenario.buildDBPersistent(lmScenario);
        dbPersistentDao.performDBChange(lmScenario, TransactionType.INSERT);
        return lmScenario.getPAObjectID();
    }

    @Override
    @Transactional
    public int update(int controlScenarioId, ControlScenario controlScenario) {
        if (CollectionUtils.isNotEmpty(controlScenario.getAllPrograms())) {
            lmServiceHelper.validateProgramsAndGear(controlScenario);
        }
        controlScenario.setId(controlScenarioId);
        LMScenario lmScenario = getDBPersistent(controlScenario);
        controlScenario.buildDBPersistent(lmScenario);
        dbPersistentDao.performDBChange(lmScenario, TransactionType.UPDATE);
        return lmScenario.getPAObjectID();
    }

    @Override
    public ControlScenario retrieve(int controlScenarioId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(controlScenarioId);
        if (pao == null) {
            throw new NotFoundException("Scenario Id not found");
        }
        LMScenario lmScenario = (LMScenario) dbPersistentDao.retrieveDBPersistent(pao);
        ControlScenario controlScenario = new ControlScenario();
        controlScenario.buildModel(lmScenario);
        controlScenario.getAllPrograms().stream().forEach(program -> {
            program.setGears(lmServiceHelper.getGearsforModel(program.getProgramId(), program.getGears()));
        });
        return controlScenario;
    }

    @Override
    @Transactional
    public int delete(int controlScenarioId, String controlScenarioName) {
        LiteYukonPAObject controlScenario = dbCache.getAllLMScenarios().stream()
                                                                       .filter(scenario -> scenario.getLiteID() == controlScenarioId && scenario.getPaoName().equalsIgnoreCase(controlScenarioName))
                                                                       .findFirst()
                                                                       .orElseThrow(() -> new NotFoundException("Scenario Id and Name combination not found"));

        YukonPAObject lmScenario = (YukonPAObject) LiteFactory.createDBPersistent(controlScenario);
        dbPersistentDao.performDBChange(lmScenario, TransactionType.DELETE);
        return lmScenario.getPAObjectID();

    }

    /**
     * Returns DB Persistent object
     */
    private LMScenario getDBPersistent(ControlScenario controlScenario) {
        LMScenario lmScenario = (LMScenario) LMFactory.createLoadManagement(PaoType.LM_SCENARIO);
        if (controlScenario.getId() != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(controlScenario.getId());
            lmScenario = (LMScenario) dbPersistentDao.retrieveDBPersistent(pao);
        }
        return lmScenario;
    }

    @Override
    @Transactional
    public int copy(int id, LMCopy lmCopy) {
        throw new UnsupportedOperationException("Not supported copy operation");
    }

}
