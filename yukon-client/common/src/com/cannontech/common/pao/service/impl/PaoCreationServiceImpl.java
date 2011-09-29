package com.cannontech.common.pao.service.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.creation.service.PaoCreationTableParserService;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PaoCreationServiceImpl implements PaoCreationService {

    private PaoDao paoDao;
    private PaoCreationHelper paoCreationHelper;
    private PaoCreationTableParserService paoCreationTableParserService;
    private PaoDefinitionDao paoDefinitionDao;
    
    private Map<PaoProviderTableEnum, PaoTypeProvider<PaoTemplatePart>> providerMap;
    private Map<PaoType, List<PaoTypeProvider<PaoTemplatePart>>> paoTypeProviders;
    
    @PostConstruct
    public void initialize() {
    	paoTypeProviders = Maps.newHashMap();
    	Set<PaoType> creationTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.PAO_CREATION_SERVICE);
    
    	for (PaoType paoType : creationTypes) {
    		List<PaoTypeProvider<PaoTemplatePart>> providers = Lists.newArrayList();
    		List<PaoProviderTableEnum> tables = paoCreationTableParserService.parseTableNames(paoType);
    		
    		// For each table required by the PaoType, get the provider and add it to the 
    		// main "PaoType-to-List of Providers" map.
    		for(PaoProviderTableEnum table : tables) {
    			providers.add(providerMap.get(table));
    		}
    		
    		paoTypeProviders.put(paoType, providers);
    	}
    }
    
    @Override
    public PaoIdentifier createPao(PaoTemplate paoTemplate) {
        PaoIdentifier paoIdentifier = handleProviders(paoTemplate);
        
        // Create and Add points
        paoCreationHelper.addDefaultPointsToPao(paoIdentifier);
        
        // Send DB change message
        paoCreationHelper.processDbChange(paoIdentifier, DbChangeType.ADD);
        
        return paoIdentifier;
    }
    
    @Override
    public PaoIdentifier createTemplatePao(PaoTemplate paoTemplate, List<PointBase> points) {
    	PaoIdentifier paoIdentifier = handleProviders(paoTemplate);
    	
    	// Write the points we need to copy to the DB.
    	paoCreationHelper.applyPoints(paoIdentifier.getPaoId(), points);
    	
    	// Send DB change message
    	paoCreationHelper.processDbChange(paoIdentifier, DbChangeType.ADD);
    	
    	return paoIdentifier;
    }
    
    private PaoIdentifier handleProviders(PaoTemplate paoTemplate) {
    	// get ID, create PaoIdentifier
        int paoId = paoDao.getNextPaoId();
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoTemplate.getPaoType());
        List<PaoTypeProvider<PaoTemplatePart>> providers = paoTypeProviders.get(paoTemplate.getPaoType());
        
        // Loop through providers
        for (PaoTypeProvider<PaoTemplatePart> paoTypeProvider : providers) {
            callProviderCreation(paoTemplate, paoIdentifier, paoTypeProvider);
        }
        
        return paoIdentifier;
    }
    
    @Override
    public void updatePao(int paoId, PaoTemplate paoTemplate) {
    	 List<PaoTypeProvider<PaoTemplatePart>> providers = Lists.newArrayList(paoTypeProviders.get(paoTemplate.getPaoType()));
    	 
    	 for (PaoTypeProvider<PaoTemplatePart> paoTypeProvider : providers) {
    		 callProviderUpdate(paoId, paoTemplate, paoTypeProvider);
    	 }
    	 
    	 PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoTemplate.getPaoType());
    	 
    	 // Send DB change message
         paoCreationHelper.processDbChange(paoIdentifier, DbChangeType.UPDATE);
    }
    
    @Override
    @Transactional
    public void deletePao(PaoIdentifier paoIdentifier) {
        List<PaoTypeProvider<PaoTemplatePart>> providers = Lists.newArrayList(paoTypeProviders.get(paoIdentifier.getPaoType()));
        
        try {
        	paoCreationHelper.deletePointsForPao(paoIdentifier.getPaoId());
        } catch (SQLException e) {
        	CTILogger.error(e.getMessage());
        }

        // Providers are in order for creation, we need them to be in reverse order for deletion.
        Collections.reverse(providers);
    	
        // Loop through providers
        for (PaoTypeProvider<PaoTemplatePart> paoTypeProvider : providers) {
            paoTypeProvider.handleDeletion(paoIdentifier);
        }
        
        // Send DB change message
        paoCreationHelper.processDbChange(paoIdentifier, DbChangeType.DELETE);
    }

    private <T extends PaoTemplatePart> void callProviderCreation(PaoTemplate paoTemplate, PaoIdentifier paoIdentifier,
                     PaoTypeProvider<T> paoTypeProvider) {
    	// loop through list, grabbing provider for each and invoking handle method
        if (paoTemplate.getPaoFields().containsKey(paoTypeProvider.getRequiredFields())) {
            T field = paoTemplate.getPaoFields().getInstance(paoTypeProvider.getRequiredFields());
            paoTypeProvider.handleCreation(paoIdentifier, field);
        } else {
            throw new IllegalArgumentException("Missing required information for creating PAO with Type: " 
                                                               + paoTemplate.getPaoType().getDbString());
        }
    }
    
    private <T extends PaoTemplatePart> void callProviderUpdate(int paoId, PaoTemplate paoTemplate, PaoTypeProvider<T> paoTypeProvider) {
    	// loop through list, grabbing provider for each and invoking handle method
    	PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoTemplate.getPaoType());
    	if (paoTemplate.getPaoFields().containsKey(paoTypeProvider.getRequiredFields())) {
    		T field = paoTemplate.getPaoFields().getInstance(paoTypeProvider.getRequiredFields());
    		paoTypeProvider.handleUpdate(paoIdentifier, field);
    	} else {
    		throw new IllegalArgumentException("Missing required information for creating PAO with Type: " 
                                                      		   + paoTemplate.getPaoType().getDbString());
    	}
}
    
    @Autowired
    public void setPaoCreationHelper(PaoCreationHelper paoCreationHelper) {
        this.paoCreationHelper = paoCreationHelper;
    }
    
    @Autowired
    public void setPaoCreationTypeProviders(List<PaoTypeProvider<PaoTemplatePart>> providers) {
    	Builder<PaoProviderTableEnum, PaoTypeProvider<PaoTemplatePart>> builder = ImmutableMap.builder();
    	for (PaoTypeProvider<PaoTemplatePart> paoCreationTypeProvider : providers) {
			builder.put(paoCreationTypeProvider.getSupportedTable(), paoCreationTypeProvider);
		}
    	
    	providerMap = builder.build();
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
		this.paoDefinitionDao = paoDefinitionDao;
	}
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPaoCreationTableParserService(PaoCreationTableParserService paoCreationTableParserService) {
		this.paoCreationTableParserService = paoCreationTableParserService;
	}

}
