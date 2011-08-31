package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeEndpointFields;
import com.cannontech.thirdparty.model.ZigbeeEndpoint;

public class ZigbeeEndpointProvider implements PaoTypeProvider<ZigbeeEndpointFields> {

    private ZigbeeDeviceDao zigbeeDeviceDao;

    private ZigbeeEndpoint createZigbeeEndpoint(PaoIdentifier paoIdentifier, ZigbeeEndpointFields fields) {
    	ZigbeeEndpoint endpoint = new ZigbeeEndpoint();
        
        endpoint.setPaoIdentifier(paoIdentifier);
        endpoint.setMacAddress(fields.getMacAddress());
        endpoint.setInstallCode(fields.getInstallCode());
        endpoint.setDestinationEndPointId(fields.getEndPointId());
        endpoint.setNodeId(fields.getNodeId());
        
        return endpoint;
    }
    
    @Override
    public Class<ZigbeeEndpointFields> getRequiredFields() {
        return ZigbeeEndpointFields.class;
    }
    
	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.ZBENDPOINT;
	}
    
    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, ZigbeeEndpointFields fields) {
        ZigbeeEndpoint endpoint = createZigbeeEndpoint(paoIdentifier, fields);
        
        zigbeeDeviceDao.createZigbeeEndPoint(endpoint);
    }

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, ZigbeeEndpointFields fields) {
		ZigbeeEndpoint endpoint = createZigbeeEndpoint(paoIdentifier, fields);
		
		zigbeeDeviceDao.updateZigbeeEndPoint(endpoint);
	}

	@Override
	public void handleDeletion(PaoIdentifier paoIdentifier) {
		zigbeeDeviceDao.deleteZigbeeEndPoint(paoIdentifier.getPaoId());
	}

    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
}