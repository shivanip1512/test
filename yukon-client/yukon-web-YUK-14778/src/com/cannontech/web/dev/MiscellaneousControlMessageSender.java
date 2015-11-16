package com.cannontech.web.dev;

import java.util.List;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.collect.Sets;


@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class MiscellaneousControlMessageSender {

    @Autowired ProgramDao programDao;
    @Autowired EnrollmentDao enrollmentDao;
    @Autowired NextValueHelper nextValueHelper;
    @Autowired private LmHardwareCommandService lmHardwareCommandService;

    private int controlId = 0;
    
    @RequestMapping("/miscellaneousMethod/sendTestTextMessage")
    public String sendTestTextMessage(int loadProgramId) {

        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(loadProgramId));
        Set<Integer> inventoryIds = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(groupIds);
        
        Instant startTime = new Instant();
        Duration displayDuration = new Duration(startTime, startTime.plus(60000));
        
        controlId = nextValueHelper.getNextValue("ExternalToYukonMessageIdMapping");
        
        YukonTextMessage message = new YukonTextMessage();

        message.setMessageId(controlId);
        message.setInventoryIds(inventoryIds);
        message.setMessage("Test Message: " + controlId);
        message.setConfirmationRequired(false);
        message.setDisplayDuration(displayDuration);
        message.setStartTime(startTime);
        
        lmHardwareCommandService.sendTextMessage(message);
        return "redirect:main";
    }
    
    @RequestMapping("/development/miscellaneousMethod/sendTestCancelMessage")
    public String sendTestCancelMessage(int loadProgramId) {
        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(loadProgramId));
        Set<Integer> inventoryIds = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(groupIds);

        YukonCancelTextMessage message = new YukonCancelTextMessage();
        
        message.setInventoryIds(inventoryIds);
        message.setMessageId(controlId);
        
        lmHardwareCommandService.cancelTextMessage(message);
        return "redirect:main";
    }
    
    
    @RequestMapping("/development/miscellaneousMethod/sendControlStartNotificationMessage")
    public String sendControlStartNotificationMessage(int loadProgramId) {
        
//        ControlNotification controlNotification = new ControlNotification();  
//        
//        Instant now = new Instant();
//        
//        controlNotification.setNotifType(NotifType.IHD);
//        controlNotification.setControlNotificationType(ControlNotificationType.STARTING_CONTROL_NOTIFICATION);
//        controlNotification.setProgramId(4442);
//        controlNotification.setStartTime(now);
//        controlNotification.setStopTime(now.plus(7200000));
//
//        jmsTemplate.convertAndSend("yukon.notif.stream.dr.ControlNotification", controlNotification);
        return "redirect:main";
    }
    
    @RequestMapping("/development/miscellaneousMethod/sendControlStopNotificationMessage")
    public String sendControlStopNotificationMessage(int loadProgramId) {
        
//        ControlNotification controlNotification = new ControlNotification();  
//        
//        Instant now = new Instant();
//        
//        controlNotification.setNotifType(NotifType.IHD);
//        controlNotification.setControlNotificationType(ControlNotificationType.FINISHING_CONTROL_NOTIFICATION);
//        controlNotification.setProgramId(4442);
//        controlNotification.setStartTime(now);
//        controlNotification.setStopTime(now.plus(7200000));
//
//        
//        jmsTemplate.convertAndSend("yukon.notif.stream.dr.ControlNotification", controlNotification);
        return "redirect:main";
    }
}
