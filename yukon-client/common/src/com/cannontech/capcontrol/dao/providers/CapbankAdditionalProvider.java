package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.providers.fields.CapbankAdditionalFields;
import com.cannontech.capcontrol.model.CapbankAdditional;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class CapbankAdditionalProvider implements PaoTypeProvider<CapbankAdditionalFields> {

	private CapbankDao capbankDao;
	
	private CapbankAdditional createCapBankAdditional(PaoIdentifier paoIdentifier, CapbankAdditionalFields fields) {
		CapbankAdditional capbankAdditional = new CapbankAdditional(paoIdentifier);
		
		capbankAdditional.setMaintenanceAreaId(fields.getMaintenanceAreaId());
		capbankAdditional.setPoleNumber(fields.getPoleNumber());
		capbankAdditional.setDriveDirections(fields.getDriveDirections());
		capbankAdditional.setLatitude(fields.getLatitude());
		capbankAdditional.setLongitude(fields.getLongitude());
		capbankAdditional.setCapbankConfig(fields.getCapbankConfig());
		capbankAdditional.setCommMedium(fields.getCommMedium());
		capbankAdditional.setCommStrength(fields.getCommStrength());
		capbankAdditional.setExtAntenna(fields.getExtAntenna());
		capbankAdditional.setAntennaType(fields.getAntennaType());
		capbankAdditional.setLastMaintenanceVisit(fields.getLastMaintenanceVisit());
		capbankAdditional.setLastInspection(fields.getLastInspection());
		capbankAdditional.setOpCountResetDate(fields.getOpCountResetDate());
		capbankAdditional.setPotentialTransformer(fields.getPotentialTransformer());
		capbankAdditional.setMaintenanceRequired(fields.getMaintenanceRequired());
		capbankAdditional.setOtherComments(fields.getOtherComments());
		capbankAdditional.setOpTeamComments(fields.getOpTeamComments());
		capbankAdditional.setCbcInstallDate(fields.getCbcInstallDate());
		
		return capbankAdditional;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPBANKADDITIONAL;
	}

	@Override
	public Class<CapbankAdditionalFields> getRequiredFields() {
		return CapbankAdditionalFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, CapbankAdditionalFields fields) {
		CapbankAdditional capbankAdditional = createCapBankAdditional(paoIdentifier, fields);
		
		capbankDao.addCapbankAdditional(capbankAdditional);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, CapbankAdditionalFields fields) {
		CapbankAdditional capbankAdditional = createCapBankAdditional(paoIdentifier, fields);
		
		capbankDao.updateCapbankAdditional(capbankAdditional);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		capbankDao.removeCapbankAdditional(paoIdentifier.getPaoId());
	}
	
	@Autowired
	public void setCapbankDao(CapbankDao capbankDao) {
		this.capbankDao = capbankDao;
	}
}
