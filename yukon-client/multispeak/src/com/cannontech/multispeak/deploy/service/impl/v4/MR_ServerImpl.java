package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.v4.MspMeterReadingReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.MR_Server;
import com.cannontech.multispeak.service.v4.MspValidationService;
import com.cannontech.multispeak.service.v4.MultispeakMeterService;

public class MR_ServerImpl implements MR_Server {

    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private MeterReadingProcessingService meterReadingProcessingService;
    @Autowired private MspValidationService mspValidationService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
   
    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    private final static String[] methods = new String[] { "PingURL", 
                                                           "GetMethods",
                                                           "GetReadingsByDate",
                                                           "GetReadingsByMeterID",
                                                           "GetLatestReadings",
                                                           "GetLatestReadingByMeterID"};

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
        return multispeakFuncs.getMethods(MultispeakDefines.MR_Server_STR, Arrays.asList(methods));
    }
    
    @Override
    public List<MeterReading> getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByDate", vendor.getCompanyName());

        MspMeterReadingReturnList mspMeterReadingReturnList = mspRawPointHistoryDao.retrieveMeterReading(ReadBy.NONE,
                                                                                            null, // get all
                                                                                            startDate.getTime(),
                                                                                            endDate.getTime(),
                                                                                            lastReceived,
                                                                                            vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadingReturnList);
        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " Readings By Date.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(),
                "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetReadingsByDate", vendor.getCompanyName());

        return meterReading;
    }

    @Override
    public List<MeterReading> getReadingsByMeterID(String meterNo, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException {
        init(); // init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetReadingsByMeterID", vendor.getCompanyName());

        // Validate the meterNo is a Yukon meterNumber
        mspValidationService.isYukonMeterNumber(meterNo);

        MspMeterReadingReturnList mspMeterReadingReturnList = mspRawPointHistoryDao.retrieveMeterReading(ReadBy.METER_NUMBER,
                                                                                                meterNo,
                                                                                                startDate.getTime(),
                                                                                                endDate.getTime(),
                                                                                                null,
                                                                                                vendor.getMaxReturnRecords());

        // There is only one MeterNo in the response, so does it make sense to update the header with lastSent?
        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " Readings By MeterID.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(),
                "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetReadingsByMeterID", vendor.getCompanyName());
    
        return meterReading;
    }

    @Override
    public List<MeterReading> getLatestReadings(String lastReceived)
            throws MultispeakWebServiceException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadings", vendor.getCompanyName());

        MspMeterReadingReturnList mspMeterReadingReturnList = mspRawPointHistoryDao.retrieveLatestMeterReading(ReadBy.NONE, null,
                lastReceived, vendor.getMaxReturnRecords());

        multispeakFuncs.updateResponseHeader(mspMeterReadingReturnList);

        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " latest Readings.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(),
                "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetLatestReadings", vendor.getCompanyName());

        return meterReading;
    }

    @Override
    public MeterReading getLatestReadingByMeterID(String meterNo) throws MultispeakWebServiceException {
        init(); //init is already performed on the call to isAMRMeter()

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("GetLatestReadingByMeterID", vendor.getCompanyName());
        
        //Validate the meterNo is a Yukon meterNumber
        YukonMeter meter = mspValidationService.isYukonMeterNumber(meterNo);
        
        boolean canInitiatePorterRequest = paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.PORTER_COMMAND_REQUESTS);
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") && canInitiatePorterRequest) {
            
            // Don't know the responseURL as it's not provided in this method (by definition!) Using default for SEDC.
            String responseUrl = multispeakFuncs.getResponseUrl(vendor, null, MultispeakDefines.CB_Server_STR);
            MeterReading meterReading = multispeakMeterService.getLatestReadingInterrogate(vendor, meter, responseUrl);
            multispeakEventLogService.returnObject("MeterReading", meterNo, "GetLatestReadingByMeterID", vendor.getCompanyName());
            return meterReading;
        } else { //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
                MeterReading meterReading = meterReadingProcessingService.createMeterReading(meter);
                
                EnumSet<BuiltInAttribute> attributesToLoad = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);
    
                for (BuiltInAttribute attribute : attributesToLoad) {
                    try {
                        LitePoint litePoint = attributeService.getPointForAttribute(meter, attribute);
                        PointValueQualityHolder pointValueQualityHolder = asyncDynamicDataSource.getPointValue(litePoint.getPointID());
                        if( pointValueQualityHolder != null && 
                                pointValueQualityHolder.getPointQuality() != PointQuality.Uninitialized) {
                            meterReadingProcessingService.updateMeterReading(meterReading, attribute, pointValueQualityHolder);
                        }
                    } catch (IllegalUseOfAttribute e) {
                        //it's okay...just skip
                    }
                }
                multispeakEventLogService.returnObject("MeterReading", meterNo, "GetLatestReadingByMeterID",
                        vendor.getCompanyName());
                return meterReading;
            } catch (DynamicDataAccessException e) {
                String message = "Connection to dispatch is invalid";
                log.error(message);
                throw new MultispeakWebServiceException(message);
            }
        }
    }

}
