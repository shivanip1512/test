package com.cannontech.loadcontrol;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.message.util.ServerRequestHelper;

public class LoadManagementProxy implements LoadManagementService {
    private LoadControlClientConnection loadControlClientConnection;
    
    public LoadManagementProxy() {
        super();
    }

    public void startProgram(int programId, Date startTime, Date stopTime) {
        //Send the message to loadmanagement               
        LMManualControlRequest msg = new LMManualControlRequest();
        msg.setCommand(LMManualControlRequest.SCHEDULED_START);
        msg.setYukonID(programId);
        msg.setStartGear(1);

        GregorianCalendar startCal = convertToCalendar(startTime);
        GregorianCalendar stopCal = convertToCalendar(stopTime);

        msg.setStartTime(startCal);
        msg.setStopTime(stopCal);
        
        ServerRequestHelper.makeServerRequest(getLoadControlClientConnection(), msg, 5000);
    }

    private GregorianCalendar convertToCalendar(Date startTime) {
        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(startTime);
        return startCal;
    }
    
    public void changeProgramStop(int programId, Date stopTime) {
        //Send the message to loadmanagement               
        LMManualControlRequest msg = new LMManualControlRequest();
        msg.setCommand(LMManualControlRequest.SCHEDULED_STOP);
        msg.setYukonID(programId);

        GregorianCalendar stopCal = convertToCalendar(stopTime);

        msg.setStartTime(stopCal);
        msg.setStopTime(stopCal);
        
        ServerRequestHelper.makeServerRequest(getLoadControlClientConnection(), msg, 30000);
    }
    
    public void stopProgram(int programId) {
        //Send the message to loadmanagement               
        LMManualControlRequest msg = new LMManualControlRequest();
        msg.setCommand(LMManualControlRequest.STOP_NOW);
        msg.setYukonID(programId);

        Date now = new Date();
        GregorianCalendar stopCal = convertToCalendar(now);

        msg.setStartTime(stopCal);
        msg.setStopTime(stopCal);
        
        ServerRequestHelper.makeServerRequest(getLoadControlClientConnection(), msg, 5000);
    }

    public LoadControlClientConnection getLoadControlClientConnection() {
        return loadControlClientConnection;
    }

    public void setLoadControlClientConnection(LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }

}
