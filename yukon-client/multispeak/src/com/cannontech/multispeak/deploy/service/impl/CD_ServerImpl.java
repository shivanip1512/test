package com.cannontech.multispeak.deploy.service.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
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
import com.cannontech.msp.beans.v3.CDDevice;
import com.cannontech.msp.beans.v3.CDDeviceExchange;
import com.cannontech.msp.beans.v3.CDState;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.data.MspMeterReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.CD_Server;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakMeterService;

@Service
public class CD_ServerImpl implements CD_Server {

    private final Logger log = YukonLogManager.getLogger(this.getClass());

    @Autowired private AttributeService attributeService;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }

    @Override
    public ErrorObject[] pingURL() throws MultispeakWebServiceException {
        init();
        return new ErrorObject[0];
    }

    @Override
    public String[] getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods =
            new String[] { "pingURL", "getMethods", "getCDSupportedMeters", "getCDMeterState",
                "initiateConnectDisconnect" };
        return multispeakFuncs.getMethods(MultispeakDefines.CD_Server_STR, methods);
    }

    @Override
    public String[] getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public DomainMember[] getDomainMembers(String domainName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public Meter[] getCDSupportedMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getCDSupportedMeters", vendor.getCompanyName());

        MspMeterReturnList meterList = null;
        Date timerStart = new Date();
        meterList = mspMeterDao.getCDSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        Meter[] meters = new Meter[meterList.getMeters().size()];
        meterList.getMeters().toArray(meters);
        log.info("Returning " + meters.length + " CD Supported Meters. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meters.length, meterList.getObjectsRemaining(), "Meter",
            meterList.getLastSent(), "getCDSupportedMeters", vendor.getCompanyName());

        return meters;
    }

    @Override
    public Meter[] getModifiedCDMeters(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public LoadActionCode getCDMeterState(String meterNo) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getCDMeterState", vendor.getCompanyName());

        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);

        boolean canInitiatePorterRequest =
            paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);

        LoadActionCode loadActionCode;
        // Performs an actual meter read instead of simply replying from the database.
        // CDMeterState can handle multiple types of communications,
        // but there is no gain from performing a real time read for non-porter meters...to-date.
        if (canInitiatePorterRequest) {
            loadActionCode = multispeakMeterService.CDMeterState(vendor, meter);
        } else { // if we can't initiate a new request (aka RFN meter), then just return what dispatch has
                 // stored.
            loadActionCode = getLoadActionCodeFromCache(meter);
        }
        multispeakEventLogService.returnObject("LoadActionCode." + loadActionCode.value(), meterNo, "getCDMeterState",
            vendor.getCompanyName());
        return loadActionCode;
    }

    @Override
    public ErrorObject[] initiateConnectDisconnect(ConnectDisconnectEvent[] cdEvents, String responseURL,
            String transactionID, Float expirationTime) throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateConnectDisconnect", vendor.getCompanyName());

        String actualResponseURL =
            multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_CD_STR,
                MultispeakDefines.CB_Server_STR);
        ErrorObject[] errorObjects = multispeakMeterService.cdEvent(vendor, cdEvents, transactionID, actualResponseURL);

        multispeakFuncs.logErrorObjects(MultispeakDefines.CD_Server_STR, "initiateConnectDisconnect", errorObjects);
        return errorObjects;
    }

    @Override
    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations)
            throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] CDDeviceAddNotification(CDDevice[] addedCDDs) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] CDDeviceChangedNotification(CDDevice[] changedCDDs) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] CDDeviceExchangeNotification(CDDeviceExchange[] CDDChangeout)
            throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] CDDeviceRemoveNotification(CDDevice[] removedCDDs) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] CDDeviceRetireNotification(CDDevice[] retiredCDDs) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    /**
     * Retrieves DISCONNECT_STATUS attribute's pointData from dispatch.
     * Gets the LoadActionCode based on the type of meter and expected state group for that type.
     * Returns loadActionCode.Unknown when cannot be determined.
     * 
     * @param meter
     * @return
     * @throws MultispeakWebServiceException
     */
    private LoadActionCode getLoadActionCodeFromCache(YukonMeter meter) throws MultispeakWebServiceException {

        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            PointValueQualityHolder pointValueQualityHolder = dynamicDataSource.getPointValue(litePoint.getPointID());

            // dispatch cache could potentially give us an Uninit value
            if (pointValueQualityHolder == null
                || pointValueQualityHolder.getPointQuality() == PointQuality.Uninitialized) {
                log.debug("pointvalueQualityHolder null or pointQuality uninitialized, returning Unknown");
                return LoadActionCode.UNKNOWN;
            }
            return multispeakFuncs.getLoadActionCode(meter, pointValueQualityHolder);
        } catch (IllegalUseOfAttribute e) {
            // meter doesn't have a point for DISCONNECT_STATUS attribute
            log.warn("Unable to find point for DISCONNECT_STATUS. meterNumber:" + meter.getMeterNumber());
            return LoadActionCode.UNKNOWN;
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message, e);
            throw new MultispeakWebServiceException(message);
        }
    }

    @Override
    public String requestRegistrationID() throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String[] getPublishMethods() throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateCDStateRequest(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateArmCDDevice(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateEnableCDDevice(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateDisableCDDevice(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException {
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}
