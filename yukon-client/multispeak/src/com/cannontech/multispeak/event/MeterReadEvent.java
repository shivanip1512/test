/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.impl.PointDaoImpl;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MeterReadEvent extends MultispeakEvent{

    private ReadableDevice device = null;

    /**
     * @param mspVendor_
     * @param pilMessageID_
     * @param returnMessages_
     */
    public MeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_, int returnMessages_) {
        super(mspVendor_, pilMessageID_, returnMessages_);
    }
    
	/**
	 * @param mspVendor_
	 * @param pilMessageID_
	 */
	public MeterReadEvent(MultispeakVendor mspVendor_, long pilMessageID_) {
		this(mspVendor_, pilMessageID_, 1);
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
        
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.CB_MR_STR);
        CTILogger.info("Sending ReadingChangedNotification ("+ endpointURL+ "): Meter Number " + getDevice().getMeterRead().getObjectID());
        
        try {            
            MeterRead [] meterReadArray = new MeterRead[1];
            meterReadArray[0] = getDevice().getMeterRead();
            ArrayOfMeterRead arrayMeterRead = new ArrayOfMeterRead(meterReadArray);

            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(getMspVendor());            
            ArrayOfErrorObject errObjects = port.readingChangedNotification(arrayMeterRead);
            if( errObjects != null)
                ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).logArrayOfErrorObjects(endpointURL, "ReadingChangedNotification", errObjects.getErrorObject());
            
        } catch (ServiceException e) {   
            CTILogger.info("CB_MR service is not defined for company(" + getMspVendor().getCompanyName()+ ") - ReadingChangedNotification failed.");
            CTILogger.error("ServiceExceptionDetail: " + e.getMessage());
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - ReadingChangedNotification (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: " + e.getMessage());
        }           
    }

    public boolean messageReceived(Return returnMsg) {

        String objectID = getObjectID(returnMsg.getDeviceID());
        
        if( returnMsg.getStatus() != 0) {
            
            String result = "MeterReadEvent(" + objectID + ") - Reading Failed (ERROR:" + returnMsg.getStatus() + ") " + returnMsg.getResultString();
            CTILogger.info(result);
            //TODO Should we send old data if a new reading fails?
            getDevice().populateWithPointData(returnMsg.getDeviceID());
            getDevice().getMeterRead().setErrorString(result);
            
        }
        else {
            
            CTILogger.info("MeterReadEvent(" + objectID + ") - Reading Successful" );
            if(returnMsg.getVector().size() > 0 )
            {
                PointDaoImpl pointDao = (PointDaoImpl)YukonSpringHook.getBean("pointDao");
                for (int i = 0; i < returnMsg.getVector().size(); i++)
                {
                    Object o = returnMsg.getVector().elementAt(i);
                    //TODO SN - Hoping at this point that only one value comes back in the point data vector 
                    if (o instanceof PointData) {
                        PointData pointData = (PointData) o;
                        LitePoint lPoint = pointDao.getLitePoint(pointData.getId());
                        getDevice().populate(lPoint.getPointType(), lPoint.getPointOffset(), lPoint.getUofmID(), pointData.getPointDataTimeStamp(), pointData.getValue());
                    }
                }
            }
        }
        
        if(returnMsg.getExpectMore() == 0){
            updateReturnMessageCount();
            if( getReturnMessages() == 0) {
                notify();
                eventNotification();
                return true;
            }
        }
        return false;
    }
}
