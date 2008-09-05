package com.cannontech.common.opc.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.opc.OpcConnectionListener;
import com.cannontech.common.opc.YukonOpcConnection;
import com.cannontech.common.opc.impl.YukonOpcConnectionImpl;
import com.cannontech.common.opc.model.FdrDirection;
import com.cannontech.common.opc.model.FdrInterfaceType;
import com.cannontech.common.opc.model.FdrTranslation;
import com.cannontech.common.opc.model.YukonOpcItem;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;

public class OpcService implements OpcConnectionListener, DBChangeListener{
	
    /*Spring loaded*/
	private FdrTranslationDao fdrTranslationDao;
	private ScheduledExecutor globalScheduledExecutor;
	private ConfigurationSource config;	
	private PointDao pointDao;
	private AsyncDynamicDataSource dataSource;
	private DynamicDataSource dynamicDataSource;

	/*Master.cfg values*/
	private final Map<String,String> serverAddressMap;
	private final Set<PointQuality> goodQualitiesSet;
	private int refreshSeconds;
	private boolean serviceEnabled = false;
	private String cparmOpcItemName = "";
	
	/*Item Tracking Maps - dynamic*/
	private final Map<String,Integer> opcServerToStatusPointIdMap;	
	private final Map<String,YukonOpcConnection> opcConnectionMap;
    
    /*Logger*/
    private Logger log = YukonLogManager.getLogger(OpcService.class);
    
	public OpcService() {	    
		serverAddressMap = new HashMap<String,String>();
		opcServerToStatusPointIdMap = new HashMap<String,Integer>();
		goodQualitiesSet = new HashSet<PointQuality>();
		opcConnectionMap = new HashMap<String,YukonOpcConnection>();
	}
	
	/**
	 * Called by Spring to start the service.
	 */
	public void initialize() {
	    try{
            serviceEnabled = Boolean.parseBoolean(config.getRequiredString("OPC_ENABLED"));
        }catch( UnknownKeyException e) {
            log.debug(" Enabled flag is not setup, defaulting to disabled. ");
        }
        if (serviceEnabled) {	        
        	
        	try{
        		cparmOpcItemName = config.getRequiredString("OPC_STATUSITEM");
        		log.info(" Status Item name set to " + cparmOpcItemName);
        	} catch (UnknownKeyException e) {
	            cparmOpcItemName = "";
        	}
        	
        	try{
	            refreshSeconds = Integer.parseInt(config.getRequiredString("OPC_REFRESH"));
	        }catch( UnknownKeyException e) {
	            log.warn(" Refresh rate is not in the Master.cfg file. Defaulting to 60 seconds ");
	            refreshSeconds = 60;
	        }
		    
            /* Reset the Good Qualities List*/
            loadGoodQualities();
            
	        try{
	            String ips = config.getRequiredString("OPC_SERVERS");
	            StringTokenizer tokens = new StringTokenizer(ips,";",false);
	            while( tokens.hasMoreTokens()) {
	                String[] value = tokens.nextToken().split(":");
	                serverAddressMap.put(value[0], value[1]);
	            }
	            dataSource.addDBChangeListener(this);

	            setupService();
	        } catch ( UnknownKeyException e) {
	            log.error(" Opc Server address's are not in the Master.cfg file. OPC Shutting down.");
	            serviceEnabled = false;
	        }
	        
	        log.debug(serverAddressMap.toString());            
        }
	}
	
