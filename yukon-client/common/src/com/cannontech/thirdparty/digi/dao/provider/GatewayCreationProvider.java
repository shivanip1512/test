package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.providers.BaseCreationProvider;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.GatewayFields;
import com.cannontech.thirdparty.digi.model.DigiGateway;

public class GatewayCreationProvider extends BaseCreationProvider<GatewayFields> {

    GatewayDeviceDao gatewayDeviceDao;

    @Override
    public float getOrder() {
        return 2;
    }

    @Override
    public boolean isTypeSupported(PaoType paoType) {
        return paoType == PaoType.DIGIGATEWAY;
    }

    @Override
    public Class<GatewayFields> getRequiredFields() {
        return GatewayFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, GatewayFields fields) {
        DigiGateway digiGateway = new DigiGateway();
        
        digiGateway.setPaoIdentifier(paoIdentifier);
        digiGateway.setDigiId(fields.getDigiId());
        digiGateway.setFirmwareVersion(fields.getFirmwareVersion());
        digiGateway.setMacAddress(fields.getMacAddress());
        
        gatewayDeviceDao.createDigiGateway(digiGateway);
    }

    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
}
