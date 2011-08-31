package com.cannontech.common.pao.service.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.providers.fields.DeviceFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class YukonDeviceProvider implements PaoTypeProvider<DeviceFields> {

    private CapbankControllerDao capbankControllerDao;

    @Override
    public PaoProviderTableEnum getSupportedTable() {
    	return PaoProviderTableEnum.DEVICE;
    }
    
    @Override
    public Class<DeviceFields> getRequiredFields() {
        return DeviceFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, DeviceFields fields) {
        capbankControllerDao.insertDeviceData(paoIdentifier, fields);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, DeviceFields fields) {
    	capbankControllerDao.updateDeviceData(paoIdentifier, fields);
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
    	capbankControllerDao.deleteControllerData(PaoProviderTableEnum.DEVICE, paoIdentifier);
    }
    
    @Autowired
    public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
		this.capbankControllerDao = capbankControllerDao;
	}
}
