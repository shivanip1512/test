package com.cannontech.common.opc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.UnknownKeyException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.opc.OpcConnectionListener;
import com.cannontech.common.opc.YukonOpcConnection;
import com.cannontech.common.opc.impl.YukonOpcConnectionImpl;
import com.cannontech.common.opc.model.YukonOpcItem;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.FdrTranslationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class OpcService implements OpcConnectionListener, DBChangeListener {

    /* Spring loaded */
    @Autowired private FdrTranslationDao fdrTranslationDao;
    @Autowired @Qualifier("main") private ScheduledExecutor globalScheduledExecutor;
    @Autowired private ConfigurationSource config;
    @Autowired private PointDao pointDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

    /* Master.cfg values */
    private final Map<String, String> serverAddressMap;
    private final Map<String,String> opcServerToStatusItemNameMap;
    private final Set<PointQuality> goodQualitiesSet;
    private int connectionRetryRate;
    private int dataUpdateRate;
    private boolean serviceEnabled = false;

    /* Item Tracking Maps - dynamic */
    private final Map<String, Integer> opcServerToStatusPointIdMap;
    private final Map<String, YukonOpcConnection> opcConnectionMap;

    /* Logger */
    private Logger log = YukonLogManager.getLogger(OpcService.class);

    public OpcService() {
        serverAddressMap = Maps.newHashMap();
        opcServerToStatusItemNameMap = Maps.newHashMap();
        opcServerToStatusPointIdMap = Maps.newHashMap();
        goodQualitiesSet = Sets.newHashSet();
        opcConnectionMap = Maps.newHashMap();
    }

    /**
     * Called by Spring to start the service.
     */
    @PostConstruct
    public void initialize() {
        try {
            serviceEnabled = Boolean.parseBoolean(config.getRequiredString("OPC_ENABLED"));
        } catch (UnknownKeyException e) {
            log.debug(" Enabled flag is not setup, defaulting to disabled. ");
            return;
        }
        
        if (serviceEnabled) {

            try {
                connectionRetryRate = Integer.parseInt(config.getRequiredString("OPC_CONN_RETRY_RATE"));
            } catch (UnknownKeyException e) {
                log.warn(" Connection Retry rate is not in the Master.cfg file. Defaulting OPC_CONN_RETRY_RATE to 60 seconds ");
                connectionRetryRate = 60;
            }

            try {
                dataUpdateRate = Integer.parseInt(config.getRequiredString("OPC_DATA_UPDATE_RATE"));
            } catch (UnknownKeyException e) {
                log.warn(" Data update rate is not in the Master.cfg file. Defaulting OPC_DATA_UPDATE_RATE to 60 seconds ");
                dataUpdateRate = 60;
            }
            
            /* Reset the Good Qualities List */
            loadGoodQualities();

            try {
                loadLinkStatusNames();
            } catch (UnknownKeyException e) {
                log.warn("No Status Items specified in master.cfg, will use default 'YukonStatusGroup.YukonStatus' for all connections.");
            }
            
            try {
                String ips = config.getRequiredString("OPC_SERVERS");
                StringTokenizer tokens = new StringTokenizer(ips, ";", false);
                while (tokens.hasMoreTokens()) {
                    String[] value = tokens.nextToken().split(":");
                    serverAddressMap.put(value[0], value[1]);
                }
                asyncDynamicDataSource.addDBChangeListener(this);

                setupService();
            } catch (UnknownKeyException e) {
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

        globalScheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
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

        for (FdrTranslation t : statusPoints) {
            String opcServerName = t.getParameter("Client");
            if (StringUtils.isNotBlank(opcServerName)) {
                opcServerToStatusPointIdMap.put(opcServerName, t.getPointId());
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

        if (direction == FdrDirection.RECEIVE || direction == FdrDirection.SEND) {
            log.debug(" Add Item call");
            processOpcTranslation(fdr);
        } else {
            log.warn(" Unhandled Fdr Direction: " + direction + " Translation Id: "
                     + fdr.getPointId());
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

        log.debug(" Parameters from fdrtranslation: ServerName: " + server + " groupName: "
                  + groupName + " itemName: " + itemName);

        // Validating input. Didn't think this would be needed, but
        if ((server == null) || (groupName == null) && (itemName == null))
        {
            log.error(" A Parameter is null, aborting Add item.");
            return;
        }

        int pointId = fdr.getPointId();

        try {
            point = pointDao.getLitePoint(pointId);
        } catch (NotFoundException e) {
            log.error(" Point for " + itemName + " was not found in the database.");
            return;
        }

        if (point.getPointTypeEnum() == PointType.Analog) {
            try {
                offset = (int)(double)point.getDataOffset();
            } catch (NullPointerException e) {
                log.error(" Data Offset for " + itemName + " was not found in the database.");
                return;
            }

            try {
                multiplier = point.getMultiplier();
            } catch (NullPointerException e) {
                log.error(" Multiplier for " + itemName + " was not found in the database.");
                return;
            }
        }

        serverAddress = serverAddressMap.get(server);

        if (serverAddress == null) {
            log.warn("Server Address for " + server
                     + " was not found in Master.cfg. Defaulting to localhost");
            serverAddress = "localhost";
        }

        YukonOpcConnection conn = getConnection(server, serverAddress);

        YukonOpcItem item = null;
        if (fdr.getDirection() == FdrDirection.RECEIVE) {
            item =
                conn.addReceiveItem(groupName,
                                    itemName,
                                    point.getPointID(),
                                    point.getPointType(),
                                    multiplier,
                                    offset);
        } else if (fdr.getDirection() == FdrDirection.SEND) {
            item =
                conn.addSendItem(groupName,
                                 itemName,
                                 point.getPointID(),
                                 point.getPointType(),
                                 multiplier,
                                 offset);
        }

        if (item == null) {
            log.error(" Error adding item, " + itemName + " to connection");
            return;
        }
    }

    /**
     * Loads the Server Name to Link Status Item Name Map.
     */
    private void loadLinkStatusNames() {
        String masterCfgLine = config.getRequiredString("OPC_SERVER_TO_STATUS_ITEM_NAME");
        StringTokenizer tokens = new StringTokenizer(masterCfgLine, ";", false);
        while (tokens.hasMoreTokens()) {
            String[] value = tokens.nextToken().split(":");
            opcServerToStatusItemNameMap.put(value[0], value[1]);
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
            StringTokenizer tokens = new StringTokenizer(goodQualitiesString, ",", false);
            while (tokens.hasMoreTokens()) {
                try {
                    qualName = tokens.nextToken();
                    PointQuality quality = PointQuality.valueOf(qualName);
                    goodQualitiesSet.add(quality);
                } catch (IllegalArgumentException e) {
                    log.error(qualName + " is not a Yukon Point Quality.");
                }
            }
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
            sendStatusUpdate(id, value);
    }

    private void sendStatusUpdate(Integer statusPointId, double status) {
        PointData pointData = new PointData();
        pointData.setId(statusPointId);
        pointData.setPointQuality(PointQuality.Normal);
        pointData.setType(PointType.Status.getPointTypeId());
        pointData.setValue(status);
        pointData.setTimeStamp(new Date());
        try {
            asyncDynamicDataSource.putValue(pointData);
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
        // List for the points to reload.
        final List<FdrTranslation> translationList = new ArrayList<FdrTranslation>();

        if (dbChange.getDatabase() == DBChangeMsg.CHANGE_POINT_DB) {
            // This is a single change, so the id in the message is the point id
            int pid = dbChange.getId();
            log.debug(" OPC dispatch event with ID: " + pid);
            FdrTranslation translation;

            try {
                translation = fdrTranslationDao.getByPointIdAndType(pid, FdrInterfaceType.OPC);
                log.debug(" Change to Point, " + translation.getPointId() + ", involved with OPC");
                translationList.add(translation);
            } catch (IncorrectResultSizeDataAccessException e) {
                log.debug(" Point Change does not have an OPC translation attached to it. " + pid);
            }

            // Removes the item from the connection even if it is there no matter what message type.
            YukonOpcItem item = getItemFromConnections(pid);
            if (item != null) {
                // If here, we are already monitoring this point; lets remove it so it can be
                // updated.
                String connName = item.getServerName();
                YukonOpcConnection conn = opcConnectionMap.get(connName);
                if (conn == null) {
                    log.error(" Expected connection not found. DB Change not Processed.");
                    return;
                }
                log.debug(" Removing Item from OPC Interface. " + pid);
                conn.removeItem(item);
            }

        } else if (dbChange.getDatabase() == DBChangeMsg.CHANGE_PAO_DB
                   && dbChange.getDbChangeType() != DbChangeType.DELETE) {
            // This is for a device, need to grab all point id's related to it.
            int paoId = dbChange.getId();
            log.debug(" OPC dispatch event with ID: " + paoId);

            List<FdrTranslation> translations =
                fdrTranslationDao.getByPaobjectIdAndType(paoId, FdrInterfaceType.OPC);

            // Working off the basis that point delete messages will be sent.
            // If this is a PAO delete, this query will return nothing, those deletes will come
            // from delete messages
            translationList.addAll(translations);
        } else {
            // Device Delete Not handled. Point messages will be sent.
            return;
        }

        globalScheduledExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (FdrTranslation translation : translationList) {
                    // If its a delete, do not attempt to re-add it. This is for all points being
                    // processed.
                    if (dbChange.getDbChangeType() != DbChangeType.DELETE) {
                        processOpcTranslation(translation);
                    }
                }
                cleanUpConnections();
            }
        });
    }

    /* This will have to change with a different implementation of Connection. */
    private YukonOpcConnection createNewConnection(String serverAddress, String serverName, OpcConnectionListener listener) {
        String statusItemName = opcServerToStatusItemNameMap.get(serverName);
        if (statusItemName == null) {
            statusItemName = "YukonStatusGroup.YukonStatus";
        }

        YukonOpcConnectionImpl conn = new YukonOpcConnectionImpl(serverAddress, serverName, statusItemName, connectionRetryRate, dataUpdateRate);

        /* Configure Connection */
        conn.addOpcConnectionListener(listener);
        conn.setDataSource(asyncDynamicDataSource);
        conn.setGoodQualitiesSet(goodQualitiesSet);
        conn.setScheduledExecutor(globalScheduledExecutor);


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

    /* This can only work while we do not support recv and send from same point. */
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
        // Check for empty connections. Shut them down, clean them up.
        Iterator<Map.Entry<String, YukonOpcConnection>> iter =
            opcConnectionMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, YukonOpcConnection> entry = iter.next();
            YukonOpcConnection conn = entry.getValue();
            if (conn.isEmpty())
            {
                conn.shutdown();
                opcConnectionMap.remove(entry.getKey());
            }
        }
    }
}
