package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.providers.fields.FeederFields;
import com.cannontech.capcontrol.model.Feeder;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class FeederProvider implements PaoTypeProvider<FeederFields> {
	
	private FeederDao feederDao;

	private Feeder createFeeder(PaoIdentifier paoIdentifier, FeederFields fields) {
		Feeder feeder = new Feeder(paoIdentifier);
		
		feeder.setCurrentVarLoadPointId(fields.getCurrentVarLoadPointId());
		feeder.setCurrentWattLoadPointId(fields.getCurrentWattLoadPointId());
		feeder.setCurrentVoltLoadPointId(fields.getCurrentVoltPointLoadId());
		feeder.setMapLocationId(fields.getMapLocationId());
		feeder.setMultiMonitorControl(fields.getMultiMonitorControl());
		feeder.setUsePhaseData(fields.getUsePhaseData());
		feeder.setPhaseb(fields.getPhaseB());
		feeder.setPhasec(fields.getPhaseC());
		feeder.setControlFlag(fields.getControlFlag());
		
		return feeder;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPCONTROLFEEDER;
	};

	@Override
	public Class<FeederFields> getRequiredFields() {
		return FeederFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, FeederFields fields) {
		Feeder feeder = createFeeder(paoIdentifier, fields);
		
		feederDao.add(feeder);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, FeederFields fields) {
		Feeder feeder = createFeeder(paoIdentifier, fields);
		
		feederDao.update(feeder);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		Feeder feeder = new Feeder(paoIdentifier);
		
		feederDao.remove(feeder);
	}
	
	@Autowired
	public void setFeederDao(FeederDao feederDao) {
		this.feederDao = feederDao;
	}
}
