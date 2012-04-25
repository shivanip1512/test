package com.cannontech.common.opc.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.opc.OpcConnectionListener;
import com.cannontech.common.opc.YukonOpcConnection;
import com.cannontech.common.opc.model.YukonOpcItem;
import com.cannontech.common.opc.model.YukonOpcServer;
import com.cannontech.common.opc.service.OpcService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dynamic.AllPointDataListener;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.netmodule.jpc.driver.opc.OpcGroup;
import com.netmodule.jpc.driver.opc.OpcItem;
import com.netmodule.jpc.driver.opc.OpcServer;

/**
 *  Extending to provide support for notifications to Yukon upon connects and disconnects.
 *  
 * @author tspar
 *
 */
public class YukonOpcConnectionImpl implements YukonOpcConnection, Runnable, AllPointDataListener {	
	private Logger log = YukonLogManager.getLogger(OpcService.class);
	
	public static final String statusGroupName = "YukonStatusGroup";
	private String statusItemName;
	
	public static final int    yukonOpcStatusItemId = 1;
	public static final int    yukonOpcStatusGroupId = 1;
	
	private int nextClientGroupHandle = yukonOpcStatusGroupId + 1;
	
    protected EventListenerList connectionStatusListeners;
	private String serverName;
	private String hostIp;
    private boolean connStatus = false;
    private boolean running = true;
    private boolean reConnecting = false;
    
    private final Map<String,OpcGroup> groupMap;
    private final Map<OpcGroup,YukonOpcAdviceSinkImpl> sinkMap;
    private final Map<Integer,YukonOpcItem> receiveItemMap;
    private final Map<Integer,YukonOpcItem> sendItemMap;
    private final int refreshRate;
    
    private YukonOpcServer opcServer;
    private int statusItemId = -1;
	private OpcGroup statusGroup = null;
    
	private ScheduledExecutor scheduledExecutor;
	private DynamicDataSource dynamicDataSource;
	private AsyncDynamicDataSource dataSource;
	private Set<PointQuality> goodQualitiesSet;
	
    public YukonOpcConnectionImpl(String host, String serverName, String statusItemName, int refreshRate) {
		this.hostIp = host;
		this.serverName = serverName;
		this.refreshRate = refreshRate;
		this.statusItemName = statusItemName;
		
		connectionStatusListeners = new EventListenerList();
		groupMap = new HashMap<String,OpcGroup>();
		sinkMap = new HashMap<OpcGroup,YukonOpcAdviceSinkImpl>();
		receiveItemMap = new HashMap<Integer,YukonOpcItem>();
		sendItemMap = new HashMap<Integer,YukonOpcItem>();
		
		opcServer = new YukonOpcServer(new OpcServer(),this.statusItemName);
	}
		
	public boolean connect() {
		if(running) {
			boolean ret = opcServer.connect(hostIp,serverName);
			
			if (ret) {
				log.info( serverName + ", is connected." );
				boolean status = registerStatusItem();
				
				if (!status) {
					log.error(" Error while registering status item. Connection Terminated.");
					opcServer.disconnect();
					return false;
				}
				
				if (reConnecting) {
					reAddItemsAfterReconnect();
				}
				
			} else {
				log.info( serverName + ", is NOT connected." );
			}
			scheduledExecutor.schedule(this, refreshRate, TimeUnit.SECONDS );
			
			return ret;
		} else {
			log.info(" Shutdown has been called. Connect canceled.");
			return false;
		}
	}
	
	public boolean reconnect() {
		running = true;
		reConnecting = true;
		log.info(" Reconnecting to Opc Server " + serverName + ". ");
		return connect();
	}
	
	public void run() {
		if (running) {
			if (reConnecting) {
				log.info(" Attempting to connect to " + serverName + ".");
				
				boolean ret = connect();
								
				if (!ret) {
					log.info(" Attempt to connect to " + serverName + " failed. Retrying in " + refreshRate + " seconds.");
				} else {
					log.info(" Connected to " + serverName);
				}
			} else {
				/*Check Conn status*/
				boolean ret = determineConnectionStatus();
				
				if (!ret) {
					reconnect();
				} else {
					scheduledExecutor.schedule(this, refreshRate, TimeUnit.SECONDS );
				}
			}
		}
	}
	
	private boolean registerStatusItem() {
	    statusGroup = opcServer.addGroup(statusGroupName, yukonOpcStatusGroupId,refreshRate);
	    
		int [] retIds = statusGroup.addItems(new String[]{statusItemName}, new int[]{yukonOpcStatusItemId});
		statusItemId = retIds[0];
		
		boolean ret = statusItemId > 0;
		
		if (ret) {
			log.info( serverName + "'s, Status Item is registered." + statusItemName);
			sendConnectionStatus(true);
		} else {
//		        int [] retIdsb = statusGroup.addItems(new String[]{statusItemName}, new int[]{yukonOpcStatusItemId});
//		        statusItemId = retIdsb[0]; 
//		        ret = statusItemId > 0;
//		        if (ret) {
//		            log.info( serverName + "'s, Status Item is registered." + statusItemName);
//		            sendConnectionStatus(true);
//		        } else {
		            log.error( serverName + "'s, Status Item could not be registered. " + statusItemName );
//		        }
		}
		
		return ret;
	}
	
