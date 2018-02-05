package com.cannontech.web.stars.rtu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBankControllerLogical;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.stars.rtu.service.RtuService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class RtuServiceImpl implements RtuService{
    @Autowired private IDatabaseCache cache;
    @Autowired private PointDao pointDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    
    @Override
    public List<MessageSourceResolvable> generateDuplicatePointsErrorMessages(int paoId, PointIdentifier pointIdentifier) {
        List<MessageSourceResolvable> messages = new ArrayList<>();
        List<LitePoint> points = getDuplicatePointsByTypeAndOffset(paoId, pointIdentifier);
        if (points.size() > 0) {
            messages.add(new YukonMessageSourceResolvable("yukon.web.modules.operator.rtu.pointUniqueness"));
            System.out.println("Point type and offset must be unique across an RTU-DNP and its Logical devices:");
            for (LitePoint point : points) {
                System.out.println("-----Device:" + cache.getAllPaosMap().get(point.getPaobjectID()).getPaoName()
                    + "---Name:" + point.getPointName() + "---Type:" + point.getPointTypeEnum() + "---Offset:"
                    + point.getPointOffset());
            }
            
            points.forEach(point -> {
                messages.add(YukonMessageSourceResolvable.createSingleCodeWithArguments(
                    "yukon.web.modules.operator.rtu.pointUniqueness.detail",
                    cache.getAllPaosMap().get(point.getPaobjectID()).getPaoName(), point.getPointName(),
                    point.getPointTypeEnum(), point.getPointOffset()));
            });
        }

        return messages;
    }
    
    @Override
    public List<MessageSourceResolvable> generateDuplicatePointsErrorMessages(int paoId) {
        return generateDuplicatePointsErrorMessages(paoId, null);
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
            if (pointIdentifier != null) {
                //RTU point edit screen
                return getDuplicatePoints(paoId, Lists.newArrayList(pointIdentifier)); 
            } 
            //RTU view
            return getDuplicatePoints(paoId, new ArrayList<>());
            
        } else if (pao.getPaoType() == PaoType.CBC_LOGICAL) {
            DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
            CapBankControllerLogical logicalCbc = (CapBankControllerLogical) dbPersistent;
            if(logicalCbc.getParentDeviceId() != null) {
                if (pointIdentifier == null) {
                    //CBC Logical view
                    List<PointIdentifier> cbcLogicalPoints = pointDao.getLitePointsByPaObjectId(paoId).stream()
                            .map(p -> new PointIdentifier(p.getPointTypeEnum(), p.getPointOffset()))
                            .collect(Collectors.toList());
                    
                    if(!cbcLogicalPoints.isEmpty()) {
                        return getDuplicatePoints(logicalCbc.getParentDeviceId(), cbcLogicalPoints);   
                    }
                } else {
                    //CBC Logical point edit
                    return getDuplicatePoints(logicalCbc.getParentDeviceId(), Lists.newArrayList(pointIdentifier)); 
                }
            }
        }
        return new ArrayList<>();
    }
    
    private List<LitePoint> getDuplicatePoints(Integer parentRtuDeviceId, List<PointIdentifier> pointIdentifiers){
        List<Integer> paoIds = Lists.newArrayList(parentRtuDeviceId);
        List<LitePoint> points = new ArrayList<>();
        // RTU-DNP and its Logical CBC devices
        paoIds.addAll(deviceDao.getChildDevices(parentRtuDeviceId).stream()
            .map(DisplayableDevice::getId)
            .collect(Collectors.toList()));
        if (!paoIds.isEmpty()) {
            points.addAll(pointDao.getDuplicatePointsByPointIdentifiers(paoIds, pointIdentifiers));
        }
        return points;
    }
}
