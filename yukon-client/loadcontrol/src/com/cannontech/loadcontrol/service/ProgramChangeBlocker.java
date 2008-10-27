package com.cannontech.loadcontrol.service;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.message.util.TimeoutException;

public class ProgramChangeBlocker implements MessageListener {

    private Logger log = YukonLogManager.getLogger(ProgramChangeBlocker.class);
    
    private int programId;
    private long afterTime;
    private long timeout;
    private LoadControlClientConnection loadControlClientConnection;
    private boolean receivedUpdate;
    
    public ProgramChangeBlocker(LoadControlClientConnection loadControlClientConnection, int programId, long afterTime, long timeout) {
        this.programId = programId;
        this.afterTime = afterTime;
        this.timeout = timeout;
        this.loadControlClientConnection = loadControlClientConnection;
        this.receivedUpdate = false;
    }
    
    public synchronized LMProgramBase getUpdatedProgram() throws TimeoutException {
        
        log.info("Waiting for program update for programId " + this.programId + " to occur after " + this.afterTime);
        loadControlClientConnection.addMessageListener(this);
        
        try {
            wait(this.timeout);
        } catch(InterruptedException e) {
            // do nothing
        } finally {
            loadControlClientConnection.removeMessageListener(this);
        }
        
        if (!receivedUpdate) {
            throw new TimeoutException();
        }
        
        return loadControlClientConnection.getProgram(programId);
    }
    
    @Override
    public synchronized void messageReceived(MessageEvent e) {

        Object obj = e.getMessage();
        if(obj instanceof LMProgramChanged) {
            
            LMProgramChanged programChangedResp = (LMProgramChanged)obj;
            if (programChangedResp.getPaoID().intValue() == this.programId) {
                
                Date now = new Date();
                long timeNow = now.getTime();
                if (timeNow > this.afterTime) {
                    log.info("Recieved program update for programId " + this.programId + " at time " + timeNow);
                    this.receivedUpdate = true;
                    notifyAll();
                }
            }
        }
    }
}
