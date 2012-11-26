package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class SimpleAttributesWidget extends WidgetControllerBase {
    
    @Autowired private AttributeReadingWidgetHelper widgetHelper;
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private MeterDao meterDao;
    @Autowired private ObjectFormattingService objectFormattingService;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        ModelAndView mav = new ModelAndView("simpleAttributesWidget/render.jsp");
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        YukonUserContext context = YukonUserContextUtils.getYukonUserContext(request);

        // device
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        mav.addObject("device", device);
        
        // get attributes
        String attributesStr = WidgetParameterHelper.getRequiredStringParameter(request, "attributes");
        Set<Attribute> attributes = getAttributesFromString(attributesStr);
        if (attributes.size() <= 0) {
            throw new ServletException("No Attributes Provided");
        }
        
        // build infos
        String attributeName;
        List<AttributeInfo> attributeInfos = new ArrayList<AttributeInfo>();
        for (Attribute attr : attributes) {
            
            AttributeInfo attrInfo = new AttributeInfo();
            attrInfo.setAttribute(attr);
            
            boolean supported = attributeService.isAttributeSupported(device, attr);
            attrInfo.setSupported(supported);
            
            boolean exists = false;
            if (supported) {
                exists = attributeService.pointExistsForAttribute(device, attr);
            }
            attrInfo.setExists(exists);
            attributeName = objectFormattingService.formatObjectAsString(attr.getMessage(), context);
            attrInfo.setDescription(attributeName);
            
            attributeInfos.add(attrInfo);
        }
        mav.addObject("attributeInfos", attributeInfos);
        
        // readable?
        Meter meter = meterDao.getForId(deviceId);
        boolean isReadable = deviceAttributeReadService.isReadable(Collections.singleton(meter), attributes, user);
        mav.addObject("isReadable", isReadable);

        return mav;
    }
    
    
    
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // get attributes
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        String attributesStr = WidgetParameterHelper.getRequiredStringParameter(request, "attributes");
        Set<Attribute> attributes = getAttributesFromString(attributesStr);
        
        // command
        Meter meter = meterDao.getForId(deviceId);
        
        Set<Attribute> allExistingAttributes = attributeService.getAllExistingAttributes(meter);
        allExistingAttributes.retainAll(attributes);
        
        ModelAndView mav = widgetHelper.initiateRead(request, 
                                                     meter, 
                                                     allExistingAttributes, 
                                                     "common/deviceAttributeReadResult.jsp", 
                                                     DeviceRequestType.SIMPLE_ATTRIBUTES_WIDGET_ATTRIBUTE_READ);
        
        return mav;
    }

    private Set<Attribute> getAttributesFromString(String attributesStr) {

        Set<Attribute> attrs = new LinkedHashSet<Attribute>();
        for(String attrStr : StringUtils.split(attributesStr, ",")) {
            attrs.add(BuiltInAttribute.valueOf(attrStr));
        }

        return attrs;
    }
    
    public class AttributeInfo {
        
        private String description;
        private Attribute attribute;
        private boolean supported;
        private boolean exists;

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }
        public Attribute getAttribute() {
            return attribute;
        }
        public void setAttribute(Attribute attribute) {
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
}