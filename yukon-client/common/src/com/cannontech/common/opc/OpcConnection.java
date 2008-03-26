package com.cannontech.common.opc;

import java.util.Date;
import java.util.Properties;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.yukon.IServerConnection;

import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ConnectivityException;


public class OpcConnection implements OpcConnectionListener{
    YukonJEasyOpc opcConnection;
    private IServerConnection defDispConn;
    
	private String host;
	private String serverName;
	private String clientName;
	private Integer statusPointId;
	
    public OpcConnection(String host, String serverName, String clientName, int statusPointId, IServerConnection disp ) {
		this.host = host;
		this.serverName = serverName;
		this.clientName = clientName;
		this.statusPointId = statusPointId;
		this.defDispConn = disp;
		
		//Modified the Library to let us pass in the location of the DLL.
		//Pass in a Properties object with the path specified to use the new code.
		Properties prop = new Properties();
		prop.put("library.path",CtiUtilities.getYukonBase() + "\\client\\bin\\JCustomOpc.dll");
		opcConnection = new YukonJEasyOpc(this.host,this.serverName,this.clientName, prop);
		opcConnection.addOpcConnectionListener(this);
	}
	/**
	 * Initializes OPC DLL
	 */
	public static void coInitialize() {
	    YukonJEasyOpc.coInitialize();
	}
	/**
	 * Uninitializes OPC DLL
	 */
	public static void coUninitialize() {
	    YukonJEasyOpc.coUninitialize();
	}

	private void sendStatusUpdate(double status) {
        if( statusPointId != null) {
    	    PointData pointData = new PointData();
            pointData.setId(statusPointId);
            pointData.setQuality(PointQualities.NORMAL_QUALITY);
            pointData.setType(PointTypes.STATUS_POINT);
            pointData.setValue(status);
            pointData.setTimeStamp(new Date());
            defDispConn.write(pointData);
        }
	}
	
	/**
	 * Starts the thread and connects to the OPC server.
	 * 
	 */
	public void start() throws ConnectivityException{
		opcConnection.start();
		opcConnection.connect();
	}
	/**
	 * Kills the thread running the OPC connection.
	 */
	public void stop() {
		opcConnection.terminate();
		
	}
	/**
	 * Returns a list of groups on the connection.
	 * 
	 * @return
	 */
	public OpcGroup[] getGroups() {
		return opcConnection.getGroupsAsArray();
	}
	/**
	 * Adds a group to a connection.
	 * 
	 * @param group
	 * @return
	 */
	public void addGroup( OpcGroup group ) {
		opcConnection.addGroup(group);
	}
	/**
	 * Adds an item to the passed in group on the connection.
	 * @param group
	 * @param item
	 * @return
	 */
	public void addItem(OpcGroup group, OpcItem item) {
		group.addItem(item);
	}
	public boolean isConnected() {
	    return opcConnection.isConnected();
	}
	public String getServerName() {
		return serverName;
	}
	
    @Override
    public void connectionStatusChanged(boolean newStatus) {
        double value;
        if(newStatus) {
            value = 1.0;
        }else {
            value = 0.0;
        }
        sendStatusUpdate(value);
    }
}
