package com.cannontech.common.pao.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;

public class PaoCreationHelper {

    private DBPersistentDao dbPersistentDao;
    private PaoDefinitionService paoDefinitionService;
    private PointDao pointDao;
    
    public void addDefaultPointsToPao(YukonPao pao) {
        List<PointBase> pointsToCreate = paoDefinitionService.createDefaultPointsForPao(pao);
        
        applyPoints(pao, pointsToCreate);
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
    
    public void addAllPointsToPao(YukonPao pao) {
        List<PointBase> pointsToCreate = paoDefinitionService.createAllPointsForPao(pao);
        
        applyPoints(pao, pointsToCreate);
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

    public void applyPoints(int paoId, List<PointBase> pointsToCreate) {
    	MultiDBPersistent pointsToAdd = new MultiDBPersistent();
        Vector<DBPersistent> newPoints = pointsToAdd.getDBPersistentVector();

        for (PointBase point : pointsToCreate) {
        
            int nextPointId = pointDao.getNextPointId();
            point.setPointID(nextPointId);
            point.getPoint().setPaoID(paoId);
            
            newPoints.add(point);
        }
        
        // Insert into DB
        dbPersistentDao.performDBChangeWithNoMsg(pointsToAdd, TransactionType.INSERT);
    }
    
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
