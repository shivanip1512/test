package com.cannontech.web.widget;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

@Controller
@RequestMapping("/rfnOutagesWidget/*")
public class RfnOutagesWidget extends AdvancedWidgetControllerBase {

    @Autowired private RfnDeviceDao rfnDeviceDao;
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
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, int deviceId) throws ServletRequestBindingException, NotFoundException {

        RfnDevice device = rfnDeviceDao.getDeviceForId(deviceId);
        model.addAttribute("device", device);
        model.addAttribute("deviceId", deviceId);
        
        Set<Attribute> availableAttributes = attributeService.getAvailableAttributes(device);
        availableAttributes.retainAll(attributes);
        
        model.addAttribute("attributes", availableAttributes);
        
        /* All rfn meters should have an Outage Log point, don't bother catching IllegalUseOfAttribute exception */
        LitePoint logPoint = attributeService.getPointForAttribute(device, BuiltInAttribute.OUTAGE_LOG);
        model.addAttribute("outageLogPointId", logPoint.getPointID());
        
        return "rfnOutagesWidget/render.jsp";
    }
    
    @RequestMapping(value = "outageData", method = RequestMethod.POST)
    public String outageData(ModelMap model, int deviceId) throws ServletRequestBindingException {

        RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(deviceId);
        Date startDate = new Instant().minus(Duration.standardDays(100)).toDate();
        Date stopDate =  new Date();
        Range<Date> dateRange = new Range<Date>(startDate, true, stopDate, true);
        
        ListMultimap<PaoIdentifier, PointValueQualityHolder> data =
            rphDao.getAttributeData(Collections.singleton(rfnDevice), BuiltInAttribute.OUTAGE_LOG, false,
                dateRange.translate(CtiUtilities.INSTANT_FROM_DATE), Order.REVERSE, null);
        
        Iterable<RfnOutageLog> logs = Iterables.transform(data.get(rfnDevice.getPaoIdentifier()), new Function<PointValueQualityHolder, RfnOutageLog>() {
            @Override
            public RfnOutageLog apply(PointValueQualityHolder input) {
                Instant end = new Instant(input.getPointDataTimeStamp()).plus(Duration.standardSeconds((long) input.getValue()));
                Instant start = new Instant(input.getPointDataTimeStamp());
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