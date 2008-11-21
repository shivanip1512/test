package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.OD_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ObjectRef;
import com.cannontech.multispeak.deploy.service.OutageDetectDeviceStatus;
import com.cannontech.multispeak.deploy.service.OutageDetectDeviceType;
import com.cannontech.multispeak.deploy.service.OutageDetectionDevice;
import com.cannontech.multispeak.deploy.service.PhaseCd;
import com.cannontech.multispeak.deploy.service.ServiceLocation;

public class OD_ServerImpl implements OD_ServerSoap_PortType
{
    public Multispeak multispeak;
    public MultispeakFuncs multispeakFuncs;
    
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }

    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    private void init() throws RemoteException {
        multispeakFuncs.init();
    }
    
    @Override
    public ErrorObject[] pingURL() throws java.rmi.RemoteException {
        init();
        return new ErrorObject[0];
    }
    
    @Override
    public String[] getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods", "initiateOutageDetectionEventRequest"};
        return multispeakFuncs.getMethods(MultispeakDefines.OD_OA_STR, methods );
    }
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.OD_OA_STR, "getDomainNames", strings);
        return strings;
    }
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    
    @Override
    public OutageDetectionDevice[] getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return new OutageDetectionDevice[0];
    }
    
    @Override
    public OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
        return new OutageDetectionDevice[0];
    }
    
    @Override
    public OutageDetectionDevice[] getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return new OutageDetectionDevice[0];
    }
    
    @Override
    public OutageDetectionDevice[] getOutageDetectionDevicesByType(OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return new OutageDetectionDevice[0];
    }
    
    @Override
    public OutageDetectionDevice[] getOutagedODDevices() throws java.rmi.RemoteException {
        init();
        return new OutageDetectionDevice[0];
    }
    
    @Override
    public ErrorObject[] initiateOutageDetectionEventRequest(String[] meterNos,
            Calendar requestDate, String responseURL, String transactionID)
            throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        errorObjects = multispeak.ODEvent(vendor, meterNos, transactionID);
        multispeakFuncs.logErrorObjects(MultispeakDefines.OD_OA_STR, "initiateOutageDetectionEventRequest", errorObjects);
        return errorObjects;
    }
    
    @Override
    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice) throws java.rmi.RemoteException {
        init();
    }
    
    @Override
    public ObjectRef[] displayODMonitoringRequests() throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] cancelODMonitoringRequestByObject(ObjectRef[] objectRef, Calendar requestDate) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] initiateODEventRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, Calendar requestDate,
            String responseURL, String transactionID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateODEventRequestByServiceLocation(
            String[] servLoc, Calendar requestDate, String responseURL,
            String transactionID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateODMonitoringRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, int periodicity,
            Calendar requestDate, String responseURL, String transactionID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        init();
        return null;
    }
}
