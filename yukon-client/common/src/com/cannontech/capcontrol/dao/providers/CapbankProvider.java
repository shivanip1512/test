package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.providers.fields.CapBankFields;
import com.cannontech.capcontrol.model.Capbank;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class CapbankProvider implements PaoTypeProvider<CapBankFields> {

	private CapbankDao capbankDao;
	
	private Capbank createCapBank(PaoIdentifier paoIdentifier, CapBankFields fields) {
		Capbank capbank = new Capbank(paoIdentifier);
		
		capbank.setMapLocationId(fields.getMapLocationId());
		capbank.setOperationalState(fields.getOperationalState());
		capbank.setControlDeviceId(fields.getControlDeviceId());
		capbank.setControlPointId(fields.getControlPointId());
		capbank.setBankSize(fields.getBankSize());
		capbank.setRecloseDelay(fields.getRecloseDelay());
		capbank.setMaxDailyOps(fields.getMaxDailyOps());
		capbank.setControllerType(fields.getControllerType());
		capbank.setTypeOfSwitch(fields.getTypeOfSwitch());
		capbank.setSwitchManufacturer(fields.getSwitchManufacturer());
		capbank.setMaxOpDisable(fields.getMaxOpDisable());
		
		return capbank;
	}
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.CAPBANK;
	};

	@Override
	public Class<CapBankFields> getRequiredFields() {
		return CapBankFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, CapBankFields fields) {
		Capbank capbank = createCapBank(paoIdentifier, fields);
		
		capbankDao.add(capbank);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, CapBankFields fields) {
		Capbank capbank = createCapBank(paoIdentifier, fields);
		
		capbankDao.update(capbank);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		Capbank bank = new Capbank(paoIdentifier);
		
		capbankDao.remove(bank);
	}
	
	@Autowired
	public void setCapbankDao(CapbankDao capbankDao) {
		this.capbankDao = capbankDao;
	}
}
