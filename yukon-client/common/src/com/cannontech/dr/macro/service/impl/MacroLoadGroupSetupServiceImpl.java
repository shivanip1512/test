package com.cannontech.dr.macro.service.impl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.MacroLoadGroupProcessingException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.dr.setup.service.LMSetupService;
import com.cannontech.yukon.IDatabaseCache;

public class MacroLoadGroupSetupServiceImpl implements LMSetupService <MacroLoadGroup, LMCopy>  {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DemandResponseEventLogService logService;

    @Override
    public MacroLoadGroup retrieve(int loadGroupId) {
        Optional<LiteYukonPAObject> liteLoadGroup = getGroupFromCache(loadGroupId);
        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Macro load group Id not found");
        }

        LMGroup lmGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(liteLoadGroup.get());
        if (lmGroup.getPaoType() != PaoType.MACRO_GROUP) {
            throw new MacroLoadGroupProcessingException("Not valid macro group ID");
        }
        MacroLoadGroup macroLoadGroup = new MacroLoadGroup();
        macroLoadGroup.buildModel(lmGroup);
        updateAssignedLoadGroups(macroLoadGroup);
        return macroLoadGroup;
    }

    @Override
    @Transactional
    public MacroLoadGroup create(MacroLoadGroup macroLoadGroup, LiteYukonUser liteYukonUser) {
        Set<Integer> invalidLoadgorups = isValidLoadGroup(macroLoadGroup);
        if (!invalidLoadgorups.isEmpty()) {
            throw new MacroLoadGroupProcessingException("Load group does not exists  " + invalidLoadgorups);
        }
        LMGroup lmGroup = getMacroLoadGroupDBPersistent(macroLoadGroup, macroLoadGroup.getId());
        macroLoadGroup.buildDBPersistent(lmGroup);
        dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);
        macroLoadGroup.buildModel(lmGroup);
        updateAssignedLoadGroups(macroLoadGroup);
        logService.loadGroupCreated(macroLoadGroup.getName(), macroLoadGroup.getType(), liteYukonUser);

        return macroLoadGroup;
    }

    @Override
    @Transactional
    public MacroLoadGroup update(int loadGroupId, MacroLoadGroup macroLoadGroup, LiteYukonUser liteYukonUser) {
        Set<Integer> invalidLoadgorups = isValidLoadGroup(macroLoadGroup);
        if (!invalidLoadgorups.isEmpty()) {
            throw new MacroLoadGroupProcessingException("Load group does not exists  " + invalidLoadgorups);
        }
        Optional<LiteYukonPAObject> liteLoadGroup = getGroupFromCache(loadGroupId);

        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Macro load group Id not found " + loadGroupId);
        }
        macroLoadGroup.setId(loadGroupId);
        LMGroup lmGroup = getMacroLoadGroupDBPersistent(macroLoadGroup, loadGroupId);
        macroLoadGroup.buildDBPersistent(lmGroup);
        dbPersistentDao.performDBChange(lmGroup, TransactionType.UPDATE);
        macroLoadGroup.buildModel(lmGroup);
        updateAssignedLoadGroups(macroLoadGroup);
        logService.loadGroupUpdated(macroLoadGroup.getName(), macroLoadGroup.getType(), liteYukonUser);
        return macroLoadGroup;
    }

    @Override
    @Transactional
    public int copy(int loadGroupId, LMCopy lmCopy, LiteYukonUser liteYukonUser) {
        Optional<LiteYukonPAObject> liteLoadGroup = getGroupFromCache(loadGroupId);
        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Macro load group Id not found");
        }
        if (!isMacroLoadGroup(liteLoadGroup.get())) {
            throw new MacroLoadGroupProcessingException("Must be valid macro load group.");
        }
        LMGroup loadGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(liteLoadGroup.get());
        lmCopy.buildModel(loadGroup);
        loadGroup.setPAObjectID(null);

        dbPersistentDao.performDBChange(loadGroup, TransactionType.INSERT);
        logService.loadGroupCreated(loadGroup.getPAOName(), loadGroup.getPaoType(), liteYukonUser);
        return loadGroup.getPAObjectID();
    }

    @Override
    @Transactional
    public int delete(int loadGroupId, LiteYukonUser liteYukonUser) {
        Optional<LiteYukonPAObject> liteLoadGroup = dbCache.getAllLMGroups()
                                                           .stream()
                                                           .filter(group -> group.getLiteID() == loadGroupId)
                                                           .findFirst();
        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Macro load Gorup Id not found");
        }

        if (!isMacroLoadGroup(liteLoadGroup.get())) {
            throw new MacroLoadGroupProcessingException("Must be valid macro load group Id");
        }

        YukonPAObject lmGroup = (YukonPAObject) LiteFactory.createDBPersistent(liteLoadGroup.get());
        dbPersistentDao.performDBChange(lmGroup, TransactionType.DELETE);
        logService.loadGroupDeleted(liteLoadGroup.get().getPaoName(), liteLoadGroup.get().getPaoType(), liteYukonUser);
        return lmGroup.getPAObjectID();
    }

    private boolean isMacroLoadGroup(LiteYukonPAObject liteLoadGroup) {
        if (liteLoadGroup.getPaoType() != PaoType.MACRO_GROUP) {
            return false;
        }
        return true;
    }

    private Set<Integer> isValidLoadGroup(MacroLoadGroup macroLoadGroup) {
        Set<Integer> paoSet = new LinkedHashSet<>();
        Set<Integer> macroGroup = new LinkedHashSet<>();
        for (LMPaoDto lmPaoDto : macroLoadGroup.getAssignedLoadGroups()) {
            if (lmPaoDto.getId() == null) {
                throw new MacroLoadGroupProcessingException("Assigned load group must contain valid Load group Id");
            }
            Optional<LiteYukonPAObject> liteLoadGroup = getGroupFromCache(lmPaoDto.getId());
            if (liteLoadGroup.isEmpty()) {
                paoSet.add(lmPaoDto.getId());
            } else {
                if (!liteLoadGroup.get().getPaoType().supportsMacroGroup() || isMacroLoadGroup(liteLoadGroup.get())) {
                    macroGroup.add(lmPaoDto.getId());
                }
            }

        }
        if (!macroGroup.isEmpty()) {
            throw new MacroLoadGroupProcessingException(
                "Assigned load groups must not contain Ecobee, Honeywell, Itron, Nest and Macro Load Group"
                    + macroGroup);
        }
        return paoSet;
    }

    private void updateAssignedLoadGroups(MacroLoadGroup macroLoadGroup) {
        for (LMPaoDto lmPaoDto : macroLoadGroup.getAssignedLoadGroups()) {
            Optional<LiteYukonPAObject> liteLoadGroup = getGroupFromCache(lmPaoDto.getId());
            lmPaoDto.setName(liteLoadGroup.get().getPaoName());
            lmPaoDto.setType(liteLoadGroup.get().getPaoType());
        }
    }

    private LMGroup getMacroLoadGroupDBPersistent(MacroLoadGroup macroLoadGroup, Integer macroloadGroupId) {
        LMGroup lmGroup = (LMGroup) LMFactory.createLoadManagement(macroLoadGroup.getType());
        if (macroloadGroupId != null) {
            Optional<LiteYukonPAObject> pao = getGroupFromCache(macroloadGroupId);
            if (!isMacroLoadGroup(pao.get())) {
                throw new MacroLoadGroupProcessingException("Must be valid macro load group Id");
            }
            lmGroup = (MacroGroup) dbPersistentDao.retrieveDBPersistent(pao.get());
        }
        return lmGroup;
    }

    private Optional<LiteYukonPAObject> getGroupFromCache(int loadGroupId) {
        Optional<LiteYukonPAObject> litePaoObject = dbCache.getAllLMGroups()
                                                           .stream()
                                                           .filter(group -> group.getLiteID() == loadGroupId)
                                                           .findFirst();
        return litePaoObject;
    }

}
