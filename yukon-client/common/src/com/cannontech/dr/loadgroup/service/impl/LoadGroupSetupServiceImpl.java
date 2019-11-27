package com.cannontech.dr.loadgroup.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.dr.setup.ControlRawState;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.dr.setup.LoadGroupPoint;
import com.cannontech.common.dr.setup.LoadGroupRoute;
import com.cannontech.common.exception.LMObjectDeletionFailureException;
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
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private PointDao pointDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private DbChangeManager dbChangeManager;
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupServiceImpl.class);

    @Override
    public List<LMPaoDto> retrieveAvailableLoadGroup() {
        List<LiteYukonPAObject> list = dbCache.getAllLMGroups();

        if (list.size() == 0) {
            throw new NotFoundException("No load group available.");
        }
        
        List<LMPaoDto> availableLoadGroups = list.stream()
                                                 .filter(yukonPAObject -> yukonPAObject.getPaoType().supportsMacroGroup() && yukonPAObject.getPaoType() != PaoType.MACRO_GROUP)
                                                 .map(yukonPAObject -> createLMPaoDto(yukonPAObject))
                                                 .collect(Collectors.toList());
        return availableLoadGroups;
    }
    
    private LMPaoDto createLMPaoDto(LiteYukonPAObject yukonPAObject) {
        return new LMPaoDto(yukonPAObject.getYukonID(), yukonPAObject.getPaoName(), yukonPAObject.getPaoType());
    }

    @Override
    @Transactional
    public int create(LoadGroupBase loadGroup) {
        LMGroup lmGroup = getDBPersistent(loadGroup);
        loadGroup.buildDBPersistent(lmGroup);

        dbPersistentDao.performDBChange(lmGroup, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(lmGroup.getPAObjectID(), lmGroup.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);

        return lmGroup.getPAObjectID();
    }

    @Override
    @Transactional
    public int update(int loadGroupId, LoadGroupBase loadGroup) {
        Optional<LiteYukonPAObject> liteLoadGroup =
            dbCache.getAllLMGroups().stream().filter(group -> group.getLiteID() == loadGroupId).findFirst();

        if (liteLoadGroup.isEmpty()) {
            throw new NotFoundException("Id not found " + loadGroupId);
        }
        loadGroup.setId(loadGroupId);
        LMGroup lmGroup = getDBPersistent(loadGroup);
        loadGroup.buildDBPersistent(lmGroup);
        dbPersistentDao.performDBChange(lmGroup, TransactionType.UPDATE);
        return lmGroup.getPAObjectID();
    }

    @Override
    public LoadGroupBase retrieve(int loadGroupId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(loadGroupId);
        if (pao == null || pao.getPaoType() == PaoType.MACRO_GROUP) {
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
            String deviceUsageName = (dbCache.getAllPaosMap().get(lmGroupPoint.getLMGroupPoint().getDeviceIDUsage())).getPaoName();
            String pointUsageName = pointDao.getPointName(lmGroupPoint.getLMGroupPoint().getPointIDUsage());
            String rawStateName = stateGroupDao.getRawStateName(lmGroupPoint.getLMGroupPoint().getPointIDUsage(),
                    lmGroupPoint.getLMGroupPoint().getStartControlRawState());
            loadGroupPoint.setDeviceUsage(new LMDto(lmGroupPoint.getLMGroupPoint().getDeviceIDUsage(), deviceUsageName));
            loadGroupPoint.setPointUsage(new LMDto(lmGroupPoint.getLMGroupPoint().getPointIDUsage(), pointUsageName));
            loadGroupPoint.setStartControlRawState(
                    new ControlRawState(lmGroupPoint.getLMGroupPoint().getStartControlRawState(), rawStateName));

        }
        return loadGroupBase;
    }

    @Override
    @Transactional
    public int delete(int loadGroupId, String loadGroupName) {
        Optional<LiteYukonPAObject> liteLoadGroup = dbCache.getAllLMGroups()
                                                           .stream()
                                                           .filter(group -> group.getLiteID() == loadGroupId && group.getPaoName().equalsIgnoreCase(loadGroupName))
                                                           .findFirst();
        if (liteLoadGroup.isEmpty() || liteLoadGroup.get().getPaoType() == PaoType.MACRO_GROUP) {
            throw new NotFoundException("Id and Name combination not found");
        }
        Integer paoId = Integer.valueOf(ServletUtils.getPathVariable("id"));
        checkIfGroupIsUsed(liteLoadGroup.get().getPaoName(), paoId);
        YukonPAObject lmGroup = (YukonPAObject) LiteFactory.createDBPersistent(liteLoadGroup.get());
        dbPersistentDao.performDBChange(lmGroup, TransactionType.DELETE);
        return lmGroup.getPAObjectID();
    }

    @Override
    @Transactional
    public int copy(int loadGroupId, LMCopy lmCopy) {
        Optional<LiteYukonPAObject> liteLoadGroup =dbCache.getAllLMGroups()
                                                          .stream()
                                                          .filter(group -> group.getLiteID() == loadGroupId)
                                                          .findFirst();
        if (liteLoadGroup.isEmpty() || liteLoadGroup.get().getPaoType() == PaoType.MACRO_GROUP) {
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
                throw new LMObjectDeletionFailureException(message);
            }
        } catch (SQLException e) {
            String message = "Unable to delete load group with name : " + Groupname + e;
            log.error(message);
            throw new LMObjectDeletionFailureException(message);
        }
    }

    @Override
    public List<ControlRawState> getStartState(int pointId) {
        List<LiteState> stateList = stateGroupDao.getStateList(pointId);
        return stateList.stream()
                        .filter(state -> state.isValidRawState())
                        .map(state -> new ControlRawState(state.getStateRawState(), state.getStateText()))
                        .collect(Collectors.toList());
    }

}