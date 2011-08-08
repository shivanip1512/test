package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.providers.BaseCreationProvider;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeEndPointFields;
import com.cannontech.thirdparty.model.ZigbeeEndPoint;

public class ZigbeeEndPointCreationProvider extends BaseCreationProvider<ZigbeeEndPointFields> {

    private ZigbeeDeviceDao zigbeeDeviceDao;
    
    @Override
    public float getOrder() {
        return 2;
    }

    @Override
    public boolean isTypeSupported(PaoType paoType) {
        return paoType == PaoType.ZIGBEE_ENDPOINT;
    }

    @Override
    public Class<ZigbeeEndPointFields> getRequiredFields() {
        return ZigbeeEndPointFields.class;
    }
    
    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, ZigbeeEndPointFields fields) {
        ZigbeeEndPoint endpoint = new ZigbeeEndPoint();
        
        endpoint.setPaoIdentifier(paoIdentifier);
        endpoint.setMacAddress(fields.getMacAddress());
        endpoint.setInstallCode(fields.getInstallCode());
        endpoint.setDestinationEndPointId(fields.getEndPointId());
        endpoint.setNodeId(fields.getNodeId());
        
        zigbeeDeviceDao.createZigbeeEndPoint(endpoint);
    }

    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
}