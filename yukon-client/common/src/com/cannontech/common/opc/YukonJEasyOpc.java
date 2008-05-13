package com.cannontech.common.opc;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.opc.service.OpcService;


import javafish.clients.opc.component.OpcGroup;
import javafish.clients.opc.component.OpcItem;
import javafish.clients.opc.exception.ComponentNotFoundException;
import javafish.clients.opc.exception.ConnectivityException;
import javafish.clients.opc.exception.UnableAddGroupException;
import javafish.clients.opc.exception.UnableAddItemException;
import javafish.clients.opc.exception.UnableRemoveGroupException;
import javafish.clients.opc.lang.Translate;
import javafish.clients.opc.JOpc;

public class YukonJEasyOpc extends JOpc {

    /** refresh for global asynch loop */
    private final int WAITIME = 100; // ms

    /** timeout for connectivity checker */
    private final int CONNTIME = 10000; // ms

    /** opc-client running thread */
    private boolean running = false;

    /** check connectivity */
    private boolean connected = false;

    private Logger log = YukonLogManager.getLogger(OpcService.class);
    
    protected EventListenerList connectionStatusListeners;
    
    /**
     * Create new YukonJEasyOpc client
     * @param host - host computer
     * @param serverProgID - OPC Server name
     * @param serverClientHandle - user name for OPC Client
     */
    public YukonJEasyOpc(String host, String serverProgID,
            String serverClientHandle, Properties props) {
        super(host, serverProgID, serverClientHandle, props);
        connectionStatusListeners = new EventListenerList();
    }

    public YukonJEasyOpc(String host, String serverProgID,
            String serverClientHandle) {
        super(host, serverProgID, serverClientHandle);
        connectionStatusListeners = new EventListenerList();
    }

    /**
     * Asynch thread of client is active
     * @return is running, boolean
     */
    synchronized public boolean isRunning() {
        return running;
    }
    synchronized public boolean isConnected() {
        return connected;
    }

    /**
     * Stop OPC Client thread
     */
    synchronized public void terminate() {
        running = false;
        notifyAll();
    }

    @SuppressWarnings("unchecked")
    public void addOpcConnectionListener(OpcConnectionListener listener) {
        List list = Arrays.asList(connectionStatusListeners.getListenerList());
        if (!list.contains(listener)) {
            connectionStatusListeners.add(OpcConnectionListener.class, listener);
        }
    }

    @SuppressWarnings("unchecked")
    public void removeOpcConnectionListener(OpcConnectionListener listener) {
        List list = Arrays.asList(connectionStatusListeners.getListenerList());
        if (list.contains(listener)) {
            connectionStatusListeners.remove(OpcConnectionListener.class,
                                             listener);
        }
    }

    @SuppressWarnings({ "unused", "unchecked" })
    private void sendConnectionStatus(boolean status) {
        connected = status;
        Object[] list = connectionStatusListeners.getListenerList();
        for (int i = 0; i < list.length; i += 2) {
            Class listenerClass = (Class) (list[i]);
            if (listenerClass == OpcConnectionListener.class) {
                OpcConnectionListener listener = (OpcConnectionListener) (list[i + 1]);
                listener.connectionStatusChanged(getServerProgID(),status);
            }
        }
    }
    
    public void registerGroups() {
		
    	OpcGroup groups [] = this.getGroupsAsArray();
		
    	for( OpcGroup g : groups) {
			List<OpcItem> items = g.getItems();
			
			try{
				super.registerGroup(g);
			} catch (UnableAddGroupException e) {
	    		log.error(" Unable to add group: " + g.getGroupName());
	    	}
			
			for (OpcItem i : items) {
				try{
					super.registerItem(g, i);
				} catch (UnableAddItemException e) {
		    		log.error(" Unable to add Item: " + i.getItemName());
		    		g.removeItem(i);
		    	} catch (ComponentNotFoundException e) {
		    		log.error(" Component Not Found Exception: " + i.getItemName());
		    		g.removeItem(i);
		    	}
			}
    	}
    }
    
    @Override
    synchronized public void run() {
        running = true;

        // global loop
        while (running) {
            try {
                // connect to OPC-Server
                connect();
                log.info(Translate.getString("JEASYOPC_CONNECTED"));
                
                // register groups on server
                registerGroups();
                log.info(Translate.getString("JEASYOPC_GRP_REG"));

                // run asynchronous mode 2.0
                for (int i = 0; i < groups.size(); i++) {
                    asynch20Read(getGroupByClientHandle(i));
                }
                log.info(Translate.getString("JEASYOPC_ASYNCH20_START"));
                sendConnectionStatus(true);
                // life cycle
                while (running) {

                    if (!ping()) {
                        throw new ConnectivityException(Translate.getString("CONNECTIVITY_EXCEPTION") + " " + getHost() + "->" + getServerProgID());
                    }

                    // check and clone downloaded group
                    OpcGroup group = getDownloadGroup();

                    if (group != null) { // send to listeners
                        sendOpcGroup(group);
                    }

                    try { // sleep time
                        wait(WAITIME);
                    } catch (InterruptedException e) {
                        log.error(e);
                    }
                } // life cycle

            } catch (ConnectivityException e) {
                log.error(e);
                sendConnectionStatus(false);
                try {
                    wait(CONNTIME); // try reconnect
                } catch (InterruptedException e1) {
                    log.error(e1);
                }
            } catch (Exception e) {
                log.error(e);
                sendConnectionStatus(false);
                try {
                    wait(CONNTIME); // try reconnect
                } catch (InterruptedException e1) {
                    log.error(e1);
                }
            }
            ;
        }
        
        if(connected) {
	        try {
	            unregisterGroups();
	            log.info(Translate.getString("JEASYOPC_GRP_UNREG"));
	        } catch (UnableRemoveGroupException e) {
	            log.error(e);
	        }
        }
        
        sendConnectionStatus(false);
        connected = false;
        running = false;
        log.info(Translate.getString("JEASYOPC_DISCONNECTED"));
    }

}
