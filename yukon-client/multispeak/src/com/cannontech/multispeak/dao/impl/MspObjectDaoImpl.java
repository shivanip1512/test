package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {

    private SystemLogHelper _systemLogHelper = null;

    private SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null)
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        return _systemLogHelper;
    }
    
    @Override
	public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor) {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) {

        Customer mspCustomer = new Customer();
        try {    
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                mspCustomer = port.getCustomerByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }                
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getCustomerByMeterNo(" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for Customer");
        }
        return mspCustomer;
    }
    @Override
    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
        ServiceLocation mspServiceLocation = new ServiceLocation();
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                mspServiceLocation =  port.getServiceLocationByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for ServiceLocation");
       }
       return mspServiceLocation;
    }
    @Override
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(Meter meter, MultispeakVendor mspVendor) {
        return getMspMeter(meter.getMeterNumber(), mspVendor);
    }
    @Override
    public com.cannontech.multispeak.deploy.service.Meter getMspMeter(String meterNumber, MultispeakVendor mspVendor) {
        com.cannontech.multispeak.deploy.service.Meter mspMeter = new com.cannontech.multispeak.deploy.service.Meter();
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor);
            if (port != null) {
                mspMeter =  port.getMeterByMeterNo(meterNumber);
            } else {
                CTILogger.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            String endpointURL = mspVendor.getEndpointURL(MultispeakDefines.CB_Server_STR);
            CTILogger.error("TargetService: " + endpointURL + " - getMeterByMeterNo (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
            CTILogger.info("A default(empty) is being used for Meter");
       }
       return mspMeter;
    }
    @Override
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method, String userName){
        ErrorObject errorObject = new ErrorObject();
        errorObject.setEventTime(new GregorianCalendar());
        errorObject.setObjectID(objectID);
        errorObject.setErrorString(errorMessage);
        errorObject.setNounType(nounType);
        
        String description = "ErrorObject: (ObjId:" + errorObject.getObjectID() +
        					" Noun:" + errorObject.getNounType() +
        					" Message:" + errorObject.getErrorString() +")";
        logMSPActivity(method, description, userName);
        return errorObject;
    }
    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType, String method, String userName) {
        ErrorObject errorObject = getErrorObject(objectID, 
                                         notFoundObjectType + ": " + objectID + " - Was NOT found in Yukon.",
                                         nounType,
                                         method,
                                         userName);
        return errorObject;
    }
    @Override
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects) {

        if( !errorObjects.isEmpty()) {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    @Override
    public void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName, SystemLog.TYPE_MULTISPEAK);
        CTILogger.debug("MSP Activity (Method: " + method +  " - " + description + ")");
    }
}