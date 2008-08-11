package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {

    public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor) {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }
    
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) {

        Customer mspCustomer = new Customer();
        try {    
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            if (port != null) {
                mspCustomer = port.getCustomerByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }                
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getCustomerByMeterNo(" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for Customer");
        }
        return mspCustomer;
    }

    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }
    
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
        ServiceLocation mspServiceLocation = new ServiceLocation();
        try {
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            if (port != null) {
                mspServiceLocation =  port.getServiceLocationByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
    }
    
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(Meter meter, MultispeakVendor mspVendor) {
        return getMspMeter(meter.getMeterNumber(), mspVendor);
    }
    
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(String meterNumber, MultispeakVendor mspVendor) {
        com.cannontech.multispeak.deploy.service.Meter mspMeter = new com.cannontech.multispeak.deploy.service.Meter();
        try {
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            if (port != null) {
                mspMeter =  port.getMeterByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspMeter;
    }
    
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType){
        ErrorObject err = new ErrorObject();
        err.setEventTime(new GregorianCalendar());
        err.setObjectID(objectID);
        err.setErrorString(errorMessage);
        err.setNounType(nounType);
        return err;
    }
   
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType ) {
        ErrorObject err = getErrorObject(objectID, 
                                         notFoundObjectType + ": " + objectID + " - Was NOT found in Yukon.",
                                         nounType);
        return err;
    }    
}