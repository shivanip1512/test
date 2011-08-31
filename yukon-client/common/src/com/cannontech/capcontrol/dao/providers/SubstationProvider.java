package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.dao.providers.fields.SubstationFields;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class SubstationProvider implements PaoTypeProvider<SubstationFields> {

	private SubstationDao substationDao;
	
	private Substation createSubstation(PaoIdentifier paoIdentifier, SubstationFields fields) {
		Substation substation = new Substation(paoIdentifier);
		
		substation.setMapLocationId(fields.getMapLocationId());
		substation.setVoltReductionPointId(fields.getVoltReductionPointId());
		
		return substation;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPCONTROLSUBSTATION;
	};

	@Override
	public Class<SubstationFields> getRequiredFields() {
		return SubstationFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, SubstationFields fields) {
		Substation substation = createSubstation(paoIdentifier, fields);
		
		substationDao.add(substation);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, SubstationFields fields) {
		Substation substation = createSubstation(paoIdentifier, fields);
		
		substationDao.update(substation);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		Substation substation = new Substation(paoIdentifier);
		
		substationDao.remove(substation);
	}
	
	@Autowired
	public void setSubstationDao(SubstationDao substationDao) {
		this.substationDao = substationDao;
	}
}
