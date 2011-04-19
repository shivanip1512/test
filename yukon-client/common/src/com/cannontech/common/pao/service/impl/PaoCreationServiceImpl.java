package com.cannontech.common.pao.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.NullFields;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Ordering;

public class PaoCreationServiceImpl implements PaoCreationService {

    private PaoDao paoDao;
    private PaoCreationHelper paoCreationHelper;
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
        
        // Loop through providers
        for (PaoCreationTypeProvider<?> paoTypeProvider : providers) {
            callProvider(paoTemplate, paoIdentifier, paoTypeProvider);
        }
        
        // Create and Add points
        paoCreationHelper.addDefaultPointsToPao(paoIdentifier);
        
        // Send DB change message
        paoCreationHelper.processDbChange(paoIdentifier, DbChangeType.ADD);
        
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
    
    @Autowired
    public void setPaoCreationHelper(PaoCreationHelper paoCreationHelper) {
        this.paoCreationHelper = paoCreationHelper;
    }
    
    @Autowired
    public void setPaoCreationTypeProviders(List<PaoCreationTypeProvider<?>> providers) {
        this.providers = ImmutableList.copyOf(Ordering.natural().sortedCopy(providers));
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

}
