package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping("/dataStreaming/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class DataStreamingController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);
    private static final List<BuiltInAttribute> attributes = ImmutableList.of(BuiltInAttribute.KVAR,
        BuiltInAttribute.DEMAND, BuiltInAttribute.DELIVERED_KWH, BuiltInAttribute.RECEIVED_KWH);
    
    @RequestMapping("configure")
    public String configure(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("deviceCollection", deviceCollection);

        DataStreamingConfig newConfig = new DataStreamingConfig(accessor);
        attributes.forEach(a -> {
            DataStreamingAttribute attribute = new DataStreamingAttribute();
            attribute.setAttribute(a);
            newConfig.addAttribute(attribute);
        });
        
        List<DataStreamingConfig> existingConfigs = dataStreamingService.getAllDataStreamingConfigurations();
        existingConfigs.forEach(config -> config.setAccessor(accessor));
        model.addAttribute("existingConfigs", existingConfigs);
        
        if (existingConfigs.size() == 0) {
            newConfig.setNewConfiguration(true);
        }
        model.addAttribute("configuration", newConfig);
        model.addAttribute("intervals", intervals);

        return "dataStreaming/configure.jsp";
    }
    
    @RequestMapping(value="configure", method=RequestMethod.POST)
    public String configureSubmit(@ModelAttribute("configuration") DataStreamingConfig configuration, ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        int configId = 0;
        
        if (configuration.isNewConfiguration()) {
            configuration.getAttributes().forEach(attribute -> attribute.setInterval(configuration.getSelectedInterval()));
            configId = dataStreamingService.saveConfig(configuration);
            configuration.setId(configId);
        } else {
            configId = configuration.getSelectedConfiguration();
        }
        
        model.addAttribute("configuration", configuration);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));     
        
    //    VerificationInfo verifyInfo = deviceBehaviorService.verify(behavior, deviceIds);
     //   model.addAttribute("verificationInfo", verifyInfo);

        return "dataStreaming/verification.jsp";
    }
    
    @RequestMapping("remove")
    public String remove(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
                
        return "dataStreaming/remove.jsp";
    }
    
    @RequestMapping(value="remove", method=RequestMethod.POST)
    public String removeSubmit(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));
        
        model.addAttribute("configuration", new DataStreamingConfig());

       // VerificationInfo verifyInfo = deviceBehaviorService.verify(behavior, deviceIds);
       // model.addAttribute("verificationInfo", verifyInfo);

        return "dataStreaming/verification.jsp";
    }
    
    @RequestMapping(value="verification", method=RequestMethod.POST)
    public String verificationSubmit(@ModelAttribute("configuration") DataStreamingConfig configuration, ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));
        
        int configId = configuration.getId();
        
        if (configId > 0) {
            dataStreamingService.assignDataStreamingConfig(configId, deviceIds);
        } else {
            dataStreamingService.unassignDataStreamingConfig(configId, deviceIds);
        }

        //TODO: display results page

        return "dataStreaming/verification.jsp";
    }


}