package com.cannontech.dr.loadgroup.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupPoint;
import com.cannontech.common.dr.setup.LoadGroupRoute;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.exception.DeletionFailureException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupPoint;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class LoadGroupSetupServiceImpl implements LoadGroupSetupService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private DemandResponseEventLogService logService;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private DbChangeManager dbChangeManager;
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupServiceImpl.class);

    private LMPaoDto createLMPaoDto(LiteYukonPAObject yukonPAObject) {
        return new LMPaoDto(yukonPAObject.getYukonID(), yukonPAObject.getPaoName(), yukonPAObject.getPaoType());
    }

    @Override
    @Transactional
    public LoadGroupBase create(LoadGroupBase loadGroup, LiteYukonUser liteYukonUser) {
        LMGroup lmGroup = getDBPersistent(loadGroup);
        loadGroup.buildDBPersistent(lmGroup);

        dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(lmGroup.getPAObjectID(), lmGroup.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        loadGroup.buildModel(lmGroup);
        if (loadGroup instanceof LoadGroupRoute) {
            setRouteName((LoadGroupRoute) loadGroup);
        }
        if (loadGroup instanceof LoadGroupPoint) {
            LMGroupPoint lmGroupPoint = (LMGroupPoint) lmGroup;
            LoadGroupPoint loadGroupPoint = (LoadGroupPoint) loadGroup;
            updateLoadGroupPoint(lmGroupPoint, loadGroupPoint);
        }
        logService.loadGroupCreated(loadGroup.getName(), loadGroup.getType(), liteYukonUser);
        return loadGroup;
    }

    @Override
    @Transactional
    public LoadGroupBase update(int loadGroupId, LoadGroupBase loadGroup, LiteYukonUser liteYukonUser) {
        Optional<LiteYukonPAObject> liteLoadGroup =
            dbCache.getAllLMGroups().stream().filter(group -> group.getLiteID() == loadGroupId).findFirst();

        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Id not found " + loadGroupId);
        }
        loadGroup.setId(loadGroupId);
        LMGroup lmGroup = getDBPersistent(loadGroup);
        loadGroup.buildDBPersistent(lmGroup);
        dbPersistentDao.performDBChange(lmGroup, TransactionType.UPDATE);
        loadGroup.buildModel(lmGroup);
        if (loadGroup instanceof LoadGroupRoute) {
            setRouteName((LoadGroupRoute) loadGroup);
        }
        if (loadGroup instanceof LoadGroupPoint) {
            LMGroupPoint lmGroupPoint = (LMGroupPoint) lmGroup;
            LoadGroupPoint loadGroupPoint = (LoadGroupPoint) loadGroup;
            updateLoadGroupPoint(lmGroupPoint, loadGroupPoint);
        }
        logService.loadGroupUpdated(loadGroup.getName(), loadGroup.getType(), liteYukonUser);
        return loadGroup;
    }

    @Override
    public LoadGroupBase retrieve(int loadGroupId, LiteYukonUser liteYukonUser) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(loadGroupId);
        if (pao == null || !pao.getPaoType().isLoadGroupSupportedFromWeb()) {
            throw new NotFoundException("Id not found");
        }
        LMGroup loadGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(pao);
        LoadGroupBase loadGroupBase = getModel(loadGroup.getPaoType());
        loadGroupBase.buildModel(loadGroup);
        if (loadGroupBase instanceof LoadGroupRoute) {
            setRouteName((LoadGroupRoute)loadGroupBase);
        }
        if (loadGroupBase instanceof LoadGroupPoint) {
            LMGroupPoint lmGroupPoint = (LMGroupPoint) loadGroup;
            LoadGroupPoint loadGroupPoint = (LoadGroupPoint) loadGroupBase;
            updateLoadGroupPoint(lmGroupPoint, loadGroupPoint);
        }
        return loadGroupBase;
    }

    @Override
    @Transactional
    public int delete(int loadGroupId, LiteYukonUser liteYukonUser) {
        Optional<LiteYukonPAObject> liteLoadGroup = dbCache.getAllLMGroups()
                                                           .stream()
                                                           .filter(group -> group.getLiteID() == loadGroupId)
                                                           .findFirst();
        if (liteLoadGroup.isEmpty() || !liteLoadGroup.get().getPaoType().isLoadGroupSupportedFromWeb()) {
            throw new NotFoundException("Id not found");
        }
        Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
        checkIfGroupIsUsed(liteLoadGroup.get().getPaoName(), paoId);
        YukonPAObject lmGroup = (YukonPAObject) LiteFactory.createDBPersistent(liteLoadGroup.get());
        dbPersistentDao.performDBChange(lmGroup, TransactionType.DELETE);
        logService.loadGroupDeleted(liteLoadGroup.get().getPaoName(), liteLoadGroup.get().getPaoType(), liteYukonUser);
        return lmGroup.getPAObjectID();
    }

    @Override
    @Transactional
    public LoadGroupBase copy(int loadGroupId, LMCopy lmCopy, LiteYukonUser liteYukonUser) {
    
        Optional<LiteYukonPAObject> liteLoadGroup =dbCache.getAllLMGroups()
                                                          .stream()
                                                          .filter(group -> group.getLiteID() == loadGroupId)
                                                          .findFirst();
        if (liteLoadGroup.isEmpty() || !liteLoadGroup.get().getPaoType().isLoadGroupSupportedFromWeb()) {
            throw new NotFoundException("Id not found");
        }

        LMGroup lmGroup = (LMGroup) dbPersistentDao.retrieveDBPersistent(liteLoadGroup.get());
        int oldLoadGroupId = lmGroup.getPAObjectID();
        lmCopy.buildModel(lmGroup);
        lmGroup.setPAObjectID(null);

        dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);

        List<PointBase> points = pointDao.getPointsForPao(oldLoadGroupId);
        SimpleDevice device = SimpleDevice.of(lmGroup.getPAObjectID(), lmGroup.getPaoType());
        paoCreationHelper.applyPoints(device, points);
        dbChangeManager.processPaoDbChange(device, DbChangeType.UPDATE);
    
        LoadGroupBase loadGroupBase = getModel(lmGroup.getPaoType());
        loadGroupBase.buildModel(lmGroup);
        if (loadGroupBase instanceof LoadGroupRoute) {
            setRouteName((LoadGroupRoute) loadGroupBase);
        }
        if (loadGroupBase instanceof LoadGroupPoint) {
            LMGroupPoint lmGroupPoint = (LMGroupPoint) lmGroup;
            LoadGroupPoint loadGroupPoint = (LoadGroupPoint) loadGroupBase;
            updateLoadGroupPoint(lmGroupPoint, loadGroupPoint);
        }
        logService.loadGroupCreated(loadGroupBase.getName(), loadGroupBase.getType(), liteYukonUser);
        
        
        return loadGroupBase;
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

    /**
     * Set the route name to send in response for the routeId 
     */
    private void setRouteName(LoadGroupRoute loadGroup) {
        Integer routeId = loadGroup.getRouteId();
        if (routeId != null && routeId > 0) {
            loadGroup.setRouteName(dbCache.getAllRoutesMap().get(routeId).getPaoName());
        }
    }

    /**
     * Checks that if load group provided is associated with any load program or not
     */
    private void checkIfGroupIsUsed(String Groupname, Integer paoId) {
        String program;
        try {
            if ((program = com.cannontech.database.db.device.lm.LMGroup.isGroupUsed(paoId)) != null) {
                String message = "You cannot delete the device '" + Groupname
                    + "' because it is utilized by the LM program named '" + program + "'";
                throw new DeletionFailureException(message);
            }
        } catch (SQLException e) {
            String message = "Unable to delete load group with name : " + Groupname + e;
            log.error(message);
            throw new DeletionFailureException(message);
        }
    }

    /**
     * Returns true in case the raw state is either 0 or 1 in case of Point Load Group
     */
    public static boolean isValidPointGroupRawState(LiteState liteState) {
        return (liteState.getStateRawState() == 0 || liteState.getStateRawState() == 1);
    }

    /**
     * Update deviceUsageName,pointUsageName and rawStateName
     */
    private void updateLoadGroupPoint(LMGroupPoint lmGroupPoint, LoadGroupPoint loadGroupPoint) {
        String deviceUsageName = (dbCache.getAllPaosMap().get(lmGroupPoint.getLMGroupPoint().getDeviceIDUsage())).getPaoName();
        String pointUsageName = pointDao.getPointName(lmGroupPoint.getLMGroupPoint().getPointIDUsage());
        String rawStateName = stateGroupDao.getRawStateName(lmGroupPoint.getLMGroupPoint().getPointIDUsage(),
                lmGroupPoint.getLMGroupPoint().getStartControlRawState());
        loadGroupPoint.setDeviceUsage(new LMDto(lmGroupPoint.getLMGroupPoint().getDeviceIDUsage(), deviceUsageName));
        loadGroupPoint.setPointUsage(new LMDto(lmGroupPoint.getLMGroupPoint().getPointIDUsage(), pointUsageName));
        loadGroupPoint.setStartControlRawState(new LMDto(lmGroupPoint.getLMGroupPoint().getStartControlRawState(), rawStateName));
    }

}