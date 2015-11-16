package com.cannontech.web.dr.cc.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupBean;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.service.CICurtailmentStrategy;
import com.cannontech.cc.service.CurtailmentEventState;
import com.cannontech.cc.service.GroupService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.PointException;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.support.CustomerPointTypeHelper;
import com.cannontech.support.NoPointException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.cc.model.CiInitEventModel;
import com.cannontech.web.dr.cc.service.CiCurtailmentService;
import com.cannontech.web.dr.cc.service.CiCustomerVerificationService;
import com.cannontech.web.updater.point.PointDataRegistrationService;

public class CiCurtailmentServiceImpl implements CiCurtailmentService {
    
    private static Logger log = YukonLogManager.getLogger(CiCurtailmentServiceImpl.class);
    private static final int UNSTOPPABLE_WINDOW_MINUTES = 1;
    
    @Autowired private CurtailmentEventDao curtailmentEventDao;
    @Autowired private CurtailmentEventParticipantDao curtailmentEventParticipantDao;
    @Autowired private CiCustomerVerificationService customerVerificationService;
    @Autowired private CustomerPointTypeHelper customerPointTypeHelper;
    @Autowired private GroupService groupService;
    @Autowired private PointDataRegistrationService pointDataRegistrationService;
    @Autowired private SimplePointAccessDao simplePointAccessDao;
    @Autowired private StrategyFactory strategyFactory;
    
    @Override
    public List<DateTime> getEconEventWindowTimes(CiInitEventModel event) {
        List<DateTime> windowTimes = new ArrayList<>(event.getNumberOfWindows());
        for (int i = 0; i < event.getNumberOfWindows(); i++) {
            DateTime windowStart = event.getStartTime().plus(Duration.standardHours(i));
            windowTimes.add(windowStart);
        }
        return windowTimes;
    }
    
    @Override
    public boolean canCustomerParticipateInExtension(CiInitEventModel event, CICustomerStub customer) {
        boolean exceedsAllowedHours = customerVerificationService.exceedsAllowedHours(customer, event, null);
        if (exceedsAllowedHours) {
            return false;
        }
        boolean exceedsPeriodHours = customerVerificationService.exceedsPeriodHours(customer, event, null);
        return !exceedsPeriodHours;
    }
    
    @Override
    public String getUpdaterString(GroupCustomerNotif customerNotif, 
                                    CICustomerPointType pointType, 
                                    YukonUserContext userContext) {
        
        CICustomerStub customer = customerNotif.getCustomer();
        try {
            int pointId = customerPointTypeHelper.getPoint(customer, pointType).getPointID();
            String format = Format.VALUE.toString();
            return pointDataRegistrationService.getRawPointDataUpdaterSpan(pointId, format, userContext);
        } catch (NoPointException e) {
            return "n/a";
        }
    }
    
