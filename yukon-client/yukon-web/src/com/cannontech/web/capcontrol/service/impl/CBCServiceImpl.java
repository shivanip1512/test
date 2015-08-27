package com.cannontech.web.capcontrol.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.capcontrol.service.CBCService;
import com.cannontech.web.editor.CapControlCBC;

@Service
public class CBCServiceImpl implements CBCService {
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoPropertyDao propertyDao;
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private CapbankControllerDao capbankControllerDao;
    @Autowired private CapbankDao capbankDao;
    @Autowired private PaoDao paoDao;
    private transient Logger logger = YukonLogManager.getLogger(RemoteBase.class);

    @Override
    public CapControlCBC getCapControlCBC(int id) {

        CapControlCBC capControlCBC = new CapControlCBC();
        YukonPAObject pao = PAOFactory.createPAObject(id);
        DBPersistent dbPersistent = retrieveDBPersistent(pao);
        DeviceBase deviceBase = (DeviceBase) dbPersistent;
        com.cannontech.database.db.pao.YukonPAObject yukonPAObject = new com.cannontech.database.db.pao.YukonPAObject();
        yukonPAObject.setPaObjectID(pao.getPAObjectID());
        yukonPAObject.setPaoClass(pao.getPAOClass());
        yukonPAObject.setPaoName(pao.getPAOName());
        yukonPAObject.setPaoType(pao.getPaoType());
        yukonPAObject.setDisableFlag(pao.getPAODisableFlag());
        capControlCBC.setYukonPAObject(yukonPAObject);

        PaoIdentifier capbank = capbankDao.findCapBankByCbc(id);
        int parentID = (capbank != null) ? capbank.getPaoId() : 0;
        if (parentID == CtiUtilities.NONE_ZERO_ID) {
            capControlCBC.setCbcParent(CtiUtilities.STRING_NONE);
        } else {
            try {
                LiteYukonPAObject parentPAO = paoDao.getLiteYukonPAO(parentID);
                capControlCBC.setCbcParent(parentPAO.getPaoName() + "   (" + parentPAO.getPaoType().getDbString()
                    + ",  id: " + parentPAO.getLiteID() + ")");
            } catch (NotFoundException nfe) {
                capControlCBC.setCbcParent(CtiUtilities.STRING_NONE);
            }
        }

        capControlCBC.setDevice(deviceBase.getDevice());
        PaoType paoType = deviceBase.getPaoType();
        // Start

        if (dbPersistent instanceof TwoWayDevice) {
            capControlCBC.setTwoWay(true);
            DNPBase dnpBase = (DNPBase) dbPersistent;

            capControlCBC.setDeviceWindow(dnpBase.getDeviceWindow());

            capControlCBC.setDeviceScanRateMap(dnpBase.getDeviceScanRateMap());

            capControlCBC.setDeviceDialupSettings(dnpBase.getDeviceDialupSettings());

            capControlCBC.setDeviceDirectCommSettings(dnpBase.getDeviceDirectCommSettings());
            int portId = dnpBase.getDeviceDirectCommSettings().getPortID();

            if (paoType.isTcpPortEligible() && DeviceTypesFuncs.isTcpPort(portId)) {
                try {
                    PaoProperty ipProperty = propertyDao.getByIdAndName(id, PaoPropertyName.TcpIpAddress);
                    capControlCBC.setIpAddress(ipProperty.getPropertyValue());
                } catch (EmptyResultDataAccessException e) {
                    logger.error(dnpBase.getPAOName() + " is missing TCP IP Address property.");
                }

                try {
                    PaoProperty portProperty = propertyDao.getByIdAndName(id, PaoPropertyName.TcpPort);
                    capControlCBC.setPort(portProperty.getPropertyValue());
                } catch (EmptyResultDataAccessException e) {
                    logger.error(dnpBase.getPAOName() + " is missing TCP Port property.");
                }
            }

            capControlCBC.setDeviceAddress(dnpBase.getDeviceAddress());

            YukonDevice device = new SimpleDevice(id, paoType);

            LightDeviceConfiguration configuration = configurationDao.findConfigurationForDevice(device);
            if (configuration != null) {
                capControlCBC.setDnpConfiguration(dnpBase.getDnpConfiguration());
            }

        }
        if (dbPersistent instanceof CapBankController) {
            CapBankController capBankController = (CapBankController) dbPersistent;
            capControlCBC.setDeviceCBC(capBankController.getDeviceCBC());
        } else if (dbPersistent instanceof CapBankController702x) {
            CapBankController702x capBankController702x = (CapBankController702x) dbPersistent;
            capControlCBC.setDeviceCBC(capBankController702x.getDeviceCBC());
        } else if (dbPersistent instanceof CapBankControllerDNP) {
            CapBankControllerDNP capBankControllerDNP = (CapBankControllerDNP) dbPersistent;
            capControlCBC.setDeviceCBC(capBankControllerDNP.getDeviceCBC());
        }

        // End

        return capControlCBC;
    }

