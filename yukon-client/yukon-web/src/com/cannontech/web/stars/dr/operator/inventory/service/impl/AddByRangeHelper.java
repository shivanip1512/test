package com.cannontech.web.stars.dr.operator.inventory.service.impl;

import java.util.List;
import java.util.Set;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.exception.Lcr3102YukonDeviceCreationException;
import com.cannontech.stars.dr.hardware.exception.StarsDeviceSerialNumberAlreadyExistsException;
import com.cannontech.stars.dr.hardware.model.AddByRange;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.stars.dr.operator.inventory.service.AbstractInventoryTask;
import com.cannontech.web.stars.dr.operator.inventory.service.InventoryActionsHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AddByRangeHelper extends InventoryActionsHelper {

    public class AddByRangeTask extends AbstractInventoryTask {
        private AddByRange abr;
        private Set<InventoryIdentifier> successful = Sets.newHashSet();
        private List<AddByRangeFailure> failed = Lists.newArrayList();
        private long start;
        private long end;
        
        private HardwareType type;
        
        public AddByRangeTask(YukonUserContext context, HardwareType type, AddByRange abr) {
            this.userContext = context;
            this.type = type;
            this.abr = abr;
            start = Long.parseLong(abr.getFrom());
            end = Long.parseLong(abr.getTo());
        }
        
        public AddByRange getAbr() {
            return abr;
        }
        
        public HardwareType getType() {
            return type;
        }
        
        public void setType(HardwareType type) {
            this.type = type;
        }
        
        public Set<InventoryIdentifier> getSuccessful() {
            return successful;
        }
        
        public List<AddByRangeFailure> getFailed() {
            return failed;
        }
        
        @Override
        public long getTotalItems() {
            return end - start + 1;
        }
        
        public Runnable getProcessor() {
            return new Runnable() {
                @Override
                public void run() {
                    for (long sn = start; sn <= end; sn++) {
                        if (canceled) break;
                        try {
                            InventoryIdentifier identifier = hardwareService.createForAddByRangeTask(abr, sn, 
                                    userContext.getYukonUser());
                            successful.add(identifier);
                            successCount++;
                        } catch (ObjectInOtherEnergyCompanyException e) {
                            fail("yukon.web.modules.operator.abr.error.snUnavailable", sn, e);
                        } catch (StarsDeviceSerialNumberAlreadyExistsException e) {
                            fail("yukon.web.modules.operator.abr.error.snUnavailable", sn, e);
                        } catch (Lcr3102YukonDeviceCreationException e) {
                            fail("yukon.web.modules.operator.abr.error.paoNameUnavailable.lcr3102", sn, e);
                        } catch (Exception e) {
                            fail("yukon.web.modules.operator.abr.error.unknown", sn, e);
                        } finally {
                            completedItems ++;
                        }
                    }
                }
            };
        }
        
        private void fail(String key, long sn, Exception e) {
            YukonMessageSourceResolvable failure = new YukonMessageSourceResolvable(key, sn, e); 
            AddByRangeFailure abrf = new AddByRangeFailure(sn, failure);
            failed.add(abrf);
            failedCount++;
        }
        
        /**
         * Class for viewing the failure reasons 
         */
        public class AddByRangeFailure {
            private long serialNumber;
            private YukonMessageSourceResolvable failureReason;
            public AddByRangeFailure(long serialNumber, YukonMessageSourceResolvable failureReason) {
                this.serialNumber = serialNumber;
                this.failureReason = failureReason;
            }
            public long getSerialNumber() {
                return serialNumber;
            }
            public void setSerialNumber(long serialNumber) {
                this.serialNumber = serialNumber;
            }
            public YukonMessageSourceResolvable getFailureReason() {
                return failureReason;
            }
            public void setFailureReason(YukonMessageSourceResolvable failureReason) {
                this.failureReason = failureReason;
            }
        }
    }

}