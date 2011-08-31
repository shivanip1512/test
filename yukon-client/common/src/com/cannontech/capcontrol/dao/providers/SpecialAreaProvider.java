package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.AreaDao;
import com.cannontech.capcontrol.dao.providers.fields.SpecialAreaFields;
import com.cannontech.capcontrol.model.SpecialArea;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class SpecialAreaProvider implements PaoTypeProvider<SpecialAreaFields> {

	private AreaDao areaDao;
	
	private SpecialArea createSpecialArea(PaoIdentifier paoIdentifier, SpecialAreaFields fields) {
		SpecialArea specialArea = new SpecialArea(paoIdentifier);
		
		specialArea.setVoltReductionPointId(fields.getVoltReductionPointId());
		
		return specialArea;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPCONTROLSPECIALAREA;
	};

	@Override
	public Class<SpecialAreaFields> getRequiredFields() {
		return SpecialAreaFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, SpecialAreaFields fields) {
		SpecialArea specialArea = createSpecialArea(paoIdentifier, fields);
	
		areaDao.addSpecialArea(specialArea);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, SpecialAreaFields fields) {
		SpecialArea specialArea = createSpecialArea(paoIdentifier, fields);
		
		areaDao.removeSpecialArea(specialArea);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		SpecialArea specialArea = new SpecialArea(paoIdentifier);
		
		areaDao.removeSpecialArea(specialArea);
	}
	
	@Autowired
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}
}
