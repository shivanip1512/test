package com.cannontech.thirdparty.digi.dao.provider;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.provider.fields.ZigbeeGatewayFields;
import com.cannontech.thirdparty.digi.model.DigiGateway;

public class ZigbeeGatewayProvider implements PaoTypeProvider<ZigbeeGatewayFields> {

	private GatewayDeviceDao gatewayDeviceDao;

	private DigiGateway createZigbeeGateway(PaoIdentifier paoIdentifier, ZigbeeGatewayFields fields) {
		DigiGateway gateway = new DigiGateway();
		
		gateway.setPaoIdentifier(paoIdentifier);
		gateway.setFirmwareVersion(fields.getFirmwareVersion());
		gateway.setMacAddress(fields.getMacAddress());
		
		return gateway;
	}

	@Override
	public PaoProviderTableEnum getSupportedTable() {
		return PaoProviderTableEnum.ZBGATEWAY;
	}

	@Override
	public Class<ZigbeeGatewayFields> getRequiredFields() {
		return ZigbeeGatewayFields.class;
	}

	@Override
	public void handleCreation(PaoIdentifier paoIdentifier, ZigbeeGatewayFields fields) {
		DigiGateway gateway = createZigbeeGateway(paoIdentifier, fields);
		
		gatewayDeviceDao.createZigbeeGateway(gateway);
	}

	@Override
	public void handleUpdate(PaoIdentifier paoIdentifier, ZigbeeGatewayFields fields) {
		DigiGateway gateway = createZigbeeGateway(paoIdentifier, fields);
		
		gatewayDeviceDao.updateZigbeeGateway(gateway);
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
