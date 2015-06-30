package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/simpleAttributesWidget/*")
public class SimpleAttributesWidget extends WidgetControllerBase {
    
    @Autowired private AttributeService attributeService;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private ObjectFormattingService objectFormattingService;

    @Autowired
    public SimpleAttributesWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        addInput(simpleWidgetInput);
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }
    
    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {

        ModelAndView mav = new ModelAndView("simpleAttributesWidget/render.jsp");
        LiteYukonUser user = ServletUtil.getYukonUser(request);

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
            attributeInfos.add(attrInfo);
        }
        mav.addObject("attributeInfos", attributeInfos);

        // readable?
        boolean isReadable = deviceAttributeReadService.isReadable(Collections.singleton(device), attributes, user);
        mav.addObject("isReadable", isReadable);

        return mav;
    }

    @RequestMapping("read")
    public ModelAndView read(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // get attributes
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        String attributesStr = WidgetParameterHelper.getRequiredStringParameter(request, "attributes");
        Set<Attribute> attributes = getAttributesFromString(attributesStr);
        
        // command
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        Set<Attribute> allExistingAttributes = attributeService.getExistingAttributes(device, attributes);

        ModelAndView mav = new ModelAndView("common/deviceAttributeReadResult.jsp");
                
		DeviceReadResult result = deviceAttributeReadService.initiateReadAndWait(device, allExistingAttributes,
				DeviceRequestType.SIMPLE_ATTRIBUTES_WIDGET_ATTRIBUTE_READ, user);

		mav.getModelMap().addAttribute("result", result);

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
        
        private Attribute attribute;
        private boolean supported;
        private boolean exists;

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