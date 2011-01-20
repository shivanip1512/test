package com.cannontech.stars.dr.optout.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsDto;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutTemporaryOverride;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.util.StarsUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation class for OptOutStatusService
 */
public class OptOutStatusServiceImpl implements OptOutStatusService {

    private CustomerAccountDao customerAccountDao;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
	private OptOutTemporaryOverrideDao optOutTemporaryOverrideDao;
	private EnergyCompanyDao energyCompanyDao;
	private StarsDatabaseCache starsDatabaseCache;
	private RoleDao roleDao;
	private RolePropertyDao rolePropertyDao;
	
	@Override
	public OptOutCountsDto getDefaultOptOutCounts(LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
		
		boolean optOutCounts = rolePropertyDao.checkProperty(YukonRoleProperty.OPT_OUTS_COUNT, null);
		OptOutCountsDto rolePropSetting = new OptOutCountsDto(OptOutCounts.valueOf(optOutCounts), null, new Date());
		
		OptOutCountsDto nullProgramIdSetting = null;
		try {
			
			List<OptOutCountsDto> allSettings = optOutTemporaryOverrideDao.getAllOptOutCounts(energyCompany);
			for (OptOutCountsDto setting : allSettings) {
				if (setting.getProgramId() == null) {
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
    public List<OptOutCountsDto> getProgramSpecificOptOutCounts(LiteYukonUser user) {

        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        
        List<OptOutCountsDto> settings = Lists.newArrayList();
        try {
            
            List<OptOutCountsDto> allSettings = optOutTemporaryOverrideDao.getAllOptOutCounts(energyCompany);
            for (OptOutCountsDto setting : allSettings) {
                if (setting.getProgramId() != null) {
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
        OptOutEnabled optOutEnabled = OptOutEnabled.valueOf(isSystemOptOutEnabledForOperator(user));

        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        OptOutTemporaryOverride energyCompanyOptOutTemporaryOverride = 
            optOutTemporaryOverrideDao.findCurrentSystemOptOutTemporaryOverrides(energyCompany.getEnergyCompanyID());
        
        // Check if a system wide temporary override has occurred for this time frame and use that
        // value if it exists.
        if (energyCompanyOptOutTemporaryOverride != null) {
            optOutEnabled = energyCompanyOptOutTemporaryOverride.getOptOutEnabled();
        }
        
        return optOutEnabled;
    }
	
	@Override
	public boolean getOptOutEnabled(LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);

		Map<Integer, OptOutTemporaryOverride> programIdOptOutTemporaryOverrideMap = 
		    getProgramIdOptOutTemporaryOverrideMap(energyCompany.getEnergyCompanyID());
		OptOutTemporaryOverride energyCompanyOptOutTemporaryOverride =
		    optOutTemporaryOverrideDao.findCurrentSystemOptOutTemporaryOverrides(energyCompany.getEnergyCompanyID());
		
		// Check to see if there have been any program based temporary overrides.
		if (programIdOptOutTemporaryOverrideMap.size() > 0) {
		    Set<Integer> optOutOverrideAssignedProgramIds = programIdOptOutTemporaryOverrideMap.keySet();
		    
			// Getting the enrollments for the given user
			CustomerAccount customerAccount = customerAccountDao.getCustomerAccount(user);
			List<LMHardwareControlGroup> lmHardwareControlGroups = Lists.newArrayList();
			lmHardwareControlGroups.addAll( 
		        lmHardwareControlGroupDao.getCurrentEnrollmentByAccountId(customerAccount.getAccountId()));

		    for (LMHardwareControlGroup lmHardwareControlGroup : lmHardwareControlGroups) {
		        
		        // Check to see if the program id is in the override list.
		        if (optOutOverrideAssignedProgramIds.contains(lmHardwareControlGroup.getProgramId())) {
		            OptOutTemporaryOverride optOutTemporaryOverride = 
		                programIdOptOutTemporaryOverrideMap.get(lmHardwareControlGroup.getProgramId());
		            
		            // Opt Outs are disabled for this account.
		            OptOutEnabled optOutEnabled = optOutTemporaryOverride.getOptOutEnabled();
		            if (optOutEnabled == OptOutEnabled.DISABLED) {
		                return false;
		            }
		            
		        // The program was not found in the override list. Now check the global entry.  
		        } else {
		            if (energyCompanyOptOutTemporaryOverride != null) {
    		            // Opt Outs are disabled energy company wide and therefore are disabled for this account.
    		            OptOutEnabled optOutEnabled = energyCompanyOptOutTemporaryOverride.getOptOutEnabled();
    		            if (optOutEnabled == OptOutEnabled.DISABLED) {
    		                return false;
    		            } 
		            }
		        }
		    } 
		    
		    return true;

        // Checking to see if there is an energy company wide override.  If there has been, check to 
		// see if opt outs are enabled or disabled energy company wide.
        } else if (energyCompanyOptOutTemporaryOverride != null) {

            OptOutEnabled optOutEnabled = energyCompanyOptOutTemporaryOverride.getOptOutEnabled();
            if (optOutEnabled == OptOutEnabled.DISABLED) {
                return false;
            }
            return  true;
		    
		// There were no temporary opt out overrides.  Use the role property value.
		} else {

		    boolean optOutEnabled = false;
			boolean isOperator = StarsUtils.isOperator(user);

			if (isOperator) {
			    optOutEnabled = isSystemOptOutEnabledForOperator(user);
			} else {
				// Residential user - check role prop value
				optOutEnabled = rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
			}

			return optOutEnabled;
		}
	}

	@Override
	public Map<Integer, OptOutEnabled> getProgramSpecificEnabledOptOuts(int energyCompanyId) {
	    Map<Integer, OptOutEnabled> programIdOptOutEnabledMap = Maps.newHashMap();

        List<OptOutTemporaryOverride> optOutTemporaryOverrides = 
            optOutTemporaryOverrideDao.getCurrentProgramOptOutTemporaryOverrides(energyCompanyId);
	    
	    for (OptOutTemporaryOverride optOutTemporaryOverride : optOutTemporaryOverrides) {
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
	private boolean isSystemOptOutEnabledForOperator(LiteYukonUser user) {
	    boolean optOutEnabled = false;
	    
        // If user is operator - there is no way to determine the 'master' optout enabled
        // state if there are multiple residential customer groups, so just grab the
        // first one in the list and use that as the default current state
        LiteStarsEnergyCompany operatorEnergyCompany = starsDatabaseCache
                .getEnergyCompanyByUser(user);

        LiteYukonGroup[] residentialCustomerGroups = operatorEnergyCompany
                .getResidentialCustomerGroups();

        if (residentialCustomerGroups.length > 0) {
            LiteYukonGroup group = residentialCustomerGroups[0];

            String enabled = 
                roleDao.getRolePropValueGroup(group, ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT,
                                              new Boolean(false).toString());

            optOutEnabled = !CtiUtilities.isFalse(enabled);
        }
        
        return optOutEnabled;
	}
	
	/**
     * This method creates a map of programIds to optOutTemporaryOverrides of the current
     * optOutTemporaryOverrides.
     */
	private Map<Integer, OptOutTemporaryOverride> getProgramIdOptOutTemporaryOverrideMap(int energyCompanyId) {
	    Map<Integer, OptOutTemporaryOverride> programIdOptOutTemporaryOverrideMap = Maps.newHashMap();
	    
        List<OptOutTemporaryOverride> optOutTemporaryOverrides = optOutTemporaryOverrideDao.getCurrentProgramOptOutTemporaryOverrides(energyCompanyId);

        for (OptOutTemporaryOverride optOutTemporaryOverride : optOutTemporaryOverrides) {
            Integer assignedProgramId = optOutTemporaryOverride.getAssignedProgramId();
            programIdOptOutTemporaryOverrideMap.put(assignedProgramId, optOutTemporaryOverride);
        }
        
	    return programIdOptOutTemporaryOverrideMap;
	}
	
	// DI Setters
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
	
	@Autowired
	public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
	
	@Autowired
	public void setOptOutTemporaryOverrideDao(OptOutTemporaryOverrideDao optOutTemporaryOverrideDao) {
		this.optOutTemporaryOverrideDao = optOutTemporaryOverrideDao;
	}
	
	@Autowired
	public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
		this.energyCompanyDao = energyCompanyDao;
	}
	
	@Autowired
	public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
		this.starsDatabaseCache = starsDatabaseCache;
	}
	
	@Autowired
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}

}
