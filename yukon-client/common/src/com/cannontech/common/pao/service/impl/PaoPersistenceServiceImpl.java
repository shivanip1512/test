package com.cannontech.common.pao.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoPersistenceDao;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeType;

public class PaoPersistenceServiceImpl implements PaoPersistenceService {
    private static final Logger log = YukonLogManager.getLogger(PaoPersistenceServiceImpl.class);
    
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private PaoPersistenceDao paoPersistenceDao;
    
    @Override
    public <T extends CompleteYukonPao> T retreivePao(PaoIdentifier paoIdentifier, Class<T> klass) {
        return paoPersistenceDao.retreivePao(paoIdentifier, klass);
    }
    
    @Override
    public void createPaoWithDefaultPoints(CompleteYukonPao pao, PaoType paoType) {
        paoPersistenceDao.createPao(pao, paoType);
        
        // DB change message happens in the processDbChange call below.
        paoCreationHelper.addDefaultPointsToPao(pao.getPaoIdentifier());
            
        // DB change message. Process Device dbChange AFTER PAO AND points have been inserted into DB.
        dbChangeManager.processPaoDbChange(pao.getPaoIdentifier(), DbChangeType.ADD);
    }
    
    @Override
    public void createPaoWithCustomPoints(CompleteYukonPao pao, PaoType paoType, List<PointBase> points) {
        paoPersistenceDao.createPao(pao, paoType);
        
        // Write the points we need to copy to the DB.
        paoCreationHelper.applyPoints(pao.getPaoIdentifier(), points);
        
        // Send DB change message
        dbChangeManager.processPaoDbChange(pao.getPaoIdentifier(), DbChangeType.ADD);
    }
    
    @Override
    public void updatePao(CompleteYukonPao pao) {
        paoPersistenceDao.updatePao(pao);
        
        dbChangeManager.processPaoDbChange(pao.getPaoIdentifier(), DbChangeType.UPDATE);
    }
    
    @Override
    public void deletePao(PaoIdentifier paoIdentifier) {
        try {
            paoCreationHelper.deletePointsForPao(paoIdentifier.getPaoId());
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        paoPersistenceDao.deletePao(paoIdentifier);
        
        dbChangeManager.processPaoDbChange(paoIdentifier, DbChangeType.DELETE);
    }

    @Override
    public boolean createsNameConflict(PaoType paoType, String name) {
        return paoPersistenceDao.createsNameConflict(paoType, name);
    }
}
