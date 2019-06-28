package com.cannontech.dr.loadgroup.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public class LoadGroupSetupServiceImpl implements LoadGroupSetupService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private PointDao pointDao;
    @Autowired private DbChangeManager dbChangeManager;

    @Override
    public int save(LoadGroupBase loadGroup) {
        LMGroup lmGroup = getDBPersistent(loadGroup);
        loadGroup.buildDBPersistent(lmGroup);

        if (loadGroup.getId() == null) {
            dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);
            SimpleDevice device = SimpleDevice.of(lmGroup.getPAObjectID(), lmGroup.getPaoType());
            paoCreationHelper.addDefaultPointsToPao(device);
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
        Optional<LiteYukonPAObject> liteLoadGroup = dbCache.getAllLMGroups()
                                                           .stream()
                                                           .filter(group -> group.getLiteID() == loadGroupId 
                                                                    && group.getPaoName().equals(loadGroupName))
                                                           .findFirst();
        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Id and Name combination not found");
        }

        YukonPAObject lmGroup = (YukonPAObject) LiteFactory.createDBPersistent(liteLoadGroup.get());
        dbPersistentDao.performDBChange(lmGroup, TransactionType.DELETE);
        return lmGroup.getPAObjectID();
    }

    @Override
    public int copy(int loadGroupId, LMCopy lmCopy) {
        Optional<LiteYukonPAObject> liteLoadGroup = dbCache.getAllLMGroups()
                                                           .stream()
                                                           .filter(group -> group.getLiteID() == loadGroupId)
                                                           .findFirst();
        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Id not found");
        }

        LMGroup loadGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(liteLoadGroup.get());
        int oldLoadGroupId = loadGroup.getPAObjectID();
        lmCopy.buildModel(loadGroup);
        loadGroup.setPAObjectID(null);

        dbPersistentDao.performDBChange(loadGroup, TransactionType.INSERT);

        List<PointBase> points = pointDao.getPointsForPao(oldLoadGroupId);
        SimpleDevice device = SimpleDevice.of(loadGroup.getPAObjectID(), loadGroup.getPaoType());
        paoCreationHelper.applyPoints(device, points);
        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);

        return loadGroup.getPAObjectID();
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
