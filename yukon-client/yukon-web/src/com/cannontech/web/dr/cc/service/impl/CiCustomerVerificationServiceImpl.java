package com.cannontech.web.dr.cc.service.impl;

import static com.cannontech.web.dr.cc.model.ExclusionType.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.iterators.ReverseListIterator;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.service.CurtailmentEventState;
import com.cannontech.cc.service.CustomerLMProgramService;
import com.cannontech.cc.service.EconomicEventState;
import com.cannontech.cc.service.GroupService;
import com.cannontech.common.exception.PointException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.support.CustomerPointTypeHelper;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.cc.model.CiEventType;
import com.cannontech.web.dr.cc.model.CiInitEventModel;
import com.cannontech.web.dr.cc.model.Exclusion;
import com.cannontech.web.dr.cc.service.CiCustomerVerificationService;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

//TODO: move this and its interface to an appropriate place
public class CiCustomerVerificationServiceImpl implements CiCustomerVerificationService {
    @Autowired private BaseEventDao baseEventDao;
    @Autowired private CustomerLMProgramService customerLMProgramService;
    @Autowired private CustomerPointTypeHelper customerPointTypeHelper;
    @Autowired private EconomicEventParticipantDao economicEventParticipantDao;
    @Autowired private GroupService groupService;
    @Autowired private SimplePointAccessDao pointAccessDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final Set<String> strategyKeys = ImmutableSet.of("isocSameDay", 
                                                                    "isocNotification", 
                                                                    "isocSupersedeNotification", 
                                                                    "isocDirect", 
                                                                     "genericAccounting");
    private static final Set<EconomicEventState> excludedEconStates = ImmutableSet.of(EconomicEventState.CANCELLED, 
                                                                                      EconomicEventState.SUPPRESSED);
    private static final Set<CurtailmentEventState> excludedCurtailmentStates = ImmutableSet.of(CurtailmentEventState.CANCELLED);
    
    private static final String keyBase = "yukon.web.modules.dr.cc.init.error.";
    
