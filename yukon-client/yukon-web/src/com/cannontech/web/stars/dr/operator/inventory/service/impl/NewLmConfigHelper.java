package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.InventoryConfigEventLogService;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.web.action.HardwareAction;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.configuration.model.NewConfigSettings;
import com.cannontech.web.stars.dr.operator.inventory.model.AssetActionFailure;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;

public class NewLmConfigHelper extends InventoryActionsHelper {
    
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareCommandService commandService;
    @Autowired private InventoryConfigEventLogService eventLog;
    @Autowired private StarsDatabaseCache cache;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    
    public class NewLmConfigTask extends CollectionBasedInventoryTask {
        
        private static final String failureKey = "yukon.web.modules.operator.inventory.config.send.failureMessage";
        
        private Set<InventoryIdentifier> unsupported = new HashSet<>();
        private Set<InventoryIdentifier> successful = new HashSet<>();
        private Set<InventoryIdentifier> failed = new HashSet<>();
        private Set<AssetActionFailure> failures = new HashSet<>();
        
        private NewConfigSettings settings;
        
        public NewLmConfigTask(InventoryCollection collection, YukonUserContext userContext, NewConfigSettings settings) {
            this.collection = collection;
            this.userContext = userContext;
            this.settings = settings;
        }
        
        public Set<InventoryIdentifier> getSuccessful() {
            return successful;
        }
        
        public Set<InventoryIdentifier> getFailed() {
            return failed;
        }
        
        public Set<AssetActionFailure> getFailures() {
            return failures;
        }
        
        public Set<InventoryIdentifier> getUnsupported() {
            return unsupported;
        }
        
        @Override
        public Runnable getProcessor() {
            
            return new Runnable() {
                @Override
                public void run() {
                    
                    for (InventoryIdentifier identifier : collection.getList()) {
                        
                        if (canceled) break;
                        
                        int inventoryId = identifier.getInventoryId();
                        LiteLmHardwareBase lmhb = null;
                        
                        try {
                            lmhb = inventoryBaseDao.getHardwareByInventoryId(inventoryId);
                        } catch (NotFoundException e) {
                         // handled below, put in unsupported (might have been deleted?).
                        }
                        
                        LiteYukonUser user = userContext.getYukonUser();
                        
                        HardwareType hardwareType = identifier.getHardwareType();
                        HardwareConfigType configType = hardwareType.getHardwareConfigType();
                        
                        if (lmhb != null 
                                && hardwareType.isConfigurable() 
                                && configType == settings.getConfig().getType()) {
                            
                            String sn = lmhb.getManufacturerSerialNumber();
                            
                            try {
                                
                                if (settings.isBatch()) {
                                    batch(settings, lmhb);
                                } else {
                                    send(settings, lmhb, user);
                                    if (lmhb.getAccountID() > 0) {
                                        updateAccount(lmhb);
                                    }
                                }
                                
                                successful.add(identifier);
                                successCount++;
                                
                                eventLog.itemConfigSucceeded(user, sn);
                                
                            } catch (CommandCompletionException e) {
                                
                                failed.add(identifier);
                                failedCount++;
                                
                                DisplayableLmHardware lmHardware = inventoryDao.getDisplayableLMHardware(inventoryId);
                                YukonMessageSourceResolvable reason = 
                                        new YukonMessageSourceResolvable(failureKey, e.getMessage());
                                AssetActionFailure failure = new AssetActionFailure(identifier, lmHardware, reason);
                                failures.add(failure);
                                
                                eventLog.itemConfigFailed(user, sn, e.getMessage());
                                
                            } finally {
                                completedItems++;
                            }
                            
                        } else {
                            
                            unsupported.add(identifier);
                            unsupportedCount++;
                            completedItems++;
                            
                            String sn = lmhb != null ? lmhb.getManufacturerSerialNumber() : "";
                            eventLog.itemConfigUnsupported(user, sn);
                        }
                    }
                    
                    // If batching, write the file to disk now that we are all done.
                    if (settings.isBatch()) SwitchCommandQueue.getInstance().addCommand( null, true );
                }
            };
        }
        
    }
    
    /** Build the config command and send it. */
    private void send(NewConfigSettings settings, LiteLmHardwareBase lmhb, LiteYukonUser user) 
    throws CommandCompletionException {
        
        Integer ecId = lmhb.getEnergyCompanyId();
        
        LmHardwareCommand command = new LmHardwareCommand();
        command.setDevice(lmhb);
        command.setType(LmHardwareCommandType.CONFIG);
        command.setUser(user);
        Map<LmHardwareCommandParam, Object> params = command.getParams();
        params.put(LmHardwareCommandParam.BULK, true);
        
        if (settings.getGroupId() == null) {
            // New configuration specified, update the database first.
            StarsLMConfiguration config = settings.getConfig().getStarsLMConfiguration();
            LiteStarsEnergyCompany ec = cache.getEnergyCompany(ecId);
            HardwareAction.updateLMConfiguration(config, lmhb, ec);
            
        } else {
            params.put(LmHardwareCommandParam.OPTIONAL_GROUP_ID, settings.getGroupId());
        }
        
        if (settings.isSpecificRoute()) {
            params.put(LmHardwareCommandParam.OPTIONAL_ROUTE_ID, settings.getRouteId());
        }
        
        if (settings.isInService()) {
            params.put(LmHardwareCommandParam.FORCE_IN_SERVICE, true);
        }
        
        commandService.sendConfigCommand(command);
    }
    
    /** Add the config command as a line to the batch file. File is not written to disk here. */
    private void batch(NewConfigSettings settings, LiteLmHardwareBase lmhb) {
        
        String options = null;
        Integer groupId = settings.getGroupId();
        Integer routeId = settings.getRouteId();
        
        if (groupId != null) {
            options = "GroupID:" + groupId;
        }
        if (routeId != null) {
            if (options != null) options += ";";
            options += "RouteID:" + routeId;
        }
        
        SwitchCommandQueue.SwitchCommand command = new SwitchCommandQueue.SwitchCommand();
        command.setEnergyCompanyID(lmhb.getEnergyCompanyId());
        command.setAccountID(lmhb.getAccountID());
        command.setInventoryID(lmhb.getInventoryID());
        command.setCommandType(SwitchCommandQueue.SWITCH_COMMAND_CONFIGURE);
        command.setInfoString(options);
        
        SwitchCommandQueue.getInstance().addCommand(command, false);
    }
    
    /** 
     * LiteStarsEnergyCompany is a cached object and it contains a list of accounts which contain
     * a list of devices.  We need to update that list of devices. 
     */
    private void updateAccount(LiteLmHardwareBase lmhb) {
        
        LiteStarsEnergyCompany ec = cache.getEnergyCompany(lmhb.getEnergyCompanyId());
        StarsCustAccountInformation accountInfo = ec.getStarsCustAccountInformation(lmhb.getAccountID());
        
        if (accountInfo != null) {
            
            StarsInventory thisDevice = StarsLiteFactory.createStarsInventory(lmhb, ec);
            StarsInventories inventories = accountInfo.getStarsInventories();
            
            for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
                StarsInventory inv = inventories.getStarsInventory(i);
                if (inv.getInventoryID() == thisDevice.getInventoryID()) {
                    inventories.setStarsInventory(i, thisDevice);
                    break;
                }
            }
        }
        
    }
    
}