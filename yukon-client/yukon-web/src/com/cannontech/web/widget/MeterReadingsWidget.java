package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.TimeUtil;
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
        Set<Attribute> allSupportedAtributes = attributeService.getAvailableAttributes(meter);
        Map<Attribute, Boolean> supportedAttributes = convertSetToMap(allSupportedAtributes);
        mav.addObject("supportedAttributes", supportedAttributes);
        Set<Attribute> allExistingAtributes = attributeService.getAllExistingAtributes(meter);
        Map<Attribute, Boolean> existingAttributes = convertSetToMap(allExistingAtributes);
        mav.addObject("existingAttributes", existingAttributes);
        LitePoint lp = attributeService.getPointForAttribute(meter, BuiltInAttribute.USAGE);
        
        List<PointValueHolder> previousReadings = getPreviousForDropdown(lp);
        mav.addObject("previousReadings", previousReadings);
        
        return mav;
    }

    private List<PointValueHolder> getPreviousForDropdown(LitePoint lp) {
        List<PointValueHolder> allReadings = new ArrayList<PointValueHolder>();
        Date today = new Date();
        
        // get one week of daily readings
        Date oneWeekAgo = TimeUtil.addDays(today, -7);
        List<PointValueHolder> dailyReadings = 
            rphDao.getIntervalPointData(lp.getPointID(), oneWeekAgo, today, 
                                        ChartInterval.DAY, RawPointHistoryDao.Mode.HIGHEST);
        allReadings.addAll(dailyReadings);
        
        // get an additional (93 - 7) days of weekly readings
        Date threeMonthsAgo = TimeUtil.addDays(oneWeekAgo, -(93 - 7));
        List<PointValueHolder> weeklyReadings = 
            rphDao.getIntervalPointData(lp.getPointID(), threeMonthsAgo, oneWeekAgo,
                                        ChartInterval.WEEK, RawPointHistoryDao.Mode.HIGHEST);
        allReadings.addAll(weeklyReadings);
        
        // create a read only delegating list
        ReverseList<PointValueHolder> reversedResult = new ReverseList<PointValueHolder>(allReadings);
        return reversedResult;
    }

    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Meter meter = getMeter(request);
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        Set<Attribute> allExistingAtributes = attributeService.getAllExistingAtributes(meter);
        
        // allExisting is a copy...
        allExistingAtributes.retainAll(attributesToShow);
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        CommandResultHolder result = meterReadService.readMeter(meter, allExistingAtributes, user);
        
        mav.addObject("errorsExist", result.isErrorsExist());
        
        mav.addObject("result", result);
        
        return mav;
    }

    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }
    
    private Map<Attribute, Boolean> convertSetToMap(Set<Attribute> allExistingAtributes) {
        Map<Attribute, Boolean> existingMap = new HashMap<Attribute, Boolean>();
        
        // convert to a map of true's because JSP EL can use this to check "contains"
        for (Attribute attribute : allExistingAtributes) {
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
