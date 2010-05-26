/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;


import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.database.data.lite.LitePoint;
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
import com.google.common.collect.Lists;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MeterReadEvent extends MultispeakEvent implements CommandResultHolderHandlingEvent {

    private final PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
    
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

        throw new UnsupportedOperationException("MeterReadEvent commands are handled by the CommandRequestExecutor only.");
    }
    
    public void handleResult(Meter meter, CommandResultHolder result) {
    	
    	// error
    	if (result.isAnyErrorOrException()) {
    		
    		List<String> errors = Lists.newArrayList();
    		if (StringUtils.isNotBlank(result.getExceptionReason())) {
    			errors.add(result.getExceptionReason());
    		}
    		for (DeviceErrorDescription e : result.getErrors()) {
    			errors.add(e.getDescription());
    		}
    		
    		String errorString = "MeterReadEvent(" + meter.getMeterNumber() + ") - Reading Failed (ERROR:" + StringUtils.join(errors, ", ") + ") " + StringUtils.join(result.getResultStrings(), ", ");
    		getDevice().populateWithCachedPointData(meter);
    		getDevice().getMeterRead().setErrorString(errorString);
    	
    	// success
    	} else {
    		
    		for (PointValueHolder pvh : result.getValues()) {
    			
    			LitePoint litePoint = pointDao.getLitePoint(pvh.getId());
    			PaoPointIdentifier paoPointIdentifier = PaoPointIdentifier.createPaoPointIdentifier(litePoint, meter);
                RichPointData richPointData = new RichPointData((PointValueQualityHolder)pvh, paoPointIdentifier); // all point data from result is of type PointValueQualityHolder
                getDevice().populate(meter, richPointData);
    		}
    	}
    	
    	// event notification - only once, after all results have come back
    	updateReturnMessageCount();
    	if(getReturnMessages() == 0) {
            eventNotification();
        }
    }

    public boolean isPopulated() {
        return getDevice().isPopulated(); 
    }
}