    @Override
    public String getConstraintStatus(CiInitEventModel event, CICustomerStub customer, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        if (event.getEventType().isHoursConstrained()) {
            try {
                Double hoursRemaining = getHoursRemaining(customer);
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);
                format.setRoundingMode(RoundingMode.FLOOR);

                String hoursRemainingString = format.format(hoursRemaining);
                
                return messageSourceAccessor.getMessage(keyBase + "hoursRemaining", hoursRemainingString);
            } catch (PointException e) {
                return messageSourceAccessor.getMessage("yukon.common.na");  
            }
        } else {
            return messageSourceAccessor.getMessage("yukon.common.dashes");
        }
    }
    
    @Override
    public List<GroupCustomerNotif> getVerifiedCustomerList(CiInitEventModel event, 
                                                            Collection<Group> selectedGroups,
                                                            Map<Integer, List<Exclusion>> exclusions) {
        List<GroupCustomerNotif> result = new ArrayList<>();
        Map<CICustomerStub, GroupCustomerNotif> seenCustomers = new HashMap<>();
        
        for (Group group : selectedGroups) {
            List<GroupCustomerNotif> customerNotifs = groupService.getAssignedCustomers(group);
            for (GroupCustomerNotif customerNotif : customerNotifs) {
                if (seenCustomers.containsKey(customerNotif.getCustomer())) {
                    // This customer has already been included as a member of a different
                    // group. We will make sure that the original customer notif
                    // object has all of the notif methods set that this one does.
                    GroupCustomerNotif previousCustomerNotif = seenCustomers.get(customerNotif.getCustomer());
                    for (NotifType method : customerNotif.getNotifMap()) {
                        previousCustomerNotif.getNotifMap().setSupportsMethod(method, true);
                    }
                } else {
                    List<Exclusion> exclusionList = verifyCustomer(event, customerNotif);
                    exclusions.put(customerNotif.getId(), exclusionList);
                    seenCustomers.put(customerNotif.getCustomer(), customerNotif);
                    result.add(customerNotif);
                }
            }
        }
        return result;
    }
    
    private List<Exclusion> verifyCustomer(CiInitEventModel event, GroupCustomerNotif customerNotif) {
        List<Exclusion> exclusions = new ArrayList<>();
        
        CiEventType eventType = event.getEventType();
        CICustomerStub customer = customerNotif.getCustomer();
        
        if (eventType == CiEventType.ACCOUNTING) {
            //Customers are always included for accounting events
        } else if (eventType == CiEventType.DIRECT) {
            verifyDirectProgram(customer, exclusions);
        } else if (eventType == CiEventType.ISOC_DIRECT || eventType == CiEventType.ISOC_NOTIFICATION) {
            verifyDirectProgram(customer, exclusions);
            doCommonVerifications(customer, event, exclusions);
        } else if (eventType == CiEventType.ISOC_SUPERCEDE_DIRECT || eventType == CiEventType.ISOC_SUPERCEDE_NOTIFICATION) {
            verifyNoticeTime(customer, event, exclusions);
        } else if (eventType == CiEventType.ISOC_SAME_DAY) {
            verifyEconEventOverlap(customer, event, exclusions);
            doCommonVerifications(customer, event, exclusions);
        }
        return exclusions;
    }
    
    private void appendExclusion(List<Exclusion> exclusions, Exclusion exclusion) {
        if (exclusion != null) {
            exclusions.add(exclusion);
        }
    }
    
    private void doCommonVerifications(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions) {
        verifyPointGroupSatisfaction(customer, exclusions);
        verifyEventOverlap(customer, event, exclusions);
        verifyExceedsAllowedHours(customer, event, exclusions);
        verifyExceedsPeriodHours(customer, event, exclusions);
        verifyNoticeTime(customer, event, exclusions);
    }
    
    private void verifyDirectProgram(CICustomerStub customer, List<Exclusion> exclusions) {
        Set<LiteYukonPAObject> programsForCustomer = customerLMProgramService.getProgramsForCustomer(customer);
        if (programsForCustomer.isEmpty()) {
            Exclusion exclusion = new Exclusion(NO_DIRECT_PROGRAM, Exclusion.Status.EXCLUDE);
            appendExclusion(exclusions, exclusion);
        }
    }
    
    private void verifyPointGroupSatisfaction(CICustomerStub customer, List<Exclusion> exclusions) {
        boolean satisfied = customerPointTypeHelper.isPointGroupSatisfied(customer, "ISOC");
        if (!satisfied) {
            Exclusion exclusion = new Exclusion(ISOC_POINTS, Exclusion.Status.EXCLUDE);
            appendExclusion(exclusions, exclusion);
        }
    }
    
    private void verifyEventOverlap(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions) {
        List<BaseEvent> eventsForCustomer = baseEventDao.getAllForCustomer(customer);
        Collections.sort(eventsForCustomer, new Comparator<BaseEvent>() {
            @Override
            public int compare(BaseEvent o1, BaseEvent o2) {
                return o1.getStopTime().compareTo(o2.getStopTime());
            }
        });
        for (Iterator<BaseEvent> iter = new ReverseListIterator<>(eventsForCustomer); iter.hasNext();) {
            BaseEvent otherEvent = iter.next();
            // rely on ordering to short circuit
            if (!otherEvent.getStopTime().after(event.getStartTime().toDate())) {
                // don't use before because stop and start could be equal
                // because of the event ordering, no more possible collisions exist
                break;
            }
            Date otherStart = otherEvent.getStartTime();
            Date thisStop = event.getStopTime().toDate();
            if (doesEventContributeToAllowedHours(otherEvent) && otherStart.before(thisStop)) {
                Exclusion exclusion = new Exclusion(EVENT_COLLISION,  Exclusion.Status.EXCLUDE, otherEvent.getDisplayName());
                appendExclusion(exclusions, exclusion);
            }
        }
    }
    
    private void verifyExceedsAllowedHours(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions) {
        double allowedHours; 
        try {
            allowedHours = customerPointTypeHelper.getPointValue(customer, CICustomerPointType.InterruptHours);
        } catch (PointException e) {
            Exclusion exclusion = new Exclusion(POINT_ERROR, Exclusion.Status.EXCLUDE, e.getMessage());
            appendExclusion(exclusions, exclusion);
            return;
        }
        
        double actualHours = getTotalEventHours(customer);
        if (((actualHours * 60) + event.getDuration()) > (allowedHours * 60)) {
            Exclusion exclusion = new Exclusion(EXCEEDS_ALLOWED_HOURS, Exclusion.Status.EXCLUDE);
            appendExclusion(exclusions, exclusion);
        }
    }
    
    private void verifyExceedsPeriodHours(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions) {
        double allowedPeriodHours;
        try {
            allowedPeriodHours = customerPointTypeHelper.getPointValue(customer, CICustomerPointType.InterruptHrs24Hr);
        } catch (PointException e) {
            Exclusion exclusion = new Exclusion(POINT_ERROR, Exclusion.Status.EXCLUDE, e.getMessage());
            appendExclusion(exclusions, exclusion);
            return;
        }
        
        if (allowedPeriodHours == 0 || allowedPeriodHours == 24) {
            //don't check
        } else {
            Date beginPeriodDate = event.getStopTime().minus(Duration.standardHours(24)).toDate();
            double actualPeriodHours = getTotalEventHours(customer, 
                                                          beginPeriodDate, 
                                                          event.getStopTime().toDate());
            if (((actualPeriodHours * 60) + event.getDuration()) > (allowedPeriodHours * 60)) {
                Exclusion exclusion = new Exclusion(EXCEEDS_ALLOWED_PERIOD_HOURS, Exclusion.Status.EXCLUDE);
                appendExclusion(exclusions, exclusion);
            }
        }
    }
    
    private void verifyNoticeTime(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions) {
        try {
            int notifMinutes = TimeUtil.differenceMinutes(event.getNotificationTime().toDate(), event.getStartTime().toDate());
            if (isEventNoticeTooShort(customer, notifMinutes)) {
                Exclusion exclusion = new Exclusion(SHORT_NOTICE, Exclusion.Status.EXCLUDE_OVERRIDABLE);
                appendExclusion(exclusions, exclusion);
            }
        } catch (PointException e) {
            Exclusion exclusion = new Exclusion(POINT_ERROR, Exclusion.Status.EXCLUDE, e.getMessage());
            appendExclusion(exclusions, exclusion);
        }
    }
    
    private void verifyEconEventOverlap(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions) {
        List<EconomicEventParticipant> allCustomersEvents = economicEventParticipantDao.getForCustomer(customer);
        Calendar calendar = Calendar.getInstance(event.getStartTime().getZone().toTimeZone());
        calendar.setTime(event.getStartTime().toDate());
        int proposedYear = calendar.get(Calendar.YEAR);
        int proposedDay = calendar.get(Calendar.DAY_OF_YEAR);
        for (EconomicEventParticipant participant : allCustomersEvents) {
            EconomicEvent econEvent = participant.getEvent();
            Date startTime = econEvent.getStartTime();
            calendar.setTime(startTime);
            if (calendar.get(Calendar.YEAR) == proposedYear
                    && calendar.get(Calendar.DAY_OF_YEAR) == proposedDay
                    && doesEventContributeToAllowedHours(econEvent)) {
                Exclusion exclusion = new Exclusion(ECON_EVENT_COLLISION, Exclusion.Status.EXCLUDE, econEvent.getDisplayName());
                appendExclusion(exclusions, exclusion);
            }
        }
    }
    
    private boolean isEventNoticeTooShort(CICustomerStub customer, int notifMinutes) throws PointException {
        LitePoint minimumNoticeMinutesPoint = customerPointTypeHelper.getPoint(customer, CICustomerPointType.MinimumNotice);
        int minimumNoticeMinutes = (int) pointAccessDao.getPointValue(minimumNoticeMinutesPoint);
        return notifMinutes < minimumNoticeMinutes;
    }
    
    private boolean doesEventContributeToAllowedHours(BaseEvent event) {
        String strategy = event.getProgram().getProgramType().getStrategy();
        if (!strategyKeys.contains(strategy)) {
            return false;
        }
        if (event instanceof EconomicEvent) {
            EconomicEvent existingEventEcon = (EconomicEvent) event;
            if (excludedEconStates.contains(existingEventEcon.getState())) {
                return false;
            }
        } else if (event instanceof CurtailmentEvent) {
            CurtailmentEvent existingEventCurt = (CurtailmentEvent) event;
            if (excludedCurtailmentStates.contains(existingEventCurt.getState())) {
                return false;
            }
        }
        return true;
    }
    
    private double getHoursRemaining(CICustomerStub customer) throws PointException{
        double interruptHoursContract = customerPointTypeHelper.getPointValue(customer, CICustomerPointType.InterruptHours);
        double interruptHoursUsed = getTotalEventHours(customer);
        return interruptHoursContract - interruptHoursUsed;
    }
    
    private double getTotalEventHours(CICustomerStub customer) {
        DateTime begin = DateTime.now().withDayOfYear(1).withTimeAtStartOfDay();
        DateTime end = begin.plusYears(1);
        
        return getTotalEventHours(customer, begin.toDate(), end.toDate());
    }
    
    private double getTotalEventHours(CICustomerStub customer, Date start, Date stop) {
        List<BaseEvent> allEvents = baseEventDao.getAllForCustomerStartsOrStopsWithin(customer, start, stop);
        int totalMinutes = 0;
        for (BaseEvent event : allEvents) {
            if (doesEventContributeToAllowedHours(event)) {
                Date computedStart = Ordering.natural().max(start, event.getStartTime());    //use the max startTime
                Date computedStop = Ordering.natural().min(stop, event.getStopTime());        //use the min stopTime
                int differenceMinutes = TimeUtil.differenceMinutes(computedStart, computedStop);
                totalMinutes += differenceMinutes;
            }
        }
        double totalHours = (double)totalMinutes / 60;
        return totalHours;
    }
}
