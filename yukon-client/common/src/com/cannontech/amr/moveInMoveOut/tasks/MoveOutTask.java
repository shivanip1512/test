package com.cannontech.amr.moveInMoveOut.tasks;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.jobs.support.YukonTaskBase;

public class MoveOutTask extends YukonTaskBase {

    private Logger logger = YukonLogManager.getLogger(MoveOutTask.class);

    // Injected variables
    private Meter meter = null;
    private String emailAddress = null;
    private Date moveOutDate = null;

    // Injected services and daos
    private MoveInMoveOutService moveInMoveOutService = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;

    @Override
    public void start() {
        startTask();
    }

    private void startTask() {
        logger.info("Starting move out task.");
        MoveOutForm moveOutFormObj = new MoveOutForm();
        moveOutFormObj.setEmailAddress(emailAddress);
        moveOutFormObj.setUserContext(getJob().getUserContext());
        moveOutFormObj.setMeter(meter);
        moveOutFormObj.setMoveOutDate(moveOutDate);
        
        MoveOutResult moveOutResult = null;
        
        moveOutResult = moveInMoveOutService.moveOut(moveOutFormObj);
        moveInMoveOutEmailService.createMoveOutEmail(moveOutResult, getJob().getUserContext());
    }

    // Setters for injected parameters
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public Meter getMeter() {
        return meter;
    }

    public Date getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(Date moveOutDate) {
        this.moveOutDate = moveOutDate;
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
