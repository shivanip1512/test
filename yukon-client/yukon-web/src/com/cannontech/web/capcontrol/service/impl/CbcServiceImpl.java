package com.cannontech.web.capcontrol.service.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.CapbankDao;
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
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController6510;
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
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.web.capcontrol.service.CbcService;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.util.CBCCopyUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class CbcServiceImpl implements CbcService {

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
        cbc.setDisableFlag(pao.getDisableFlag() == "Y" ? true : false);
        PaoIdentifier capbank = capbankDao.findCapBankByCbc(id);

        if (capbank != null) {
            LiteYukonPAObject parent = dbCache.getAllPaosMap().get(capbank.getPaoId());
            cbc.setParent(parent);
        }

        cbc.setDevice(deviceBase.getDevice());
        PaoType paoType = deviceBase.getPaoType();

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
        } else if (dbPersistent instanceof CapBankController6510) {
            CapBankController6510 capBankController6510 = (CapBankController6510) dbPersistent;
            cbc.setDeviceCBC(capBankController6510.getDeviceCBC());
        } 

        return cbc;
    }

    @Override
    public int save(CapControlCBC cbc) {
        if (cbc.getId() == null) {
            create(cbc);
        }
        int id = cbc.getId();

        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);

        if (dbPersistent instanceof TwoWayDevice) {
            setDnpFields(dbPersistent, cbc);
            assignDNPConfig(cbc);
        }
        if (dbPersistent instanceof DeviceBase) {
            setDeviceFields(dbPersistent, cbc);
        }

        dbPersistentDao.performDBChange(dbPersistent, TransactionType.UPDATE);

        return cbc.getId();
    }

    @Override
    public boolean delete(int id) {

        PaoIdentifier capbank = capbankDao.findCapBankByCbc(id);
        if (capbank != null) {
            return false;
        }

        LiteYukonPAObject lite = dbCache.getAllPaosMap().get(id);

        boolean twoWayDevice = CapControlCBC.getTwoWayTypes().contains(lite.getPaoType());

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

    @Override
    public DNPConfiguration getDnpConfigForDevice(CapControlCBC cbc) {
        DeviceConfiguration configuration = null;
        if (cbc.getDnpConfigId() != null) {
            try {
                configuration = configurationDao.getDeviceConfiguration(cbc.getDnpConfigId());
            } catch (EmptyResultDataAccessException e) {
                //let it remain null and get the default
            }
        }
        if (configuration == null) {
            configuration = configurationDao.getDefaultDNPConfiguration();
        }

        DNPConfiguration dnpConfig = configurationDao.getDnpConfiguration(configuration);
        return dnpConfig;
    }

    private void setDnpFields(DBPersistent dbPersistent, CapControlCBC cbc) {
        Integer id = cbc.getId();
        DNPBase dnpBase = (DNPBase) dbPersistent;

        dnpBase.getDeviceAddress().setMasterAddress(cbc.getDeviceAddress().getMasterAddress());
        dnpBase.getDeviceAddress().setSlaveAddress(cbc.getDeviceAddress().getSlaveAddress());
        dnpBase.getDeviceAddress().setPostCommWait(cbc.getDeviceAddress().getPostCommWait());
        dnpBase.getDeviceDirectCommSettings().setPortID(cbc.getDeviceDirectCommSettings().getPortID());

        dnpBase.setDeviceScanRateMap(new HashMap<String, DeviceScanRate>());
        if (cbc.getDeviceScanRateMap().containsKey("Exception")) {
            DeviceScanRate scanRate = new DeviceScanRate(id, "Exception");
            DeviceScanRate newScanRate = cbc.getDeviceScanRateMap().get("Exception");
            scanRate.setAlternateRate(newScanRate.getAlternateRate());
            scanRate.setScanGroup(newScanRate.getScanGroup());
            scanRate.setIntervalRate(newScanRate.getIntervalRate());
            dnpBase.getDeviceScanRateMap().put("Exception", scanRate);
        }
        if (cbc.getDeviceScanRateMap().containsKey("Integrity")) {
            DeviceScanRate scanRate = new DeviceScanRate(id, "Integrity");
            DeviceScanRate newScanRate = cbc.getDeviceScanRateMap().get("Integrity");
            scanRate.setAlternateRate(newScanRate.getAlternateRate());
            scanRate.setScanGroup(newScanRate.getScanGroup());
            scanRate.setIntervalRate(newScanRate.getIntervalRate());
            dnpBase.getDeviceScanRateMap().put("Integrity", scanRate);
        }
        
        PaoType paoType = cbc.getPaoType();
        int portId = dnpBase.getDeviceDirectCommSettings().getPortID();

        if (paoType.isTcpPortEligible() && DeviceTypesFuncs.isTcpPort(portId)) {
            dnpBase.setIpAddress(cbc.getIpAddress());
            dnpBase.setPort(cbc.getPort());
        }
    }

    private void setDeviceFields(DBPersistent dbPersistent, CapControlCBC cbc) {
        DeviceBase device = (DeviceBase) dbPersistent;
        device.setPAOName(cbc.getName());
        device.setDisableFlag(cbc.isDisableFlag() ? 'Y' : 'N');

        DeviceCBC deviceCbc = null;
        if (dbPersistent instanceof CapBankController) {
            deviceCbc = ((CapBankController) dbPersistent).getDeviceCBC();
        } else if (dbPersistent instanceof CapBankController702x) {
            deviceCbc = ((CapBankController702x) dbPersistent).getDeviceCBC();
        } else if (dbPersistent instanceof CapBankControllerDNP) {
            deviceCbc = ((CapBankControllerDNP) dbPersistent).getDeviceCBC();
        }

        if (deviceCbc != null) {
            deviceCbc.setSerialNumber(cbc.getDeviceCBC().getSerialNumber());
            deviceCbc.setRouteID(cbc.getDeviceCBC().getRouteID());
        }
    }

    private void assignDNPConfig(CapControlCBC cbc) {
        SimpleDevice device = SimpleDevice.of(cbc.getPaoIdentifier());
        LightDeviceConfiguration config = configurationDao.getLightConfigurationById(cbc.getDnpConfigId());

        try {
            deviceConfigService.assignConfigToDevice(config, device);
        } catch (InvalidDeviceTypeException e) {
            /*
             * This should have already been validated. 
             * Even if not, the old (valid) device config will still be in use.
             * Log it and move on.
             */
            log.error("An error occurred attempting to assign a DNP configuration to CBC " +
                      "'" + cbc.getName() + "'. Please assign this device a configuration manually.", e);
        }
    }

    public int create(CapControlCBC cbc) {
        CompleteCbcBase completeCbc = null;
        if (cbc.isTwoWay()) {
            CompleteTwoWayCbc completeTwoWay = new CompleteTwoWayCbc();
            completeTwoWay.setPortId(cbc.getDeviceDirectCommSettings().getPortID());
            
            completeCbc = completeTwoWay;
        } else {
            completeCbc = new CompleteOneWayCbc();
        }
        completeCbc.setPaoName(cbc.getName());

        paoPersistenceService.createPaoWithDefaultPoints(completeCbc, cbc.getPaoType());
        cbc.setId(completeCbc.getPaObjectId());
        return cbc.getId();
    }
}
