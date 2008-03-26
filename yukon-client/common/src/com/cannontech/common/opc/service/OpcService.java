package com.cannontech.common.opc.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;

import javafish.clients.opc.asynch.AsynchEvent;
import javafish.clients.opc.asynch.OpcAsynchGroupListener;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.ItemExistsException;
import javafish.clients.opc.variant.Variant;
import javafish.clients.opc.variant.VariantTypes;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.opc.OpcConnection;
import com.cannontech.common.opc.YukonOpcItem;
import com.cannontech.common.opc.model.FdrInterfaceType;
import com.cannontech.common.opc.model.FdrTranslation;
import com.cannontech.common.util.ScheduledExecutorDelegate;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.database.data.point.PointTypes;

public class OpcService implements Runnable, MessageListener, OpcAsynchGroupListener{
	
	private FdrTranslationDao fdrTranslationDao;
	private IServerConnection defDispConn;
	
	//Refresh rate and Server Address come from the master configuration file.
	private static ConfigurationSource config = MasterConfigHelper.getLocalConfiguration();
	
	private Map<String,OpcConnection> opcServerMap;
	private Map<Integer,OpcConnection> pointIdToOpcServer;
	private Map<String,String> serverAddressMap;
	private Map<String,Integer> opcServerStatusPoint;
	
	private int refreshRate;
	private ScheduledExecutorDelegate globalScheduledExecutor;
	
    private boolean refresh = true;
    private boolean serviceEnabled = false;
    private boolean deadConnection = false;
	
	public OpcService() {
		opcServerMap =  new HashMap<String,OpcConnection>();
		serverAddressMap = new HashMap<String,String>();
		opcServerStatusPoint = new HashMap<String,Integer>();
        try{
            String ips = config.getRequiredString("OPC_SERVERS");
            StringTokenizer tokens = new StringTokenizer(ips,";",false);
            while( tokens.hasMoreTokens()) {
                String[] value = tokens.nextToken().split(":");
                serverAddressMap.put(value[0], value[1]);
            }            
        }catch( UnknownKeyException e) {
            CTILogger.error("OPC: Opc Server address's are not in the Master.cfg file. Defaulting to localhost");
        }
		try{
			refreshRate = Integer.parseInt(config.getRequiredString("OPC_REFRESH"));
		}catch( UnknownKeyException e) {
			CTILogger.error("OPC: Refresh rate is not in the Master.cfg file. Defaulting to 60 seconds ");
			refreshRate = 60;
		}
		
	}
	
    @Override
    public void run() {
        try{
        /* Code to check if the OPC_ENABLED flag changed in the master.cfg file.*/
        boolean newServiceState = false;
        try{
            newServiceState = Boolean.parseBoolean(config.getRequiredString("OPC_ENABLED"));
        }catch( UnknownKeyException e) {}
        
        if( newServiceState != serviceEnabled) {
            if(serviceEnabled == false) {
                //Turn on
                defDispConn.addMessageListener(this);
                refresh = true;
            }else {
                //Turn Off
                defDispConn.removeMessageListener(this);
                shutdownAll();
                refresh = false;
                deadConnection = false;
            }
        }
        /* Refresh the OPC connections or Kill a bad connection */
        if( refresh || deadConnection ) {
            setupService();
            deadConnection = false;
        }else {
            List<OpcConnection> connList = new ArrayList<OpcConnection>();
            for( OpcConnection conn : opcServerMap.values() ) {
                if(!conn.isConnected()) {
                    connList.add(conn);
                }
            }
            if(connList.size() > 0) {
                CTILogger.error("Stopping Reconnects to OPC server until the configuration is fixed.");
                deadConnection = true;
                for(OpcConnection conn : connList) {
                    opcServerMap.remove(conn.getServerName());
                    conn.stop();
                }
            }
        }
        }catch(Exception e) {
            CTILogger.error("Exception in OPC Run Thread: ",e);
            return;
        }
    }
	
	/**
	 * Called by Spring to start the service.
	 */
	public void initialize() {
	    globalScheduledExecutor.scheduleAtFixedRate(this, 0, refreshRate, TimeUnit.SECONDS );
        try{
            serviceEnabled = Boolean.parseBoolean(config.getRequiredString("OPC_ENABLED"));
        }catch( UnknownKeyException e) {
            CTILogger.error("OPC: Enabled flag is not setup, defaulting to disabled. ");
        }
        
        if(serviceEnabled) {
            defDispConn.addMessageListener(this);
        }else {
            refresh = false;
        }
	}
	
