package com.cannontech.services.rfn.endpoint;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingPorterUtil;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.dataStreaming.ReportedDataStreamingConfig;
import com.cannontech.message.porter.message.RfnDataStreamingUpdate;
import com.cannontech.message.porter.message.RfnDataStreamingUpdateReply;
import com.cannontech.services.ThriftServiceBase;

public class RfnDataStreamingUpdateListener extends ThriftServiceBase<RfnDataStreamingUpdate, RfnDataStreamingUpdateReply> {
    private Logger log = YukonLogManager.getLogger(RfnDataStreamingUpdateListener.class);
    
    @Autowired private DataStreamingService dataStreamingService;
    
    @Override
    protected RfnDataStreamingUpdateReply handleRequest(RfnDataStreamingUpdate request) {
        boolean success = false;
        try {
            //send update to Data Streaming service
            log.debug("Updating data streaming config report for pao ID: " + request.paoId + " " + request.json);
            success = updateDataStreamingConfigReport(request.paoId, request.json);
            log.debug("Updated pao ID: " + request.paoId + ", success: " + success);
        } catch (Exception e) {
            log.error("Caught exception updating data streaming config report for pao ID: " + request.paoId + " " + request.json);
        }
        return new RfnDataStreamingUpdateReply(success);
    }

    public boolean updateDataStreamingConfigReport(int paoId, String json) {
        try {
            ReportedDataStreamingConfig config = DataStreamingPorterUtil.extractReportedDataStreamingConfig(json);
            if (config == null){
                log.error("Could not parse Data Streaming config for paoId " + paoId + " " + json);
            } else {
                return dataStreamingService.updateReportedConfig(paoId, config);
            }
        } catch (IOException e) {
            log.error("Failed to update Data Streaming config for paoId " + paoId + " " + json, e);
        }
        
        return false;
    }

    @Override
    protected RfnDataStreamingUpdateReply handleFailure() {
        return new RfnDataStreamingUpdateReply(false);
    }
}
