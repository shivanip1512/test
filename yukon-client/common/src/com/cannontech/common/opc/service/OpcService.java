package com.cannontech.common.opc.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataRetrievalFailureException;

import javafish.clients.opc.asynch.AsynchEvent;
import javafish.clients.opc.asynch.OpcAsynchGroupListener;
import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.ItemExistsException;
import javafish.clients.opc.variant.Variant;
import javafish.clients.opc.variant.VariantTypes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.opc.*;
import com.cannontech.common.opc.model.FdrInterfaceType;
import com.cannontech.common.opc.model.FdrTranslation;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.*;
import com.cannontech.yukon.BasicServerConnection;
import com.cannontech.database.data.point.PointTypes;

public class OpcService implements Runnable, MessageListener, OpcAsynchGroupListener, OpcConnectionListener{
	
	private FdrTranslationDao fdrTranslationDao;
	private BasicServerConnection dispatchConnection;
	private ConfigurationSource config;	

	private Map<String,OpcConnection> opcServerMap;
	private Map<Integer,OpcConnection> pointIdToOpcServer;
	private Map<String,String> serverAddressMap;
	private Map<String,Integer> opcServerToStatusPointIdMap;
	
	private int refreshSeconds;
	private ScheduledExecutor globalScheduledExecutor;
	
    private boolean refresh = true;
    private boolean serviceEnabled = false;
    private boolean deadConnection = false;
	
    Logger log = YukonLogManager.getLogger(OpcService.class);
    
	public OpcService() {
		opcServerMap =  new HashMap<String,OpcConnection>();
		serverAddressMap = new HashMap<String,String>();
		opcServerToStatusPointIdMap = new HashMap<String,Integer>();		
	}
	
