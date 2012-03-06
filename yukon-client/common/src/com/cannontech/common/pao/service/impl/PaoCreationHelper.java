package com.cannontech.common.pao.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoCreationHelper {

    private final Logger log = YukonLogManager.getLogger(PaoCreationHelper.class);
    private DBPersistentDao dbPersistentDao;
    private PaoDefinitionService paoDefinitionService;
    private PointDao pointDao;
    
    /**
     * Creates the points for the newly created pao based on pao definition
     */
    public void addDefaultPointsToPao(YukonPao pao) {
        List<PointBase> pointsToCreate = paoDefinitionService.createDefaultPointsForPao(pao);
        applyPointsForNewPao(pointsToCreate);
    }

    /**
     * Deletes all points for the Pao with the given PAObjectID. This is currently done using 
     * DBPersistent points, and in the future should be done using some sort of Dao as we try
     * to get away from using DBPersistents.
     * @param paObjectId the ID 
     * @throws SQLException
     */
    public void deletePointsForPao(int paObjectId) throws SQLException {
    	List<LitePoint> points = pointDao.getLitePointsByPaObjectId(paObjectId);
        for (LitePoint point : points) {
            PointBase chubbyPt = PointFactory.createPoint( point.getPointType() );
            chubbyPt.setPointID(point.getPointID());
            dbPersistentDao.performDBChange(chubbyPt, TransactionType.DELETE);
        }	
    }
    
    /**
     * Creates all points for a pao based on pao definiton meaning this
     *  will even create points that are not defined as <init>true</init>.
     * @param pao
     */
    public void addAllPointsToPao(YukonPao pao) {
        List<PointBase> pointsToCreate = paoDefinitionService.createAllPointsForPao(pao);
        applyPointsForNewPao(pointsToCreate);
    }
    
    public void processDbChange(YukonPao pao, DbChangeType changeType) {
        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
        
        DBChangeMsg msg = new DBChangeMsg(paoIdentifier.getPaoId(),
                                           DBChangeMsg.CHANGE_PAO_DB,
                                           paoIdentifier.getPaoType().getPaoCategory().getDbString(),
                                           paoIdentifier.getPaoType().getDbString(),
                                           changeType);
        
        dbPersistentDao.processDBChange(msg);
    }

    /**
     * @see PaoCreationHelper.applyPoints(YukonPao pao, List<PointBase> pointsToCreate)
     * @param paoId
     * @param pointsToCreate
     */
    private void applyPoints(int paoId, List<PointBase> pointsToCreate) {
    	MultiDBPersistent pointsToAdd = new MultiDBPersistent();
        Vector<DBPersistent> newPoints = pointsToAdd.getDBPersistentVector();
        Set<PointBase> calculatedPoints = Sets.newHashSet();
        Map<String, Integer> nonCalcPointLookupMap = Maps.newHashMap();
        
        // Non-calculated points
        for (PointBase point : pointsToCreate) {
            if (point instanceof CalculatedPoint == false) {
                int nextPointId = pointDao.getNextPointId();
                point.setPointID(nextPointId);
                point.getPoint().setPaoID(paoId);
                
                nonCalcPointLookupMap.put(StringUtils.lowerCase(point.getPoint().getPointName()), nextPointId);
                newPoints.add(point);
            } else {
                calculatedPoints.add(point);
            }
        }
        
        // Calculated points
        for (PointBase point : calculatedPoints) {
            int nextPointId = pointDao.getNextPointId();
            point.setPointID(nextPointId);
            point.getPoint().setPaoID(paoId);
            
            CalculatedPoint calcPoint = (CalculatedPoint) point;
            for (CalcComponent calcComponent: calcPoint.getCalcComponentVector()) {
                Integer componentPointID = calcComponent.getComponentPointID();
                if (componentPointID == null) continue;
                
                LitePoint litePoint = pointDao.getLitePoint(componentPointID);
                String pointName = litePoint.getPointName();
                
                // Find the pointId that our CalcComponent pointName references
                // (We are assuming here that pointName is referencing a point on the same device.
                // If it is referencing a point on a different device, that specific CalcComponent
                // will not be included in the point. The Calculated point itself will still be created, 
                // since we are still adding the point to newPoints below.)
                Integer pointId = nonCalcPointLookupMap.get(StringUtils.lowerCase(pointName));
                if (pointId == null) {
                    log.warn("Could not find point with name \"" + pointName + "\". Continuing with insertion.");
                    continue;
                }
                
                calcComponent.setComponentPointID(pointId);
            }
            newPoints.add(point);
        }
        
        // Insert into DB
        dbPersistentDao.performDBChangeWithNoMsg(pointsToAdd, TransactionType.INSERT);
    }
    
    /**
     * Performs DB persistents of provided points, points are expected
     * to be provide with there PAObjectId already set.
     * For use with the 'addDefaultPointsToPao' method.
     * @param pointsToCreate
     */
    public void applyPointsForNewPao(List<PointBase> pointsToCreate) {
        MultiDBPersistent pointsToAdd = new MultiDBPersistent();
        Vector<DBPersistent> newPoints = pointsToAdd.getDBPersistentVector();
        newPoints.addAll(pointsToCreate);

        // Insert into DB
        dbPersistentDao.performDBChangeWithNoMsg(pointsToAdd, TransactionType.INSERT);
    }
    
    /**
     * Creates points base on list of points provided that exist on a template device.
     */
    public void applyPoints(YukonPao pao, List<PointBase> pointsToCreate) {
        applyPoints(pao.getPaoIdentifier().getPaoId(), pointsToCreate);
    }
    
    @Autowired
    public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}
