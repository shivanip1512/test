package com.cannontech.web.widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.outagestate.OutageState;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ExpireLRUMap;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class MeterOutagesWidget extends WidgetControllerBase {

    private MeterDao meterDao;
    private MeterReadService meterReadService;
    private AttributeService attributeService;
    private PointFormattingService pointFormattingService;

    //Contains <DeviceID>,<PerishableOutageData>
    private ExpireLRUMap<Integer,PerishableOutageData> recentOutageLogs = 
        new ExpireLRUMap<Integer,PerishableOutageData>(100);
    
    public class PerishableOutageData implements ExpireLRUMap.ReadDate{
        public Date readDate;
        
        public List<OutageData> outageData;

        private PerishableOutageData (Date readDate, List<OutageData> outageData) {
            super();
            this.readDate = readDate;
            this.outageData = outageData;
        }
        public Date getReadDate() {
            return readDate;
        }
        public List<OutageData> getOutageData() {
            return outageData;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj != null && obj instanceof MeterOutagesWidget.PerishableOutageData) {
                MeterOutagesWidget.PerishableOutageData data = (MeterOutagesWidget.PerishableOutageData) obj;
                return (data.outageData.equals(this.outageData) &&
                        data.readDate.equals(this.readDate));
            }
            return false;
        }
        
    }

    public class OutageData {
        public String timestamp;
        public String duration;
        public OutageData(String timestamp, String duration ) {
            super();
            this.timestamp = timestamp;
            this.duration = duration;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public String getDuration() {
            return duration;
        }
    }
    
    public class PointValueComparator implements Comparator<PointValueHolder>, Serializable
    {
        public int compare(PointValueHolder o1, PointValueHolder o2) {
            Date thisVal = o1.getPointDataTimeStamp();
            Date anotherVal = o2.getPointDataTimeStamp();
            //return in descreasing order
            return (anotherVal.compareTo(thisVal));
        }
    };
    
    public PointValueComparator pointValueComparator = new PointValueComparator();
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Meter meter = getMeter(request);

        Set<Attribute> allExistingAttributes = getExistingAttributes(meter);

        ModelAndView mav = getOutagesModelAndView(meter, allExistingAttributes);
        
        PerishableOutageData data = getOutageData(meter);
        mav.addObject("data", data);
                             
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        boolean readable = meterReadService.isReadable(meter, allExistingAttributes, user);
        mav.addObject("readable", readable);

        return mav;
    }

    public ModelAndView read(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        Meter meter = getMeter(request);
        
        Set<Attribute> allExistingAttributes = getExistingAttributes(meter);
        
        ModelAndView mav = getOutagesModelAndView(meter, allExistingAttributes);

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        CommandResultHolder result = meterReadService.readMeter(meter, allExistingAttributes, userContext.getYukonUser());
        
        if( allExistingAttributes.contains(BuiltInAttribute.OUTAGE_LOG)) {
            PerishableOutageData data = addOutageData(meter, result.getValues(), userContext);
            mav.addObject("data", data);
        }

        mav.addObject("isRead", true);

        mav.addObject("errorsExist", result.isErrorsExist());
        
        mav.addObject("result", result);
        
        boolean readable = meterReadService.isReadable(meter, allExistingAttributes, userContext.getYukonUser());
        mav.addObject("readable", readable);
        
        return mav;
    }
    
    private ModelAndView getOutagesModelAndView(Meter meter, Set<Attribute> allExistingAttributes) throws Exception{

        ModelAndView mav = new ModelAndView("meterOutagesWidget/render.jsp");
        mav.addObject("device", meter);
        mav.addObject("attribute", BuiltInAttribute.BLINK_COUNT);
        mav.addObject("isRead", false);
        mav.addObject("state", OutageState.UNKNOWN);
        
        mav.addObject("isBlinkConfigured", allExistingAttributes.contains(BuiltInAttribute.BLINK_COUNT) );
        mav.addObject("isOutageSupported", attributeService.isAttributeSupported(meter, BuiltInAttribute.OUTAGE_LOG) );
        mav.addObject("isOutageConfigured", allExistingAttributes.contains(BuiltInAttribute.OUTAGE_LOG) );

        return mav; 
    }
    
    private Set<Attribute> getExistingAttributes(Meter meter) {
        
        Set<Attribute> attributesToShow = new HashSet<Attribute>();
        attributesToShow.add(BuiltInAttribute.BLINK_COUNT);
        attributesToShow.add(BuiltInAttribute.OUTAGE_LOG);
        Set<Attribute> allExistingAtributes = attributeService.getAllExistingAttributes(meter);
        
        // allExisting is a copy...
        allExistingAtributes.retainAll(attributesToShow);
        
        return allExistingAtributes;        
    }

    private Meter getMeter(HttpServletRequest request) throws ServletRequestBindingException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        return meter;
    }

    private PerishableOutageData addOutageData(Meter meter, List<PointValueHolder> values, YukonUserContext userContext) {

        LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.OUTAGE_LOG);
        
        Collections.sort(values, pointValueComparator);
        List<OutageData> outageData = new ArrayList<OutageData>(values.size());
        for (PointValueHolder holder : values) {
            if( holder.getId() == litePoint.getPointID()) {
                String duration = TimeUtil.convertSecondsToTimeString(holder.getValue());
                String timestamp = pointFormattingService.getValueString(holder, Format.DATE, userContext);
                OutageData od = new OutageData(timestamp, duration);
                outageData.add(od);
            }
        }
        PerishableOutageData data = new PerishableOutageData(new Date(), outageData);
            recentOutageLogs.put(meter.getDeviceId(), data);
        return data;
    }
    
    private PerishableOutageData getOutageData(Meter meter) {
            return recentOutageLogs.get(meter.getDeviceId());
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
    public void setMeterReadService(MeterReadService meterReadService) {
        this.meterReadService = meterReadService;
    }
    
    @Required
    public void setPointFormattingService(
            PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }
}