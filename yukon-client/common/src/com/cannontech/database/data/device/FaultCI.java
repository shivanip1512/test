package com.cannontech.database.data.device;

import com.cannontech.database.data.pao.DeviceClasses;


public class FaultCI extends GridAdvisorBase {

public FaultCI() {
	super();
	setDeviceClass( DeviceClasses.STRING_CLASS_GRID );
}

public void add() throws java.sql.SQLException {
	super.add();
}

public void addPartial() throws java.sql.SQLException {
	super.addPartial();
	
	
	}

public void delete() throws java.sql.SQLException{
	super.delete();
}

public void deletePartial() throws java.sql.SQLException {
	super.deletePartial();
	
	
	}

public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
}

public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
}

public void setDeviceID(Integer deviceID) 
{
	super.setDeviceID(deviceID);
}

public void update() throws java.sql.SQLException
{
	super.update();
}
}
