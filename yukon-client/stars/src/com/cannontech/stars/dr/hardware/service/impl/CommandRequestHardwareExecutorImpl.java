package com.cannontech.stars.dr.hardware.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.CommandRequestHardwareExecutor;

/**
 * Implementation class for CommandRequestHardwareExecutor
 */
public class CommandRequestHardwareExecutorImpl implements
		CommandRequestHardwareExecutor {
	
	private ECMappingDao ecMappingDao;
	private StarsInventoryBaseDao starsInventoryBaseDao;
	private CommandRequestRouteExecutor commandRequestRouteExecutor;

	@Override
	public CommandResultHolder execute(LiteStarsLMHardware hardware,
			String command, LiteYukonUser user)
			throws CommandCompletionException {

		int routeId = hardware.getRouteID();
		
		// Use the energy company default route if routeId is 0
		if(routeId == CtiUtilities.NONE_ZERO_ID) {
			LiteStarsEnergyCompany energyCompany = 
				ecMappingDao.getCustomerAccountEC(hardware.getAccountID());
			routeId = energyCompany.getDefaultRouteID();
		}
		
		return commandRequestRouteExecutor.execute(routeId, command, user);
		
	}
	
	@Override
	public CommandResultHolder execute(Thermostat thermostat, String command,
			LiteYukonUser user)  throws CommandCompletionException {

		LiteStarsLMHardware hardware = 
			(LiteStarsLMHardware) starsInventoryBaseDao.getById(thermostat.getId());
		
		return this.execute(hardware, command, user);
	}
	
	@Autowired
	public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
	
	@Autowired
	public void setStarsInventoryBaseDao(
			StarsInventoryBaseDao starsInventoryBaseDao) {
		this.starsInventoryBaseDao = starsInventoryBaseDao;
	}
	
	@Autowired
	public void setCommandRequestRouteExecutor(
			CommandRequestRouteExecutor commandRequestRouteExecutor) {
		this.commandRequestRouteExecutor = commandRequestRouteExecutor;
	}

}
