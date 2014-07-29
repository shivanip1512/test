package com.cannontech.loadcontrol.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.message.util.TimeoutException;

public class ProgramChangeBlocker implements MessageListener {

    private Logger log = YukonLogManager.getLogger(ProgramChangeBlocker.class);
    
    private LMManualControlRequest controlRequest;
    private ProgramStatus programStatus;
    private LoadControlCommandService loadControlCommandService;
    private LoadControlClientConnection loadControlClientConnection;
    private long timeout;
    
    private int programId;
    private long afterTime;
    private boolean receivedUpdate;
    private CountDownLatch countDownLatch;
    
    
    public ProgramChangeBlocker(LMManualControlRequest controlRequest,
            ProgramStatus programStatus,
            LoadControlCommandService loadControlCommandService,
            LoadControlClientConnection loadControlClientConnection,
            long timeout) {
        
        this.controlRequest = controlRequest;
        this.programStatus = programStatus;
        this.loadControlCommandService = loadControlCommandService;
        this.loadControlClientConnection = loadControlClientConnection;
        this.timeout = timeout;
        
        this.programId = programStatus.getProgramId();
        this.receivedUpdate = false;
    }
    
    public void updateProgramStatus() throws TimeoutException, BadServerResponseException {
        
        log.debug("Adding message listener.");
        loadControlClientConnection.addMessageListener(this);
        
        log.debug("Creating countDown latch.");
        this.countDownLatch = new CountDownLatch(1);
        
        this.afterTime = System.currentTimeMillis();
        log.info("Executing program update for programId " + this.programId);
        ConstraintViolations executeViolations = loadControlCommandService.executeManualCommand(controlRequest);
        programStatus.setConstraintViolations(executeViolations.getConstraintContainers());
        
        try {
            log.info("Waiting for program update for programId " + this.programId + " to occur after " + this.afterTime);
            this.receivedUpdate = countDownLatch.await(this.timeout, TimeUnit.MILLISECONDS);
        } catch(InterruptedException e) {
            // do nothing
        } finally {
            log.debug("Removing message listener.");
            loadControlClientConnection.removeMessageListener(this);
        }
        
        if (!receivedUpdate) {
            throw new TimeoutException();
        }
        
        LMProgramBase updatedProgram = loadControlClientConnection.getProgram(programId);
        programStatus.setProgram(updatedProgram);
    }
    
    @Override
    public void messageReceived(MessageEvent e) {

        Object obj = e.getMessage();
        if(obj instanceof LMProgramChanged) {
            
            LMProgramChanged programChangedResp = (LMProgramChanged)obj;
            if (programChangedResp.getPaoID().intValue() == this.programId) {
                
                long timeNow = System.currentTimeMillis();
                if (timeNow > this.afterTime) {
                    log.info("Recieved program update for programId " + this.programId + " at time " + timeNow);
                    this.countDownLatch.countDown();
                }
            }
        }
    }
}
