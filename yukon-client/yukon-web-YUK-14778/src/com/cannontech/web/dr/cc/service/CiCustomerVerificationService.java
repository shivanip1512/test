package com.cannontech.web.dr.cc.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.dr.cc.model.CiInitEventModel;
import com.cannontech.web.dr.cc.model.Exclusion;

/**
 * Service that handles the CI curtailment event customers, constraints and exclusions.
 */
public interface CiCustomerVerificationService {
    
    /**
     * Gets the list of verified customer notifications, and updates the map of exclusions for those customers.
     */
    List<GroupCustomerNotif> getVerifiedCustomerList(CiInitEventModel event, 
                                                     Collection<Group> selectedGroups,
                                                     Map<Integer, List<Exclusion>> exclusions);
    
    /**
     * Gets the i18ned constraint status string for the specified event and customer.
     */
    String getConstraintStatus(CiInitEventModel event, CICustomerStub customer, YukonUserContext userContext);
    
    /**
     * Returns true if the customer will exceed the allowed hours by participating in the specified event, and adds an
     * exclusion to the exclusion list.
     */
    public boolean exceedsAllowedHours(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions);
    
    /**
     * Returns true if the customer would exceed the period hours by participating in the specified event, and adds an
     * exclusion to the exclusion list.
     */
    public boolean exceedsPeriodHours(CICustomerStub customer, CiInitEventModel event, List<Exclusion> exclusions);
}
