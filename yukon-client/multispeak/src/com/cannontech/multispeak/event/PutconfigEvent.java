package com.cannontech.multispeak.event;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.spring.YukonSpringHook;

public class PutconfigEvent extends MultispeakEvent {

    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    
    public PutconfigEvent(MultispeakVendor vendor, long pilMessageID_, String transactionID_)
    {
        super(vendor, pilMessageID_, transactionID_);
    }
    
    public boolean messageReceived(Return returnMsg) {

        int deviceId = returnMsg.getDeviceID();
        Meter meter = meterDao.getForId(deviceId);
        String meterNumber = meter.getMeterNumber();
        
        int returnStatus = returnMsg.getStatus();
        
        //Meter responsed in some way or another, 0 status was perfect
        if(returnStatus == 0) {   
            CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Command 'putconfig emetcon intervals'  Successful (status=" + returnStatus + ")");
        }
        else {   
            CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Command 'putconfig emetcon intervals' Failed (status=" + returnStatus + ")");
        }
        
        return true;
    }
    
    public void eventNotification() {
       return;
    }
    
    public boolean isPopulated() {
        return true;
    }
}
