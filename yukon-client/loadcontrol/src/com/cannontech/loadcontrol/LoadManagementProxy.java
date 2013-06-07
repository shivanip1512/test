package com.cannontech.loadcontrol;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.util.ServerRequest;

public class LoadManagementProxy implements LoadManagementService {
    private LoadControlClientConnection loadControlClientConnection;
    private ServerRequest serverRequest;
    
    public LoadManagementProxy() {
        super();
    }

    public void startProgram(int programId, Date startTime, Date stopTime) {
        //Send the message to loadmanagement               
        ManualControlRequestMessage msg = new ManualControlRequestMessage();
        msg.setCommand(ManualControlRequestMessage.SCHEDULED_START);
        msg.setYukonId(programId);
        msg.setStartGear(1);

        GregorianCalendar startCal = convertToCalendar(startTime);
        GregorianCalendar stopCal = convertToCalendar(stopTime);

        msg.setStartTime(startCal);
        msg.setStopTime(stopCal);
        
        serverRequest.makeServerRequest(getLoadControlClientConnection(), msg);
    }

    private GregorianCalendar convertToCalendar(Date startTime) {
        GregorianCalendar startCal = new GregorianCalendar();
        startCal.setTime(startTime);
        return startCal;
    }
    
    public void changeProgramStop(int programId, Date stopTime) {
        //Send the message to loadmanagement               
        ManualControlRequestMessage msg = new ManualControlRequestMessage();
        msg.setCommand(ManualControlRequestMessage.SCHEDULED_STOP);
        msg.setYukonId(programId);

        GregorianCalendar stopCal = convertToCalendar(stopTime);

        msg.setStartTime(stopCal);
        msg.setStopTime(stopCal);
        
        serverRequest.makeServerRequest(getLoadControlClientConnection(), msg);
    }
    
    public void stopProgram(int programId) {
        //Send the message to loadmanagement               
        ManualControlRequestMessage msg = new ManualControlRequestMessage();
        msg.setCommand(ManualControlRequestMessage.STOP_NOW);
        msg.setYukonId(programId);

        Date now = new Date();
        GregorianCalendar stopCal = convertToCalendar(now);

        msg.setStartTime(stopCal);
        msg.setStopTime(stopCal);
        
        serverRequest.makeServerRequest(getLoadControlClientConnection(), msg);
    }

    public LoadControlClientConnection getLoadControlClientConnection() {
        return loadControlClientConnection;
    }

    public void setLoadControlClientConnection(LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }

    public ServerRequest getServerRequest() {
        return serverRequest;
    }

    public void setServerRequest(ServerRequest serverRequest) {
        this.serverRequest = serverRequest;
    }

}
