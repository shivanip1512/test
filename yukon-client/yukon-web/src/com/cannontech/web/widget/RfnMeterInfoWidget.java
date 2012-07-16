package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnDeviceReadCompletionCallback;
import com.cannontech.amr.rfn.service.RfnMeterReadService;
import com.cannontech.amr.rfn.service.WaitableRfnDeviceReadCompletionCallback;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class RfnMeterInfoWidget extends WidgetControllerBase {

    private RfnDeviceDao rfnDeviceDao;
    private RfnMeterReadService rfnMeterReadService;
    private static final Logger log = YukonLogManager.getLogger(RfnMeterInfoWidget.class);

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = getMeterInformationModelAndView(deviceId);
        
        return mav;
    }
    
    private ModelAndView getMeterInformationModelAndView(int deviceId) {
        
        ModelAndView mav = new ModelAndView("rfnMeterInfoWidget/render.jsp");
        
        RfnMeter meter = rfnDeviceDao.getMeterForId(deviceId);
        
        mav.addObject("meter", meter);
        
        return mav;
    }
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        final ModelAndView mav = new ModelAndView("common/rfnMeterReadingsResult.jsp");
        
        RfnMeter meter = rfnDeviceDao.getMeterForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        
        /* Using a waitable, this will block for the initial response to the read request or until a 10 second timeout expires. */
        
        WaitableRfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType> waitableCallback = 
                new WaitableRfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType>(
                    new RfnDeviceReadCompletionCallback<RfnMeterReadingReplyType, RfnMeterReadingDataReplyType>() { 
            
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
        
        rfnMeterReadService.send(meter, waitableCallback);
        
        try {
            waitableCallback.waitForStatusResponse();
        } catch (InterruptedException e) {
            // TODO log something?
        }
        
        return mav;
    }
    
    @Autowired
    public void setRfnDeviceDao(RfnDeviceDao rfnDeviceDao) {
        this.rfnDeviceDao = rfnDeviceDao;
    }
    
    @Autowired
    public void setRfnMeterReadService(RfnMeterReadService rfnMeterReadService) {
        this.rfnMeterReadService = rfnMeterReadService;
    }
    
}