package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.crf.dao.CrfMeterDao;
import com.cannontech.amr.crf.message.CrfMeterReadingDataReplyType;
import com.cannontech.amr.crf.message.CrfMeterReadingReplyType;
import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.amr.crf.service.CrfMeterReadCompletionCallback;
import com.cannontech.amr.crf.service.CrfMeterReadService;
import com.cannontech.amr.crf.service.WaitableCrfMeterReadCompletionCallback;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class CrfMeterInfoWidget extends WidgetControllerBase {

    private CrfMeterDao crfMeterDao;
    private CrfMeterReadService crfMeterReadService;
    private static final Logger log = YukonLogManager.getLogger(CrfMeterInfoWidget.class);

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        
        ModelAndView mav = getMeterInformationModelAndView(deviceId);
        
        return mav;
    }
    
    private ModelAndView getMeterInformationModelAndView(int deviceId) {
        
        ModelAndView mav = new ModelAndView("crfMeterInfoWidget/render.jsp");
        
        CrfMeter meter = crfMeterDao.getForId(deviceId);
        
        mav.addObject("meter", meter);
        
        return mav;
    }
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        final ModelAndView mav = new ModelAndView("common/crfMeterReadingsResult.jsp");
        
        CrfMeter meter = crfMeterDao.getForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        
        /* Using a waitable, this will block for the initial response to the read request or until a 10 second timeout expires. */
        
        WaitableCrfMeterReadCompletionCallback waitableCallback = new WaitableCrfMeterReadCompletionCallback(new CrfMeterReadCompletionCallback() {
            
            @Override
            public void receivedStatus(CrfMeterReadingReplyType status) {
                mav.addObject("responseStatus", status);
            }
            
            @Override
            public void receivedData(PointValueHolder value) {
            }
            
            @Override
            public void receivedDataError(CrfMeterReadingDataReplyType replyType) {
            }
            
            @Override
            public void complete() {
            }

            @Override
            public void receivedStatusError(CrfMeterReadingReplyType replyType) {
            }

            @Override
            public void processingExceptionOccured(String message) {
                log.error(message);
            }
        });
        
        crfMeterReadService.send(meter.getMeterIdentifier(), waitableCallback);
        
        try {
            waitableCallback.waitForStatusResponse();
        } catch (InterruptedException e) {
            // TODO log something?
        }
        
        return mav;
    }
    
    @Autowired
    public void setCrfMeterDao(CrfMeterDao crfMeterDao) {
        this.crfMeterDao = crfMeterDao;
    }
    
    @Autowired
    public void setCrfMeterReadService(CrfMeterReadService crfMeterReadService) {
        this.crfMeterReadService = crfMeterReadService;
    }
    
}