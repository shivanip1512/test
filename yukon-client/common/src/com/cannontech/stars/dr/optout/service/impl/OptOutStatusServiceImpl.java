package com.cannontech.stars.dr.optout.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutEnabledTemporaryOverride;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation class for OptOutStatusService
 */
public class OptOutStatusServiceImpl implements OptOutStatusService {
    private static final Logger log = Logger.getLogger(OptOutStatusServiceImpl.class);

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    @Autowired private OptOutTemporaryOverrideDao optOutTemporaryOverrideDao;
    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private GlobalSettingsDao globalSettingsDao;

	@Override
	public OptOutCountsTemporaryOverride getDefaultOptOutCounts(LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
		
		OptOutCountsTemporaryOverride rolePropSetting = new OptOutCountsTemporaryOverride();
		if(globalSettingsDao.checkSetting(GlobalSetting.OPT_OUTS_COUNT)) {
		    rolePropSetting.setCounting(OptOutCounts.COUNT);
		} else {
		    rolePropSetting.setCounting(OptOutCounts.DONT_COUNT);
		}
		rolePropSetting.setStartDate(new Instant());
		
		OptOutCountsTemporaryOverride nullProgramIdSetting = null;
		try {
			
			List<OptOutCountsTemporaryOverride> allSettings = optOutTemporaryOverrideDao.getAllOptOutCounts(energyCompany);
			for (OptOutCountsTemporaryOverride setting : allSettings) {
				if (setting.getAssignedProgramId() == null) {
					nullProgramIdSetting = setting;
					break;
				}
			}
		} catch (NoTemporaryOverrideException e) {
			// ok, rolePropSetting will end up being returned then
		}
		
		return nullProgramIdSetting == null ? rolePropSetting : nullProgramIdSetting;
	}
	
    @Override
    public List<OptOutCountsTemporaryOverride> getProgramSpecificOptOutCounts(LiteYukonUser user) {

        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        
        List<OptOutCountsTemporaryOverride> settings = Lists.newArrayList();
        try {
            
            List<OptOutCountsTemporaryOverride> allSettings = optOutTemporaryOverrideDao.getAllOptOutCounts(energyCompany);
            for (OptOutCountsTemporaryOverride setting : allSettings) {
                if (setting.getAssignedProgramId() != null) {
                    settings.add(setting);
                }
            }
            
        } catch (NoTemporaryOverrideException e) {
            // ok, no program specific setting are in effect, return empty list
        }
        
        return settings;
    }

    @Override
    public OptOutEnabled getDefaultOptOutEnabled(LiteYukonUser user) {
        // Get the default value for the system without taking into effect temporary overrides.
        OptOutEnabled optOutEnabled = isSystemOptOutEnabledForOperator(user);

        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        OptOutEnabledTemporaryOverride energyCompanyOptOutTemporaryOverride = 
            optOutTemporaryOverrideDao.findCurrentSystemOptOutTemporaryOverrides(energyCompany.getEnergyCompanyID());
        
        // Check if a system wide temporary override has occurred for this time frame and use that
        // value if it exists.
        if (energyCompanyOptOutTemporaryOverride != null) {
            optOutEnabled = energyCompanyOptOutTemporaryOverride.getOptOutEnabled();
        }
        
        return optOutEnabled;
    }
	
	@Override
	public OptOutEnabled getOptOutEnabled(LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);

		Map<Integer, OptOutEnabledTemporaryOverride> programIdOptOutTemporaryOverrideMap = 
		    getProgramIdOptOutTemporaryOverrideMap(energyCompany.getEnergyCompanyID());
		OptOutEnabledTemporaryOverride energyCompanyOptOutTemporaryOverride =
		    optOutTemporaryOverrideDao.findCurrentSystemOptOutTemporaryOverrides(energyCompany.getEnergyCompanyID());
		OptOutEnabled optOutEnabled = OptOutEnabled.ENABLED;
		
		//use the global setting first
		if(energyCompanyOptOutTemporaryOverride != null && 
		        optOutEnabled.ordinal() < energyCompanyOptOutTemporaryOverride.getOptOutEnabled().ordinal()){
		    optOutEnabled = energyCompanyOptOutTemporaryOverride.getOptOutEnabled();
		}
		
