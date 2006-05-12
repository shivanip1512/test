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
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedCustomer;
import com.cannontech.common.exception.PointException;

public abstract class StrategyBase {
    private ProgramService programService;
    private GroupService groupService;
    private ProgramParameterDao programParameterDao;
    private Map<String, String> parameterKeys;
    private Properties viewLabels;
    
    public ProgramParameterDao getProgramParameterDao() {
        return programParameterDao;
    }

    public void setProgramParameterDao(ProgramParameterDao programParameterDao) {
        this.programParameterDao = programParameterDao;
    }

    public Properties getViewLabels() {
        return viewLabels;
    }

    public void setViewLabels(Properties viewLabels) {
        this.viewLabels = viewLabels;
    }

    public Map<String, String> getParameterKeys() {
        return parameterKeys;
    }

    public void setParameterKeys(Map<String, String> parameterKeys) {
        this.parameterKeys = parameterKeys;
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
    
    public String getParameterValue(Program program, String key) {
        Validate.isTrue(parameterKeys.containsKey(key), "Not a valid parameter: ", key);
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
            result = parameterKeys.get(key);
        }
        return result;
    }
    
    public int getParameterValueInt(Program program, String key) {
        return Integer.parseInt(getParameterValue(program, key));
    }
    
    public float getParameterValueFloat(Program program, String key) {
        return Float.parseFloat(getParameterValue(program, key));
    }
    
    public abstract String getMethodKey();
    
    public 
    List<VerifiedCustomer> 
    getVerifiedCustomerList(EventBuilderBase builder, 
                            Collection<Group> selectedGroups) {
        List<VerifiedCustomer> result = new ArrayList<VerifiedCustomer>();
        Map<CICustomerStub, GroupCustomerNotif> seenCustomers = new HashMap<CICustomerStub, GroupCustomerNotif>();
        for (Group group : selectedGroups) {
            List<GroupCustomerNotif> customers = getGroupService().getAssignedCustomers(group);
            for (GroupCustomerNotif customerNotif : customers) {
                VerifiedCustomer vCustoemr = new VerifiedCustomer(customerNotif);
                if (seenCustomers.containsKey(customerNotif.getCustomer())) {
                    // This customer has already been included as a member of a different
                    // group. We will make sure that the original customer notif
                    // object has all of the notif methods set that this one does.
                    GroupCustomerNotif previousNotif = seenCustomers.get(customerNotif.getCustomer());
                    for (Integer method : customerNotif.getNotifMap()) {
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
        for (String key : getParameterKeys().keySet()) {
            ProgramParameter parameter;
            try {
                parameter = programParameterDao.getFor(key, program);
            } catch (UnknownParameterException e) {
                parameter = new ProgramParameter();
                parameter.setParameterKey(key);
                parameter.setProgram(program);
                parameter.setParameterValue(parameterKeys.get(key));
            }
            result.add(parameter);
        }
        return result;
    }
    
    public abstract
    List<? extends BaseEvent> getEventsForProgram(Program program);

    public abstract BigDecimal getInterruptibleLoad(CICustomerStub customer) throws PointException;


}
