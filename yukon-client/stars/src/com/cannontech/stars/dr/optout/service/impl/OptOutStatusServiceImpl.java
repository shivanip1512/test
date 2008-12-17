package com.cannontech.stars.dr.optout.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.yukon.ConfigurationRole;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.util.StarsUtils;

/**
 * Implementation class for OptOutStatusService
 */
public class OptOutStatusServiceImpl implements OptOutStatusService {

	private AuthDao authDao;
	private OptOutTemporaryOverrideDao optOutTemporaryOverrideDao;
	private EnergyCompanyDao energyCompanyDao;
	private StarsDatabaseCache starsDatabaseCache;
	private RoleDao roleDao;
	
	@Override
	public boolean getOptOutCounts(LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
		
		boolean optOutCounts = false;
		try {
			optOutCounts = optOutTemporaryOverrideDao.getOptOutCounts(energyCompany);
		} catch (NoTemporaryOverrideException e) {
			// Opt out counts is not temporarily overridden today - get role property value
			optOutCounts = Boolean.valueOf(authDao.getRolePropertyValue(
											user, 
											ConfigurationRole.OPT_OUTS_COUNT));
		}
		
		return optOutCounts;
	}

	@Override
	public boolean getOptOutEnabled(LiteYukonUser user) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);

		boolean optOutEnabled = false;
		try {
			optOutEnabled = optOutTemporaryOverrideDao.getOptOutEnabled(energyCompany);
		} catch (NoTemporaryOverrideException e) {
			// Opt outs enabled is not temporarily overridden today - get role
			// property value

			boolean isOperator = StarsUtils.isOperator(user);

			if (isOperator) {
				// If user is operator - check the first residential customer group
				// for enabled value
				LiteStarsEnergyCompany operatorEnergyCompany = starsDatabaseCache
						.getEnergyCompanyByUser(user);

				LiteYukonGroup[] residentialCustomerGroups = operatorEnergyCompany
						.getResidentialCustomerGroups();

				if (residentialCustomerGroups.length > 0) {
					LiteYukonGroup group = residentialCustomerGroups[0];

					String enabled = roleDao.getRolePropValueGroup(
							group,
							ConfigurationRole.OPT_OUTS_COUNT,
							new Boolean(false).toString());

					optOutEnabled = Boolean.valueOf(enabled);
				}

			} else {
				// Residential user - check role prop value

				optOutEnabled = Boolean.valueOf(authDao.getRolePropertyValue(
											user,
											ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT));
			}
		}
		
		return optOutEnabled;
	}
	
	@Autowired
	public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}
	
	@Autowired
	public void setOptOutTemporaryOverrideDao(
			OptOutTemporaryOverrideDao optOutTemporaryOverrideDao) {
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

}
