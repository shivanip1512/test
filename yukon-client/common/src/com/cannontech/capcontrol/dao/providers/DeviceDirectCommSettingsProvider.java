package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceDirectCommSettingsFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class DeviceDirectCommSettingsProvider implements PaoTypeProvider<DeviceDirectCommSettingsFields> {

	private CapbankControllerDao capbankControllerDao;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICEDIRECTCOMMSETTINGS;
	}

	@Override
	public Class<DeviceDirectCommSettingsFields> getRequiredFields() {
		return DeviceDirectCommSettingsFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceDirectCommSettingsFields fields) {
		capbankControllerDao.insertCommSettingsData(paoIdentifier, fields);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceDirectCommSettingsFields fields) {
		capbankControllerDao.updateCommSettingsData(paoIdentifier, fields);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		capbankControllerDao.deleteControllerData(PaoProviderTableEnum.DEVICEDIRECTCOMMSETTINGS, 
												  paoIdentifier);
	}

	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
}
