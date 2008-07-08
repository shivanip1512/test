package com.cannontech.multispeak.event;

import java.rmi.RemoteException;
import java.util.List;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.spring.YukonSpringHook;

public class AddMeterRouteDiscoveryEvent extends MultispeakEvent {

    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    private final DeviceDao deviceDao = YukonSpringHook.getBean("deviceDao", DeviceDao.class);
    
    private Multispeak multispeak = null;
    private MultispeakVendor vendor;
    private List<Integer> routeIds;
    private int currentRouteIdx;
    private int currentAttemptCount;
    
    
    public AddMeterRouteDiscoveryEvent(Multispeak multispeak, MultispeakVendor vendor, long pilMessageID_, String transactionID_, List<Integer> routeIds, int currentRouteIdx, int currentAttemptCount)
    {
        super(vendor, pilMessageID_, transactionID_);
        this.multispeak = multispeak;
        this.vendor = vendor;
        this.routeIds = routeIds;
        this.currentRouteIdx = currentRouteIdx;
        this.currentAttemptCount = currentAttemptCount;
    }
    
    public boolean messageReceived(Return returnMsg) {

        int deviceId = returnMsg.getDeviceID();
        Meter meter = meterDao.getForId(deviceId);
        String meterNumber = meter.getMeterNumber();
        
        int returnStatus = returnMsg.getStatus();
        
        try {
        
            //Meter responsed in some way or another, 0 status was perfect
            if(returnStatus == 0) {   
                
                CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Ping Successful (status=" + returnStatus + ")");
                
                // set route
                int routeId = routeIds.get(currentRouteIdx);
                deviceDao.changeRoute(meter, routeId);
                
                // send putconfig command
                multispeak.sendPutconfigRequest(meterNumber, vendor, routeId);
                
                return true;
            }
            else {   
                
                CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Ping Failed (status=" + returnStatus + ")");
                
                // not a success, try next route
                int nextRouteIdxToTry;
                int attemptCount;
                
                // meter was not found to exist yet, wait 5 secs and try that same route again
                // if hasn't responded in an 20min, move to next route. If there was a problem
                // getting porter to see new meter at least we'll eventually exit this retry loop
                if (returnStatus == 54 && currentAttemptCount < 240) {
                    
                    CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Meter does not yet exist, retry in 5 sec.");
                    
                    nextRouteIdxToTry = currentRouteIdx;
                    attemptCount = currentAttemptCount + 1;
                    Thread.sleep(5000);
                }
                else {
                    
                    if (currentAttemptCount >= 240) {
                        CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Status says meter still does not exist, moving to next route.");
                    }
                    
                    nextRouteIdxToTry = currentRouteIdx + 1;
                    attemptCount = 1;
                }
                
                // send command to ping meter on next route
                multispeak.sendRouteDiscoveryRequest(meterNumber, vendor, routeIds, nextRouteIdxToTry, attemptCount);
            }
            
        } catch (RemoteException e) {
            CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Unable to send request (" + e.getMessage() + ")");
        } catch (InterruptedException e) {
            CTILogger.info("AddMeterRouteDiscoveryEvent(" + meterNumber + ") - Sleep interrupted (" + e.getMessage() + ")");
            Thread.currentThread().interrupt();
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
