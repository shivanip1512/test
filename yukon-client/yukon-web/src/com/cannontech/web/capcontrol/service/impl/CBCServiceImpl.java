package com.cannontech.web.capcontrol.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.pao.YukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.capcontrol.service.CBCService;
import com.cannontech.web.editor.CapControlCBC;

@Service
public class CBCServiceImpl implements CBCService {
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoPropertyDao propertyDao;
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private CapbankControllerDao capbankControllerDao;
    private transient Logger logger = YukonLogManager.getLogger(RemoteBase.class);

    @Override
    public CapControlCBC getCapControlCBC(int id) {

        CapControlCBC capControlCBC = new CapControlCBC();
        YukonPAObject yukonPAObject = capbankControllerDao.getForId(id);
        capControlCBC.setYukonPAObject(yukonPAObject);
        capControlCBC.setDisableFlag((yukonPAObject.getDisableFlag() == 'N') ? false : true);
        YukonPAObject parentCBC = capbankControllerDao.getParentCBC(id);
        capControlCBC.setCbcParent(parentCBC.getPaoName());
        capControlCBC.setDevice(deviceDao.getDevice(id));
        PaoType paoType = yukonPAObject.getPaoType();
        // Start
       
        boolean isCBCTwoWay = DeviceTypesFuncs.isCBCTwoWay(paoType);
        capControlCBC.setTwoWay(isCBCTwoWay);
        if (isCBCTwoWay) {

            // capControlCBC.setEditingController(true);
            capControlCBC.setDeviceWindow(deviceDao.getDeviceWindow(id));

            capControlCBC.setDeviceScanRateMap(getScanRates(id));

            capControlCBC.setDeviceDialupSettings(deviceDao.getDeviceDialupSettings(id));

            DeviceDirectCommSettings deviceDirectCommSettings = deviceDao.getDeviceDirectCommSettings(id);
            capControlCBC.setDeviceDirectCommSettings(deviceDirectCommSettings);
            int portId = deviceDirectCommSettings.getPortID();

            if (paoType.isTcpPortEligible() && DeviceTypesFuncs.isTcpPort(portId)) {
                try {
                    PaoProperty ipProperty = propertyDao.getByIdAndName(id, PaoPropertyName.TcpIpAddress);
                    capControlCBC.setIpAddress(ipProperty.getPropertyValue());
                } catch (EmptyResultDataAccessException e) {
                    logger.error(yukonPAObject.getPaoName() + " is missing TCP IP Address property.");
                }

                try {
                    PaoProperty portProperty = propertyDao.getByIdAndName(id, PaoPropertyName.TcpPort);
                    capControlCBC.setPort(portProperty.getPropertyValue());
                } catch (EmptyResultDataAccessException e) {
                    logger.error(yukonPAObject.getPaoName() + " is missing TCP Port property.");
                }
            }

            capControlCBC.setDeviceAddress(deviceDao.getDeviceAddress(id));

            YukonDevice device = new SimpleDevice(id, paoType);

            LightDeviceConfiguration configuration = configurationDao.findConfigurationForDevice(device);
            if (configuration != null) {
                DeviceConfiguration deviceConfiguration =
                    configurationDao.getDeviceConfiguration(configuration.getConfigurationId());
                capControlCBC.setDnpConfiguration(configurationDao.getDnpConfiguration(deviceConfiguration));
            }
        }
        // End
        capControlCBC.setDeviceCBC(deviceDao.getDeviceCBC(id));

        return capControlCBC;
    }

    private Map<String, DeviceScanRate> getScanRates(int deviceId) {
        Map<String, DeviceScanRate> deviceScanRateMap = new HashMap<String, DeviceScanRate>();
        List<DeviceScanRate> deviceScanRates = deviceDao.getDeviceScanRates(deviceId);
        for (DeviceScanRate scanRate : deviceScanRates) {
            deviceScanRateMap.put(scanRate.getScanType(), scanRate);
        }
        return deviceScanRateMap;
    }