	/**
	 * Call to reload all service configurations.
	 */
	private void setupService() {
		shutdownAll();
		pointIdToOpcServer = new HashMap<Integer, OpcConnection>();
		
		List<FdrTranslation> lst = fdrTranslationDao.getByInterfaceType(FdrInterfaceType.OPC);

		/* Load Server Status Points*/
		List<FdrTranslation> statusPoints = fdrTranslationDao.getByInterfaceType(FdrInterfaceType.SYSTEM);
		setupServerStatusPoints(statusPoints);
		
		for( FdrTranslation fdr : lst ) {
        	try{
        	    processFdrTranslation(fdr);
        	}catch(ItemExistsException e) {
                CTILogger.error("OPC: Error registering Item. ");
            }catch(Exception e) {
                CTILogger.error("Exception Processing Translation for Point. ",e);
            }
		}
		startupAll();
		refresh = false;
	}
	
	private void setupServerStatusPoints(List<FdrTranslation> statusPoints) {
	    opcServerStatusPoint.clear();
	    
	    for( FdrTranslation t : statusPoints ) {
	        String opcServerName = t.getParameter("Client");
	        if( !"".equalsIgnoreCase(opcServerName) ) {
	            opcServerStatusPoint.put(opcServerName, t.getFdrPointId());
	        }
	    }
	}
	
	/**
	 * Parses the FdrTranslation for configuration of the service.
	 * 
	 * This function sets up the connections and maps the listener 
	 * functions will need to do their magic.
	 * @param fdr
	 */
	private void processFdrTranslation( FdrTranslation fdr ) throws Exception {
		String server = fdr.getParameter("Server Name");
		String groupName = fdr.getParameter("OPC Group");
		String itemName = fdr.getParameter("OPC Item");
		String pointTypeStr = fdr.getParameter("POINTTYPE");
		String ip;
		int pointType;
		boolean newConnection = false;
		
		try{
		    pointType = PointTypes.getType(pointTypeStr);
		} catch(Error e) {
		    throw new Exception("Unknown Point type, skipping item: " + itemName);
		}
		
		Integer pointId = fdr.getFdrPointId();
		if( !pointIdToOpcServer.containsKey(pointId)) {
			
			ip = serverAddressMap.get(server);
			if( ip == null) {
			    CTILogger.warn("Server Address not found in Master.cfg. Derfaulting to localhost for, " + server);
			    ip = "localhost";
			}
			
			OpcConnection conn = opcServerMap.get(server);
			if( conn == null ) {
			    Integer t = opcServerStatusPoint.get(server);
				conn = new OpcConnection(ip,server,"YukonOpcClient",t,defDispConn);
				newConnection = true;
			}
			
			OpcGroup[] groupArray = conn.getGroups();
			OpcGroup group = null; 
			
			for(OpcGroup g : groupArray) {
				if( g.getGroupName().equals(groupName) ) {
					group = g;
					break;
				}
			}
			if(group == null) {
				group = new OpcGroup(groupName, true, refreshRate*1000, 0.0f);
				group.addAsynchListener(this);
				conn.addGroup(group);
			}
			OpcItem item = new YukonOpcItem(itemName,true,"",pointId,pointType);
			group.addItem(item);
			
			/* Sync up maps */
		    if(newConnection)
		        opcServerMap.put(server, conn);
			pointIdToOpcServer.put(pointId, conn);

		} else {
			throw new Exception("OPC: Opc Configuration Error. Point ID: " + pointId 
			                    + " is mapped to multiple Opc Items, ignoring new item." );
		}
	}
	
	/* Spring stuff */
	public void setFdrTranslationDao( FdrTranslationDao dao ) {
		this.fdrTranslationDao = dao;
	}

	public void setDefDispConn(IServerConnection defDispConn) {
		this.defDispConn = defDispConn;
	}

    public void setGlobalScheduledExecutor(
            ScheduledExecutorDelegate globalScheduledExecutor) {
        this.globalScheduledExecutor = globalScheduledExecutor;
    }