	/**
     * Call to reload all service configurations.
     */
    private void setupService() {

    	globalScheduledExecutor.execute(new Runnable(){
    		public void run(){
    	        List<FdrTranslation> opcTranslations = fdrTranslationDao.getByInterfaceType(FdrInterfaceType.OPC);

    	        /* Load Server Status Points */
    	        List<FdrTranslation> statusPointsTranslations = fdrTranslationDao.getByInterfaceType(FdrInterfaceType.SYSTEM);
    	        setupServerStatusPoints(statusPointsTranslations);
    	        

    	        for (FdrTranslation fdr : opcTranslations) {
    	        	processFdrTranslation(fdr);
    	        }
    		}
    	});
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
	private synchronized void processFdrTranslation(FdrTranslation fdr) {
		FdrDirection direction = fdr.getDirection();

		if( direction == FdrDirection.Receive || direction == FdrDirection.Send) {
			log.debug(" Add Item call");
			processOpcTranslation(fdr);
		} else {
			log.warn(" Unhandled Fdr Direction: " + direction + " Translation Id: " + fdr.getId());
		}
	}
	
	private synchronized void processOpcTranslation(FdrTranslation fdr) {
		LitePoint point;
		int offset = 0;
		double multiplier = 1.0;
		String serverAddress;
		
		String server = fdr.getParameter("Server Name");
		String groupName = fdr.getParameter("OPC Group");
		String itemName = fdr.getParameter("OPC Item");
		
		log.debug(" Parameters from fdrtranslation: ServerName: " + server + " groupName: " + groupName + " itemName: " + itemName);			

		//Validating input. Didn't think this would be needed, but
		if ((server == null) || (groupName == null) && (itemName == null))
		{
			log.error(" A Parameter is null, aborting Add item.");
			return;
		}
		
        int pointId = fdr.getId();
        
        try {
            point = pointDao.getLitePoint(pointId);
        } catch( NotFoundException e) {
            log.error(" Point for " + itemName + " was not found in the database.");
            return;
        }
        
        if (point.getPointType() == PointTypes.ANALOG_POINT) {
            try {
                offset = pointDao.getPointDataOffset(pointId);
            } catch(IncorrectResultSizeDataAccessException e) {
                log.error(" Data Offset for " + itemName + " was not found in the database.");
                return;
            }
            
            try {
                multiplier = pointDao.getPointMultiplier(pointId);  
            } catch(IncorrectResultSizeDataAccessException e) {
                log.error(" Multiplier for " + itemName + " was not found in the database.");
                return;
            }
        }
			
		serverAddress = serverAddressMap.get(server);
		
		if( serverAddress == null) {
		    log.warn("Server Address for " + server + " was not found in Master.cfg. Defaulting to localhost");
		    serverAddress = "localhost";
		}

		YukonOpcConnection conn = getConnection(server,serverAddress);
		
		YukonOpcItem item = null;
		if (fdr.getDirection() == FdrDirection.Receive) {
			item = conn.addReceiveItem(groupName,itemName,point.getPointID(),point.getPointType(),multiplier,offset);
		} else if (fdr.getDirection() == FdrDirection.Send) {
			item = conn.addSendItem(groupName,itemName,point.getPointID(),point.getPointType(),multiplier,offset);
		}
		
		if (item == null) {
			log.error(" Error adding item, "+ itemName + ". to connection");
			return;
		}
	}
	
	private void loadGoodQualities() {
		goodQualitiesSet.clear();
        String qualName = "";
        String goodQualitiesString = config.getString("OPC_GOODQUALITY");
        if (goodQualitiesString.equals("")) {
        	log.debug("Good Qualities not defined in Master.cfg. Defaulting to Normal and Manual");
        	goodQualitiesSet.add(PointQuality.Normal);
        	goodQualitiesSet.add(PointQuality.Manual);
        } else {
	        StringTokenizer tokens = new StringTokenizer(goodQualitiesString,",",false);
			while(tokens.hasMoreTokens()) {
	        	try{
			    	qualName = tokens.nextToken(); 
					PointQuality quality = PointQuality.valueOf(qualName);
					goodQualitiesSet.add(quality);
			    } catch ( IllegalArgumentException e) {
			    	log.error( qualName + " is not a Yukon Point Quality.");
			    }
			}
        }
	}
	
	/**
	 * Shuts down all OPC connections.
	 */
	private void shutdownAll() {
		for( YukonOpcConnection conn : opcConnectionMap.values() ) {
			conn.shutdown();
		}

	}
	
    @Override
    public synchronized void connectionStatusChanged(String serverName, boolean newStatus) {
        double value;
        if (newStatus) {
            value = 1.0;
        } else {
            value = 0.0;
        }
        Integer id = opcServerToStatusPointIdMap.get(serverName);
        if (id != null)
            sendStatusUpdate(id,value);
    }
    
	private void sendStatusUpdate(Integer statusPointId, double status) {
        PointData pointData = new PointData();
        pointData.setId(statusPointId);
        pointData.setQuality(PointQualities.NORMAL_QUALITY);
        pointData.setType(PointTypes.STATUS_POINT);
        pointData.setValue(status);
        pointData.setTimeStamp(new Date());
        try {
            dynamicDataSource.putValue(pointData);
        } catch (DispatchNotConnectedException e) {
            log.info(" Dispatch not connected. OPC Connection status point cannot be updated.");
        }
    }
	
    /**
     * dbChangeReceived(MessageEvent e)
     * 
     * If a db change message come in on a point we are monitoring we 
     * will restart the service to have the most up to date information.
     * All other messages from dispatch are ignored.
     */
    @Override
    public void dbChangeReceived(final DBChangeMsg dbChange) {
        
        if (dbChange.getDatabase() == DBChangeMsg.CHANGE_POINT_DB) {
            final int pid = dbChange.getId();
            log.debug(" OPC dispatch event with ID: " + pid);
            
            globalScheduledExecutor.execute(new Runnable() {
                public void run() {
                    
                    YukonOpcItem item = getItemFromConnections(pid);
                    if (item != null) {
                        log.debug(" Change to Point, " + pid +", involved with OPC");
                        //If here, we are already monitoring this point; lets remove it so it can be updated.
                        String connName = item.getServerName();
                        YukonOpcConnection conn = opcConnectionMap.get(connName);
                        if (conn == null) {
                            log.error(" Expected connection not found. DB Change not Processed.");
                            return;
                        }
                        conn.removeItem(item);
                    }
                    
                    //If its a delete, do not attempt to re-add it.
                    if (dbChange.getTypeOfChange() != DBChangeMsg.CHANGE_TYPE_DELETE) {
                        try {
                            FdrTranslation translation = fdrTranslationDao.getByPointIdAndType(pid, FdrInterfaceType.OPC);
                            //If found its a new one to add to the list.
                            //if not an exception will have thrown. Point could have been removed or its missing...
                            processOpcTranslation(translation);                            
                            log.debug(" translation found, new FDR translation");
                        } catch (IncorrectResultSizeDataAccessException exception) {
                            log.error(" translation not found for ID: " + pid);
                        }
                    }
                    cleanUpConnections();
                }
            });
        }
    }
	
    /* This will have to change with a different implementation of Connection. */
	private YukonOpcConnection createNewConnection(String serverAddress, String serverName, OpcConnectionListener listener) {
		
		YukonOpcConnectionImpl conn = new YukonOpcConnectionImpl(serverAddress,serverName,refreshSeconds);
		
		/*Configure Connection*/
		conn.addOpcConnectionListener(listener);
		conn.setDynamicDataSource(dynamicDataSource);
		conn.setGoodQualitiesSet(goodQualitiesSet);
		conn.setDataSource(dataSource);
		conn.setScheduledExecutor(globalScheduledExecutor);
		
		if (!cparmOpcItemName.equals("")) {
			conn.setStatusItemName(cparmOpcItemName);
		}
		
		return conn;
	}
	
	private YukonOpcConnection getConnection(String server, String serverAddress) {
		YukonOpcConnection conn = opcConnectionMap.get(server);
		
		if (conn == null) {
			conn = createNewConnection(serverAddress, server, this);
			conn.connect();
			opcConnectionMap.put(server, conn);
		}
		return conn;
	}
	
	/*This can only work while we do not support recv and send from same point.*/
	private YukonOpcItem getItemFromConnections(int pid) {
		YukonOpcItem item = null;

		for (YukonOpcConnection conn : opcConnectionMap.values()) {
			item = conn.getOpcReceiveItem(pid);
			if (item == null) {
				item = conn.getOpcSendItem(pid);
			}
			if (item != null) {
				return item;
			}
		}
		
		return item;
	}
	
	private void cleanUpConnections() {
        //Check for empty connections. Shut them down, clean them up.
        Iterator<Map.Entry<String, YukonOpcConnection>> iter = opcConnectionMap.entrySet().iterator();
		while ( iter.hasNext()) {
			Map.Entry<String, YukonOpcConnection> entry = iter.next();
			YukonOpcConnection conn = entry.getValue();
			if (conn.isEmpty())
			{
				conn.shutdown();
				opcConnectionMap.remove(conn);
			}
		}
	}
	
	/* Spring stuff */
	public void setFdrTranslationDao( FdrTranslationDao dao ) {
		this.fdrTranslationDao = dao;
	}

    public void setConfig(ConfigurationSource config) {
        this.config = config;
    }
    
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public void setDataSource(AsyncDynamicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public void setGlobalScheduledExecutor(ScheduledExecutor globalScheduledExecutor) {
    	this.globalScheduledExecutor = globalScheduledExecutor;
    }
    
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
    	this.dynamicDataSource = dynamicDataSource;
    }
}
