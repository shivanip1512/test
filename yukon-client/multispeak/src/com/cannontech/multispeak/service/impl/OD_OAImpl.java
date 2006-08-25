package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.*;

public class OD_OAImpl extends OD_OASoap_BindingImpl
{
    public MultispeakDao multispeakDao;
    
    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao)
    {
        this.multispeakDao = multispeakDao;
    }


    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return MultispeakFuncs.pingURL(MultispeakDefines.OD_OA_STR);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods", "initiateOutageDetectionEventRequest"};
        return MultispeakFuncs.getMethods(MultispeakDefines.OD_OA_STR, methods );
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        MultispeakFuncs.logArrayOfString(MultispeakDefines.OD_OA_STR, "getDomainNames", strings);
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

    public ArrayOfErrorObject initiateOutageDetectionEventRequest(ArrayOfString meterNos, java.util.Calendar requestDate) throws java.rmi.RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        String companyName = MultispeakFuncs.getCompanyNameFromSOAPHeader();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendor(companyName);

        String url = (vendor != null ? vendor.getUrl() : "(none)");
        if( url == null || url.equalsIgnoreCase(CtiUtilities.STRING_NONE))
        {
            throw new RemoteException("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
/*          ErrorObject error = new ErrorObject();
            error.setErrorString("OMS vendor unknown.  Please contact Yukon administrator to set the Multispeak Vendor Role Property value in Yukon.");
            error.setEventTime(new GregorianCalendar());
            errorObjects = new ErrorObject[]{error};
*/
        }
        else if ( ! Multispeak.getInstance().getPilConn().isValid() )
        {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
/*          ErrorObject error = new ErrorObject();
            error.setErrorString("Connection to Yukon Porter is not valid.");
            error.setEventTime(new GregorianCalendar());
            errorObjects = new ErrorObject[]{error};
*/
        }
        else
        {
            errorObjects = Multispeak.getInstance().ODEvent(vendor, meterNos.getString());
        }
        MultispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.OD_OA_STR, "initiateOutageDetectionEventRequest", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }

    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice) throws java.rmi.RemoteException {
        init();
    }

    public ArrayOfErrorObject initiateODEventRequestByObject(ObjectRef objectRef, PhaseCd phaseCode, Calendar requestDate) throws RemoteException
    {
        init();
        return null;
    }

    public ArrayOfErrorObject initiateODMonitoringRequestByObject(ObjectRef objectRef, PhaseCd phaseCode, int periodicity, Calendar requestDate) throws RemoteException
    {
        init();
        return null;
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
    
    private void init()
    {
        MultispeakFuncs.init();
    }

}