    /* Thread Control */
	/**
	 * Starts all OPC connections that are configured. Also initializes the DLL
	 */
	private void startupAll() {
		OpcConnection.coInitialize();
		for( OpcConnection conn : opcServerMap.values() ) {
			try{
				startup(conn);
			} catch (ConnectivityException e) {
				CTILogger.error(" Unable to start connection with OPC Server: " + conn.getServerName() );
			}
		}
	}
	/**
	 * Starts the OPC connection, register's the groups and items with the server for asynch updates.
	 * 
	 * Each connection has it's own thread
	 * 
	 * @param conn
	 * @throws ConnectivityException
	 */
	private void startup( OpcConnection conn ) throws ConnectivityException{
		conn.start();
	}
	/**
	 * Shuts down all OPC connections.
	 */
	public void shutdownAll() {
		for( OpcConnection conn : opcServerMap.values() ) {
			shutdown(conn);
		}
		opcServerMap.clear();
		OpcConnection.coUninitialize();
	}
	/**
	 * Shuts down an individual OPC Connection.
	 * @param conn
	 */
	private void shutdown(OpcConnection conn) {
		conn.stop();
	}
	/* end Thread Control*/
	
	/* These next two functions are the bread and butter for updating the points.*/
	/**
	 * getAsynchEvent( AsynchEvent event)
	 * 
	 * This is called by the connections when an update is received to the OPC Item's we are watching.
	 * 
	 * The data is relayed to dispatch from here.
	 */
	@Override	
	public void getAsynchEvent( AsynchEvent event) {

		CTILogger.debug("OPC DEBUG: OPC Asynch event");
		OpcGroup group3 = event.getOPCGroup();
		ArrayList<OpcItem> items = group3.getItems();
        Multi<Message> multi = new Multi<Message>();
        
		for (OpcItem opcItem : items) {
            YukonOpcItem yOpcItem = (YukonOpcItem)opcItem;
        	Calendar timeStamp = yOpcItem.getTimeStamp();
            boolean quality = yOpcItem.isQuality();
            String itemName = yOpcItem.getItemName();
            Integer pointId = yOpcItem.getPointId();

            if( pointId == null ) {
                CTILogger.error("OPC: Cannot find point id, " + pointId + ", link to OPC item, " + itemName + ", skipping update.");
                continue;
            }
            
            Variant value = yOpcItem.getValue();
            int type = value.getVariantType();

            double newValue;
            switch(type) {
                case VariantTypes.VT_BOOL: {
                    boolean b = value.getBoolean();
                    if(b)
                        newValue = 1.0;
                    else
                        newValue = 0.0;
                    break;
                }
                case VariantTypes.VT_R4: {
                    newValue = value.getFloat();
                    break;
                }
                case VariantTypes.VT_R8: {
                    newValue = value.getDouble();
                    break;
                }
                default: {
                    CTILogger.error("Received an unhandled data type from the OPC server.");
                    continue;
                }
            }

            PointData pointData = new PointData();
            pointData.setId(pointId);
            pointData.setQuality(quality ? PointQualities.NORMAL_QUALITY : PointQualities.UNKNOWN_QUALITY);
            pointData.setType(yOpcItem.getPointType());
            pointData.setValue(newValue);
            pointData.setTimeStamp(timeStamp.getTime());
            CTILogger.debug("OPC DEBUG: Sending update for " + itemName + ". Value: " + newValue );
            multi.getVector().add(pointData);

        }
		defDispConn.write(multi);
	}
	/**
	 * messageReceived(MessageEvent e)
	 * 
	 * If a db change message come in on a point we are monitoring we 
	 * will restart the service to have the most up to date information.
	 * All other messages from dispatch are ignored.
	 */
	@Override
	public void messageReceived(MessageEvent e) {
		CTILogger.debug("OPC DEBUG: OPC dispatch event");
		Message msg = e.getMessage();
		if( msg instanceof DBChangeMsg) {
			int pid = ((DBChangeMsg)msg).getId();
			if( pointIdToOpcServer.containsKey(pid) ) {
				CTILogger.debug("OPC DEBUG: Change to Point involved with OPC");
				refresh = true;
			} else {
				try {
					fdrTranslationDao.getByPointIdAndType(pid,FdrInterfaceType.OPC);
					refresh = true;
					CTILogger.debug("OPC DEBUG: translation found, new FDR translation");
				} catch ( DataAccessException exception ) {
					CTILogger.debug("OPC DEBUG: translation not found, not a new FDR translation");
				}
			}
		}
	}
}