    @Override
    public void run() {
        try{
        
        try{
            serverAddressMap.clear();
            String ips = config.getRequiredString("OPC_SERVERS");
            StringTokenizer tokens = new StringTokenizer(ips,";",false);
            while( tokens.hasMoreTokens()) {
                String[] value = tokens.nextToken().split(":");
                serverAddressMap.put(value[0], value[1]);
            }
        }catch( UnknownKeyException e) {}
        
        int newRefreshSeconds;
        try{
            newRefreshSeconds = Integer.parseInt(config.getRequiredString("OPC_REFRESH"));
        }catch( UnknownKeyException e) {
            newRefreshSeconds = 60;
        }        
        if( refreshSeconds != newRefreshSeconds) {
            refreshSeconds = newRefreshSeconds;
            refresh = true;
        }
        
        /* Code to check if the OPC_ENABLED flag changed in the master.cfg file.*/
        boolean newServiceState = false;
        try{
            newServiceState = Boolean.parseBoolean(config.getRequiredString("OPC_ENABLED"));
        }catch( UnknownKeyException e) {}
        
        if( newServiceState != serviceEnabled) {
            if(serviceEnabled == false) {
                //Turn on
                dispatchConnection.addMessageListener(this);
                refresh = true;
            }else {
                //Turn Off
                dispatchConnection.removeMessageListener(this);
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
            List<OpcConnection> notConnectedConnectionList = new ArrayList<OpcConnection>();
            for( OpcConnection conn : opcServerMap.values() ) {
                if(!conn.isConnected()) {
                    notConnectedConnectionList.add(conn);
                }
            }
            if(notConnectedConnectionList.size() > 0) {
                log.error("Stopping Reconnects to OPC server until the configuration is fixed.");
                deadConnection = true;
                for(OpcConnection conn : notConnectedConnectionList) {
                    opcServerMap.remove(conn.getServerName());
                    conn.stop();
                }
            }
        }
        }catch(Exception e) {
            log.error("Exception in OPC Run Thread: ",e);
            return;
        }
    }
	
	/**
	 * Called by Spring to start the service.
	 */
	public void initialize() {
	    
        try{
            String ips = config.getRequiredString("OPC_SERVERS");
            StringTokenizer tokens = new StringTokenizer(ips,";",false);
            while( tokens.hasMoreTokens()) {
                String[] value = tokens.nextToken().split(":");
                serverAddressMap.put(value[0], value[1]);
            }            
        }catch( UnknownKeyException e) {
            log.error("OPC: Opc Server address's are not in the Master.cfg file. Defaulting to localhost");
        }
        
        try{
            refreshSeconds = Integer.parseInt(config.getRequiredString("OPC_REFRESH"));
        }catch( UnknownKeyException e) {
            log.error("OPC: Refresh rate is not in the Master.cfg file. Defaulting to 60 seconds ");
            refreshSeconds = 60;
        }
	    
	    globalScheduledExecutor.scheduleAtFixedRate(this, 0, refreshSeconds, TimeUnit.SECONDS );
        
	    try{
            serviceEnabled = Boolean.parseBoolean(config.getRequiredString("OPC_ENABLED"));
        }catch( UnknownKeyException e) {
            log.error("OPC: Enabled flag is not setup, defaulting to disabled. ");
        }
        
        if(serviceEnabled) {
            dispatchConnection.addMessageListener(this);
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

        List<FdrTranslation> opcTranslations = fdrTranslationDao.getByInterfaceType(FdrInterfaceType.OPC);

        /* Load Server Status Points */
        List<FdrTranslation> statusPointsTranslations = fdrTranslationDao.getByInterfaceType(FdrInterfaceType.SYSTEM);
        setupServerStatusPoints(statusPointsTranslations);

        for (FdrTranslation fdr : opcTranslations) {
            try {
                processFdrTranslation(fdr);
            } catch (ItemExistsException e) {
                log.error("OPC: Error registering Item. ");
            }
        }
        startupAll();
        refresh = false;
    }
	
	private void setupServerStatusPoints(List<FdrTranslation> statusPoints) {
	    opcServerToStatusPointIdMap.clear();
	    
	    for( FdrTranslation t : statusPoints ) {
	        String opcServerName = t.getParameter("Client");
	        if( StringUtils.isNotBlank(opcServerName) ) {
	            opcServerToStatusPointIdMap.put(opcServerName, t.getId());
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
	private void processFdrTranslation( FdrTranslation fdr ) {
		String server = fdr.getParameter("Server Name");
		String groupName = fdr.getParameter("OPC Group");
		String itemName = fdr.getParameter("OPC Item");
		String pointTypeStr = fdr.getParameter("POINTTYPE");
		String serverAddress;
		int pointType = -1;
		boolean newConnection = false;
		
		pointType = PointTypes.getType(pointTypeStr);

		if(pointType < 0) {   
		    log.error("Unknown Point type, skipping item: " + itemName);
		    return;
		}
		
		Integer pointId = fdr.getId();
		if( !pointIdToOpcServer.containsKey(pointId)) {
			
			serverAddress = serverAddressMap.get(server);
			if( serverAddress == null) {
			    log.warn("Server Address not found in Master.cfg. Defaulting to localhost for, " + server);
			    serverAddress = "localhost";
			}
			
			OpcConnection conn = opcServerMap.get(server);
			if( conn == null ) {
				conn = new OpcConnection(serverAddress,server,"YukonOpcClient",this);
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
				group = new OpcGroup(groupName, true, refreshSeconds*1000, 0.0f);
				group.addAsynchListener(this);
				conn.addGroup(group);
			}
			OpcItem item = new YukonOpcItem(itemName,true,"",pointId,pointType);
			group.addItem(item);
			
			/* Sync up maps */
		    if(newConnection) opcServerMap.put(server, conn);
			pointIdToOpcServer.put(pointId, conn);

		} else {
		    log.error("OPC: Opc Configuration Error. Point ID: " + pointId 
                                + " is mapped to multiple Opc Items, ignoring new item." );
		    return;
		}
	}
	
	/* Spring stuff */
	public void setFdrTranslationDao( FdrTranslationDao dao ) {
		this.fdrTranslationDao = dao;
	}

	public void setDispatchConnection(BasicServerConnection defDispConn) {
		this.dispatchConnection = defDispConn;
	}

    public void setGlobalScheduledExecutor(
            ScheduledExecutor globalScheduledExecutor) {
        this.globalScheduledExecutor = globalScheduledExecutor;
    }

    public void setConfig(ConfigurationSource config) {
        this.config = config;
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
				log.error(" Unable to start connection with OPC Server: " + conn.getServerName(),e);
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

		log.debug("OPC DEBUG: OPC Asynch event");
		OpcGroup group = event.getOPCGroup();
		List<OpcItem> items = group.getItems();
        Multi<Message> multi = new Multi<Message>();
        
		for (OpcItem opcItem : items) {
            YukonOpcItem yOpcItem = (YukonOpcItem)opcItem;
        	Calendar timeStamp = yOpcItem.getTimeStamp();
            boolean quality = yOpcItem.isQuality();
            String itemName = yOpcItem.getItemName();
            Integer pointId = yOpcItem.getPointId();

            if( pointId == null ) {
                log.error("OPC: Cannot find point id, " + pointId + ", link to OPC item, " + itemName + ", skipping update.");
                continue;
            }
            
            Variant value = yOpcItem.getValue();
            int type = value.getVariantType();

            double newValue;
            switch(type) {
                case VariantTypes.VT_BOOL: {
                    boolean b = value.getBoolean();
                    if(b) {
                        newValue = 1.0;
                    }
                    else {
                        newValue = 0.0;
                    }
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
                    log.error("Received an unhandled data type from the OPC server. Type: " + type);
                    continue;
                }
            }

            PointData pointData = new PointData();
            pointData.setId(pointId);
            pointData.setQuality(quality ? PointQualities.NORMAL_QUALITY : PointQualities.UNKNOWN_QUALITY);
            pointData.setType(yOpcItem.getPointType());
            pointData.setValue(newValue);
            pointData.setTimeStamp(timeStamp.getTime());
            
            log.debug("OPC DEBUG: Sending update for " + itemName + ". Value: " + newValue + " to PointID: " + pointId );
            multi.getVector().add(pointData);

        }
		try{
		    dispatchConnection.write(multi);
		}catch( ConnectionException e) {
		    log.info("OPC: Dispatch not connected. Point updates cannot go out.");
		}
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
		log.debug("OPC DEBUG: OPC dispatch event");
		Message msg = e.getMessage();
		if( msg instanceof DBChangeMsg) {
			int pid = ((DBChangeMsg)msg).getId();
			if( pointIdToOpcServer.containsKey(pid) ) {
				log.debug("OPC DEBUG: Change to Point involved with OPC");
				refresh = true;
			} else {
				try {
					fdrTranslationDao.getByPointIdAndType(pid,FdrInterfaceType.OPC);
					refresh = true;
					log.debug("OPC DEBUG: translation found, new FDR translation");
				} catch ( DataRetrievalFailureException exception ) {
					log.debug("OPC DEBUG: translation not found, not a new FDR translation");
				}
			}
		}
	}
	
	private void sendStatusUpdate(Integer statusPointId, double status) {
        PointData pointData = new PointData();
        pointData.setId(statusPointId);
        pointData.setQuality(PointQualities.NORMAL_QUALITY);
        pointData.setType(PointTypes.STATUS_POINT);
        pointData.setValue(status);
        pointData.setTimeStamp(new Date());
        dispatchConnection.write(pointData);
    }
	
    @Override
    public void connectionStatusChanged(String serverName, boolean newStatus) {
        double value;
        if(newStatus) {
            value = 1.0;
        }else {
            value = 0.0;
        }
        Integer id = opcServerToStatusPointIdMap.get(serverName);
        if( id != null)
            sendStatusUpdate(id,value);
    }
}
