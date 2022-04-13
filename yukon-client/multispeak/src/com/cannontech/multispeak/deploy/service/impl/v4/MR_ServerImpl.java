package com.cannontech.multispeak.deploy.service.impl.v4;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.v4.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.v4.MspMeterReadingReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.MR_Server;

public class MR_ServerImpl implements MR_Server {

    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
   
    private final Logger log = YukonLogManager.getLogger(MR_ServerImpl.class);
    private final static String[] methods = new String[] { "PingURL", 
                                                           "GetMethods",
                                                           "getReadingsByDate"};

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
                                                                          null,         //get all
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          lastReceived,
                                                                          vendor.getMaxReturnRecords());
        
        multispeakFuncs.updateResponseHeader(mspMeterReadingReturnList);
        List<MeterReading> meterReading = mspMeterReadingReturnList.getMeterReading();
        log.info("Returning " + meterReading.size() + " Readings By Date.");
        multispeakEventLogService.returnObjects(meterReading.size(), mspMeterReadingReturnList.getObjectsRemaining(), "MeterReading",
                mspMeterReadingReturnList.getLastSent(), "GetReadingsByDate", vendor.getCompanyName());

        return meterReading;
    }

}
