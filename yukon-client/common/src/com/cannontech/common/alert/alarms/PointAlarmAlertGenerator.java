package com.cannontech.common.alert.alarms;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.SignalListener;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.clientutils.CTILogger;

public class PointAlarmAlertGenerator implements SignalListener {
    private AlertService alertService;
    private AsyncDynamicDataSource asyncDynamicDataSource;
    private PointDao pointDao;
    private PaoDao paoDao;
    private RolePropertyUserCheckerFactory userCheckerFactory;
    
    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.registerForAllAlarms(this);
    }
    
    @Override
    public void signalReceived(Signal signal) {
        final int tags = signal.getTags();
        
        boolean isAlarmAlertable = TagUtils.isAlarmActive(tags) && TagUtils.isAlarmUnacked(tags);
        if (!isAlarmAlertable) return;
        
        ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.alarm");
        
        LitePoint litePoint = null;
        try {
            litePoint = pointDao.getLitePoint(signal.getPointID());
        }catch (NotFoundException nfe) {
            CTILogger.error("The point (pointId:"+ signal.getPointID() + ") for this Alarm might have been deleted!", nfe);
        }
        if(litePoint != null) {
        	LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        
        	resolvableTemplate.addData("paoName", liteYukonPAO.getPaoName());
        	resolvableTemplate.addData("pointName", litePoint.getPointName());
        	// the getAlarmConditionText method really sucks!
        	resolvableTemplate.addData("alarmText", AlarmUtils.getAlarmConditionText(signal.getCondition(), litePoint));
        
        	AlarmAlert alarmAlert = new AlarmAlert(signal.getTimeStamp(), resolvableTemplate);
        	alarmAlert.setPointId(signal.getPointID());
        	alarmAlert.setCondition(signal.getCondition());
        	boolean isUnacknowledgedAlarm = TagUtils.isAlarmUnacked(signal.getTags());
        	alarmAlert.setUnacknowledgedAlarm(isUnacknowledgedAlarm);
        	UserChecker userChecker = userCheckerFactory.createBooleanPropertyChecker(YukonRoleProperty.VIEW_ALARMS_AS_ALERTS);
        	alarmAlert.setUserChecker(userChecker);
        
        	alertService.add(alarmAlert);
        }
    }
    
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
    public void setAsyncDynamicDataSource(AsyncDynamicDataSource asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }

    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setUserCheckerFactory(RolePropertyUserCheckerFactory userCheckerFactory) {
        this.userCheckerFactory = userCheckerFactory;
    }

}
