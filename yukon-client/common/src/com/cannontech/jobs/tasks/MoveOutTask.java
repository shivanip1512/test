package com.cannontech.jobs.tasks;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResultObj;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutEmailService;
import com.cannontech.amr.moveInMoveOut.service.MoveInMoveOutService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.jobs.support.YukonTask;

public class MoveOutTask implements YukonTask {

    private Logger logger = YukonLogManager.getLogger(MoveOutTask.class);

    // Injected variables
    private LiteYukonUser liteYukonUser = null;
    private Meter meter = null;
    private String emailAddress = null;
    private Date moveOutDate = null;

    // Injected services and daos
    private MoveInMoveOutService moveInMoveOutService = null;
    private MoveInMoveOutEmailService moveInMoveOutEmailService = null;
    private RawPointHistoryDao rphDao = null;

    public void start() {
        startTask();
    }

    private void startTask() {
        MoveOutFormObj moveOutFormObj = new MoveOutFormObj();
        moveOutFormObj.setEmailAddress(emailAddress);
        moveOutFormObj.setLiteYukonUser(liteYukonUser);
        moveOutFormObj.setMeter(meter);
        moveOutFormObj.setMoveOutDate(moveOutDate);
        
        MoveOutResultObj moveOutResultObj = null;

        moveOutResultObj = moveInMoveOutService.moveOut(moveOutFormObj);
        if (StringUtils.isNotBlank(moveOutFormObj.getEmailAddress())) {
            if (moveOutResultObj.getErrors().isEmpty()) {
                moveInMoveOutEmailService.createMoveOutSuccessEmail(moveOutResultObj,
                                                                    liteYukonUser);
            } else {
                moveInMoveOutEmailService.createMoveOutFailureEmail(moveOutResultObj, liteYukonUser);
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

    @Required
    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }

}
