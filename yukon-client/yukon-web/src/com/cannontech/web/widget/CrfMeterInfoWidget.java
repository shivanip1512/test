package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.crf.dao.CrfMeterDao;
import com.cannontech.amr.crf.model.CrfMeter;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class CrfMeterInfoWidget extends WidgetControllerBase {

    private CrfMeterDao crfMeterDao = null;

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
        ModelAndView mav = new ModelAndView("common/crfMeterReadingsResult.jsp");
        CrfMeter meter = crfMeterDao.getForId(WidgetParameterHelper.getRequiredIntParameter(request, "deviceId"));
        String meterNo = meter.getMeterIdentifier().getCombinedIdentifier();
        
//        List<ErrorObject> errors = mspObjectDao.initiateMeterReadByMeterNo(vendor, new String[] {meterNo});
//        mav.addObject("errors", errors);
        
        return mav;
    }
    
    @Autowired
    public void setCrfMeterDao(CrfMeterDao crfMeterDao) {
        this.crfMeterDao = crfMeterDao;
    }
    
}