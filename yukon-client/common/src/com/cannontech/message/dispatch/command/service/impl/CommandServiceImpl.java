package com.cannontech.message.dispatch.command.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.ThreeStateStatusState;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.command.service.CommandService;
import com.cannontech.message.util.Command;
import com.google.common.collect.ImmutableList;

public class CommandServiceImpl implements CommandService {
    private Logger log = YukonLogManager.getLogger(CommandServiceImpl.class);
    private DispatchClientConnection dispatchConnection;

    @Override
    public void sendAcknowledgeAlarm(int pointId, int condition, LiteYukonUser user) {

        List<Integer> data = ImmutableList.of(-1, pointId, condition);

        final Command command = new Command();
        command.setUserName(user.getUsername());
        command.setOperation(Command.ACKNOWLEGDE_ALARM);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());

        dispatchConnection.queue(command);
        log.debug("Sent command to acknowledge alarm for pointId:" + pointId + " condition:"
                  + condition);
    }

    @Override
    public void togglePointEnablement(int pointId, boolean disable, LiteYukonUser user) {

        List<Integer> data = new ArrayList<Integer>(4);
        data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);
        data.add(Command.ABLEMENT_POINT_IDTYPE);
        data.add(pointId);
        if (disable) {
            data.add(Command.ABLEMENT_DISABLE);
        } else {
            data.add(Command.ABLEMENT_ENABLE);
        }

        Command command = new Command();
        command.setUserName(user.getUsername());
        command.setOperation(Command.ABLEMENT_TOGGLE);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());
        dispatchConnection.queue(command);
        if (disable) {
            log.debug("Sent command to disable point:" + pointId);
        } else {
            log.debug("Sent command to enable point:" + pointId);
        }

    }

    @Override
    public void toggleDeviceEnablement(int deviceId, boolean disable, LiteYukonUser user) {

        List<Integer> data = new ArrayList<Integer>(4);
        data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);
        data.add(Command.ABLEMENT_DEVICE_IDTYPE);
        data.add(deviceId);
        if (disable) {
            data.add(Command.ABLEMENT_DISABLE);
        } else {
            data.add(Command.ABLEMENT_ENABLE);
        }
        Command command = new Command();
        command.setUserName(user.getUsername());
        command.setOperation(Command.ABLEMENT_TOGGLE);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());
        dispatchConnection.queue(command);
        if (disable) {
            log.debug("Sent command to disable device:" + deviceId);
        } else {
            log.debug("Sent command to enable device:" + deviceId);
        }
    }

    @Override
    public void sendAltScanRate(int deviceId, AltScanRate scanRate, LiteYukonUser user) {

        Command command = new Command();
        command.setOperation(Command.ALTERNATE_SCAN_RATE);
        command.setPriority(14);
        command.setUserName(user.getUsername());
        List<Integer> opArgList = new ArrayList<Integer>(4);
        opArgList.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);
        opArgList.add(deviceId);
        opArgList.add(-1);
        opArgList.add(scanRate.getDuration());
        command.setOpArgList(opArgList);
        dispatchConnection.queue(command);
        log.debug("Send Alternate Scan Rate command for deviceId: " + deviceId);
    }

    @Override
    public void toggleControlRequest(int deviceId, int pointId, boolean closed, LiteYukonUser user) {

        List<Integer> data = new ArrayList<Integer>(4);
        data.add(-1);
        if (deviceId > 0) {
            data.add(deviceId);
        }
        else {
            data.add(0);
        }
        data.add(pointId);
        if (closed) {
            data.add(ThreeStateStatusState.CLOSED.getRawState());
        } else {
            data.add(ThreeStateStatusState.OPEN.getRawState());
        }
        Command command = new Command();
        command.setUserName(user.getUsername());
        command.setOperation(Command.CONTROL_REQUEST);
        command.setOpArgList(data);
        command.setTimeStamp(new java.util.Date());
        dispatchConnection.queue(command);
        if (closed) {
            log.debug("Sent command for control request deviceId: " + deviceId + "pointId: "
                      + pointId + "state closed:" + ThreeStateStatusState.CLOSED.getRawState());
        } else {
            log.debug("Sent command for control request deviceId: " + deviceId + "pointId: "
                      + pointId + "state opened:" +ThreeStateStatusState.OPEN.getRawState());
        }
    }

    @Override
    public void sendAnalogOutputRequest(int pointId, double outputValue, LiteYukonUser user) {

        List<Integer> data = new ArrayList<Integer>(2);
        data.add(pointId);
        data.add((int) outputValue);

        Command command = new Command();
        command.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());
        command.setOperation(Command.ANALOG_OUTPUT_REQUEST);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());
        command.setUserName(user.getUsername());
        dispatchConnection.queue(command);
        log.debug("Sent analog output request for pointId: " + pointId + "value:" + outputValue);
    }

    @Override
    public void toggleControlEnablement(int deviceId, boolean disable, LiteYukonUser user) {

        List<Integer> data = new ArrayList<Integer>(4);
        data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);
        data.add(Command.ABLEMENT_DEVICE_IDTYPE);
        data.add(deviceId);
        if (disable) {
            data.add(Command.ABLEMENT_DISABLE);
        } else {
            data.add(Command.ABLEMENT_ENABLE);
        }
        Command command = new Command();
        command.setOperation(Command.CONTROL_ABLEMENT);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());
        command.setUserName(user.getUsername());
        dispatchConnection.queue(command);
        if (disable) {
            log.debug("Sent command to inhibit control for all points attached to deviceId: "
                      + deviceId);
        } else {
            log.debug("Sent command to allow control for all points attached to deviceId: "
                      + deviceId);
        }
    }

    public void setDispatchConnection(DispatchClientConnection dispatchConnection) {
        this.dispatchConnection = dispatchConnection;
    }
    
    @Override
    public void resetSeasonControlHrs() {
        
        List<Integer> data = new ArrayList<Integer>(1);
        data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);
        
        Command command = new Command();
        command.setOperation(Command.RESET_CNTRL_HOURS);
        command.setOpArgList(data);
        command.setTimeStamp(new Date());
        
        dispatchConnection.queue(command);
    }
    
}