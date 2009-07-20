package com.cannontech.amr.moveInMoveOut.tasks;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.jobs.support.YukonTaskBase;

public class MoveInTask extends YukonTaskBase {

    private Logger logger = YukonLogManager.getLogger(MoveInTask.class);

    // Injected variables
    private Meter meter = null;
    private String newMeterName = null;
    private String newMeterNumber = null;
    private String emailAddress = null;
    private Date moveInDate = null;

    // Injected services and daos
    private MoveInMoveOutService moveInMoveOutService = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;

    public void start(int jobId) {
        startTask();
    }
    
    private void startTask(){
        logger.info("Starting move in task.");
        MoveInForm moveInFormObj = new MoveInForm();
        moveInFormObj.setEmailAddress(emailAddress);
        moveInFormObj.setUserContext(getUserContext());
        moveInFormObj.setMeterName(newMeterName);
        moveInFormObj.setMeterNumber(newMeterNumber);
        moveInFormObj.setMoveInDate(moveInDate);
        moveInFormObj.setPreviousMeter(meter);

        MoveInResult moveInResult = null;

        moveInResult = moveInMoveOutService.moveIn(moveInFormObj);
        moveInMoveOutEmailService.createMoveInEmail(moveInResult, getUserContext());
    }

    public void stop(int jobId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Setters for injected parameters
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setMoveInDate(Date moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Date getMoveInDate() {
        return moveInDate;
    }

    public String getNewMeterName() {
        return newMeterName;
    }

    public void setNewMeterName(String newMeterName) {
        this.newMeterName = newMeterName;
    }

    public void setNewMeterNumber(String newMeterNumber) {
        this.newMeterNumber = newMeterNumber;
    }

    public String getNewMeterNumber() {
        return newMeterNumber;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public Meter getMeter() {
        return meter;
    }

    // Setters for injected services and daos
    @Required
    public void setMoveInMoveOutEmailService(
            MoveInMoveOutEmailService moveInMoveOutEmailService) {
        this.moveInMoveOutEmailService = moveInMoveOutEmailService;
    }

    @Required
    public void setMoveInMoveOutService(
            MoveInMoveOutService moveInMoveOutService) {
        this.moveInMoveOutService = moveInMoveOutService;
    }
}
