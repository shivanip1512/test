package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.inventory.InventoryCollection;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.displayable.model.DisplayableLmHardware;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.program.service.ProgramEnrollmentService;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.CollectionBasedInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Sets;

public class ResendLmConfigHelper extends InventoryActionsHelper {

    @Autowired ProgramEnrollmentService enrollmentService;
    @Autowired InventoryDao inventoryDao;
    
    
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
                            
                            /* Future expansion of this should handle resending configuration for all device types */
                            if (identifier.getHardwareType().isZigbeeEndpoint()) {
                                int deviceId = inventoryDao.getDeviceId(identifier.getInventoryId());
                                enrollmentService.sendZigbeeConfigMessage(deviceId);
                                successful.add(identifier);
                                successCount++;
                            } else {
                                unsupported.add(identifier);
                                unsupportedCount++;
                            }
                        } catch (DigiWebServiceException dwse) {
                            fail(failureKey, identifier, dwse);
                        } catch (DigiNotConfiguredException dnce) {
                            fail(failureKey, identifier, dnce);
                        } catch (InvalidParameterException ipe) {
                            fail(failureKey, identifier, ipe);
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