package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.amr.rfn.service.WaitableRfnMeterReadCompletionCallback;
import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class RfnMeterInfoWidget extends WidgetControllerBase {

    private RfnMeterDao rfnMeterDao;
    private RfnMeterReadService rfnMeterReadService;
    private static final Logger log = YukonLogManager.getLogger(RfnMeterInfoWidget.class);

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = getMeterInformationModelAndView(deviceId);
        
        return mav;
    }
    
    private ModelAndView getMeterInformationModelAndView(int deviceId) {
        
        ModelAndView mav = new ModelAndView("rfnMeterInfoWidget/render.jsp");
        
        RfnMeter meter = rfnMeterDao.getForId(deviceId);
        
        mav.addObject("meter", meter);
        
        return mav;
    }
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        final ModelAndView mav = new ModelAndView("common/rfnMeterReadingsResult.jsp");
        
        RfnMeter meter = rfnMeterDao.getForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        
        /* Using a waitable, this will block for the initial response to the read request or until a 10 second timeout expires. */
        
        WaitableRfnMeterReadCompletionCallback waitableCallback = new WaitableRfnMeterReadCompletionCallback(new RfnMeterReadCompletionCallback() {
            
            @Override
            public void receivedStatus(RfnMeterReadingReplyType status) {
                mav.addObject("responseStatus", status);
            }
            
            @Override
            public void receivedData(PointValueHolder value) {
            }
            
            @Override
            public void receivedDataError(RfnMeterReadingDataReplyType replyType) {
            }
            
            @Override
            public void complete() {
            }

            @Override
            public void receivedStatusError(RfnMeterReadingReplyType replyType) {
            }

            @Override
            public void processingExceptionOccured(String message) {
                log.error(message);
            }
        });
        
        rfnMeterReadService.send(meter.getMeterIdentifier(), waitableCallback);
        
        try {
            waitableCallback.waitForStatusResponse();
        } catch (InterruptedException e) {
            // TODO log something?
        }
        
        return mav;
    }
    
    @Autowired
    public void setRfnMeterDao(RfnMeterDao rfnMeterDao) {
        this.rfnMeterDao = rfnMeterDao;
    }
    
    @Autowired
    public void setRfnMeterReadService(RfnMeterReadService rfnMeterReadService) {
        this.rfnMeterReadService = rfnMeterReadService;
    }
    
}