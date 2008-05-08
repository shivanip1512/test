package com.cannontech.common.opc;

import java.util.Properties;
import com.cannontech.common.util.CtiUtilities;

import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.SynchWriteException;


public class OpcConnection {
    private YukonJEasyOpc opcConnection;
    
	private String host;
	private String serverName;
	private String clientName;
	
    public OpcConnection(String host, String serverName, String clientName, OpcConnectionListener listener ) {
		this.host = host;
		this.serverName = serverName;
		this.clientName = clientName;
		
		//Modified the Library to let us pass in the location of the DLL.
		//Pass in a Properties object with the path specified to use the new code.
		Properties prop = new Properties();
		prop.put("library.path",CtiUtilities.getYukonBase() + "\\client\\bin\\JCustomOpc.dll");
		opcConnection = new YukonJEasyOpc(this.host,this.serverName,this.clientName, prop);
		opcConnection.addOpcConnectionListener(listener);
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
	 * 
	 */
	public void writeOutPointData(OpcGroup group, OpcItem item) throws SynchWriteException{
		opcConnection.synchWriteItem(group, item);
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
	public void removeGroup(OpcGroup group) {
		opcConnection.removeGroup(group);
	}
}
