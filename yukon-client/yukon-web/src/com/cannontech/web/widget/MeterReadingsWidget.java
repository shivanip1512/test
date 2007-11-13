package com.cannontech.web.widget;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterReadingsWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private AttributeService attributeService;
    private RawPointHistoryDao rphDao;
    private List<? extends Attribute> attributesToShow;

    private boolean readable = false;
    
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }
    
    public void setAttributesToShow(List<BuiltInAttribute> attributesToShow) {
        // this setter accepts the enum to make Spring happy
        this.attributesToShow = attributesToShow;
    }
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("meterReadingsWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attributes", attributesToShow);
        Set<Attribute> allSupportedAttributes = attributeService.getAvailableAttributes(meter);
        Map<Attribute, Boolean> supportedAttributes = convertSetToMap(allSupportedAttributes);
        mav.addObject("supportedAttributes", supportedAttributes);
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        Map<Attribute, Boolean> existingAttributes = convertSetToMap(allExistingAttributes);
        mav.addObject("existingAttributes", existingAttributes);
        LitePoint lp = attributeService.getPointForAttribute(meter, BuiltInAttribute.USAGE);
        fillInPreviousReadings(mav, lp);

        allExistingAttributes.retainAll(attributesToShow);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        readable = meterReadService.isReadable(meter, allExistingAttributes, user);
        mav.addObject("readable", readable);

        return mav;
    }

    private void fillInPreviousReadings(ModelAndView mav, LitePoint lp) {
        // ask for six months, with a max of 36 results
        Date today = new Date();
        
        // first 36 hours - all points
        Date sixMonthsAgo = DateUtils.addMonths(today, -6);
        List<PointValueHolder> previous36 = rphDao.getPointData(lp.getPointID(), today, sixMonthsAgo, 36);
        
        List<PointValueHolder> previous3Months = Collections.emptyList();
        if (previous36.size() == 36) {
            // great, let's go get some more 
            PointValueHolder lastPvhOfThe36 = previous36.get(36 - 1);
            Date lastDateOfThe36 = lastPvhOfThe36.getPointDataTimeStamp();
            Date beforeDate = DateUtils.truncate(lastDateOfThe36, Calendar.DATE);
            beforeDate = DateUtils.addSeconds(beforeDate, -1);
            mav.addObject("previousReadings_CutoffDate", beforeDate);
            // ask for daily readings from 93 days ago to "before"
            Date today1 = new Date();
            
            Date ninetyThreeDaysAgo = DateUtils.addDays(today1, -93);
            
            if (!beforeDate.before(ninetyThreeDaysAgo)) {
                previous3Months = rphDao.getIntervalPointData(lp.getPointID(), beforeDate, ninetyThreeDaysAgo,
                                                              ChartInterval.DAY_MIDNIGHT, RawPointHistoryDao.Mode.HIGHEST);
            }
        } else {
            mav.addObject("previousReadings_CutoffDate", sixMonthsAgo);
        }
        mav.addObject("previousReadings_All", previous36);
        mav.addObject("previousReadings_Daily", previous3Months);
        mav.addObject("previousReadings_Cutoff", !previous3Months.isEmpty());
    }
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        
        // allExisting is a copy...
        allExistingAttributes.retainAll(attributesToShow);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = meterReadService.readMeter(meter, allExistingAttributes, user);
        
        mav.addObject("errorsExist", result.isErrorsExist());
        
        mav.addObject("result", result);
        
        mav.addObject("readable", readable);
        
        return mav;
    }

    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    private Map<Attribute, Boolean> convertSetToMap(Set<Attribute> allExistingAttributes) {
        Map<Attribute, Boolean> existingMap = new HashMap<Attribute, Boolean>();
        
        // convert to a map of true's because JSP EL can use this to check "contains"
        for (Attribute attribute : allExistingAttributes) {
            existingMap.put(attribute, Boolean.TRUE);
        }
        
        return existingMap;
    }
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Required
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Required
    public void setRphDao(RawPointHistoryDao rphDao) {
        this.rphDao = rphDao;
    }

}