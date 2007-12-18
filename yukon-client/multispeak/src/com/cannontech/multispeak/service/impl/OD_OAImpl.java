package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfObjectRef;
import com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.OD_OASoap_PortType;
import com.cannontech.multispeak.service.OutageDetectDeviceStatus;
import com.cannontech.multispeak.service.OutageDetectDeviceType;
import com.cannontech.multispeak.service.OutageDetectionDevice;
import com.cannontech.multispeak.service.PhaseCd;

public class OD_OAImpl implements OD_OASoap_PortType
{
    public Multispeak multispeak;
    public MultispeakFuncs multispeakFuncs;
    
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }

    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    private void init() {
        multispeakFuncs.init();
    }

    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return new ArrayOfErrorObject(new ErrorObject[0]);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods", "initiateOutageDetectionEventRequest"};
        return multispeakFuncs.getMethods(MultispeakDefines.OD_OA_STR, methods );
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logArrayOfString(MultispeakDefines.OD_OA_STR, "getDomainNames", strings);
        return new ArrayOfString(strings);
    }

    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new ArrayOfDomainMember(new DomainMember[0]);
    }

    public ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
        return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException {
        init();
        return new ArrayOfOutageDetectionDevice(new OutageDetectionDevice[0]);
    }

    public ArrayOfErrorObject initiateOutageDetectionEventRequest(ArrayOfString meterNos, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        errorObjects = multispeak.ODEvent(vendor, meterNos.getString());
        multispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.OD_OA_STR, "initiateOutageDetectionEventRequest", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }

    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice) throws java.rmi.RemoteException {
        init();
    }

    public ArrayOfObjectRef displayODMonitoringRequests() throws RemoteException
    {
        init();
        return null;
    }

    public ArrayOfErrorObject cancelODMonitoringRequestByObject(ArrayOfObjectRef objectRef, Calendar requestDate) throws RemoteException
    {
        init();
        return null;
    }

    public ArrayOfErrorObject initiateODEventRequestByObject(String objectName, String nounType, PhaseCd phaseCode, Calendar requestDate, String responseURL) throws RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject initiateODMonitoringRequestByObject(String objectName, String nounType, PhaseCd phaseCode, int periodicity, Calendar requestDate, String responseURL) throws RemoteException {
        init();
        return null;
    }
}
