package com.cannontech.common.alert.alarms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.model.Alert;
import com.cannontech.common.alert.service.AlertClearHandler;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.Command;

public class AlarmAlertClearHandler implements AlertClearHandler {
    private DispatchClientConnection dispatchConnection;
    private Logger log = YukonLogManager.getLogger(AlarmAlertClearHandler.class);

    @Override
    public void clear(Alert alert, LiteYukonUser user) {
        if (!(alert instanceof AlarmAlert)) return; 

        AlarmAlert alarmAlert = (AlarmAlert) alert;
        if (!alarmAlert.isUnacknowledgedAlarm()) return;
        
        final List<Integer> data = new ArrayList<Integer>(2);
        data.add(-1);  // this is the ClientRegistrationToken
        data.add(alarmAlert.getPointId());
        data.add(alarmAlert.getCondition());

        final Command command = new Command();
        command.setUserName(user.getUsername());
        command.setOperation(Command.ACKNOWLEGDE_ALARM);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());

        dispatchConnection.queue(command);
        log.debug("sent command to acknowledge alarm alert: " + alarmAlert);
    }
    
    public void setDispatchConnection(DispatchClientConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
    
}
