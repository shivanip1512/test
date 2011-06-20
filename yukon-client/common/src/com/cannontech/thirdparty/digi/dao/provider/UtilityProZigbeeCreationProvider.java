package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.providers.BaseCreationProvider;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.UtilityProZigbeeFields;
import com.cannontech.thirdparty.model.ZigbeeThermostat;

public class UtilityProZigbeeCreationProvider extends BaseCreationProvider<UtilityProZigbeeFields> {

    private ZigbeeDeviceDao zigbeeDeviceDao;
    
    @Override
    public float getOrder() {
        return 2;
    }

    @Override
    public boolean isTypeSupported(PaoType paoType) {
        return paoType == PaoType.ZIGBEEUTILPRO;
    }

    @Override
    public Class<UtilityProZigbeeFields> getRequiredFields() {
        return UtilityProZigbeeFields.class;
    }
    
    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, UtilityProZigbeeFields fields) {
        ZigbeeThermostat thermostat = new ZigbeeThermostat();
        
        thermostat.setPaoIdentifier(paoIdentifier);
        thermostat.setMacAddress(fields.getMacAddress());
        thermostat.setInstallCode(fields.getInstallCode());
        thermostat.setNodeId(fields.getNodeId());
        
        zigbeeDeviceDao.createZigbeeUtilPro(thermostat);
    }

    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
}
