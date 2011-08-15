package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.providers.BaseCreationProvider;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeEndpointFields;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;

public class ZigbeeEndpointCreationProvider extends BaseCreationProvider<ZigbeeEndpointFields> {

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
    public Class<ZigbeeEndpointFields> getRequiredFields() {
        return ZigbeeEndpointFields.class;
    }
    
    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, ZigbeeEndpointFields fields) {
        ZigbeeEndpoint endpoint = new ZigbeeEndpoint();
        
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