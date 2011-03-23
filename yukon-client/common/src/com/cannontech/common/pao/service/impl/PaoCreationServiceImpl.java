package com.cannontech.common.pao.service.impl;

import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.NullFields;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Ordering;

public class PaoCreationServiceImpl implements PaoCreationService {

    private PaoDao paoDao;
    private PaoDefinitionService paoDefinitionService;
    private PointDao pointDao;
    private DBPersistentDao dbPersistentDao;
    private ImmutableList<PaoCreationTypeProvider<?>> providers;

    @Override
    public ClassToInstanceMap<PaoTemplatePart> createFieldMap() {
        MutableClassToInstanceMap<PaoTemplatePart> map  = MutableClassToInstanceMap.create();
        
        map.putInstance(NullFields.class, new NullFields());
        
        return map;
    }
    
    @Override
    public PaoIdentifier createPao(PaoTemplate paoTemplate) {
        // get ID, create PaoIdentifier
        int paoId = paoDao.getNextPaoId();
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoTemplate.getPaoType());
        
        // loop through providers
        for (PaoCreationTypeProvider<?> paoTypeProvider : providers) {
            callProvider(paoTemplate, paoIdentifier, paoTypeProvider);
        }
        
        // create points        
        List<PointBase> pointsToCreate = paoDefinitionService.createAllPointsForPao(paoIdentifier);
        applyPoints(paoIdentifier, pointsToCreate);
        
        // send db change messages
        //TODO
        
        return paoIdentifier;
    }

    private <T extends PaoTemplatePart> void callProvider(PaoTemplate paoTemplate, PaoIdentifier paoIdentifier,
                     PaoCreationTypeProvider<T> paoTypeProvider) {
        if (paoTypeProvider.isTypeSupported(paoIdentifier.getPaoType())) {
            if (paoTemplate.getPaoFields().containsKey(paoTypeProvider.getRequiredFields())) {
                T field = paoTemplate.getPaoFields().getInstance(paoTypeProvider.getRequiredFields());
                paoTypeProvider.handleCreation(paoIdentifier, field);
            } else {
                throw new IllegalArgumentException("Missing required information for creating PAO with Type: " 
                                                                   + paoTemplate.getPaoType().getDbString());
            }
        }
    }
    
    private void applyPoints(PaoIdentifier paoIdentifier, List<PointBase> pointsToCreate) {
        
        int paoId = paoIdentifier.getPaoId();
        
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
    public void setPaoCreationTypeProviders(List<PaoCreationTypeProvider<?>> providers) {
        this.providers = ImmutableList.copyOf(Ordering.natural().sortedCopy(providers));
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
