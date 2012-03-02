package com.cannontech.web.support.development;

import java.util.List;
import java.util.Set;

import javax.jms.ConnectionFactory;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.web.security.annotation.CheckDevelopmentMode;
import com.google.common.collect.Sets;


@Controller
@CheckDevelopmentMode
public class MiscellaneousControlMessageSender {    

    @Autowired ProgramDao programDao;
    @Autowired EnrollmentDao enrollmentDao;
    @Autowired NextValueHelper nextValueHelper;
    
    //Autowired by setter.
    private JmsTemplate jmsTemplate;
    private int controlId = 0;
    
    @RequestMapping("/development/miscellaneousMethod/sendTestTextMessage")
    public String sendTestTextMessage(int loadProgramId) {

        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(loadProgramId));
        Set<Integer> inventoryIds = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(groupIds);
        
        Instant startTime = new Instant();
        Duration displayDuration = new Duration(startTime, startTime.plus(60000));
        
        controlId = nextValueHelper.getNextValue("ExternalToYukonMessageIdMapping");
        
        YukonTextMessage yukonTextMessage = new YukonTextMessage();

        yukonTextMessage.setMessageId(controlId);
        yukonTextMessage.setInventoryIds(inventoryIds);
        yukonTextMessage.setMessage("Test Message: " + controlId);
        yukonTextMessage.setConfirmationRequired(false);
        yukonTextMessage.setDisplayDuration(displayDuration);
        yukonTextMessage.setStartTime(startTime);
        
        jmsTemplate.convertAndSend("yukon.notif.stream.message.yukonTextMessage.Send", yukonTextMessage);
        return "redirect:main";
    }
    
    @RequestMapping("/development/miscellaneousMethod/sendTestCancelMessage")
    public String sendTestCancelMessage(int loadProgramId) {
        List<Integer> groupIds = programDao.getDistinctGroupIdsByYukonProgramIds(Sets.newHashSet(loadProgramId));
        Set<Integer> inventoryIds = enrollmentDao.getActiveEnrolledInventoryIdsForGroupIds(groupIds);

        YukonCancelTextMessage cancelZigbeeText = new YukonCancelTextMessage();
        
        cancelZigbeeText.setInventoryIds(inventoryIds);
        cancelZigbeeText.setMessageId(controlId);
        
        jmsTemplate.convertAndSend("yukon.notif.stream.message.yukonTextMessage.Cancel", cancelZigbeeText);
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
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory); 
        jmsTemplate.setPubSubDomain(false);
    }
}
