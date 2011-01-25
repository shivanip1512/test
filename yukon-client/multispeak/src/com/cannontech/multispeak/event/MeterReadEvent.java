/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MeterReadEvent extends MultispeakEvent{

    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    
    private ReadableDevice device = null;

    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public MeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, Meter meter, 
            int returnMessages_, String transactionID_) {
        super(mspVendor_, pilMessageID_, returnMessages_, transactionID_);
        setDevice(MeterReadFactory.createMeterReadObject(meter));
    }
    
    /**
     * @param mspVendor_
     * @param pilMessageID_
     */
    public MeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, Meter meter,
            String transactionID_) {
        this(mspVendor_, pilMessageID_, meter, 1, transactionID_);
    }
    
    /**
     * @return Returns the device.
     */
    public ReadableDevice getDevice()
    {
        return device;
    }
    /**
     * @param device The device to set.
     */
    public void setDevice(ReadableDevice device)
    {
        this.device = device;
    }

    /**
     * Send an ReadingChangedNotification to the CB webservice containing meterReadEvents 
     * @param odEvents
     */
    public void eventNotification() {
        
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.CB_Server_STR);
        CTILogger.info("Sending ReadingChangedNotification ("+ endpointURL+ "): Meter Number " + getDevice().getMeterRead().getObjectID());
        
        try {            
            MeterRead [] meterReads = new MeterRead[1];
            meterReads[0] = getDevice().getMeterRead();

            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(getMspVendor());
            if (port != null) {
                ErrorObject[] errObjects = port.readingChangedNotification(meterReads, getTransactionID());
                if( errObjects != null)
                    ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).logErrorObjects(endpointURL, "ReadingChangedNotification", errObjects);
            } else {
                CTILogger.error("Port not found for CB_MR (" + getMspVendor().getCompanyName() + ")");
            }
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - ReadingChangedNotification (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: " + e.getMessage());
        }           
    }

    public boolean messageReceived(Return returnMsg) {

        Meter meter = meterDao.getForId(returnMsg.getDeviceID());
        
        if( returnMsg.getStatus() != 0) {
            
            String result = "MeterReadEvent(" + meter.getMeterNumber() + ") - Reading Failed (ERROR:" + returnMsg.getStatus() + ") " + returnMsg.getResultString();
            CTILogger.info(result);
            //TODO Should we send old data if a new reading fails?
            getDevice().populateWithPointData(returnMsg.getDeviceID());
            getDevice().getMeterRead().setErrorString(result);
            
        }
        else {
            
            CTILogger.info("MeterReadEvent(" + meter.getMeterNumber() + ") - Reading Successful" );
            if(returnMsg.getVector().size() > 0 )
            {
                PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
                for (int i = 0; i < returnMsg.getVector().size(); i++)
                {
                    Object o = returnMsg.getVector().elementAt(i);
                    //TODO SN - Hoping at this point that only one value comes back in the point data vector 
                    if (o instanceof PointData) {
                        PointData pointData = (PointData) o;
                        LitePoint lPoint = pointDao.getLitePoint(pointData.getId());
                        PointIdentifier pointIdentifier = PointIdentifier.createPointIdentifier(lPoint);
                        getDevice().populate(pointIdentifier, pointData.getPointDataTimeStamp(), pointData.getValue());
                    }
                }
            }
        }
        
        if(returnMsg.getExpectMore() == 0){
            updateReturnMessageCount();
            if( getReturnMessages() == 0) {
                eventNotification();
                return true;
            }
        }
        return false;
    }

    public boolean isPopulated() {
        return getDevice().isPopulated(); 
    }
}
