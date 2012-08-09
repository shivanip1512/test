package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.deploy.service.CDDevice;
import com.cannontech.multispeak.deploy.service.CDDeviceExchange;
import com.cannontech.multispeak.deploy.service.CDState;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.DomainNameChange;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.RegistrationInfo;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakMeterService;

public class CD_ServerImpl implements CD_ServerSoap_PortType
{
    private final Logger log = YukonLogManager.getLogger(CD_ServerImpl.class);
    
    @Autowired public MultispeakMeterService multispeakMeterService;
    @Autowired public MultispeakFuncs multispeakFuncs;
    @Autowired public MspMeterDao mspMeterDao;
    @Autowired public MspValidationService mspValidationService;
    @Autowired public AttributeService attributeService;
    @Autowired public DynamicDataSource dynamicDataSource;
    @Autowired public PaoDefinitionDao paoDefinitionDao;

    private void init() throws RemoteException{
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
        String [] methods = new String[]{"pingURL", "getMethods",
        								 "getCDSupportedMeters",
                                         "getCDMeterState",
                                         "initiateConnectDisconnect"};
        return multispeakFuncs.getMethods(MultispeakDefines.CD_Server_STR, methods );
    }
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.CD_Server_STR, "getDomainNames", strings);
        return strings;
    }
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    
    @Override
    public Meter[] getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        MspMeterReturnList meterList = null;
        Date timerStart = new Date();
        meterList = mspMeterDao.getCDSupportedMeters(lastReceived, vendor.getMaxReturnRecords());
        
        multispeakFuncs.updateResponseHeader(meterList);
        
        Meter[] meters = new Meter[meterList.getMeters().size()];
        meterList.getMeters().toArray(meters);
        log.info("Returning " + meters.length + " CD Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        return meters;
    }
    
    @Override
    public Meter[] getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);

        boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
        
        // Performs an actual meter read instead of simply replying from the database.
        // CDMeterState can handle multiple types of communications, 
        //  but there is no gain from performing a real time read for non-porter meters...to-date. 
        if (canInitiatePorterRequest) {
            LoadActionCode loadActionCode = multispeakMeterService.CDMeterState(vendor, meter);
            return loadActionCode;
        } else {    // if we can't initiate a new request (aka RFN meter), then just return what dispatch has stored.
            return getLoadActionCodeFromCache(meter);
        }
    }

    @Override
    public ErrorObject[] initiateConnectDisconnect(
            ConnectDisconnectEvent[] cdEvents, String responseURL,
            String transactionID, float expirationTime) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        errorObjects = multispeakMeterService.cdEvent(vendor, cdEvents, transactionID);
        
        multispeakFuncs.logErrorObjects(MultispeakDefines.CD_Server_STR, "initiateConnectDisconnect", errorObjects);
        return errorObjects;
    }
    
    @Override
    public ErrorObject[] serviceLocationChangedNotification(
            com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws java.rmi.RemoteException {
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
    public ErrorObject[] CDDeviceAddNotification(CDDevice[] addedCDDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceChangedNotification(CDDevice[] changedCDDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceExchangeNotification(CDDeviceExchange[] CDDChangeout) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceRemoveNotification(CDDevice[] removedCDDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceRetireNotification(CDDevice[] retiredCDDs) throws RemoteException {
        init();
        return null;
    }

    /**
     * Retrieves DISCONNECT_STATUS attribute's pointData from dispatch.
     * Gets the LoadActionCode based on the type of meter and expected state group for that type.
     * Returns loadActionCode.Unknown when cannot be determined.
     * @param meter
     * @return
     * @throws RemoteException
     */
    private LoadActionCode getLoadActionCodeFromCache(YukonMeter meter) throws RemoteException {
        
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            PointValueQualityHolder pointValueQualityHolder = dynamicDataSource.getPointValue(litePoint.getPointID());
            
            // dispatch cache could potentially give us an Uninit value
            if( pointValueQualityHolder != null && 
                    pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                return LoadActionCode.Unknown;
            }
            return multispeakFuncs.getLoadActionCode(meter, pointValueQualityHolder);
        } catch (IllegalUseOfAttribute e) {
            // meter doesn't have a point for DISCONNECT_STATUS attribute
            log.warn("Unable to find point for DISCONNECT_STATUS. meterNumber:" + meter.getMeterNumber());
            return LoadActionCode.Unknown;
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message, e);
            throw new RemoteException(message);
        }
    }

    @Override
    public String requestRegistrationID() throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public String[] getPublishMethods() throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(
            DomainMember[] changedDomainMembers) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(
            DomainNameChange[] changedDomainNames) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateCDStateRequest(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateArmCDDevice(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateEnableCDDevice(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateDisableCDDevice(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        init();
        return null;
    }
}