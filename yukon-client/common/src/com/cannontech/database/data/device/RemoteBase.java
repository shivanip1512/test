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
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public class RemoteBase extends TwoWayDevice {
	private DeviceDirectCommSettings deviceDirectCommSettings = null;
	private DeviceDialupSettings deviceDialupSettings = null;
	
	private transient Logger logger = YukonLogManager.getLogger(RemoteBase.class);
	
	private String ipAddress = CtiUtilities.STRING_NONE;
	private String port = CtiUtilities.STRING_NONE;
/**
 * RemoteBase constructor comment.
 */
public RemoteBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException {
	super.add();
	getDeviceDialupSettings().add();
	getDeviceDirectCommSettings().add();
	addTcpProperties();
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:28:26 AM)
 * @param deviceID int
 */
public void addPartial() throws java.sql.SQLException
{
	super.addPartial();
	getDeviceDialupSettingsDefaults().add();
	getDeviceDirectCommSettings().add();
	addTcpProperties();
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException {
    int deviceId = getPAObjectID();
    PaoPropertyDao propertyDao = YukonSpringHook.getBean("paoPropertyDao",PaoPropertyDao.class);
    propertyDao.removeAll(deviceId);
    
    getDeviceDialupSettings().delete();
	getDeviceDirectCommSettings().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 11:18:49 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {

	super.deletePartial();

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceDialupSettings
 */
public DeviceDialupSettings getDeviceDialupSettings() 
{
	if( deviceDialupSettings == null)
		deviceDialupSettings = new DeviceDialupSettings();
		
	return deviceDialupSettings;
}
/**
 * Insert the method's description here.
 * Creation date: (6/18/2001 10:54:04 AM)
 * @return com.cannontech.database.db.device.DeviceDialupSettings
 */
public DeviceDialupSettings getDeviceDialupSettingsDefaults()
{
	if (this instanceof PagingTapTerminal)
		getDeviceDialupSettings().setLineSettings("7E2");
	else
		getDeviceDialupSettings().setLineSettings("8N1");

	return getDeviceDialupSettings();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.device.DeviceDirectCommSettings
 */
public DeviceDirectCommSettings getDeviceDirectCommSettings() {
	if( deviceDirectCommSettings == null )
		deviceDirectCommSettings = new DeviceDirectCommSettings();
		
	return deviceDirectCommSettings;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException {

	super.retrieve();
	getDeviceDialupSettings().retrieve();
	getDeviceDirectCommSettings().retrieve();
	
	PaoType paoType = PaoType.getForDbString(getPAOType());
	int portId = getDeviceDirectCommSettings().getPortID();

	if (PaoType.isTcpPortEligible(paoType) && DeviceTypesFuncs.isTcpPort(portId)) {
	    int deviceId = getPAObjectID();
	    PaoPropertyDao propertyDao = YukonSpringHook.getBean("paoPropertyDao",PaoPropertyDao.class);
        try {
            PaoProperty ipProperty = propertyDao.getByIdAndName(deviceId,PaoPropertyName.TcpIpAddress);
            ipAddress = ipProperty.getPropertyValue();
        } catch (EmptyResultDataAccessException e) {
            logger.error(getPAOName() + " is missing TCP IP Address property.");
        }
        
        try {
            PaoProperty portProperty = propertyDao.getByIdAndName(deviceId,PaoPropertyName.TcpPort);
            port = portProperty.getPropertyValue();
        } catch (EmptyResultDataAccessException e) {
            logger.error(getPAOName() + " is missing TCP Port property.");
        }
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	if( getDeviceDialupSettings() != null )
		getDeviceDialupSettings().setDbConnection(conn);

	getDeviceDirectCommSettings().setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceDialupSettings
 */
public void setDeviceDialupSettings(DeviceDialupSettings newValue) {
	this.deviceDialupSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.device.DeviceDirectCommSettings
 */
public void setDeviceDirectCommSettings(DeviceDirectCommSettings newValue) {
	this.deviceDirectCommSettings = newValue;
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setDeviceID( Integer deviceID) {
	super.setDeviceID(deviceID);

	if( getDeviceDialupSettings() != null )
		getDeviceDialupSettings().setDeviceID(deviceID);

	getDeviceDirectCommSettings().setDeviceID( deviceID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();
	getDeviceDialupSettings().update();
	getDeviceDirectCommSettings().update();
	addTcpProperties();
}

public boolean hasPhoneNumber()
{
	if(getDeviceDialupSettings().getPhoneNumber() == null)
		return false;
	return (!(getDeviceDialupSettings().getPhoneNumber().compareTo("0") == 0 ||getDeviceDialupSettings().getPhoneNumber() == null));

}

public void addTcpProperties() {
    PaoType paoType = PaoType.getForDbString(getPAOType());
    if (PaoType.isTcpPortEligible(paoType)) {
        
        PaoPropertyDao propertyDao = YukonSpringHook.getBean("paoPropertyDao",PaoPropertyDao.class);
        int deviceId = getPAObjectID();
        propertyDao.removeAll(deviceId);
        
        int portId = getDeviceDirectCommSettings().getPortID();
        if (DeviceTypesFuncs.isTcpPort(portId)) {
            PaoIdentifier identifier = new PaoIdentifier(deviceId,PaoType.TCPPORT);
            propertyDao.add(new PaoProperty(identifier,PaoPropertyName.TcpIpAddress,ipAddress));
            propertyDao.add(new PaoProperty(identifier,PaoPropertyName.TcpPort,port));
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
