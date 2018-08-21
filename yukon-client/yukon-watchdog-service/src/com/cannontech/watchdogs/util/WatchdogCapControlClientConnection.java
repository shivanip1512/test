package com.cannontech.watchdogs.util;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.capcontrol.model.DeleteItem;
import com.cannontech.message.capcontrol.model.SpecialAreas;
import com.cannontech.message.capcontrol.model.SubAreas;
import com.cannontech.message.capcontrol.model.SubStations;
import com.cannontech.message.capcontrol.model.SubstationBuses;
import com.cannontech.message.capcontrol.model.SystemStatus;
import com.cannontech.message.capcontrol.model.VoltageRegulatorFlagMessage;
import com.cannontech.message.util.Message;
import com.cannontech.watchdogs.impl.CapControlServiceWatcher;
import com.cannontech.yukon.conns.CapControlClientConnection;

public class WatchdogCapControlClientConnection extends CapControlClientConnection {

    private static final Logger log = YukonLogManager.getLogger(WatchdogCapControlClientConnection.class);
    private CapControlServiceWatcher watcher;

    public void addWatchdogMessageListener(CapControlServiceWatcher watcher) {
        this.watcher = watcher;
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof SubstationBuses || msg instanceof SubStations || msg instanceof SpecialAreas
            || msg instanceof SubAreas || msg instanceof DeleteItem || msg instanceof SystemStatus
            || msg instanceof VoltageRegulatorFlagMessage) {
            watcher.handleMessage(msg);
        } else {
            log.debug("Other received message:" + msg.toString());
        }
    }

}

