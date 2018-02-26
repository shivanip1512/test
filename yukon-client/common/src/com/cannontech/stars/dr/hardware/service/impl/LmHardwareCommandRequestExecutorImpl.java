package com.cannontech.stars.dr.hardware.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandRequestExecutor;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.google.common.collect.Lists;

/**
 * Implementation class for CommandRequestHardwareExecutor
 */
public class LmHardwareCommandRequestExecutorImpl implements LmHardwareCommandRequestExecutor {
	
	@Autowired private EnergyCompanyDao ecDao;
	@Autowired private InventoryBaseDao inventoryBaseDao;
	@Autowired private InventoryDao inventoryDao;
	@Autowired private PaoDao paoDao;
	@Autowired private CommandExecutionService commandRequestService;
	@Autowired private DefaultRouteService defaultRouteService;
	
	private Logger logger = YukonLogManager.getLogger(LmHardwareCommandRequestExecutorImpl.class);

	@Override
	public void execute(final LiteLmHardwareBase hardware, 
	                    String command,
	                    LiteYukonUser user) throws CommandCompletionException {
	    
	    CommandCompletionCallback<CommandRequestRoute> callback =
            new CommandCompletionCallback<CommandRequestRoute>(){
            @Override
            public void receivedLastError(CommandRequestRoute command, SpecificDeviceErrorDescription error) {
                logger.error("Could not execute command for inventory with id: " + hardware.getInventoryID() + " Error: " + error.toString());
            }
            
            @Override
            public void receivedLastResultString(CommandRequestRoute command, String value) {
                logger.debug("Command executed successfully for inventory with id: " + hardware.getInventoryID() + " Command: " + command.toString());
            }
            
        };
        execute(hardware, command, user, callback);
	}

    @Override
    public void execute(LiteLmHardwareBase hardware, 
                        String command, 
                        LiteYukonUser user, 
                        CommandCompletionCallback<CommandRequestRoute> callback) throws CommandCompletionException {
        
        int routeId = getRouteId(hardware);
        CommandRequestRoute request = new CommandRequestRoute(command, routeId);
        commandRequestService.execute(Lists.newArrayList(request), callback, DeviceRequestType.LM_HARDWARE_COMMAND, user);
    }
    
    @Override
    public void executeOnRoute(String command, int routeId, LiteYukonUser user) throws CommandCompletionException {
        CommandRequestRoute request = new CommandRequestRoute(command, routeId);
        CommandResultHolder result =
            commandRequestService.execute(request, DeviceRequestType.LM_HARDWARE_COMMAND, user);
        if (result.isExceptionOccured()) {
            throw new CommandCompletionException(result.getExceptionReason());
        }
    }

    @Override
    public void execute(int inventoryId, String command, LiteYukonUser user) throws CommandCompletionException {
        LiteLmHardwareBase hardware = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);
        this.execute(hardware, command, user);
    }

	/**
	 * Use the energy company default route if the hardware's routeId is 0.
	 */
	private int getRouteId(LiteLmHardwareBase hardware) {
	    
        int routeId = hardware.getRouteID();
        InventoryIdentifier identifier = inventoryDao.getYukonInventory(hardware.getInventoryID());
        if (identifier.getHardwareType() == HardwareType.LCR_3102) {
            routeId = paoDao.getLiteYukonPAO(hardware.getDeviceID()).getRouteID();
        }
        if (routeId == CtiUtilities.NONE_ZERO_ID) {
            EnergyCompany energyCompany = ecDao.getEnergyCompanyByInventoryId(hardware.getInventoryID());
            routeId = defaultRouteService.getDefaultRouteId(energyCompany);
        }
        return routeId;
	}

}