package com.cannontech.web.stars.rtu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBankControllerLogical;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.stars.rtu.service.RtuService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class RtuServiceImpl implements RtuService{
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private IDatabaseCache cache;
    @Autowired private PointDao pointDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private RtuDnpService rtuDnpService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoCreationHelper paoCreationHelper;
    
    private static final Logger log = YukonLogManager.getLogger(RtuServiceImpl.class);
    
    @Override
    public List<MessageSourceResolvable> generateDuplicatePointsErrorMessages(int paoId, PointIdentifier pointIdentifier, HttpServletRequest request) {
        List<MessageSourceResolvable> messages = new ArrayList<>();
        List<LitePoint> points = getDuplicatePointsByTypeAndOffset(paoId, pointIdentifier);
        if (points.size() > 0) {
            messages.add(new WebMessageSourceResolvable("yukon.web.modules.operator.rtu.pointUniqueness"));            
            points.forEach(point -> {
                LiteYukonPAObject pao = cache.getAllPaosMap().get(point.getPaobjectID());
                String urlForPaoDetailPage = paoDetailUrlHelper.getUrlForPaoDetailPage(pao);
                String contextPath = request.getContextPath();
                String paoLinkHtml = "<a href='" + contextPath + urlForPaoDetailPage + "'>" + pao.getPaoName() + "</a>";
                String pointLinkHtml = "<a href='" + contextPath + "/tools/points/" + point.getPointID() + "'>" + point.getPointName() + "</a>";

                messages.add(new WebMessageSourceResolvable("yukon.web.modules.operator.rtu.pointUniqueness.detail",
                    point.getPointTypeEnum(), point.getPointOffset(),                                 
                    paoLinkHtml, pointLinkHtml));
            });
        }

        return messages;
    }
    
    @Override
    public List<MessageSourceResolvable> generateDuplicatePointsErrorMessages(int paoId, HttpServletRequest request) {
        return generateDuplicatePointsErrorMessages(paoId, null, request);
    }
    
    /**
     * Checks DNP RTU hierarchy for duplicate points.
     * 
     * @param pointIdentifier - if null, returns all duplicate points for the device
     * @return duplicate points
     */
    private List<LitePoint> getDuplicatePointsByTypeAndOffset(int paoId, PointIdentifier pointIdentifier) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        if (pao.getPaoType() == PaoType.RTU_DNP) {
            List<Integer> parentAndChildren = rtuDnpService.getParentAndChildDevices(paoId);
            if (pointIdentifier != null) {
                //RTU point edit screen
                return pointDao.getDuplicatePoints(parentAndChildren, Lists.newArrayList(pointIdentifier));
            } 
            //RTU view
            return pointDao.getAllDuplicatePoints(parentAndChildren);
            
        } else if (pao.getPaoType() == PaoType.CBC_LOGICAL) {
            DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
            CapBankControllerLogical logicalCbc = (CapBankControllerLogical) dbPersistent;
            if(logicalCbc.getParentDeviceId() != null) {
                List<Integer> parentAndChildren = rtuDnpService.getParentAndChildDevices(logicalCbc.getParentDeviceId());
                if (pointIdentifier == null) {
                    //CBC Logical view
                    List<PointIdentifier> cbcLogicalPoints = pointDao.getLitePointsByPaObjectId(paoId).stream()
                            .map(PointIdentifier::createPointIdentifier)
                            .collect(Collectors.toList());
                    
                    if(!cbcLogicalPoints.isEmpty()) {
                        return pointDao.getDuplicatePoints(parentAndChildren,  cbcLogicalPoints);
                    }
                } else {
                    //CBC Logical point edit
                    return pointDao.getDuplicatePoints(parentAndChildren,  Lists.newArrayList(pointIdentifier));
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<LiteYukonPAObject> getRtusByType(List<PaoType> rtuTypes) {
        List<LiteYukonPAObject> filteredRtus = cache.getAllDevices().stream()
                                                                    .filter(rtu -> rtuTypes.contains(rtu.getPaoType()))
                                                                    .collect(Collectors.toList());
        return filteredRtus;
    }

    @Override
    public boolean deleteRtu(int id) {
        try {
            deviceDao.removeDevice(id);
            return true;
        } catch (Exception e) {
            log.error("Unable to delete RTU with id " + id, e);
            return false;
        }
    }

    @Override
    public Integer copyRtu(RtuDnp rtuDnp) {
        String newName = rtuDnp.getName();
        Integer slaveAddress = rtuDnp.getDeviceAddress().getSlaveAddress();
        boolean copyPointFlag = rtuDnp.isCopyPointFlag();
        Integer oldRtuId = rtuDnp.getId();
        rtuDnp = rtuDnpService.getRtuDnp(oldRtuId);
        rtuDnp.setName(newName);
        rtuDnp.getDeviceAddress().setSlaveAddress(slaveAddress);
        rtuDnp.setId(null);
        int newPaoId = rtuDnpService.save(rtuDnp);
        rtuDnp.setId(newPaoId);
        // Copy Points check : start
        if (copyPointFlag) {
            List<PointBase> points = pointDao.getPointsForPao(oldRtuId);
            paoCreationHelper.applyPoints(rtuDnp, points);
            dbChangeManager.processPaoDbChange(rtuDnp, DbChangeType.UPDATE);
        }
        return newPaoId;
    }

}