	public void shutdown() {
		running = false;
		log.info(" Closing Opc Connection to " + serverName + ". ");
		disconnect();
	}
	
	private void disconnect() {
		for (OpcGroup group : groupMap.values()) {
			opcServer.removeGroup(group);
		}
		opcServer.disconnect();
		
		sinkMap.clear();
		groupMap.clear();
		
		/*Unregister for point data messages*/
		dataSource.unRegisterForPointData(this);
		
		sendConnectionStatus(false);
	}
	
	public boolean isConnected() {
		return connStatus;
	}
	
	public boolean isShutdown() {
		return !running;
	}
	
	public boolean isEmpty() {
		boolean ret = false;
		
		if (running) {
			if(receiveItemMap.isEmpty() && sendItemMap.isEmpty()) {
				ret = true;
			}
		} else {
			ret = true;
		}
		
		return ret;
	}
	
    public void removeOpcConnectionListener(OpcConnectionListener listener) {
        connectionStatusListeners.remove(OpcConnectionListener.class, listener);
    }
    
    public void addOpcConnectionListener(OpcConnectionListener listener) {
        connectionStatusListeners.add(OpcConnectionListener.class, listener);
    }

    private void sendConnectionStatus(boolean status) {
    	connStatus = status;
        Object[] list = connectionStatusListeners.getListenerList();
        for (int i = 0; i < list.length; i += 2) {
            if (list[i] == OpcConnectionListener.class) {
                OpcConnectionListener listener = (OpcConnectionListener) (list[i+1]);
                listener.connectionStatusChanged(serverName,status);
            }
        }
    }

    private YukonOpcItem addReceiveItem(YukonOpcItem item) {
		OpcGroup group = groupMap.get(item.getGroupName());
		
		item = addItem(item.getGroupName(),item.getItemName(),item.getPointId(), item.getPointType(),item.getMultiplier(),item.getOffset());
		
		if (item != null) {
			receiveItemMap.put(item.getHandle(),item);
			if (group == null) { /* Only should make a sink if this is a new group. */
				group = groupMap.get(item.getGroupName());
				/*Only need 'sinks' for the receive points*/
				sinkMap.put(group, new YukonOpcAdviceSinkImpl(group,this,dynamicDataSource));
			}
		}
		return item;
	}
    
	@Override
	public YukonOpcItem addReceiveItem(String groupName, String itemName, int pointId, int pointType, double multiplier, int offset) {
		if (running) {

			YukonOpcItem item = new YukonOpcItemImpl();
			item.setItemName(itemName);
			item.setHandle(pointId);
			item.setServerHandle(0);
			item.setGroupName(groupName);
			item.setServerName(serverName);
			item.setMultiplier(multiplier);
			item.setOffset(offset);
			item.setPointType(pointType);
			item.setPointId(pointId);
			
			return addReceiveItem(item);
		}
		return null;
	}
	
	private YukonOpcItem addSendItem (YukonOpcItem item) {		
    	item = addItem(item.getGroupName(),item.getItemName(),item.getPointId(),item.getPointType(),item.getMultiplier(),item.getOffset());
		
    	if (item != null) {
			sendItemMap.put(item.getHandle(),item);
			
			Set<Integer> set = new HashSet<Integer>();
			set.add(item.getHandle());
			
			dataSource.registerForPointData(this, set);
		}
		return item;	
	}
	@Override
	public YukonOpcItem addSendItem(String groupName, String itemName, int pointId, int pointType, double multiplier, int offset) {
		if (running) {
			YukonOpcItem item = new YukonOpcItemImpl();
			item.setItemName(itemName);
			item.setHandle(pointId);
			item.setServerHandle(0);
			item.setGroupName(groupName);
			item.setServerName(serverName);
			item.setMultiplier(multiplier);
			item.setOffset(offset);
			item.setPointType(pointType);
			item.setPointId(pointId);
			
			return addSendItem(item);
		}
		return null;
	}
	
	private YukonOpcItem addItem(String groupName, String itemName, int pointId, int pointType, double multiplier, int offset) {
		OpcGroup group = groupMap.get(groupName);
		
		if (group == null) {
			//New Group
			group = opcServer.addGroup(groupName, nextClientGroupHandle, refreshRate);
			nextClientGroupHandle++;
			if (group == null){
				log.error(" Error adding Group " + groupName);
				return null;
			}
			groupMap.put(groupName,group);
			
		}
		log.debug(" Group Name: " + groupName + " Item Name: " + itemName + " Point Id: " + pointId);
		int[] ret = group.addItems(new String[]{itemName}, new int[]{pointId});
		YukonOpcItem item = null;
		
		/* addItems returns a list of id's for the list of items to be added. 0's indicate error in the add.*/
		if (ret[0] != 0) {
			item = new YukonOpcItemImpl();
			item.setItemName(itemName);
			item.setHandle(pointId);
			item.setServerHandle(ret[0]);
			item.setGroupName(groupName);
			item.setServerName(serverName);
			item.setMultiplier(multiplier);
			item.setOffset(offset);
			//item.setLitePoint(point);
			item.setPointType(pointType);
			item.setPointId(pointId);
		}
		
		return item;
	}
	
