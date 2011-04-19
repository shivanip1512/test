package com.cannontech.common.pao.service.impl;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class PaoCreationHelper {

    private DBPersistentDao dbPersistentDao;
    private PaoDefinitionService paoDefinitionService;
    private PointDao pointDao;
    
    public void addDefaultPointsToPao(YukonPao pao) {
        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
        List<PointBase> pointsToCreate = paoDefinitionService.createDefaultPointsForPao(paoIdentifier);
        
        applyPoints(pao.getPaoIdentifier().getPaoId(),pointsToCreate);
    }

    public void addAllPointsToPao(YukonPao pao) {
        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
        List<PointBase> pointsToCreate = paoDefinitionService.createAllPointsForPao(paoIdentifier);
        
        applyPoints(pao.getPaoIdentifier().getPaoId(),pointsToCreate);
    }
    
    public void processDbChange(YukonPao pao, DbChangeType changeType) {
        PaoIdentifier paoIdentifier = pao.getPaoIdentifier();
        
        DBChangeMsg msg = new DBChangeMsg(paoIdentifier.getPaoId(),
                                           DBChangeMsg.CHANGE_PAO_DB,
                                           paoIdentifier.getPaoType().getPaoCategory().name(),
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
