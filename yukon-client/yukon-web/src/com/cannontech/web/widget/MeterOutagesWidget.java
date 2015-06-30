package com.cannontech.web.widget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ExpireLRUMap;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.google.common.collect.Sets;

/**
 * Widget used to display basic device information
 * This is assumed to be for PLC meters. See RfnOutagesWidget for RFN meter types.
 */
@Controller
@RequestMapping("/meterOutagesWidget/*")
public class MeterOutagesWidget extends AdvancedWidgetControllerBase {

	@Autowired private MeterDao meterDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private AttributeService attributeService;
    
    @Autowired
    public MeterOutagesWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        addInput(simpleWidgetInput);
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }

    //Contains <DeviceID>,<PerishableOutageData>
    private final ExpireLRUMap<Integer,PerishableOutageData> recentOutageLogs = 
        new ExpireLRUMap<Integer,PerishableOutageData>(100);
    
    public class PerishableOutageData implements ExpireLRUMap.ReadDate{
        public Date readDate;
        
        public List<OutageData> outageData;

        private PerishableOutageData (Date readDate, List<OutageData> outageData) {
            super();
            this.readDate = readDate;
            this.outageData = outageData;
        }
        @Override
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
        public String outageLogIndex = "-";
        public PointValueHolder timestamp;
        public String duration;
        public OutageData(String outageLogIndex, PointValueHolder timestamp, String duration ) {
            super();
            this.timestamp = timestamp;
            this.duration = duration;
            if (StringUtils.isNumeric(outageLogIndex)) {
                this.outageLogIndex = outageLogIndex;
            }
        }
        public PointValueHolder getTimestamp() {
            return timestamp;
        }
        public String getDuration() {
            return duration;
        }
        public String getOutageLogIndex() {
            return outageLogIndex;
        }
    }
    
    public class PointValueComparator implements Comparator<PointValueHolder>, Serializable
    {
        @Override
        public int compare(PointValueHolder o1, PointValueHolder o2) {
            Date thisVal = o1.getPointDataTimeStamp();
            Date anotherVal = o2.getPointDataTimeStamp();
            //return in descreasing order
            return (anotherVal.compareTo(thisVal));
        }
    };
    
    public PointValueComparator pointValueComparator = new PointValueComparator();
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId){

    	YukonMeter meter = meterDao.getForId(deviceId);
        Set<Attribute> attributes = getExistingAttributes(meter);

        initModel( model, meter, attributes, userContext);
        
        PerishableOutageData data = getOutageData(meter);
        model.put("data", data);

        return "meterOutagesWidget/render.jsp";
    }

    @RequestMapping("read")
    public String read(ModelMap model, YukonUserContext userContext, Integer deviceId){
  
    	YukonMeter meter = meterDao.getForId(deviceId);
        Set<Attribute> attributes = getExistingAttributes(meter);
        
        initModel( model, meter, attributes, userContext);
        
		DeviceReadResult result = deviceAttributeReadService.initiateReadAndWait(meter, attributes,
				DeviceRequestType.METER_OUTAGES_WIDGET_ATTRIBUTE_READ, userContext.getYukonUser());
       
        if( attributes.contains(BuiltInAttribute.OUTAGE_LOG)) {
            PerishableOutageData data = addOutageData(meter, result.getPointValues(), userContext);
            model.put("data", data);
        }

        model.put("isRead", true);
        model.put("result", result);
        
        return "meterOutagesWidget/render.jsp";
    }
    
    private void initModel(ModelMap model, YukonMeter meter, Set<Attribute> attributes, YukonUserContext userContext){

        boolean readable = deviceAttributeReadService.isReadable(Sets.newHashSet(meter), attributes, userContext.getYukonUser());
        model.put("readable", readable);
        model.put("device", meter);
        model.put("attribute", BuiltInAttribute.BLINK_COUNT);
        model.put("isRead", false);
        model.put("isBlinkConfigured", attributes.contains(BuiltInAttribute.BLINK_COUNT) );
        model.put("isOutageSupported", attributeService.isAttributeSupported(meter, BuiltInAttribute.OUTAGE_LOG) );
        model.put("isOutageConfigured", attributes.contains(BuiltInAttribute.OUTAGE_LOG) );
        
    }
    
    private Set<Attribute> getExistingAttributes(YukonDevice device) {
        
        Set<Attribute> attributesToShow = new HashSet<Attribute>();
        attributesToShow.add(BuiltInAttribute.BLINK_COUNT);
        attributesToShow.add(BuiltInAttribute.OUTAGE_LOG);
        return attributeService.getExistingAttributes(device, attributesToShow);
    }

    private PerishableOutageData addOutageData(YukonMeter meter, List<PointValueHolder> values, YukonUserContext userContext) {

        LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.OUTAGE_LOG);
        
        Collections.sort(values, pointValueComparator);
        List<OutageData> outageData = new ArrayList<OutageData>(values.size());
        for (PointValueHolder holder : values) {
            if( holder.getId() == litePoint.getPointID()) {
                String duration = TimeUtil.convertSecondsToNormalizedStandard(holder.getValue());
                PointValueHolder timestamp = holder;

                // based on the string returned from porter, parse out the outage index
                int outageStrIndex = ((PointData)holder).getStr().indexOf("/ Outage ");
                int beginIndex = outageStrIndex + 9;   // 9 = num chars from "/ Outage " to the log index in the log
                
                // Substring from the outage log index to the end of the string
                String outageLogIndexSubStr = ((PointData)holder).getStr().substring(beginIndex);
                
                // Read the index value with a Scanner
                Scanner scanner = new Scanner(outageLogIndexSubStr);
                String outageLogIndex = String.valueOf(scanner.nextInt());
                scanner.close();
                
                OutageData od = new OutageData(outageLogIndex, timestamp, duration);
                outageData.add(od);
            }
        }
        PerishableOutageData data = new PerishableOutageData(new Date(), outageData);
            recentOutageLogs.put(meter.getDeviceId(), data);
        return data;
    }
    
    private PerishableOutageData getOutageData(YukonMeter meter) {
            return recentOutageLogs.get(meter.getDeviceId());
    }
}