		// Check to see if there have been any program based temporary overrides.
		if (programIdOptOutTemporaryOverrideMap.size() > 0) {
		    Set<Integer> optOutOverrideAssignedProgramIds = programIdOptOutTemporaryOverrideMap.keySet();
		    
			// Getting the enrollments for the given user
			CustomerAccount customerAccount = customerAccountDao.getCustomerAccount(user);
			List<LMHardwareControlGroup> lmHardwareControlGroups = Lists.newArrayList();
			lmHardwareControlGroups.addAll( 
		        lmHardwareControlGroupDao.getCurrentEnrollmentByAccountId(customerAccount.getAccountId()));

			//look for the most restrictive OptOutEnabled value among the energy company level and
			//enrolled programs
		    for (LMHardwareControlGroup lmHardwareControlGroup : lmHardwareControlGroups) {
		        
		        // Check to see if the program id is in the override list.
		        if (optOutOverrideAssignedProgramIds.contains(lmHardwareControlGroup.getProgramId())) {
		            OptOutEnabledTemporaryOverride optOutTemporaryOverride = 
		                programIdOptOutTemporaryOverrideMap.get(lmHardwareControlGroup.getProgramId());
		            
		            //is it more restrictive than the global setting?
		            if(optOutEnabled.ordinal() < optOutTemporaryOverride.getOptOutEnabled().ordinal()){
		                optOutEnabled = optOutTemporaryOverride.getOptOutEnabled();
		            }
		        }
		    } 
		    return optOutEnabled;

        // Checking to see if there is an energy company wide override.  If there has been, check to 
		// see if opt outs are enabled or disabled energy company wide.
        } else if (energyCompanyOptOutTemporaryOverride != null) {
            return energyCompanyOptOutTemporaryOverride.getOptOutEnabled();
		// There were no temporary opt out overrides.  Use the role property value.
		} else {
			boolean isOperator = energyCompanyService.isOperator(user);

			if (isOperator) {
			    return isSystemOptOutEnabledForOperator(user);
			} else {
				// Residential user - check role prop value
				if(rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT, user)){
				    return OptOutEnabled.ENABLED;
				}else{
				    return OptOutEnabled.DISABLED_BY_ROLE_PROP;
				}
				
			}
		}
	}

	@Override
	public Map<Integer, OptOutEnabled> getProgramSpecificEnabledOptOuts(int energyCompanyId) {
	    Map<Integer, OptOutEnabled> programIdOptOutEnabledMap = Maps.newHashMap();

        List<OptOutEnabledTemporaryOverride> optOutTemporaryOverrides = 
            optOutTemporaryOverrideDao.getCurrentProgramOptOutTemporaryOverrides(energyCompanyId);
	    
	    for (OptOutEnabledTemporaryOverride optOutTemporaryOverride : optOutTemporaryOverrides) {
	        if (optOutTemporaryOverride.getAssignedProgramId() != null) {
	            int assignedProgramId = optOutTemporaryOverride.getAssignedProgramId();
	            OptOutEnabled optOutEnabled = optOutTemporaryOverride.getOptOutEnabled();
	            
	            programIdOptOutEnabledMap.put(assignedProgramId, optOutEnabled);
	        }
        }
	    
	    return programIdOptOutEnabledMap;
	}

	/**
     * This method checks to see if opt outs are enabled for an energy company
     * through an operator user.
     */
	private OptOutEnabled isSystemOptOutEnabledForOperator(LiteYukonUser user) {
        // If user is operator - there is no way to determine the 'master' optout enabled
        // state if there are multiple residential customer groups, so just grab the
        // first one in the list and use that as the default current state
	    YukonEnergyCompany energyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        List<LiteUserGroup> residentialCustomerUserGroups = ecMappingDao.getResidentialUserGroups(energyCompany.getEnergyCompanyId());

        if (residentialCustomerUserGroups.size() > 0) {
            log.debug("Checking the first user group "+residentialCustomerUserGroups.get(0).getUserGroupName()+" to see if system opt outs are enabled.");
            LiteUserGroup residentialUserGroup = residentialCustomerUserGroups.get(0);

            List<LiteYukonGroup> residentialRoleGroups = yukonGroupDao.getRoleGroupsForUserGroupId(residentialUserGroup.getUserGroupId());
            for (LiteYukonGroup residentialRoleGroup : residentialRoleGroups) {

                // Only use the role groups that have the 
                Set<YukonRole> rolesForGroup = roleDao.getRolesForGroup(residentialRoleGroup.getGroupID());
                if (!rolesForGroup.contains(YukonRole.RESIDENTIAL_CUSTOMER)) {
                    continue;
                }

                String enabled = 
                    roleDao.getRolePropValueGroup(residentialRoleGroup, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT, new Boolean(false).toString());
                if(!CtiUtilities.isFalse(enabled)){ //true
                    return OptOutEnabled.ENABLED;
                }
            }
        }
        //pre addition of the communications toggle, this was the equivalent default value
        return OptOutEnabled.ENABLED;
	}
	
	/**
     * This method creates a map of programIds to optOutTemporaryOverrides of the current
     * optOutTemporaryOverrides.
     */
	private Map<Integer, OptOutEnabledTemporaryOverride> getProgramIdOptOutTemporaryOverrideMap(int energyCompanyId) {
	    Map<Integer, OptOutEnabledTemporaryOverride> programIdOptOutTemporaryOverrideMap = Maps.newHashMap();
	    
        List<OptOutEnabledTemporaryOverride> optOutTemporaryOverrides = optOutTemporaryOverrideDao.getCurrentProgramOptOutTemporaryOverrides(energyCompanyId);

        for (OptOutEnabledTemporaryOverride optOutTemporaryOverride : optOutTemporaryOverrides) {
            Integer assignedProgramId = optOutTemporaryOverride.getAssignedProgramId();
            programIdOptOutTemporaryOverrideMap.put(assignedProgramId, optOutTemporaryOverride);
        }
        
	    return programIdOptOutTemporaryOverrideMap;
	}
}