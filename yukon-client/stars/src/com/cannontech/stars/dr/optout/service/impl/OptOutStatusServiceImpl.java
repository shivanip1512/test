package com.cannontech.stars.dr.optout.service.impl;

import java.util.Date;
import java.util.List;

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
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsDto;
import com.cannontech.stars.dr.optout.service.OptOutStatusService;
import com.cannontech.stars.util.StarsUtils;
import com.google.common.collect.Lists;

/**
 * Implementation class for OptOutStatusService
 */
public class OptOutStatusServiceImpl implements OptOutStatusService {

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
				// If user is operator - there is no way to determine the 'master' optout enabled
				// state if there are multiple residential customer groups, so just grab the
				// first one in the list and use that as the default current state
				LiteStarsEnergyCompany operatorEnergyCompany = starsDatabaseCache
						.getEnergyCompanyByUser(user);

				LiteYukonGroup[] residentialCustomerGroups = operatorEnergyCompany
						.getResidentialCustomerGroups();

				if (residentialCustomerGroups.length > 0) {
					LiteYukonGroup group = residentialCustomerGroups[0];

					String enabled = roleDao.getRolePropValueGroup(
							group,
							ResidentialCustomerRole.CONSUMER_INFO_PROGRAMS_OPT_OUT,
							new Boolean(false).toString());

					optOutEnabled = !CtiUtilities.isFalse(enabled);
				}

			} else {
				// Residential user - check role prop value
				optOutEnabled = rolePropertyDao.checkProperty(YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
			}
		}
		
		return optOutEnabled;
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
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}

}
