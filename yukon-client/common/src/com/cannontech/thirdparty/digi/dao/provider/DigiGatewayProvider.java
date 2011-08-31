package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.DigiGatewayFields;
import com.cannontech.thirdparty.digi.model.DigiGateway;

public class DigiGatewayProvider implements PaoTypeProvider<DigiGatewayFields> {

    private GatewayDeviceDao gatewayDeviceDao;
    
    private DigiGateway createGateway(PaoIdentifier paoIdentifier, DigiGatewayFields fields) {
    	DigiGateway digiGateway = new DigiGateway();
        
        digiGateway.setPaoIdentifier(paoIdentifier);
        digiGateway.setDigiId(fields.getDigiId());
        
        return digiGateway;
    }

    @Override
    public PaoProviderTableEnum getSupportedTable() {
    	return PaoProviderTableEnum.DIGIGATEWAY;
    }

    @Override
    public Class<DigiGatewayFields> getRequiredFields() {
        return DigiGatewayFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, DigiGatewayFields fields) {
        DigiGateway digiGateway = createGateway(paoIdentifier, fields);
        
        gatewayDeviceDao.createDigiGateway(digiGateway);
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, DigiGatewayFields fields) {
    	DigiGateway digiGateway = createGateway(paoIdentifier, fields);
    	
    	gatewayDeviceDao.updateDigiGateway(digiGateway);
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
    	gatewayDeviceDao.deleteDigiGateway(paoIdentifier.getPaoId());
    }

    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
}
