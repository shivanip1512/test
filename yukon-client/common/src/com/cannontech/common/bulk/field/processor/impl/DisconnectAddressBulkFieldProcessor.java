package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.service.MeterService;
import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.database.TransactionException;


public class DisconnectAddressBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private MeterService meterService = null;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) throws ProcessingException  {

        try {
            
            if (value.getDisconnectAddress() != null) {   
                
                meterService.addDisconnectAddress(device, value.getDisconnectAddress());
            } else {
                
                try {
                    meterService.removeDisconnectAddress(device);
                }
                catch (IllegalArgumentException e) {
                    ; // don't error if wrong device type and was trying to remove disconnect
                }
            }
        } catch (IllegalArgumentException e) {
            throw new ProcessingException(e.getMessage(), "disconnectAddressNotAccepted");
        } catch (IllegalUseOfAttribute e) {
            throw new ProcessingException(e.getMessage(), "illegalAttributeUse");
        } catch (TransactionException e) {
            throw new ProcessingException("Unable to update disconnect address.", "disconnectUpdate");
        }
    }
    
    @Required
    public void setMeterService(MeterService meterService) {
        this.meterService = meterService;
    }
    
}
