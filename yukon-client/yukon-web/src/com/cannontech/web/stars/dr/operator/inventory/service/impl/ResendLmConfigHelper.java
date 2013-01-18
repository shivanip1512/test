package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Sets;

public class ResendLmConfigHelper extends InventoryActionsHelper {

    @Autowired ProgramEnrollmentService enrollmentService;
    @Autowired InventoryBaseDao inventoryBaseDao;
    @Autowired InventoryDao inventoryDao;
    @Autowired LmHardwareCommandService commandService;
    @Autowired YukonEnergyCompanyService yecService;
    
    public class ResendLmConfigTask extends CollectionBasedInventoryTask {
        
        private static final String failureKey = "yukon.web.modules.operator.resendConfig.failureMessage";
        
        private Set<InventoryIdentifier> unsupported = Sets.newHashSet();
        private Set<InventoryIdentifier> successful = Sets.newHashSet();
        private Set<InventoryIdentifier> failed = Sets.newHashSet();
        private Set<ResendLmConfigFailure> failureReasons = Sets.newHashSet();
        
        public ResendLmConfigTask(InventoryCollection collection, YukonUserContext context) {
            this.collection = collection;
            this.context = context;
        }
        
        public Set<InventoryIdentifier> getSuccessful() {
            return successful;
        }
        
        public Set<InventoryIdentifier> getFailed() {
            return failed;
        }
        
        public Set<ResendLmConfigFailure> getFailureReasons() {
            return failureReasons;
        }
        
        public Set<InventoryIdentifier> getUnsupported() {
            return unsupported;
        }
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (InventoryIdentifier identifier : collection.getList()) {
                        if (canceled) break;
                        try {
                            if (identifier.getHardwareType().isConfigurable()) {
                                LiteLmHardwareBase lmhb = inventoryBaseDao.getHardwareByInventoryId(identifier.getInventoryId());
                                YukonEnergyCompany yec = yecService.getEnergyCompanyByInventoryId(identifier.getInventoryId());
                                
                                LmHardwareCommand command = new LmHardwareCommand();
                                command.setDevice(lmhb);
                                command.setType(LmHardwareCommandType.CONFIG);
                                command.setUser(yec.getEnergyCompanyUser());
                                command.getParams().put(LmHardwareCommandParam.BULK, true);
                                
                                commandService.sendConfigCommand(command);
                                
                                successful.add(identifier);
                                successCount++;
                            } else {
                                unsupported.add(identifier);
                                unsupportedCount++;
                            }
                        } catch (CommandCompletionException cce) {
                            fail(failureKey, identifier, cce);
                        } finally {
                            completedItems ++;
                        }
                    }
                }
            };
        }
        
        private void fail(String key, InventoryIdentifier identifier, Exception e) {
            failed.add(identifier);
            
            DisplayableLmHardware lmHardware = inventoryDao.getDisplayableLMHardware(Collections.singletonList(identifier)).get(0);
            
            YukonMessageSourceResolvable failureReason = new YukonMessageSourceResolvable(key, e.getMessage());
            ResendLmConfigFailure failure = new ResendLmConfigFailure(identifier, lmHardware, failureReason);
            failureReasons.add(failure);
            failedCount++;
        }
        
        /**
         * Class for viewing the failure reasons 
         */
        public class ResendLmConfigFailure {
            private YukonMessageSourceResolvable failureReason;
            private InventoryIdentifier identifier;
            private DisplayableLmHardware lmHardware;
            public ResendLmConfigFailure(InventoryIdentifier identifier, DisplayableLmHardware lmHardware, YukonMessageSourceResolvable failureReason) {
                this.identifier = identifier;
                this.lmHardware = lmHardware;
                this.failureReason = failureReason;
            }
            public YukonMessageSourceResolvable getFailureReason() {
                return failureReason;
            }
            public void setFailureReason(YukonMessageSourceResolvable failureReason) {
                this.failureReason = failureReason;
            }
            public InventoryIdentifier getIdentifier() {
                return identifier;
            }
            public void setIdentifier(InventoryIdentifier identifier) {
                this.identifier = identifier;
            }
            public DisplayableLmHardware getLmHardware() {
                return lmHardware;
            }
            public void setLmHardware(DisplayableLmHardware lmHardware) {
                this.lmHardware = lmHardware;
            }
        }
        
    }

}