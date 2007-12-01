package com.cannontech.jobs.tasks;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResultObj;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.support.YukonTask;

public class MoveInTask implements YukonTask {

    private Logger logger = YukonLogManager.getLogger(MoveInTask.class);

    // Injected variables
    private LiteYukonUser liteYukonUser = null;
    private Meter meter = null;
    private String newMeterName = null;
    private String newMeterNumber = null;
    private String emailAddress = null;
    private Date moveInDate = null;

    // Injected services and daos
    private MoveInMoveOutService moveInMoveOutService = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;

    public void start() {
        startTask();
    }
    
    private void startTask(){
        MoveInFormObj moveInFormObj = new MoveInFormObj();
        moveInFormObj.setEmailAddress(emailAddress);
        moveInFormObj.setLiteYukonUser(liteYukonUser);
        moveInFormObj.setMeterName(newMeterName);
        moveInFormObj.setMeterNumber(newMeterNumber);
        moveInFormObj.setMoveInDate(moveInDate);
        moveInFormObj.setPreviousMeter(meter);

        MoveInResultObj moveInResultObj = null;

        moveInResultObj = moveInMoveOutService.moveIn(moveInFormObj);
        if (StringUtils.isNotBlank(moveInFormObj.getEmailAddress())) {
            if (moveInResultObj.getErrors().isEmpty()) {
                moveInMoveOutEmailService.createMoveInSuccessEmail(moveInResultObj,
                                                                   liteYukonUser);
            } else {
                moveInMoveOutEmailService.createMoveInFailureEmail(moveInResultObj, liteYukonUser);
            }
        }
    }

    public void stop() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
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

    public void setRunAsUser(LiteYukonUser user) {
        this.liteYukonUser = user;
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
