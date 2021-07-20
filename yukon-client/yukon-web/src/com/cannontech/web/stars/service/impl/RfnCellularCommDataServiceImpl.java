package com.cannontech.web.stars.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.gateway.model.CellularDeviceCommData;
import com.cannontech.web.stars.service.RfnCellularCommDataService;

public class RfnCellularCommDataServiceImpl implements RfnCellularCommDataService{

    private static final Logger log = YukonLogManager.getLogger(RfnCellularCommDataServiceImpl.class);

    @Autowired private AttributeService attributeService;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private RfnDeviceDao rfnDeviceDao;

    public List<CellularDeviceCommData> getCellularDeviceCommDataForGateways(List<Integer> gatewayIds) {
        // Select all the Cellular devices in DynamicRfnDeviceData
        List<RfnDevice> cellDevices = rfnDeviceDao.getDevicesForGateways(gatewayIds, PaoType.getCellularTypes());
        // Turn the list of RfnDevices into CellularDeviceCommData objects
        List<CellularDeviceCommData> cellDeviceCommData = cellDevices.stream()
                                                              .map(this::buildCellularDeviceCommDataObject)
                                                              .filter(Objects::nonNull)
                                                              .collect(Collectors.toList());

        return cellDeviceCommData;
    }

    public CellularDeviceCommData buildCellularDeviceCommDataObject(RfnDevice rfnDevice) {
        PaoIdentifier paoIdentifier = rfnDevice.getPaoIdentifier();
        LitePoint commStatusPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.COMM_STATUS);
        LitePoint rssiPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR);
        LitePoint rsrpPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.REFERENCE_SIGNAL_RECEIVED_POWER);
        LitePoint rsrqPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.REFERENCE_SIGNAL_RECEIVED_QUALITY);
        LitePoint sinrPoint = attributeService.findPointForAttribute(paoIdentifier, BuiltInAttribute.SIGNAL_TO_INTERFERENCE_PLUS_NOISE_RATIO);

        CellularDeviceCommData cellDeviceCommData = new CellularDeviceCommData(rfnDevice, commStatusPoint, rssiPoint, 
                                                                               rsrpPoint, rsrqPoint, sinrPoint);
        log.debug("Created CellularDeviceCommData object for {}", cellDeviceCommData);

        return cellDeviceCommData;
    }

    public void refreshCellularDeviceConnection(List<PaoIdentifier> paoIdentifiers, LiteYukonUser user) {

        paoIdentifiers.stream()
                      .filter(pao -> pao.getPaoType().isCellularDevice())
                      .forEach(pao -> initiateCellularDeviceStatusRefresh(pao, user));
    }

    private void initiateCellularDeviceStatusRefresh(PaoIdentifier paoIdentifier, LiteYukonUser user) {
        //TODO: Verify what the new command will be
        CommandRequestDevice request = new CommandRequestDevice("getstatus cell", new SimpleDevice(paoIdentifier));
        CommandResultHolder result = commandExecutionService.execute(request,
                DeviceRequestType.CELLULAR_CONNECTION_STATUS_REFRESH, user);
        log.debug("Cellular device connection refresh result: {}", result);
    }

}
