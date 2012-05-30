package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

public class RfnOutagesWidget extends AdvancedWidgetControllerBase {

    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private AttributeReadingWidgetHelper widgetHelper;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private AttributeService attributeService;
    
    private final static ImmutableSet<Attribute> attributes;
    static {
        Builder<Attribute> b = ImmutableSet.builder();
        b.add(BuiltInAttribute.RFN_BLINK_COUNT);
        b.add(BuiltInAttribute.RFN_OUTAGE_COUNT);
        b.add(BuiltInAttribute.BLINK_COUNT);
        attributes = b.build();
    }
    
    @RequestMapping
    public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, NotFoundException {
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        RfnMeter meter = rfnDeviceDao.getMeterForId(deviceId);
        model.addAttribute("meter", meter);
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("attributes", attributes);
        
        /* All rfn meters should have an Outage Log point, don't bother catching IllegalUseOfAttribute exception */
        LitePoint logPoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.OUTAGE_LOG);
        model.addAttribute("outageLogPointId", logPoint.getPointID());
        
        return "rfnOutagesWidget/render.jsp";
    }
    
    @RequestMapping
    public String outageData(ModelMap model, HttpServletRequest req, HttpServletResponse resp, YukonUserContext context) throws ServletRequestBindingException {
        Meter meter = widgetHelper.getMeter(req);
        ListMultimap<PaoIdentifier, PointValueQualityHolder> data = rphDao.getAttributeData(Collections.singleton(meter), 
                                BuiltInAttribute.OUTAGE_LOG, 
                                new Instant().minus(Duration.standardDays(100)).toDate(), 
                                new Date(), 
                                false, 
                                Clusivity.INCLUSIVE_INCLUSIVE, 
                                Order.REVERSE);
        
        Iterable<RfnOutageLog> logs = Iterables.transform(data.get(meter.getPaoIdentifier()), new Function<PointValueQualityHolder, RfnOutageLog>() {
            @Override
            public RfnOutageLog apply(PointValueQualityHolder input) {
                Instant end = new Instant(input.getPointDataTimeStamp());
                Instant start = new Instant(input.getPointDataTimeStamp()).minus(Duration.standardSeconds((long) input.getValue()));
                boolean isInvalid = input.getValue() == RfnInvalidValues.OUTAGE_DURATION.getValue();
                return new RfnOutageLog(start, end, isInvalid);
            }
        });
        if (!Iterables.isEmpty(logs)) {
            model.addAttribute("logs", logs.iterator());
        }
        model.addAttribute("logLoadedAt", new Instant());
        
        return "rfnOutagesWidget/outageLog.jsp";
    }
    
    /**
     * MUST BE PUBLIC FOR c:forEach TAG
     */
    public static class RfnOutageLog {
        private Instant start;
        private Instant end;
        private boolean invalid;
        public RfnOutageLog(Instant start, Instant end, boolean invalid) {
            this.start = start;
            this.end = end;
            this.invalid = invalid;
        }
        public Instant getStart() {
            return start;
        }
        public void setStart(Instant start) {
            this.start = start;
        }
        public Instant getEnd() {
            return end;
        }
        public void setEnd(Instant end) {
            this.end = end;
        }
        public boolean isInvalid() {
            return invalid;
        }
        public void setInvalid(boolean invalid) {
            this.invalid = invalid;
        }
    }
    
    @RequestMapping
    public String read(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException {
        Meter meter = widgetHelper.getMeter(request);
        widgetHelper.initiateRead(request, meter, attributes, model, DeviceRequestType.METER_OUTAGES_WIDGET_ATTRIBUTE_READ);
        return "common/deviceAttributeReadResult.jsp";
    }
    
}