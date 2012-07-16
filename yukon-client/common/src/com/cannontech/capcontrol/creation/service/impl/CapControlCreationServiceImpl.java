package com.cannontech.capcontrol.creation.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.service.CapControlCreationService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.CompleteOneWayCbc;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.model.CompleteTwoWayCbc;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;

public class CapControlCreationServiceImpl implements CapControlCreationService {
    private static final Logger log = YukonLogManager.getLogger(CapControlCreationServiceImpl.class);

    private @Autowired PaoPersistenceService paoPersistenceService;
    private @Autowired DeviceConfigurationDao deviceConfigurationDao;
    private @Autowired PaoDefinitionDao paoDefinitionDao;
	
    @Override
    @Transactional
    public PaoIdentifier createCbc(PaoType paoType, String name, boolean disabled, int portId, ConfigurationBase config) {
        CompleteYukonPao pao;
        
        if (paoDefinitionDao.isTagSupported(paoType, PaoTag.ONE_WAY_DEVICE)) {
            pao = new CompleteOneWayCbc();
        } else if (paoDefinitionDao.isTagSupported(paoType, PaoTag.TWO_WAY_DEVICE)) {
            CompleteTwoWayCbc twoWayCbc = new CompleteTwoWayCbc();
            pao = twoWayCbc;
            twoWayCbc.setPortId(portId);
        } else {
            throw new IllegalArgumentException("Import of " + name + " failed. Unknown CBC Type: " + paoType.getDbString());
        }
        
        pao.setDisabled(disabled);
        pao.setPaoName(name);
        
        paoPersistenceService.createPao(pao, paoType);
        
        SimpleDevice device = new SimpleDevice(pao.getPaoIdentifier());
        try {
            deviceConfigurationDao.assignConfigToDevice(config, device);
        } catch (InvalidDeviceTypeException e) {
            log.warn("caught exception in assignConfig", e);
        }
        
        return pao.getPaoIdentifier();
    }
    
    @Override
    @Transactional
	public PaoIdentifier createCapControlObject(PaoType paoType, String name, boolean disabled) {
        CompleteYukonPao pao = null;
        
        try {
            HierarchyPaoCreator creator = HierarchyPaoCreator.valueOf(paoType.name());
            HierarchyImportData data = new HierarchyImportData(paoType, name, ImportAction.ADD);
            data.setDisabled(disabled);
            
            pao = creator.getCompleteYukonPao(data);
        } catch (IllegalArgumentException e) {
            // paoType doesn't have a HierarchyPaoCreator, must be a regulator!
            if (paoType == PaoType.LOAD_TAP_CHANGER || paoType == PaoType.GANG_OPERATED || paoType == PaoType.PHASE_OPERATED) {
                pao = new CompleteRegulator();
                pao.setDisabled(disabled);
                pao.setPaoName(name);
            } else {
                throw new UnsupportedOperationException("Import of " + name + " failed. Unknown " +
                		                                "Pao Type: " + paoType.getDbString());
            }
        }
        
        paoPersistenceService.createPao(pao, paoType);
        
        return pao.getPaoIdentifier();
    }
}