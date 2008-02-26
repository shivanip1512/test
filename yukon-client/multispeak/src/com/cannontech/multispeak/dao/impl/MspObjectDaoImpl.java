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
            long start = System.currentTimeMillis();
            CTILogger.debug("Begin call to getCustomerByMeterNo for Meter:" + meterNumber);
            mspCustomer = port.getCustomerByMeterNo(meterNumber);
            CTILogger.debug("End call to getCustomerByMeterNo for Meter:" + meterNumber + "  (took " + (System.currentTimeMillis() - start) + " millis)");
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
            long start = System.currentTimeMillis();
            CTILogger.debug("Begin call to getServiceLocationByMeterNo for Meter:" + meterNumber);
            mspServiceLocation =  port.getServiceLocationByMeterNo(meterNumber);
            CTILogger.debug("End call to getServiceLocationByMeterNo for Meter:" + meterNumber + "  (took " + (System.currentTimeMillis() - start) + " millis)");
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_MR_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
    }

    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType){
        ErrorObject err = new ErrorObject();
        err.setEventTime(new GregorianCalendar());
        err.setObjectID(objectID);
        err.setErrorString(errorMessage);
        err.setNounType(nounType);
        return err;
    }
}
