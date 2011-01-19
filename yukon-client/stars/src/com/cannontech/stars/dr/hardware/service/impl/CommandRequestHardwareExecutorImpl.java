package com.cannontech.stars.dr.hardware.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
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
	private Logger logger = YukonLogManager.getLogger(CommandRequestHardwareExecutorImpl.class);

	@Override
	public void execute(final LiteStarsLMHardware hardware, String command,
			LiteYukonUser user) throws CommandCompletionException {
	    CommandCompletionCallbackAdapter<CommandRequestRoute> callback =
            new CommandCompletionCallbackAdapter<CommandRequestRoute>(){
            @Override
            public void receivedLastError(CommandRequestRoute command,
                    DeviceErrorDescription error) {
                logger.error("Could not execute command for inventory with id: " + 
                        hardware.getInventoryID() + " Error: " + error.toString());
            }
            
            @Override
            public void receivedLastResultString(
                    CommandRequestRoute command, String value) {
                logger.debug("Command executed successfully for inventory with id: " + 
                        hardware.getInventoryID() + " Command: " + command.toString());
            }
            
        };
        execute(hardware, command, user, callback);
	}

    @Override
    public void execute(LiteStarsLMHardware hardware, String command, LiteYukonUser user,
            CommandCompletionCallback<CommandRequestRoute> callback)
            throws CommandCompletionException {
        int routeId = getRouteId(hardware);
        commandRequestRouteExecutor.execute(routeId, command, callback,
                                            DeviceRequestType.LM_HARDWARE_COMMAND, user);
    }

    @Override
	public void execute(Thermostat thermostat, String command,
			LiteYukonUser user) throws CommandCompletionException {

		LiteStarsLMHardware hardware = 
			(LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(thermostat.getId());
		
		this.execute(hardware, command, user);
	}

	@Override
    public void executeWithTemplate(CommandRequestExecutionTemplate<CommandRequestRoute> template,
            LiteStarsLMHardware hardware, String command,
            CommandCompletionCallback<CommandRequestRoute> callback)
            throws CommandCompletionException {
	    CommandRequestRoute commandRequest = new CommandRequestRoute();
        commandRequest.setCommand(command);
        commandRequest.setRouteId(getRouteId(hardware));
        List<CommandRequestRoute> commands = Collections.singletonList(commandRequest);
        template.execute(commands, callback);
	}

	private int getRouteId(LiteStarsLMHardware hardware) {
        int routeId = hardware.getRouteID();

        // Use the energy company default route if routeId is 0
        if (routeId == CtiUtilities.NONE_ZERO_ID) {
            LiteStarsEnergyCompany energyCompany = 
                ecMappingDao.getInventoryEC(hardware.getInventoryID());
            routeId = energyCompany.getDefaultRouteId();
        }
        return routeId;
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
