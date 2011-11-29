package com.cannontech.cbc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.dao.providers.fields.AreaFields;
import com.cannontech.capcontrol.dao.providers.fields.CapBankFields;
import com.cannontech.capcontrol.dao.providers.fields.CapbankAdditionalFields;
import com.cannontech.capcontrol.dao.providers.fields.FeederFields;
import com.cannontech.capcontrol.dao.providers.fields.SpecialAreaFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationBusFields;
import com.cannontech.capcontrol.dao.providers.fields.SubstationFields;
import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.cbc.service.CapControlCreationService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.PaoCreationService;
import com.cannontech.common.pao.service.PaoTemplate;
import com.cannontech.common.pao.service.PaoTemplatePart;
import com.cannontech.common.pao.service.providers.fields.DeviceAddressFields;
import com.cannontech.common.pao.service.providers.fields.DeviceCbcFields;
import com.cannontech.common.pao.service.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.common.pao.service.providers.fields.DeviceFields;
import com.cannontech.common.pao.service.providers.fields.DeviceScanRateFields;
import com.cannontech.common.pao.service.providers.fields.DeviceWindowFields;
import com.cannontech.common.pao.service.providers.fields.YukonPaObjectFields;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

public class CapControlCreationServiceImpl implements CapControlCreationService {

    private PaoCreationService paoCreationService;
	
    @Override
    @Transactional
    public PaoIdentifier createCbc(PaoType paoType, String name, boolean disabled, int portId) {
    	ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
    	
		// Create and set-up the YukonPaObjectFields
		paoFields.put(YukonPaObjectFields.class, new YukonPaObjectFields(name));
		paoFields.put(DeviceFields.class, new DeviceFields());
		
		// Set up the fields based on the CBC type.
		switch (paoType) {
			case CBC_7020:
			case CBC_7022:
			case CBC_7023:
			case CBC_7024:
			case CBC_8020:
			case CBC_8024:
			case CBC_DNP:
			case DNP_CBC_6510:
				paoFields.put(DeviceScanRateFields.class, new DeviceScanRateFields());
				paoFields.put(DeviceWindowFields.class, new DeviceWindowFields());
				paoFields.put(DeviceDirectCommSettingsFields.class, new DeviceDirectCommSettingsFields(portId));
				
				if (paoType != PaoType.DNP_CBC_6510) {
					paoFields.put(DeviceCbcFields.class, new DeviceCbcFields());
				}
				
				paoFields.put(DeviceAddressFields.class, new DeviceAddressFields());
				
				break;

			case CBC_7010:
			case CBC_7011:
			case CBC_7012:
			case CBC_EXPRESSCOM:
			case CBC_FP_2800:
			case CAPBANKCONTROLLER:
				paoFields.put(DeviceCbcFields.class, new DeviceCbcFields());
				
				break;
				
			default:
				throw new IllegalArgumentException("Import of " + name + " failed. Unknown CBC Type: " + paoType.getDbString());
		}
		
        PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoCreationService.createPao(paoTemplate);
    }
    
    @Override
	public PaoIdentifier createCapControlObject(PaoType paoType, String name) {
        PaoIdentifier pao = null;
        switch(paoType) {

            case CAP_CONTROL_SPECIAL_AREA :
            case CAP_CONTROL_AREA :
            case CAP_CONTROL_SUBSTATION :
            case CAP_CONTROL_SUBBUS :
            case CAP_CONTROL_FEEDER :
            case CAPBANK :
            	pao = createHierarchyObject(paoType, name);
                break;
                
            case LOAD_TAP_CHANGER:
            case GANG_OPERATED:
            case PHASE_OPERATED:
                pao = createRegulator(paoType, name);
                break;
                
            default:
                throw new UnsupportedOperationException("Import of " + name + " failed. Unknown Pao Type: " + paoType.getDbString());
        }
        
        return pao;
    }
	
	private PaoIdentifier createHierarchyObject(PaoType paoType, String name) {
		ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
		
		// Create and set-up the YukonPaObjectFields
		paoFields.put(YukonPaObjectFields.class, new YukonPaObjectFields(name));
		
		switch (paoType) {
		case CAP_CONTROL_AREA:
			paoFields.put(AreaFields.class, new AreaFields());
			break;
		case CAP_CONTROL_SPECIAL_AREA:
			paoFields.put(SpecialAreaFields.class, new SpecialAreaFields());
			break;
		case CAP_CONTROL_SUBSTATION:
			paoFields.put(SubstationFields.class, new SubstationFields());
			break;
		case CAP_CONTROL_SUBBUS:
			paoFields.put(SubstationBusFields.class, new SubstationBusFields());
			break;
		case CAP_CONTROL_FEEDER:
			paoFields.put(FeederFields.class, new FeederFields());
			break;
		case CAPBANK:
			paoFields.put(CapBankFields.class, new CapBankFields());
			paoFields.put(DeviceFields.class, new DeviceFields());
			paoFields.put(CapbankAdditionalFields.class, new CapbankAdditionalFields());
			break;	
		default:
			throw new IllegalArgumentException("Import of hierarchy object " + name + " failed. Unknown type: " + paoType.getDbString());
		}
		
        PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoCreationService.createPao(paoTemplate);
	}
	
    @Transactional
	private PaoIdentifier createRegulator(PaoType paoType, String name) {
		YukonPaObjectFields yukonPaObjectFields = new YukonPaObjectFields(name);
	    VoltageRegulatorFields voltageRegulatorFields = new VoltageRegulatorFields(0,0);

	    ClassToInstanceMap<PaoTemplatePart> paoFields = MutableClassToInstanceMap.create();
	    paoFields.put(YukonPaObjectFields.class, yukonPaObjectFields);
	    paoFields.put(VoltageRegulatorFields.class, voltageRegulatorFields);

	    PaoTemplate paoTemplate = new PaoTemplate(paoType, paoFields);
        
        return paoCreationService.createPao(paoTemplate);
    }
	
	@Autowired
	public void setPaoCreationService(PaoCreationService paoCreationService) {
        this.paoCreationService = paoCreationService;
    }
}
