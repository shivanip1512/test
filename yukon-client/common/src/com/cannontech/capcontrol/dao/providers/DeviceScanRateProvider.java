package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceScanRateFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class DeviceScanRateProvider implements PaoTypeProvider<DeviceScanRateFields>{

	private CapbankControllerDao capbankControllerDao;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICESCANRATE;
	}

	@Override
	public Class<DeviceScanRateFields> getRequiredFields() {
		return DeviceScanRateFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceScanRateFields fields) {
		capbankControllerDao.insertScanRateData(paoIdentifier, fields);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceScanRateFields fields) {
		capbankControllerDao.updateScanRateData(paoIdentifier, fields);
	}

	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		capbankControllerDao.deleteControllerData(PaoProviderTableEnum.DEVICESCANRATE, paoIdentifier);
	}
	
	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
}
