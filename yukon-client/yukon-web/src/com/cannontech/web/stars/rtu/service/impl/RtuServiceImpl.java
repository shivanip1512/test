package com.cannontech.web.stars.rtu.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.CapBankControllerLogical;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.stars.rtu.service.RtuService;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class RtuServiceImpl implements RtuService{
    @Autowired private IDatabaseCache cache;
    @Autowired private PointDao pointDao;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private RtuDnpService rtuDnpService;
    
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
            List<Integer> parentAndChildren = rtuDnpService.getChildDevices(paoId);
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
                List<Integer> parentAndChildren = rtuDnpService.getChildDevices(logicalCbc.getParentDeviceId());
                if (pointIdentifier == null) {
                    //CBC Logical view
                    List<PointIdentifier> cbcLogicalPoints = pointDao.getLitePointsByPaObjectId(paoId).stream()
                            .map(p -> new PointIdentifier(p.getPointTypeEnum(), p.getPointOffset()))
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
}
