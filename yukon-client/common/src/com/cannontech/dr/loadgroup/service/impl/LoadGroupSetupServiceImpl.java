package com.cannontech.dr.loadgroup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.yukon.IDatabaseCache;

public class LoadGroupSetupServiceImpl implements LoadGroupSetupService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    
    @Override
    public int save(LoadGroupBase loadGroup) {
        LMGroup lmGroup = getDBPersistent(loadGroup);
        loadGroup.buildDBPersistent(lmGroup);

        if (loadGroup.getId() == null) {
            dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);
        } else {
            dbPersistentDao.performDBChange(lmGroup, TransactionType.UPDATE);
        }
        return lmGroup.getPAObjectID();
    }
    
    @Override
    public LoadGroupBase retrieve(int loadGroupId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(loadGroupId);
        if (pao == null) {
            throw new NotFoundException("Id not found");
        }
        LMGroup loadGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(pao);
        LoadGroupBase loadGroupBase = getModel(loadGroup.getPaoType());
        loadGroupBase.buildModel(loadGroup);
        return loadGroupBase;
    }

    @Override
    public int delete(int loadGroupId, String loadGroupName) {
        LoadGroupBase loadGroup = retrieve(loadGroupId);
        if (!loadGroup.getName().equals(loadGroupName)) {
            throw new NotFoundException("Id and Name combination not found");
        }
        LMGroup lmGroup = getDBPersistent(loadGroup);
        dbPersistentDao.performDBChange(lmGroup, TransactionType.DELETE);
        return lmGroup.getPAObjectID();
    }

    @Override
    public int copy(int loadGroupID, String loadGroupName) {
        LoadGroupBase loadGroup = retrieve(loadGroupID);

        LoadGroupBase newLoadGroup = new LoadGroupBase();
        newLoadGroup.setName(loadGroupName);
        newLoadGroup.setType(loadGroup.getType());
        newLoadGroup.setkWCapacity(loadGroup.getkWCapacity());
        newLoadGroup.setDisableControl(loadGroup.isDisableControl());
        newLoadGroup.setDisableGroup(loadGroup.isDisableGroup());

        LMGroup lmGroup = getDBPersistent(newLoadGroup);
        newLoadGroup.buildDBPersistent(lmGroup);

        if (loadGroup.getId() == null) {
            throw new NotFoundException("Id not found");
        } else {
            dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);
        }

        return lmGroup.getPAObjectID();
    }

    /**
     * Returns DB Persistent object 
     */
    private LMGroup getDBPersistent(LoadGroupBase loadGroup) {
        LMGroup lmGroup = (LMGroup) LMFactory.createLoadManagement(loadGroup.getType());
        if (loadGroup.getId() != null) {
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(loadGroup.getId());
            lmGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(pao);
        }
        return lmGroup;
    }
    
    /**
     * Returns LM Model object 
     */
    private LoadGroupBase getModel(PaoType paoType) {
        LoadGroupBase lmGroup = (LoadGroupBase) LMModelFactory.createLoadGroup(paoType);
        return lmGroup;
    }

}
