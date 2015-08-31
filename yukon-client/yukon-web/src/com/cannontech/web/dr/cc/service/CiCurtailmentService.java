package com.cannontech.web.dr.cc.service;

import java.util.List;

import org.joda.time.DateTime;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupBean;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.cc.model.CiInitEventModel;

public interface CiCurtailmentService {
    
    /**
     * Get the window start times for all windows in the specified event.
     */
    List<DateTime> getEconEventWindowTimes(CiInitEventModel event);
    
    /**
     * @return True if the customer can participate in the extension event.
     */
    boolean canCustomerParticipateInExtension(CiInitEventModel event, CICustomerStub customer);
    
    /**
     * Get a point updater string for the customer and point type.
     */
    String getUpdaterString(GroupCustomerNotif customerNotif, CICustomerPointType pointType, YukonUserContext userContext);
    
    /**
     * @return True if the customer is allowed to be removed from the event.
     */
    boolean canCustomerBeRemovedFromEvent(CICustomerStub customer, CurtailmentEvent event);
    
    /**
     * @return True if the event can be modified (split or adjusted).
     */
    boolean canCurtailmentEventBeAdjusted(CurtailmentEvent event, LiteYukonUser user);
    
    /**
     * Creates a GroupBean based upon the specified group, assigned customers, and unassigned customers.
     */
    GroupBean buildGroupBean(Group group, List<GroupCustomerNotif> assigned, List<GroupCustomerNotif> unassigned);
    
    /**
     * Builds up a list of GroupCustomerNotif objects for all assigned customers on the group bean.
     */
    List<GroupCustomerNotif> buildAssignedGroupCustomerNotifSettings(Group group, GroupBean groupBean);
    
    /**
     * Retrieves the list of customer ids for the specified curtailment event.
     */
    List<Integer> getCurtailmentCustomerIds(int eventId);
    
    /**
     * @return A Predicate that selects currently active events.
     */
    Predicate<BaseEvent> getCurrentEventPredicate();
    
    /**
     * @return A Predicate that selects recent events.
     */
    Predicate<BaseEvent> getRecentEventPredicate();
    
    /**
     * @return A Predicate that selects pending events.
     */
    Predicate<BaseEvent> getPendingEventPredicate();
    
    /**
     * @return A Predicate that matches the event with the specified id.
     */
    java.util.function.Predicate<BaseEvent> getEventIdFilter(int eventId);
    
}
