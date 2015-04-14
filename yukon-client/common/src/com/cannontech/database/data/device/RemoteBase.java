package com.cannontech.database.data.device;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.spring.YukonSpringHook;

public abstract class RemoteBase extends TwoWayDevice {
    private DeviceDirectCommSettings deviceDirectCommSettings = null;
    private DeviceDialupSettings deviceDialupSettings = null;

    private transient Logger logger = YukonLogManager.getLogger(RemoteBase.class);

    private String ipAddress = CtiUtilities.STRING_NONE;
    private String port = CtiUtilities.STRING_NONE;

    public RemoteBase(PaoType paoType) {
        super(paoType);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getDeviceDialupSettings().add();
        getDeviceDirectCommSettings().add();
        addTcpProperties();
    }

    @Override
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
        getDeviceDialupSettingsDefaults().add();
        getDeviceDirectCommSettings().add();
        addTcpProperties();
    }
    
    @Override
    public void deletePartial() throws java.sql.SQLException {
        int deviceId = getPAObjectID();
        PaoPropertyDao propertyDao = YukonSpringHook.getBean("paoPropertyDao", PaoPropertyDao.class);
        propertyDao.removeAll(deviceId);

        getDeviceDialupSettings().delete();
        getDeviceDirectCommSettings().delete();
        super.deletePartial();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        int deviceId = getPAObjectID();
        PaoPropertyDao propertyDao = YukonSpringHook.getBean("paoPropertyDao", PaoPropertyDao.class);
        propertyDao.removeAll(deviceId);

        getDeviceDialupSettings().delete();
        getDeviceDirectCommSettings().delete();
        super.delete();
    }

    public DeviceDialupSettings getDeviceDialupSettings() {
        if (deviceDialupSettings == null) {
            deviceDialupSettings = new DeviceDialupSettings();
        }

        return deviceDialupSettings;
    }

    public DeviceDialupSettings getDeviceDialupSettingsDefaults() {
        if (this instanceof TapTerminalBase) {
            getDeviceDialupSettings().setLineSettings("7E2");
        } else {
            getDeviceDialupSettings().setLineSettings("8N1");
        }

        return getDeviceDialupSettings();
    }

    public DeviceDirectCommSettings getDeviceDirectCommSettings() {
        if (deviceDirectCommSettings == null) {
            deviceDirectCommSettings = new DeviceDirectCommSettings();
        }

        return deviceDirectCommSettings;
    }

    @Override
    public void retrieve() throws java.sql.SQLException {

        super.retrieve();
        getDeviceDialupSettings().retrieve();
        getDeviceDirectCommSettings().retrieve();

        int portId = getDeviceDirectCommSettings().getPortID();

        if (getPaoType().isTcpPortEligible() && DeviceTypesFuncs.isTcpPort(portId)) {
            int deviceId = getPAObjectID();
            PaoPropertyDao propertyDao = YukonSpringHook.getBean("paoPropertyDao", PaoPropertyDao.class);
            try {
                PaoProperty ipProperty = propertyDao.getByIdAndName(deviceId, PaoPropertyName.TcpIpAddress);
                ipAddress = ipProperty.getPropertyValue();
            } catch (EmptyResultDataAccessException e) {
                logger.error(getPAOName() + " is missing TCP IP Address property.");
            }

            try {
                PaoProperty portProperty = propertyDao.getByIdAndName(deviceId, PaoPropertyName.TcpPort);
                port = portProperty.getPropertyValue();
            } catch (EmptyResultDataAccessException e) {
                logger.error(getPAOName() + " is missing TCP Port property.");
            }
        }
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);

        if (getDeviceDialupSettings() != null) {
            getDeviceDialupSettings().setDbConnection(conn);
        }

        getDeviceDirectCommSettings().setDbConnection(conn);
    }

    public void setDeviceDialupSettings(DeviceDialupSettings newValue) {
        this.deviceDialupSettings = newValue;
    }

    public void setDeviceDirectCommSettings(DeviceDirectCommSettings newValue) {
        this.deviceDirectCommSettings = newValue;
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);

        if (getDeviceDialupSettings() != null) {
            getDeviceDialupSettings().setDeviceID(deviceID);
        }

        getDeviceDirectCommSettings().setDeviceID(deviceID);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getDeviceDialupSettings().update();
        getDeviceDirectCommSettings().update();
        addTcpProperties();
    }

    public boolean hasPhoneNumber() {
        if (getDeviceDialupSettings().getPhoneNumber() == null) {
            return false;
        }
        return (!(getDeviceDialupSettings().getPhoneNumber().compareTo("0") == 0 || getDeviceDialupSettings().getPhoneNumber() == null));

    }

    public void addTcpProperties() {

        if (getPaoType().isTcpPortEligible()) {

            PaoPropertyDao propertyDao = YukonSpringHook.getBean(PaoPropertyDao.class);
            int deviceId = getPAObjectID();
            propertyDao.removeAll(deviceId);

            int portId = getDeviceDirectCommSettings().getPortID();
            if (DeviceTypesFuncs.isTcpPort(portId)) {
                PaoIdentifier identifier = new PaoIdentifier(deviceId, PaoType.TCPPORT);
                propertyDao.add(new PaoProperty(identifier, PaoPropertyName.TcpIpAddress, ipAddress));
                propertyDao.add(new PaoProperty(identifier, PaoPropertyName.TcpPort, port));
            }
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}