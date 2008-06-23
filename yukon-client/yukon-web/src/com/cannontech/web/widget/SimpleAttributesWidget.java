package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.MeterReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class SimpleAttributesWidget extends WidgetControllerBase {
    
    private AttributeService attributeService = null;
    private MeterReadService meterReadService = null;
    private DeviceDao deviceDao;
    private MeterDao meterDao;
    
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("simpleAttributesWidget/render.jsp");
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        // device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        YukonDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("device", device);
        
        // get attributes
        String attributesStr = WidgetParameterHelper.getRequiredStringParameter(request, "attributes");
        Set<BuiltInAttribute> attributes = getAttributesFromString(attributesStr);
        
        // get labels
        String labelsStr = WidgetParameterHelper.getStringParameter(request, "labels");
        List<String> labels = new ArrayList<String>();
        if (labelsStr != null) {
            labels.addAll(Arrays.asList(StringUtils.split(labelsStr, ",")));
        }
        
        // build infos
        int attrIndx = 0;
        List<AttributeInfo> attributeInfos = new ArrayList<AttributeInfo>();
        for (BuiltInAttribute attr : attributes) {
            
            AttributeInfo attrInfo = new AttributeInfo();
            attrInfo.setAttribute(attr);
            attrInfo.setSupported(attributeService.isAttributeSupported(device, attr));
            attrInfo.setExists(attributeService.pointExistsForAttribute(device, attr));
            
            if (labelsStr != null) {
                attrInfo.setDescription(labels.get(attrIndx));
            }
            else {
                attrInfo.setDescription(attr.getDescription());
            }
            
            attributeInfos.add(attrInfo);
            attrIndx++;
        }
        mav.addObject("attributeInfos", attributeInfos);
        
        // readable?
        Meter meter = meterDao.getForId(deviceId);
        boolean isReadable = meterReadService.isReadable(meter, attributes, user);
        mav.addObject("isReadable", isReadable);

        return mav;
    }
    
    
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("common/meterReadingsResult.jsp");
        
        // get attributes
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        String attributesStr = WidgetParameterHelper.getRequiredStringParameter(request, "attributes");
        Set<BuiltInAttribute> attributes = getAttributesFromString(attributesStr);
        
        // command
        Meter meter = meterDao.getForId(deviceId);
        CommandResultHolder result = meterReadService.readMeter(meter, attributes, user);
        
        //result
        mav.addObject("result", result);
        
        return mav;
    }

    private Set<BuiltInAttribute> getAttributesFromString(String attributesStr) {

        Set<BuiltInAttribute> attrs = new LinkedHashSet<BuiltInAttribute>();
        for(String attrStr : StringUtils.split(attributesStr, ",")) {
            attrs.add(BuiltInAttribute.valueOf(attrStr));
        }

        return attrs;
    }
    
    public class AttributeInfo {
        
        private String description;
        private BuiltInAttribute attribute;
        private boolean supported;
        private boolean exists;

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }
        public BuiltInAttribute getAttribute() {
            return attribute;
        }
        public void setAttribute(BuiltInAttribute attribute) {
            this.attribute = attribute;
        }
        public boolean isSupported() {
            return supported;
        }
        public void setSupported(boolean supported) {
            this.supported = supported;
        }
        public boolean isExists() {
            return exists;
        }
        public void setExists(boolean exists) {
            this.exists = exists;
        }
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
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
}
