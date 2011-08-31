package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceAddressFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class DeviceAddressProvider implements PaoTypeProvider<DeviceAddressFields> {

	private CapbankControllerDao capbankControllerDao;
	
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.DEVICEADDRESS;
	}

	@Override
	public Class<DeviceAddressFields> getRequiredFields() {
		return DeviceAddressFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		capbankControllerDao.insertDeviceAddressData(paoIdentifier, fields);
	}
	
	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, DeviceAddressFields fields) {
		capbankControllerDao.updateDeviceAddressData(paoIdentifier, fields);
	}
	
	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		capbankControllerDao.deleteControllerData(PaoProviderTableEnum.DEVICEADDRESS, paoIdentifier);
	}

	@Autowired
	public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
}
