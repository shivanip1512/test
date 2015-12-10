package com.cannontech.web.capcontrol.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.model.CompleteCbcBase;
import com.cannontech.common.pao.model.CompleteOneWayCbc;
import com.cannontech.common.pao.model.CompleteTwoWayCbc;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.web.capcontrol.service.CbcService;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.util.CBCCopyUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class CbcServiceImpl implements CbcService {

    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoPropertyDao propertyDao;
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private DeviceConfigurationService deviceConfigService;
    @Autowired private CapbankDao capbankDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    
    private static final Logger log = YukonLogManager.getLogger(CbcServiceImpl.class);

    @Override
    public CapControlCBC getCbc(int id) {

        CapControlCBC cbc = new CapControlCBC();
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
        DeviceBase deviceBase = (DeviceBase) dbPersistent;

        cbc.setName(deviceBase.getPAOName());
        cbc.setId(deviceBase.getPAObjectID());
        cbc.setPaoType(deviceBase.getPaoType());
        PaoIdentifier capbank = capbankDao.findCapBankByCbc(id);

        if (capbank == null) {
            cbc.setParent(null);
        } else {
            LiteYukonPAObject parent = dbCache.getAllPaosMap().get(capbank.getPaoId());
            cbc.setParent(parent);
        }

        cbc.setDevice(deviceBase.getDevice());
        PaoType paoType = deviceBase.getPaoType();
        // Start

        if (dbPersistent instanceof TwoWayDevice) {
            DNPBase dnpBase = (DNPBase) dbPersistent;

            cbc.setDeviceWindow(dnpBase.getDeviceWindow());

            cbc.setDeviceScanRateMap(dnpBase.getDeviceScanRateMap());

            cbc.setDeviceDialupSettings(dnpBase.getDeviceDialupSettings());

            cbc.setDeviceDirectCommSettings(dnpBase.getDeviceDirectCommSettings());
            int portId = dnpBase.getDeviceDirectCommSettings().getPortID();

            if (paoType.isTcpPortEligible() && DeviceTypesFuncs.isTcpPort(portId)) {
                try {
                    PaoProperty ipProperty = propertyDao.getByIdAndName(id, PaoPropertyName.TcpIpAddress);
                    cbc.setIpAddress(ipProperty.getPropertyValue());
                } catch (EmptyResultDataAccessException e) {
                    log.error(dnpBase.getPAOName() + " is missing TCP IP Address property.");
                }

                try {
                    PaoProperty portProperty = propertyDao.getByIdAndName(id, PaoPropertyName.TcpPort);
                    cbc.setPort(portProperty.getPropertyValue());
                } catch (EmptyResultDataAccessException e) {
                    log.error(dnpBase.getPAOName() + " is missing TCP Port property.");
                }
            }

            cbc.setDeviceAddress(dnpBase.getDeviceAddress());

            YukonDevice device = new SimpleDevice(id, paoType);

            LightDeviceConfiguration liteConfig = configurationDao.findConfigurationForDevice(device);
            int configId;
            if (liteConfig != null) {
                configId = liteConfig.getConfigurationId();
            } else {
                DeviceConfiguration configuration = configurationDao.getDefaultDNPConfiguration();
                configId = configuration.getConfigurationId();
            }
            cbc.setDnpConfigId(configId);
        }

        if (dbPersistent instanceof CapBankController) {
            CapBankController capBankController = (CapBankController) dbPersistent;
            cbc.setDeviceCBC(capBankController.getDeviceCBC());
        } else if (dbPersistent instanceof CapBankController702x) {
            CapBankController702x capBankController702x = (CapBankController702x) dbPersistent;
            cbc.setDeviceCBC(capBankController702x.getDeviceCBC());
        } else if (dbPersistent instanceof CapBankControllerDNP) {
            CapBankControllerDNP capBankControllerDNP = (CapBankControllerDNP) dbPersistent;
            cbc.setDeviceCBC(capBankControllerDNP.getDeviceCBC());
        }

        return cbc;
    }

    @Override
    public DNPConfiguration getDnpConfigForDevice(CapControlCBC cbc) {
        DeviceConfiguration configuration = configurationDao.getDeviceConfiguration(cbc.getDnpConfigId());
        DNPConfiguration dnpConfig = configurationDao.getDnpConfiguration(configuration);
        return dnpConfig;
    }

    private void checkForErrorsAndSave(CapControlCBC cbc) throws PortDoesntExistException,
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException, SerialNumberExistsException {

        int id = cbc.getId();

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);

        // error handling when serial number exists
        handleSerialNumber(cbc, id);

        if (dbPersistent instanceof TwoWayDevice) {
            /*
             * A. Show an error and don't save the update if the user tries to put
             * the same master/slave address combination for a different device on the same communication port
             * B. Show a warning if the user uses the same master/slave address for a
             * device on a different communication port
             */
            DeviceAddress currentDeviceAddress;
            Integer commPortId;

            currentDeviceAddress = cbc.getDeviceAddress();
            commPortId = cbc.getDeviceDirectCommSettings().getPortID();

            List<Integer> devicesWithSameAddress =
                deviceDao.getDevicesByDeviceAddress(currentDeviceAddress.getMasterAddress(),
                    currentDeviceAddress.getSlaveAddress());
            List<Integer> devicesByPort = deviceDao.getDevicesByPort(commPortId.intValue());
            // remove the current device from the list
            devicesWithSameAddress.remove(new Integer(id));

            if (commPortId <= 0) {
                throw new PortDoesntExistException();
            }

            // check to see if the master slave combination is the same
            if (devicesWithSameAddress.size() > 0) {
                for (int i = 0; i < devicesWithSameAddress.size(); i++) {
                    Integer paoId = devicesWithSameAddress.get(i);
                    if (devicesByPort.contains(paoId)) {
                        LiteYukonPAObject litePAO = dbCache.getAllPaosMap().get(paoId);
                        throw new MultipleDevicesOnPortException(litePAO.getPaoName());
                    }
                }
                LiteYukonPAObject litePAO = dbCache.getAllPaosMap().get(devicesWithSameAddress.get(0));
                throw new SameMasterSlaveCombinationException(litePAO.getPaoName());
            }

            DNPBase dnpBase = (DNPBase) dbPersistent;

            dnpBase.getDeviceAddress().setMasterAddress(cbc.getDeviceAddress().getMasterAddress());
            dnpBase.getDeviceAddress().setSlaveAddress(cbc.getDeviceAddress().getSlaveAddress());
            dnpBase.getDeviceAddress().setPostCommWait(cbc.getDeviceAddress().getPostCommWait());
            dnpBase.getDeviceDirectCommSettings().setPortID(cbc.getDeviceDirectCommSettings().getPortID());

            if (cbc.getDeviceScanRateMap().containsKey("Exception")) {
                if (dnpBase.getDeviceScanRateMap().get("Exception") == null)
                    dnpBase.getDeviceScanRateMap().put("Exception", new DeviceScanRate(id, "Exception"));
                dnpBase.getDeviceScanRateMap().get("Exception").setAlternateRate(
                    cbc.getDeviceScanRateMap().get("Exception").getAlternateRate());
                dnpBase.getDeviceScanRateMap().get("Exception").setScanGroup(
                    (cbc.getDeviceScanRateMap().get("Exception").getScanGroup()));
                dnpBase.getDeviceScanRateMap().get("Exception").setIntervalRate(
                    (cbc.getDeviceScanRateMap().get("Exception").getIntervalRate()));
            }
            if (cbc.getDeviceScanRateMap().containsKey("Integrity")) {
                if (dnpBase.getDeviceScanRateMap().get("Integrity") == null)
                    dnpBase.getDeviceScanRateMap().put("Integrity", new DeviceScanRate(id, "Integrity"));
                dnpBase.getDeviceScanRateMap().get("Integrity").setAlternateRate(
                    cbc.getDeviceScanRateMap().get("Integrity").getAlternateRate());
                dnpBase.getDeviceScanRateMap().get("Integrity").setScanGroup(
                    (cbc.getDeviceScanRateMap().get("Integrity").getScanGroup()));
                dnpBase.getDeviceScanRateMap().get("Integrity").setIntervalRate(
                    (cbc.getDeviceScanRateMap().get("Integrity").getIntervalRate()));
            }

        }
        if (dbPersistent instanceof CapBankController) {
            CapBankController capBankController = (CapBankController) dbPersistent;
            capBankController.setPAOName(cbc.getName());
            capBankController.setDisableFlag(cbc.isDisableFlag() ? 'Y' : 'N');
            capBankController.getDeviceCBC().setSerialNumber(cbc.getDeviceCBC().getSerialNumber());
            capBankController.getDeviceCBC().setSerialNumber(cbc.getDeviceCBC().getSerialNumber());
            capBankController.getDeviceCBC().setRouteID(cbc.getDeviceCBC().getRouteID());
        } else if (dbPersistent instanceof CapBankController702x) {
            CapBankController702x capBankController702x = (CapBankController702x) dbPersistent;
            capBankController702x.setDisableFlag(cbc.isDisableFlag() ? 'Y' : 'N');
            capBankController702x.setPAOName(cbc.getName());
            capBankController702x.getDeviceCBC().setSerialNumber(cbc.getDeviceCBC().getSerialNumber());
            capBankController702x.getDeviceCBC().setRouteID(cbc.getDeviceCBC().getRouteID());
        } else if (dbPersistent instanceof CapBankControllerDNP) {
            CapBankControllerDNP capBankControllerDNP = (CapBankControllerDNP) dbPersistent;
            capBankControllerDNP.setDisableFlag(cbc.isDisableFlag() ? 'Y' : 'N');
            capBankControllerDNP.setPAOName(cbc.getName());
            capBankControllerDNP.getDeviceCBC().setSerialNumber(cbc.getDeviceCBC().getSerialNumber());
            capBankControllerDNP.getDeviceCBC().setRouteID(cbc.getDeviceCBC().getRouteID());
        }
        
        SimpleDevice device = SimpleDevice.of(cbc.getPaoIdentifier());
        LightDeviceConfiguration config = configurationDao.getLiteConfigurationById(cbc.getDnpConfigId());
        
        try {
            deviceConfigService.assignConfigToDevice(config, device);
        } catch (InvalidDeviceTypeException e) {
            /*
             *  This only happens if config is null for a DNP CBC or if we try to assign a
             *  non-DNP configuration to a DNP paoType. We've covered both cases, so this
             *  isn't possible. Let's log it just to be safe.
             */
            log.error("An error occurred attempting to assign a DNP configuration to CBC " +
                      "'" + cbc.getName() + "'. Please assign this device a configuration manually.", e);
        }

        dbPersistentDao.performDBChange(dbPersistent, TransactionType.UPDATE);
    }

    private void handleSerialNumber(CapControlCBC capControlCBC, int cbcId) throws SerialNumberExistsException {
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

        }
    }

    @Override
    public int save(CapControlCBC capControlCBC) throws SerialNumberExistsException, SQLException, PortDoesntExistException,
            MultipleDevicesOnPortException, SameMasterSlaveCombinationException {

        checkForErrorsAndSave(capControlCBC);
        return capControlCBC.getId();
    }


    @Override
    public boolean delete(int id) {

        PaoIdentifier capbank = capbankDao.findCapBankByCbc(id);
        if (capbank != null) {
            return false;
        }

        LiteYukonPAObject lite = dbCache.getAllPaosMap().get(id);

        boolean twoWayDevice = CapControlCBC.isTwoType(lite.getPaoType());

        Class<? extends CompleteCbcBase> clazz = twoWayDevice ? CompleteTwoWayCbc.class : CompleteOneWayCbc.class;

        CompleteCbcBase completeCbc= paoPersistenceService.retreivePao(lite, clazz);

        paoPersistenceService.deletePao(completeCbc);
        return true;

    }

    @Override
    public int copy(int originalId, String newName, boolean copyPoints) {

        LiteYukonPAObject litePao = dbCache.getAllPaosMap().get(originalId);
        DBPersistent original = dbPersistentDao.retrieveDBPersistent(litePao);
        YukonPAObject copy = (YukonPAObject) CBCCopyUtils.copy(original);

        if (!paoDao.isNameAvailable(newName, copy.getPaoType())) {
            throw new IllegalArgumentException("Name is not available");
        }
        copy.setPAOName(newName);
        try {
            dbPersistentDao.performDBChange(copy, TransactionType.INSERT);
        } catch (PersistenceException e) {
            // TODO Auto-generated catch block
        }
        if (copyPoints) {
            CBCCopyUtils.copyAllPointsForPAO(originalId, copy.getPAObjectID());
        }
        return copy.getPAObjectID();
    }
}
