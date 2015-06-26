package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetInput;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

@Controller
@RequestMapping("/rfnOutagesWidget/*")
public class RfnOutagesWidget extends AdvancedWidgetControllerBase {

    @Autowired private MeterDao meterDao;
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
    
    @Autowired
    public RfnOutagesWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        Set<WidgetInput> simpleWidgetInputSet = new HashSet<WidgetInput>();
        simpleWidgetInputSet.add(simpleWidgetInput);
        
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setInputs(simpleWidgetInputSet);
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, int deviceId) throws ServletRequestBindingException, NotFoundException {

        RfnMeter meter = meterDao.getRfnMeterForId(deviceId);
        model.addAttribute("meter", meter);
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("attributes", attributes);
        
        /* All rfn meters should have an Outage Log point, don't bother catching IllegalUseOfAttribute exception */
        LitePoint logPoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.OUTAGE_LOG);
        model.addAttribute("outageLogPointId", logPoint.getPointID());
        
        return "rfnOutagesWidget/render.jsp";
    }
    
    @RequestMapping("outageData")
    public String outageData(ModelMap model, int deviceId) throws ServletRequestBindingException {

        YukonMeter meter = meterDao.getForId(deviceId);
        Date startDate = new Instant().minus(Duration.standardDays(100)).toDate();
        Date stopDate =  new Date();
        Range<Date> dateRange = new Range<Date>(startDate, true, stopDate, true);
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> data =
            rphDao.getAttributeData(Collections.singleton(meter), BuiltInAttribute.OUTAGE_LOG, false,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.REVERSE, null);
        
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
}