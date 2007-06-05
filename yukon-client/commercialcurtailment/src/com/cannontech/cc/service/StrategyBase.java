package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.BaseParticipant;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.builder.VerifiedNotifCustomer;
import com.cannontech.cc.service.exception.EventCreationException;
import com.cannontech.common.exception.PointException;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.support.CustomerPointTypeHelper;
import com.cannontech.yukon.INotifConnection;

public abstract class StrategyBase implements CICurtailmentStrategy {
    protected SimplePointAccessDao pointAccess;
    private CustomerPointTypeHelper pointTypeHelper;
    private ProgramService programService;
    private GroupService groupService;
    private Set<ProgramParameterKey> parameters = new TreeSet<ProgramParameterKey>();
    protected ProgramParameterDao programParameterDao;
    protected ProgramDao programDao;
    private INotifConnection notificationProxy;
    private String methodKey;
    private CICustomerPointType loadPoint;
    
    public ProgramParameterDao getProgramParameterDao() {
        return programParameterDao;
    }

    public GroupService getGroupService() {
        return groupService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }
    
    public String getMethodKey() {
        return this.methodKey;
    }
    
    public void setMethodKey(String methodKey) {
        this.methodKey = methodKey;
    }
    
    /** Meant to be used to determine if an event is NOT cancelled or
     *  NOT an accounting-only event or NOT a suppressed event.
     * @return
     */
    public abstract boolean isConsideredActive(BaseEvent event);
    
    public 
    List<VerifiedNotifCustomer> 
    getVerifiedCustomerList(EventBuilderBase builder, 
                            Collection<Group> selectedGroups) {
        List<VerifiedNotifCustomer> result = new ArrayList<VerifiedNotifCustomer>();
        Map<CICustomerStub, GroupCustomerNotif> seenCustomers = new HashMap<CICustomerStub, GroupCustomerNotif>();
        for (Group group : selectedGroups) {
            List<GroupCustomerNotif> customers = getGroupService().getAssignedCustomers(group);
            for (GroupCustomerNotif customerNotif : customers) {
                VerifiedNotifCustomer vCustoemr = new VerifiedNotifCustomer(customerNotif);
                if (seenCustomers.containsKey(customerNotif.getCustomer())) {
                    // This customer has already been included as a member of a different
                    // group. We will make sure that the original customer notif
                    // object has all of the notif methods set that this one does.
                    GroupCustomerNotif previousNotif = seenCustomers.get(customerNotif.getCustomer());
                    for (NotifType method : customerNotif.getNotifMap()) {
                        previousNotif.getNotifMap().setSupportsMethod(method, true);
                    }
                } else {
                    verifyCustomer(builder, vCustoemr);
                    seenCustomers.put(customerNotif.getCustomer(), customerNotif);
                    result.add(vCustoemr);
                }
            }
        }
        Collections.sort(result, new Comparator<VerifiedCustomer>() {
            public int compare(VerifiedCustomer o1, VerifiedCustomer o2) {
                return o1.getCustomer().getCompanyName().compareTo(o2.getCustomer().getCompanyName());
            }
        });
        return result;
    }
    
    protected void verifyCustomers(EventBuilderBase builder) throws EventCreationException {
        List<GroupCustomerNotif> customerList = builder.getCustomerList();
        for (GroupCustomerNotif customer : customerList) {
            VerifiedCustomer vCustoemr = new VerifiedNotifCustomer(customer);
            verifyCustomer(builder, vCustoemr);
            if (!vCustoemr.isIncludable()) {
                throw new EventCreationException("Customer " + customer +
                                                 " can no longer be included: " + vCustoemr.getReasonForExclusion());
            }
        }
    }
    protected abstract void verifyCustomer(EventBuilderBase builder, 
                                           VerifiedCustomer vCustomer);

    public List<ProgramParameter> getParameters(Program program) {
        List<ProgramParameter> result = new LinkedList<ProgramParameter>();
        for (ProgramParameterKey key : parameters) {
            ProgramParameter parameter;
            try {
                parameter = programParameterDao.getFor(program, key);
            } catch (UnknownParameterException e) {
                parameter = new ProgramParameter();
                parameter.setParameterKey(key);
                parameter.setProgram(program);
                parameter.setParameterValue(key.getDefaultValue());
            }
            result.add(parameter);
        }
        return result;
    }
    
    public abstract
    List<? extends BaseEvent> getEventsForProgram(Program program);

    public BigDecimal getCurrentLoad(CICustomerStub customer) throws PointException {
        LitePoint point = pointTypeHelper.getPoint(customer, loadPoint);
        double interruptLoad = pointAccess.getPointValue(point);
        
        BigDecimal bigDecimal = new BigDecimal(interruptLoad, new MathContext(7));
        return bigDecimal;
    }
    
    protected void sendProgramNotifications(BaseEvent event, List<? extends BaseParticipant> participants, String action) {
        int customerIds[] = new int[participants.size()];
        int i = 0;
        for (BaseParticipant participant : participants) {
            customerIds[i++] = participant.getCustomer().getId();
        }
        notificationProxy.sendProgramEventNotification(event.getProgram().getId(), 
                                                       event.getDisplayName(), 
                                                       action, 
                                                       event.getStartTime(), 
                                                       event.getStopTime(), 
                                                       event.getNotificationTime(), 
                                                       customerIds);
    }

    public Set<ProgramParameterKey> getParameters() {
        return parameters;
    }

    @Required
    final public void setParameters(Set<ProgramParameterKey> parameters) {
        this.parameters = parameters;
    }

    @Required
    final public void setProgramParameterDao(ProgramParameterDao programParameterDao) {
        this.programParameterDao = programParameterDao;
    }

    @Required
    final public INotifConnection getNotificationProxy() {
        return notificationProxy;
    }

    @Required
    final public void setNotificationProxy(INotifConnection notificationProxy) {
        this.notificationProxy = notificationProxy;
    }
    
    @Required
    final public void setPointAccess(SimplePointAccessDao pointAccess) {
        this.pointAccess = pointAccess;
    }
    
    @Required
    final public void setProgramDao(ProgramDao programDao) {
        this.programDao = programDao;
    }
    
    @Required
    final public void setPointTypeHelper(CustomerPointTypeHelper pointTypeHelper) {
        this.pointTypeHelper = pointTypeHelper;
    }

    @Required
    final public void setLoadPoint(CICustomerPointType loadPoint) {
        this.loadPoint = loadPoint;
    }

}