	private void reAddItemsAfterReconnect() {
		reConnecting = false;
		//re register items we have.
		Map<Integer,YukonOpcItem> receiveItemMapTemp = new HashMap<Integer,YukonOpcItem>();
		receiveItemMapTemp.putAll(receiveItemMap);
		receiveItemMap.clear();
		
		Map<Integer,YukonOpcItem> sendItemMapTemp = new HashMap<Integer,YukonOpcItem>();
		sendItemMapTemp.putAll(sendItemMap);
		sendItemMap.clear();
		
		for (YukonOpcItem item : receiveItemMapTemp.values()) {
			addReceiveItem(item);
		}
		
		for (YukonOpcItem item : sendItemMapTemp.values()) {
			addSendItem(item);
		}
	}
	
	@Override
	public YukonOpcItem getOpcReceiveItem(int clientHandle) {
		return receiveItemMap.get(clientHandle);
	}

	@Override
	public YukonOpcItem getOpcSendItem(int clientHandle) {
		return sendItemMap.get(clientHandle);
	}
	
	@Override
	public void pointDataReceived(PointValueQualityHolder pointData) {
		if (running) {
			int id = pointData.getId();
			//Find the item in the send list. If not there, it is not a send point and we are likely registered for this by mistake.
			YukonOpcItem item = getOpcSendItem(id);
			
			log.debug(" POINT DATA RECEIVED, Point Id: " + id + " Value: " + pointData.getValue());
			
			if (item != null) {
				PointQuality quality = pointData.getPointQuality();
				boolean good = goodQualitiesSet.contains(quality);
				if (good) { //Do not send if it is a bad quality
					OpcGroup group = groupMap.get(item.getGroupName());
					if (group != null) {
						Runnable run = new SendMessageToOpcRunnable(pointData,group,item);
						scheduledExecutor.execute(run);
					} else {
						log.error(" Group not found for Opc Item " + item.getItemName() + ". Could not send Point Update.");
					}
				} else {
					log.info(" Bad quality point, not sending update to Opc.");
				}
			} else {
				log.error(" Attempted to send to an opc point not configured for sending. Point Id:" + id);
				return;
			}
		}
	}
	
	@Override
	public boolean removeItem(YukonOpcItem item) {

		//Lets remove it from our lists. (halting updates, etc)
		receiveItemMap.remove(item.getHandle());
		sendItemMap.remove(item.getHandle());
		
		OpcGroup group = groupMap.get(item.getGroupName());
		
		if (group == null) {
			log.warn("Group does not exist on connection, cannot remove item: " + item.getItemName());
			return false;
		}
		
		int ret = group.removeItems(new int[]{item.getServerHandle()});
		
		if (ret != 0) {
			log.warn("Cannot remove item: " + item.getItemName() + ". Error code: " + ret);
			return false;
		}
		
		/* Unregister for point data updates. */
		Set<Integer> set = new HashSet<Integer>();
		set.add(item.getHandle());
		
		/*if this was a send point. this might blow up?*/
		dataSource.unRegisterForPointData(this, set);
		
		return true;
	}

	@Override
	public String getServerName() {
		return serverName;
	}
	
	@Override
	public void setServerName(String name) {
		serverName = name;
	}
	
	public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
		this.dynamicDataSource = dynamicDataSource;
	}
	
	public void setDataSource(AsyncDynamicDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setGoodQualitiesSet (Set<PointQuality> goodQualitiesSet) {
		this.goodQualitiesSet = goodQualitiesSet;
	}
	
	public void setScheduledExecutor (ScheduledExecutor executor) {
		this.scheduledExecutor = executor;
	}
	
    /**
     * Requires a server to have a group 'YukonStatus' with a boolean item 'Status'
     * We poll for the item, if there are error's the connection is not up.
     * 
     * This is needed since JOPC-Bridge does not support connection link status monitoring.
     * 
     */
    private boolean determineConnectionStatus() {
    	if( statusGroup == null || statusItemId == 0 || !running) {
    		return false;
    	}
    	
    	OpcItem [] items = statusGroup.read(OpcGroup.OPC_DS_DEVICE, new int [] {statusItemId});
    	
    	if (items[0] == null) {
    		log.warn(" Unable to read status item. Opc connection is down.");
    		shutdown();
    		return false;
    	}
    	sendConnectionStatus(true);
    	return true;
    }
}
