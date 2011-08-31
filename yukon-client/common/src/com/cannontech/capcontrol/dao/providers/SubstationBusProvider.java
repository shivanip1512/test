package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.providers.fields.SubstationBusFields;
import com.cannontech.capcontrol.model.SubstationBus;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class SubstationBusProvider implements PaoTypeProvider<SubstationBusFields> {

	private SubstationBusDao substationBusDao;
	
	private SubstationBus createSubstationBus(PaoIdentifier paoIdentifier, SubstationBusFields fields) {
		SubstationBus substationBus = new SubstationBus(paoIdentifier);
		
		substationBus.setCurrentVarLoadPointId(fields.getCurrentVarLoadPointId());
		substationBus.setCurrentVoltLoadPointId(fields.getCurrentVoltLoadPointId());
		substationBus.setCurrentWattLoadPointId(fields.getCurrentWattLoadPointId());
		substationBus.setMapLocationId(fields.getMapLocationId());
		substationBus.setAltSubId(fields.getAltSubId());
		substationBus.setSwitchPointId(fields.getSwitchPointId());
		substationBus.setDualBusEnabled(fields.getDualBusEnabled());
		substationBus.setMultiMonitorControl(fields.getMultiMonitorControl());
		substationBus.setUsephasedata(fields.getUsePhaseData());
		substationBus.setPhaseb(fields.getPhaseB());
		substationBus.setPhasec(fields.getPhaseC());
		substationBus.setControlFlag(fields.getControlFlag());
		substationBus.setVoltReductionPointId(fields.getVoltReductionPointId());
		substationBus.setDisabledPointId(fields.getDisableBusPointId());
	
		return substationBus;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPCONTROLSUBSTATIONBUS;
	};

	@Override
	public Class<SubstationBusFields> getRequiredFields() {
		return SubstationBusFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, SubstationBusFields fields) {
		SubstationBus substationBus = createSubstationBus(paoIdentifier, fields);
		
		substationBusDao.add(substationBus);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, SubstationBusFields fields) {
		SubstationBus substationBus = createSubstationBus(paoIdentifier, fields);
		
		substationBusDao.update(substationBus);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		SubstationBus substationBus = new SubstationBus(paoIdentifier);
		
		substationBusDao.remove(substationBus);
	}

	@Autowired
	public void setSubstationBusDao(SubstationBusDao substationBusDao) {
		this.substationBusDao = substationBusDao;
	}
}
