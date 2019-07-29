package com.cannontech.loadcontrol;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.message.util.ServerRequest;

public class LoadManagementProxy implements LoadManagementService {
    private LoadControlClientConnection loadControlClientConnection;
    private ServerRequest serverRequest;
    
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
        msg.setOriginSource(ProgramOriginSource.MANUAL.getDatabaseRepresentation());
        serverRequest.makeServerRequest(getLoadControlClientConnection(), msg);
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
        msg.setOriginSource(ProgramOriginSource.MANUAL.getDatabaseRepresentation());
        serverRequest.makeServerRequest(getLoadControlClientConnection(), msg);
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
        msg.setOriginSource(ProgramOriginSource.MANUAL.getDatabaseRepresentation());
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