    private void checkForErrorsAndSave(DBPersistent dbPersistent, CapControlCBC capControlCBC)
            throws PortDoesntExistException, MultipleDevicesOnPortException, SameMasterSlaveCombinationException,
            SQLException, SerialNumberExistsException {
        int cbcId = capControlCBC.getYukonPAObject().getPaObjectID();
        // error handling when serial number exists
        handleSerialNumber(capControlCBC, cbcId);

        /*
         * A. Show an error and don't save the update if the user tries to put
         * the same master/slave address combination for a different device on the same communication port
         * B. Show a warning if the user uses the same master/slave address for a
         * device on a different communication port
         */

        DeviceAddress currentDeviceAddress;
        Integer commPortId;

        currentDeviceAddress = capControlCBC.getDeviceAddress();
        commPortId = capControlCBC.getDeviceDirectCommSettings().getPortID();

        List<Integer> devicesWithSameAddress =
            deviceDao.getDevicesByDeviceAddress(currentDeviceAddress.getMasterAddress(),
                currentDeviceAddress.getSlaveAddress());
        List<Integer> devicesByPort = deviceDao.getDevicesByPort(commPortId.intValue());
        // remove the current device from the list
        devicesWithSameAddress.remove(new Integer(cbcId));

        if (commPortId.intValue() <= 0) {
            throw new PortDoesntExistException();
        }

        // check to see if the master slave combination is the same
        if (devicesWithSameAddress.size() > 0) {
            for (int i = 0; i < devicesWithSameAddress.size(); i++) {
                Integer paoId = devicesWithSameAddress.get(i);
                if (devicesByPort.contains(paoId)) {
                    LiteYukonPAObject litePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(paoId.intValue());
                    throw new MultipleDevicesOnPortException(litePAO.getPaoName());
                }
            }
            LiteYukonPAObject litePAO =
                YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(devicesWithSameAddress.get(0).intValue());
            throw new SameMasterSlaveCombinationException(litePAO.getPaoName());
        }
        if (dbPersistent instanceof TwoWayDevice) {
            DNPBase dnpBase = (DNPBase) dbPersistent;

            dnpBase.getDeviceAddress().setMasterAddress(capControlCBC.getDeviceAddress().getMasterAddress());
            dnpBase.getDeviceAddress().setSlaveAddress(capControlCBC.getDeviceAddress().getSlaveAddress());
            dnpBase.getDeviceAddress().setPostCommWait(capControlCBC.getDeviceAddress().getPostCommWait());
            dnpBase.getDeviceDirectCommSettings().setPortID(capControlCBC.getDeviceDirectCommSettings().getPortID());

            if (capControlCBC.getDeviceScanRateMap().containsKey("Exception")) {
                dnpBase.getDeviceScanRateMap().get("Exception").setAlternateRate(
                    capControlCBC.getDeviceScanRateMap().get("Exception").getAlternateRate());
                dnpBase.getDeviceScanRateMap().get("Exception").setScanGroup(
                    (capControlCBC.getDeviceScanRateMap().get("Exception").getScanGroup()));
                dnpBase.getDeviceScanRateMap().get("Exception").setIntervalRate(
                    (capControlCBC.getDeviceScanRateMap().get("Exception").getIntervalRate()));
            }
            if (capControlCBC.getDeviceScanRateMap().containsKey("Integrity")) {
                dnpBase.getDeviceScanRateMap().get("Integrity").setAlternateRate(
                    capControlCBC.getDeviceScanRateMap().get("Integrity").getAlternateRate());
                dnpBase.getDeviceScanRateMap().get("Integrity").setScanGroup(
                    (capControlCBC.getDeviceScanRateMap().get("Integrity").getScanGroup()));
                dnpBase.getDeviceScanRateMap().get("Integrity").setIntervalRate(
                    (capControlCBC.getDeviceScanRateMap().get("Integrity").getIntervalRate()));
            }

        }
        if (dbPersistent instanceof CapBankController) {
            CapBankController cbc = (CapBankController) dbPersistent;
            cbc.setPAOName(capControlCBC.getYukonPAObject().getPaoName());
            cbc.getDeviceCBC().setSerialNumber(capControlCBC.getDeviceCBC().getSerialNumber());
            cbc.getDeviceCBC().setSerialNumber(capControlCBC.getDeviceCBC().getSerialNumber());
            cbc.getDeviceCBC().setRouteID(capControlCBC.getDeviceCBC().getRouteID());
        } else if (dbPersistent instanceof CapBankController702x) {
            CapBankController702x capBankController702x = (CapBankController702x) dbPersistent;
            capBankController702x.setPAOName(capControlCBC.getYukonPAObject().getPaoName());
            capBankController702x.getDeviceCBC().setSerialNumber(capControlCBC.getDeviceCBC().getSerialNumber());
            capBankController702x.getDeviceCBC().setRouteID(capControlCBC.getDeviceCBC().getRouteID());
        } else if (dbPersistent instanceof CapBankControllerDNP) {
            CapBankControllerDNP capBankControllerDNP = (CapBankControllerDNP) dbPersistent;
            capBankControllerDNP.setPAOName(capControlCBC.getYukonPAObject().getPaoName());
            capBankControllerDNP.getDeviceCBC().setSerialNumber(capControlCBC.getDeviceCBC().getSerialNumber());
            capBankControllerDNP.getDeviceCBC().setRouteID(capControlCBC.getDeviceCBC().getRouteID());
        }

        dbPersistent.update();

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

        }
    }

    @Override
    public int save(CapControlCBC capControlCBC) throws SerialNumberExistsException, SQLException,
            PortDoesntExistException, MultipleDevicesOnPortException, SameMasterSlaveCombinationException {
        int cbcId = capControlCBC.getYukonPAObject().getPaObjectID();
        YukonPAObject pao = PAOFactory.createPAObject(cbcId);
        DBPersistent dbPersistent = retrieveDBPersistent(pao);
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        dbPersistent.setDbConnection(connection);
        checkForErrorsAndSave(dbPersistent, capControlCBC);
        return capControlCBC.getYukonPAObject().getPaObjectID();
    }

    protected DBPersistent retrieveDBPersistent(com.cannontech.database.data.pao.YukonPAObject yukonPAObject) {

        Connection conn = null;

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            yukonPAObject.setDbConnection(conn);
            yukonPAObject.retrieve();
        } catch (SQLException sql) {
            CTILogger.error("Unable to retrieve DB Object", sql);
            return null;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } finally {
            yukonPAObject.setDbConnection(null);
            try {
                if (conn != null)
                    conn.close();
            } catch (java.sql.SQLException e2) {}
        }

        return yukonPAObject;
    }
}
