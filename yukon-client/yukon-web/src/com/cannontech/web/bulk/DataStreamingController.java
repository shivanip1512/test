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
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.device.streaming.service.DeviceBehaviorService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingHelper;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/dataStreaming/*")
@CheckRoleProperty(YukonRoleProperty.MASS_CHANGE)
public class DataStreamingController {

    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceBehaviorService deviceBehaviorService;
    
    @RequestMapping("configure")
    public String configure(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        
        DataStreamingConfig newConfig = new DataStreamingConfig();
        DataStreamingHelper.addAllAttributesToConfig(newConfig);
        model.addAttribute("configuration", newConfig);
        model.addAttribute("intervals", DataStreamingHelper.getAllIntervals());
        List<DataStreamingConfig> existingConfigs = new ArrayList<>();
        List<Behavior> existingBehaviors = deviceBehaviorDao.getBehaviorsByType(BehaviorType.DATA_STREAMING);
        existingBehaviors.forEach(behavior->existingConfigs.add(DataStreamingHelper.convertBehaviorToConfig(behavior)));
        model.addAttribute("existingConfigs", existingConfigs);

        return "dataStreaming/configure.jsp";
    }
    
    @RequestMapping(value="configure", method=RequestMethod.POST)
    public String configureSubmit(@ModelAttribute("configuration") DataStreamingConfig configuration, ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        Behavior behavior = new Behavior();
        int behaviorId = 0;
        
        if (configuration.isNewConfiguration()) {
            behavior = DataStreamingHelper.convertConfigToBehavior(configuration);
            behaviorId = deviceBehaviorDao.saveBehavior(behavior);
            behavior.setId(behaviorId);
        } else {
            behaviorId = configuration.getSelectedConfiguration();
            behavior = deviceBehaviorDao.getBehaviorById(behaviorId);
        }
        
        model.addAttribute("behavior", behavior);
        model.addAttribute("configuration", configuration);
        model.addAttribute("remove", false);
        
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
        
        Behavior behavior = new Behavior();
        model.addAttribute("behavior", behavior);
        model.addAttribute("remove", true);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));
       // VerificationInfo verifyInfo = deviceBehaviorService.verify(behavior, deviceIds);
       // model.addAttribute("verificationInfo", verifyInfo);

        return "dataStreaming/verification.jsp";
    }
    
    @RequestMapping(value="verification", method=RequestMethod.POST)
    public String verificationSubmit(@ModelAttribute("behavior") Behavior behavior, ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));
        
        int behaviorId = behavior.getId();
        
        if (behaviorId > 0) {
            deviceBehaviorService.assignBehavior(behaviorId, BehaviorType.DATA_STREAMING, deviceIds);
        } else {
            deviceBehaviorService.unassignBehavior(behaviorId, BehaviorType.DATA_STREAMING, deviceIds);
        }

        //TODO: display results page

        return "dataStreaming/verification.jsp";
    }


}