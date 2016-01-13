package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
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
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
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
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }

    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods =
            new String[] { "pingURL", "getMethods", "getCDSupportedMeters", "getCDMeterState",
                "initiateConnectDisconnect" };
        return multispeakFuncs.getMethods(MultispeakDefines.CD_Server_STR, Arrays.asList(methods));
    }

    @Override
    public List<Meter> getCDSupportedMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getCDSupportedMeters", vendor.getCompanyName());

        Date timerStart = new Date();
        MspMeterReturnList meterList = mspMeterDao.getCDSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        List<Meter> meters = meterList.getMeters();
        log.info("Returning " + meters.size() + " CD Supported Meters. ("
            + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meters.size(), meterList.getObjectsRemaining(), "Meter",
            meterList.getLastSent(), "getCDSupportedMeters", vendor.getCompanyName());

        return meters;
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
    public List<ErrorObject> initiateConnectDisconnect(List<ConnectDisconnectEvent> cdEvents, String responseURL,
            String transactionID, Float expirationTime) throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateConnectDisconnect", vendor.getCompanyName());

        String actualResponseURL =
            multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.CB_CD_STR,
                MultispeakDefines.CB_Server_STR);
        List<ErrorObject> errorObjects = multispeakMeterService.cdEvent(vendor, cdEvents, transactionID, actualResponseURL);

        multispeakFuncs.logErrorObjects(MultispeakDefines.CD_Server_STR, "initiateConnectDisconnect", errorObjects);
        return errorObjects;
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
            PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());

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

}
