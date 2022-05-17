package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import com.cannontech.msp.beans.v4.CDState;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.RCDState;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.data.v4.MspMeterReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.CD_Server;
import com.cannontech.multispeak.service.v4.MspValidationService;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;

@Service("CD_ServerImplV4")
public class CD_ServerImpl implements CD_Server {
    @Autowired private AttributeService attributeService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired @Qualifier("mspMeterDaoV4") private MspMeterDao mspMeterDao;

    private final Logger log = YukonLogManager.getLogger(CD_ServerImpl.class);
    private final static String[] methods = new String[] { "PingURL", 
                                                           "GetMethods",
                                                           "GetCDMeterState",
                                                           "getCDSupportedMeters"};

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
        return multispeakFuncs.getMethods(MultispeakDefines.CD_Server_STR, Arrays.asList(methods));
    }

    public CDState getCDMeterState(MeterID meterId) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetCDMeterState", vendor.getCompanyName());

        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterId.getMeterNo());

        boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(),
                PaoTag.PORTER_COMMAND_REQUESTS);
        RCDState rCDState;
        // Performs an actual meter read instead of simply replying from the database.
        // CDMeterState can handle multiple types of communications,
        // but there is no gain from performing a real time read for non-porter meters...to-date.
        if (canInitiatePorterRequest) {
            rCDState = multispeakMeterService.cdMeterState(vendor, meter);
        } else { // if we can't initiate a new request (aka RFN meter), then just return what dispatch has
            // stored.
            rCDState = getRCDStateFromCache(meter);
        }
        multispeakEventLogService.returnObject("RCDState." + rCDState.value(), meterId.getMeterNo(), "GetCDMeterState",
                vendor.getCompanyName());

        CDState cdState = new CDState();
        cdState.setMeterID(meterId);
        cdState.setRCDState(rCDState);

        return cdState;

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
    private RCDState getRCDStateFromCache(YukonMeter meter) throws MultispeakWebServiceException {

        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());

            // dispatch cache could potentially give us an Unit value
            if (pointValueQualityHolder == null
                    || pointValueQualityHolder.getPointQuality() == PointQuality.Uninitialized) {
                log.debug("pointvalueQualityHolder null or pointQuality uninitialized, returning Unknown");
                return RCDState.UNKNOWN;
            }
            return multispeakFuncs.getRCDState(meter, pointValueQualityHolder);
        } catch (IllegalUseOfAttribute e) {
            // meter doesn't have a point for DISCONNECT_STATUS attribute
            log.warn("Unable to find point for DISCONNECT_STATUS. meterNumber:" + meter.getMeterNumber());
            return RCDState.UNKNOWN;
        } catch (DynamicDataAccessException e) {
            String message = "Connection to dispatch is invalid";
            log.error(message, e);
            throw new MultispeakWebServiceException(message);
        }
    }

    @Override
    public Meters getCDSupportedMeters(String lastReceived) throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetCDSupportedMeters", vendor.getCompanyName());

        Date timerStart = new Date();
        MspMeterReturnList meterList = (MspMeterReturnList) mspMeterDao.getCDSupportedMeters(lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(meterList);

        log.info("Returning " + meterList.getSize() + " CD Supported Meters. ("
                + (new Date().getTime() - timerStart.getTime()) * .001 + " secs)");
        multispeakEventLogService.returnObjects(meterList.getSize(), 
                                                meterList.getObjectsRemaining(), 
                                                "Meter",
                                                meterList.getLastSent(), 
                                                "GetCDSupportedMeters", 
                                                vendor.getCompanyName());

        return meterList.getMeters();
    }
}
