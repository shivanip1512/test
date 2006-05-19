package com.cannontech.cc.service;

import java.math.BigDecimal;
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

import org.apache.commons.lang.StringUtils;

import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.cc.service.builder.VerifiedNotifCustomer;
import com.cannontech.common.exception.PointException;
import com.cannontech.database.data.notification.NotifType;

public abstract class StrategyBase {
    private ProgramService programService;
    private GroupService groupService;
    private ProgramParameterDao programParameterDao;
    private Set<ProgramParameterKey> parameters = new TreeSet<ProgramParameterKey>();
    
    public ProgramParameterDao getProgramParameterDao() {
        return programParameterDao;
    }

    public void setProgramParameterDao(ProgramParameterDao programParameterDao) {
        this.programParameterDao = programParameterDao;
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
    
    public String getParameterValue(Program program, ProgramParameterKey key) {
        String result = null;
        try {
            ProgramParameter parameter;
            parameter = getProgramParameterDao().getFor(key, program);
            if (!StringUtils.isBlank(parameter.getParameterValue())) {
                result = parameter.getParameterValue();
            }
        } catch (UnknownParameterException e) {
        }
        if (result == null) {
            // get default
            result = key.getDefaultValue();
        }
        return result;
    }
    
    public int getParameterValueInt(Program program, ProgramParameterKey key) {
        return Integer.parseInt(getParameterValue(program, key));
    }
    
    public float getParameterValueFloat(Program program, ProgramParameterKey key) {
        return Float.parseFloat(getParameterValue(program, key));
    }
    
    public abstract String getMethodKey();
    
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
    
    protected abstract 
    void 
    verifyCustomer(EventBuilderBase builder, 
                   VerifiedCustomer vCustoemr);

    public List<ProgramParameter> getParameters(Program program) {
        List<ProgramParameter> result = new LinkedList<ProgramParameter>();
        for (ProgramParameterKey key : parameters) {
            ProgramParameter parameter;
            try {
                parameter = programParameterDao.getFor(key, program);
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

    public abstract BigDecimal getCurrentLoad(CICustomerStub customer) throws PointException;

    public void setParameterStrings(Set<String> parameterStrings) {
        for (String string : parameterStrings) {
            ProgramParameterKey key = ProgramParameterKey.valueOf(string);
            parameters.add(key);
        }
    }

    public Set<ProgramParameterKey> getParameters() {
        return parameters;
    }

    public void setParameters(Set<ProgramParameterKey> parameters) {
        this.parameters = parameters;
    }


}
