package com.cannontech.cbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.pojo.CompleteCapBank;
import com.cannontech.common.pao.pojo.CompleteCapControlArea;
import com.cannontech.common.pao.pojo.CompleteCapControlFeeder;
import com.cannontech.common.pao.pojo.CompleteCapControlSpecialArea;
import com.cannontech.common.pao.pojo.CompleteCapControlSubstation;
import com.cannontech.common.pao.pojo.CompleteCapControlSubstationBus;
import com.cannontech.common.pao.pojo.CompleteOneWayCbc;
import com.cannontech.common.pao.pojo.CompleteRegulator;
import com.cannontech.common.pao.pojo.CompleteTwoWayCbc;
import com.cannontech.common.pao.pojo.CompleteYukonPaObject;
import com.cannontech.common.pao.service.PaoPersistenceService;

public class CapControlCreationServiceImpl implements CapControlCreationService {

    private @Autowired PaoPersistenceService paoPersistenceService;
    private @Autowired PaoDefinitionDao paoDefinitionDao;
	
    @Override
    @Transactional
    public PaoIdentifier createCbc(PaoType paoType, String name, boolean disabled, int portId) {
        CompleteYukonPaObject pao;
        
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
        
        return pao.getPaoIdentifier();
    }
    
    @Override
    @Transactional
	public PaoIdentifier createCapControlObject(PaoType paoType, String name, boolean disabled) {
        CompleteYukonPaObject pao;
        switch(paoType) {

            case CAP_CONTROL_SPECIAL_AREA :
                pao = new CompleteCapControlSpecialArea();
                break;
            case CAP_CONTROL_AREA :
                pao = new CompleteCapControlArea();
                break;
            case CAP_CONTROL_SUBSTATION :
                pao = new CompleteCapControlSubstation();
                break;
            case CAP_CONTROL_SUBBUS :
                pao = new CompleteCapControlSubstationBus();
                break;
            case CAP_CONTROL_FEEDER :
                pao = new CompleteCapControlFeeder();
                break;
            case CAPBANK :
                pao = new CompleteCapBank();
                break;
            case LOAD_TAP_CHANGER:
            case GANG_OPERATED:
            case PHASE_OPERATED:
                pao = new CompleteRegulator();
                break;
            default:
                throw new UnsupportedOperationException("Import of " + name + " failed. Unknown Pao Type: " + paoType.getDbString());
        }
        
        pao.setPaoName(name);
        pao.setDisabled(disabled);
        
        paoPersistenceService.createPao(pao, paoType);
        
        return pao.getPaoIdentifier();
    }
}