    public void checkForErrors(CapControlCBC capControlCBC, int cbcId) throws PortDoesntExistException,
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException, SQLException,
            SerialNumberExistsException {

        // error handling when serial number exists
        handleSerialNumber(capControlCBC, cbcId);

        /*
         * A. Show an error and don't save the update if the user tries to put
         * the same master/slave address combination for a different device on the same communication port
         * B. Show a warning if the user uses the same master/slave address for a
         * device on a different communication port
         */
        if (DeviceTypesFuncs.isCBCTwoWay(capControlCBC.getYukonPAObject().getPaoType())) {
            DeviceAddress currentDeviceAddress;
            Integer commPortId;

            currentDeviceAddress = capControlCBC.getDeviceAddress();
            commPortId = capControlCBC.getDeviceDirectCommSettings().getPortID();

            List<Integer> devicesWithSameAddress =
                deviceDao.getDevicesByDeviceAddress(currentDeviceAddress.getMasterAddress(),
                    currentDeviceAddress.getSlaveAddress());
            List<Integer> devicesByPort = deviceDao.getDevicesByPort(commPortId.intValue());
            // remove the current device from the list
            // devicesByPort.remove(capControlCBC.getYukonPAObject().getPaObjectID());
            devicesWithSameAddress.remove(new Integer(cbcId));

            if (commPortId.intValue() <= 0) {
                throw new PortDoesntExistException();
            }

            // check to see if the master slave combination is the same
            if (devicesWithSameAddress.size() > 0) {
                for (int i = 0; i < devicesWithSameAddress.size(); i++) {
                    Integer paoId = devicesWithSameAddress.get(i);
                    if (devicesByPort.contains(paoId)) {
                        LiteYukonPAObject litePAO =
                            YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(paoId.intValue());
                        throw new MultipleDevicesOnPortException(litePAO.getPaoName());
                    }
                }
                LiteYukonPAObject litePAO =
                    YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(devicesWithSameAddress.get(0).intValue());
                throw new SameMasterSlaveCombinationException(litePAO.getPaoName());
            }
            deviceDao.saveDeviceDirectCommSettings(commPortId, cbcId);
            if (capControlCBC.getDeviceScanRateMap().containsKey("Exception"))
                deviceDao.saveDeviceScanRates(capControlCBC.getDeviceScanRateMap().get("Exception"), cbcId,
                    DeviceScanRate.TYPE_EXCEPTION);
            if (capControlCBC.getDeviceScanRateMap().containsKey("Integrity"))
                deviceDao.saveDeviceScanRates(capControlCBC.getDeviceScanRateMap().get("Integrity"), cbcId,
                    DeviceScanRate.TYPE_INTEGRITY);
        }

    }

    private void handleSerialNumber(CapControlCBC capControlCBC, int cbcId) throws SQLException,
            SerialNumberExistsException {
        String[] paos = null;
        // find out if the serial number is unique
        if (capControlCBC != null) {

            paos = DeviceCBC.isSerialNumberUnique(capControlCBC.getDeviceCBC().getSerialNumber(), cbcId);
            // if serial was unique then paos would be empty
            // throw an exception to the calling class to indicate
            if (paos != null && paos.length > 0) {
                String paosWithSameSerialNumber = "";
                for (int i = 0; i < paos.length; i++) {
                    if (i == paos.length - 1) {
                        paosWithSameSerialNumber += paos[i] + ".";
                    } else {
                        paosWithSameSerialNumber += paos[i] + ", ";
                    }
                }
                throw new SerialNumberExistsException(paosWithSameSerialNumber);
            }
            // if got to the point then we can set the serial number because it is unique
            deviceDao.saveDeviceCBC(capControlCBC.getDeviceCBC(), cbcId);
        }
    }

    @Override
    public int save(CapControlCBC capControlCBC) throws SerialNumberExistsException, SQLException,
            PortDoesntExistException, MultipleDevicesOnPortException, SameMasterSlaveCombinationException {
        int cbcId = capControlCBC.getYukonPAObject().getPaObjectID();
        boolean isDisabled=capControlCBC.isDisableFlag();
        checkForErrors(capControlCBC, capControlCBC.getYukonPAObject().getPaObjectID());
       
        deviceDao.saveYukonPao(capControlCBC.getYukonPAObject().getPaoName(),
            isDisabled==true?"Y":"N", cbcId);

        if (DeviceTypesFuncs.isCBCTwoWay(capControlCBC.getYukonPAObject().getPaoType())) {
            deviceDao.saveDeviceAddress(capControlCBC.getDeviceAddress(), cbcId);
        } else {

        }
        return capControlCBC.getYukonPAObject().getPaObjectID();
    }
}