    @Override
    public boolean canCustomerBeRemovedFromEvent(CICustomerStub customer, CurtailmentEvent event) {
        boolean pointSatisfied = customerPointTypeHelper.isPointGroupSatisfied(customer, "ISOC");
        if (!pointSatisfied) {
            return false;
        }
        
        try {
            LitePoint minEventDuration = customerPointTypeHelper.getPoint(customer, CICustomerPointType.MinEventDuration);
            int minEventDurationMinutes = (int) simplePointAccessDao.getPointValue(minEventDuration);
            int eventMinutes = TimeUtil.differenceMinutes(event.getStartTime(), new Date());
            boolean minDurationExceeded = eventMinutes < minEventDurationMinutes;
            if (!minDurationExceeded) {
                return false;
            }
        } catch (PointException e) {
            log.error("Error loading Min Event Duration point value.", e);
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean canCurtailmentEventBeAdjusted(CurtailmentEvent event, LiteYukonUser user) {
        if (event.getState().equals(CurtailmentEventState.CANCELLED)) {
            return false;
        }
        Date now = new Date();
        Date paddedStart = TimeUtil.addMinutes(event.getStartTime(), 0);
        Date paddedStop = TimeUtil.addMinutes(event.getStopTime(), -UNSTOPPABLE_WINDOW_MINUTES);
        return now.after(paddedStart) && now.before(paddedStop);
    }
    
    @Override
    public GroupBean buildGroupBean(Group group, List<GroupCustomerNotif> assigned, List<GroupCustomerNotif> unassigned) {
        GroupBean groupBean = new GroupBean();
        groupBean.setId(group.getId());
        groupBean.setName(group.getName());
        
        List<GroupBean.Customer> assignedCustomers = new ArrayList<>();
        for (GroupCustomerNotif assignedNotif : assigned) {
            GroupBean.Customer customer = GroupBean.Customer.of(assignedNotif);
            assignedCustomers.add(customer);
        }
        groupBean.setAssignedCustomers(assignedCustomers);
        
        List<GroupBean.Customer> unassignedCustomers = new ArrayList<>();
        for (GroupCustomerNotif unassignedNotif : unassigned) {
            GroupBean.Customer customer = GroupBean.Customer.of(unassignedNotif);
            unassignedCustomers.add(customer);
        }
        groupBean.setAvailableCustomers(unassignedCustomers);
        
        return groupBean;
    }
    
    @Override
    public List<GroupCustomerNotif> buildAssignedGroupCustomerNotifSettings(Group group, GroupBean groupBean) {
        //Get list of all customers - available and unassigned
        List<GroupCustomerNotif> allCustomers = groupService.getAssignedCustomers(group);
        allCustomers.addAll(groupService.getUnassignedCustomers(group, false));
        
        //Build a list of assigned customer notifications, and update settings
        List<GroupCustomerNotif> assignedGroupCustomerNotifs = new ArrayList<>();
        
        if (groupBean.getAssignedCustomers() != null) {
            for (GroupBean.Customer customer : groupBean.getAssignedCustomers()) {
                int customerId = customer.getId();
                for (GroupCustomerNotif groupCustomerNotif : allCustomers) {
                    if (groupCustomerNotif.getCustomer().getId() == customerId) {
                        //update notif values
                        boolean emails = customer.isEmails();
                        boolean voice = customer.isVoice();
                        boolean sms = customer.isSms();
                        groupCustomerNotif.getNotifMap().setSendEmails(emails);
                        groupCustomerNotif.getNotifMap().setSendOutboundCalls(voice);
                        groupCustomerNotif.getNotifMap().setSendSms(sms);
                        //add to list
                        assignedGroupCustomerNotifs.add(groupCustomerNotif);
                    }
                }
            }
        }
        return assignedGroupCustomerNotifs;
    }
    
    @Override
    public List<Integer> getCurtailmentCustomerIds(int eventId) {
        CurtailmentEvent originalEvent = curtailmentEventDao.getForId(eventId);
        List<CurtailmentEventParticipant> curtailmentEventParticipants = curtailmentEventParticipantDao.getForEvent(originalEvent);
        List<Integer> customers = curtailmentEventParticipants.stream()
                                                              .map(new Function<CurtailmentEventParticipant, Integer>() {
                                                                  @Override
                                                                  public Integer apply(CurtailmentEventParticipant participant) {
                                                                      return participant.getCustomer().getId();
                                                                  }
                                                              }).collect(Collectors.toList());
        return customers;
    }
    
    @Override
    public Predicate<BaseEvent> getCurrentEventPredicate() {
        return new CurrentEventPredicate();
    }
    
    @Override
    public Predicate<BaseEvent> getRecentEventPredicate() {
        return new RecentEventPredicate();
    }
    
    @Override
    public Predicate<BaseEvent> getPendingEventPredicate() {
        return new PendingEventPredicate();
    }
    
    @Override
    public java.util.function.Predicate<BaseEvent> getEventIdFilter(int eventId) {
        return new EventFilter(eventId);
    }
    
    private boolean isConsideredActive(BaseEvent event) {
        CICurtailmentStrategy strategy = strategyFactory.getStrategy(event.getProgram());
        return strategy.isConsideredActive(event);
    }
    
    private class CurrentEventPredicate implements Predicate<BaseEvent> {
        @Override
        public boolean evaluate(BaseEvent event) {
            Date now = new Date();
            if (event.getStopTime().before(now)) {
                return false;
            } else if (event.getStartTime().after(now)) {
                return false;
            } else if (!isConsideredActive(event)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
    private class PendingEventPredicate implements Predicate<BaseEvent> {
        private Date now = new Date();
        @Override
        public boolean evaluate(BaseEvent event) {
            if (event.getStartTime().before(now)) {
                return false;
            } else if (!isConsideredActive(event)) {
                return false;
            } else {
                return true;
            }
        }
    }
    
    private class RecentEventPredicate implements Predicate<BaseEvent> {
        private Date now;
        private Date sixMonthsAgo;
        public RecentEventPredicate () {
            now = new Date();
            // get for last six months
            // used computer's TZ because the length of time is so long it won't matter
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.MONTH, -6);
            sixMonthsAgo = calendar.getTime();
        }

        @Override
        public boolean evaluate(BaseEvent event) {
            if (event.getStopTime().before(sixMonthsAgo)) {
                return false;
            } else if (event.getStopTime().after(now)) {
                return !isConsideredActive(event);
            }
                return true;
        }
    }
    
    private class EventFilter implements java.util.function.Predicate<BaseEvent>{
        private int eventId;
        
        public EventFilter(int eventId) {
            this.eventId = eventId;
        }
        
        @Override
        public boolean test(BaseEvent event) {
            return event.getId() == eventId;
        }
    };
    
}
