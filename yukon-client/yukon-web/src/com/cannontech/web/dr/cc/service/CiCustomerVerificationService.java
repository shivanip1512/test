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
}
