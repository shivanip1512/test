package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceWindowFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class DeviceWindowProvider implements PaoTypeProvider<DeviceWindowFields> {

	private CapbankControllerDao capbankControllerDao;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICEWINDOW;
	}

	@Override
	public Class<DeviceWindowFields> getRequiredFields() {
		return DeviceWindowFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {
		capbankControllerDao.insertDeviceWindowData(paoIdentifier, fields);
	}

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceWindowFields fields) {
		capbankControllerDao.updateDeviceWindowData(paoIdentifier, fields);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		capbankControllerDao.deleteControllerData(PaoProviderTableEnum.DEVICEWINDOW, paoIdentifier);
	}
	
	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
}
