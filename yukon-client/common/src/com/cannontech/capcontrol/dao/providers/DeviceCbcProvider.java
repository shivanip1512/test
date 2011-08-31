package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceCbcFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class DeviceCbcProvider implements PaoTypeProvider<DeviceCbcFields> {

	private CapbankControllerDao capbankControllerDao;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICECBC;
	}

	@Override
	public Class<DeviceCbcFields> getRequiredFields() {
		return DeviceCbcFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier,DeviceCbcFields fields) {
		capbankControllerDao.insertDeviceCbcData(paoIdentifier, fields);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceCbcFields fields) {
		capbankControllerDao.updateDeviceCbcData(paoIdentifier, fields);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		capbankControllerDao.deleteControllerData(PaoProviderTableEnum.DEVICECBC, paoIdentifier);
	}

	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
}
