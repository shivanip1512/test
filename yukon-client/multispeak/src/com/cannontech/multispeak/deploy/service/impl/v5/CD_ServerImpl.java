package com.cannontech.multispeak.deploy.service.impl.v5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.enumerations.RCDState;
import com.cannontech.msp.beans.v5.enumerations.RCDStateKind;
import com.cannontech.msp.beans.v5.multispeak.CDDevice;
import com.cannontech.msp.beans.v5.multispeak.CDDeviceIdentifier;
import com.cannontech.msp.beans.v5.multispeak.CDState;
import com.cannontech.msp.beans.v5.multispeak.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.data.v5.MspCDDeviceReturnList;
import com.cannontech.multispeak.data.v5.MspMeterReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.CD_Server;
import com.cannontech.multispeak.service.v5.MspValidationService;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;

@Service("CD_ServerImplV5")
public class CD_ServerImpl implements CD_Server {

    private final Logger log = YukonLogManager.getLogger(this.getClass());

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired @Qualifier("mspMeterDaoV5") private MspMeterDao mspMeterDao;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }

    @Override
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }

    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods = null;
        methods =
            new String[] { "PingURL", "GetMethods", "GetAllCDDevices", "GetCDEnabledMeters", "GetCDDeviceStates",
                "InitiateConnectDisconnect" };
        return multispeakFuncs.getMethods(MultispeakDefines.CD_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<CDDevice> getAllCDDevices(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetAllCDDevices", vendor.getCompanyName());
        Date timerStart = new Date();
        MspCDDeviceReturnList mspCDDevices = (MspCDDeviceReturnList) mspMeterDao.getAllCDDevices(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspCDDevices);

        List<CDDevice> cdMeters = mspCDDevices.getCDDevices();
        log.info("Returning " + cdMeters.size() + " CD Supported Meters. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(cdMeters.size(), mspCDDevices.getObjectsRemaining(), "CDDevice",
            mspCDDevices.getLastSent(), "GetAllCDDevices", vendor.getCompanyName());
        return cdMeters;
    }
    
    @Override
    public Meters getCDEnabledMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetCDEnabledMeters", vendor.getCompanyName());
        Date timerStart = new Date();
        MspMeterReturnList meterList = (MspMeterReturnList) mspMeterDao.getCDSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        log.info("Returning " + meterList.getSize() + " CD Enabled Supported Meters. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meterList.getSize(), meterList.getObjectsRemaining(), "Meters",
            meterList.getLastSent(), "GetCDEnabledMeters", vendor.getCompanyName());
        
        return meterList.getMeters();
    }
    

    @Override
    public List<CDState> getCDDeviceStates(List<CDDeviceIdentifier> cdDeviceIdentifiers)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetCDDeviceStates", vendor.getCompanyName());
        
        // TODO can we also support objectsRemaining, LastSent for this method ?
        
        List<CDState> cdStates = new ArrayList<CDState>();
        YukonMeter meter = null;
        if (CollectionUtils.isNotEmpty(cdDeviceIdentifiers)) {
            for (CDDeviceIdentifier cdDeviceIdentifier : cdDeviceIdentifiers) {
                if (cdDeviceIdentifier.getMeterID() != null) {
                    meter = mspValidationService.isYukonMeterNumber(cdDeviceIdentifier.getMeterID().getMeterName());
                } else {
                    String errorMessage = "MeterID is not present in request";
                    log.error(errorMessage);
                    throw new MultispeakWebServiceException(errorMessage);
                }

                boolean canInitiatePorterRequest =
                        paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);

                RCDStateKind rcdStateKind;
                // Performs an actual meter read instead of simply replying from the database.
                // CDMeterState can handle multiple types of communications,
                // but there is no gain from performing a real time read for non-porter meters...to-date.
                if (canInitiatePorterRequest) {
                    rcdStateKind = multispeakMeterService.CDMeterState(vendor, meter);
                } else { // if we can't initiate a new request (aka RFN meter), then just return what dispatch has stored.
                    rcdStateKind = getRCDStateFromCache(meter);
                }
                multispeakEventLogService.returnObject("RCDStateKind." + rcdStateKind.value(),
                    cdDeviceIdentifier.getMeterID().getMeterName(), "GetCDDeviceStates", vendor.getCompanyName());

                // TODO MeterID is also part of request CDDeviceIdentifier and also part of response and both are
                // necessary objects in both (choose either CD device or meter)
                // Can i pass request object's meterID in response if all the data are same.(probably RegisterName
                // and SystemName will not be same)

                CDState cdState = new CDState();
                CDDeviceIdentifier deviceIdentifier = new CDDeviceIdentifier();
            
                MeterID meterID = new MeterID();
                meterID.setValue(cdDeviceIdentifier.getMeterID().getMeterName());
                meterID.setMeterName(cdDeviceIdentifier.getMeterID().getMeterName());
                meterID.setServiceType(cdDeviceIdentifier.getMeterID().getServiceType());
                meterID.setCommunicationAddress(cdDeviceIdentifier.getMeterID().getCommunicationAddress());
                meterID.setCommunicationAddress(cdDeviceIdentifier.getMeterID().getCommunicationsPort());
                meterID.setRegisteredName("Eaton");
                meterID.setSystemName("Yukon");

                deviceIdentifier.setMeterID(meterID);
                cdState.setCDDeviceIdentifier(deviceIdentifier);

                RCDState rcdState = new RCDState();
                rcdState.setValue(rcdStateKind);
                cdState.setRCDState(rcdState);
                cdStates.add(cdState);
            }
        }
        return cdStates;
    }

    @Override
    public List<ErrorObject> initiateConnectDisconnect(List<ConnectDisconnectEvent> cdEvents, String responseURL,
            String transactionID, XMLGregorianCalendar expirationTime) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("InitiateConnectDisconnect", vendor.getCompanyName());

        String actualResponseURL =
            multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.NOT_Server_STR);
        List<ErrorObject> errorObjects = multispeakMeterService.cdEvent(vendor, cdEvents, transactionID, actualResponseURL);

        multispeakFuncs.logErrorObjects(MultispeakDefines.CD_Server_STR, "InitiateConnectDisconnect", errorObjects);
        return errorObjects;
    }

    /**
     * Retrieves DISCONNECT_STATUS attribute's pointData from dispatch.
     * Gets the RCDState based on the type of meter and expected state group for that type.
     * Returns RCDStateKind.Unknown when cannot be determined.
     * 
     * @param meter
     * @return
     * @throws MultispeakWebServiceException
     */
    private RCDStateKind getRCDStateFromCache(YukonMeter meter) throws MultispeakWebServiceException {

        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());

            // dispatch cache could potentially give us an Uninit value
            if (pointValueQualityHolder == null
                || pointValueQualityHolder.getPointQuality() == PointQuality.Uninitialized) {
                log.debug("pointvalueQualityHolder null or pointQuality uninitialized, returning Unknown");
                return RCDStateKind.UNKNOWN;
            }
            return multispeakFuncs.getRCDStateKind(meter, pointValueQualityHolder);
        } catch (IllegalUseOfAttribute e) {
            // meter doesn't have a point for DISCONNECT_STATUS attribute
            log.warn("Unable to find point for DISCONNECT_STATUS. meterNumber:" + meter.getMeterNumber());
            return RCDStateKind.UNKNOWN;
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message, e);
            throw new MultispeakWebServiceException(message);
        }
    }

}
