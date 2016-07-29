package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.bulk.callbackResult.DataStreamingConfigResult;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.web.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.web.rfn.dataStreaming.model.VerificationInformation;
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
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    
    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);
    private static final List<BuiltInAttribute> attributes = ImmutableList.of(BuiltInAttribute.KVAR,
        BuiltInAttribute.DEMAND, BuiltInAttribute.DELIVERED_KWH, BuiltInAttribute.RECEIVED_KWH);
    
    @RequestMapping("configure")
    public String configure(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("deviceCollection", deviceCollection);

        DataStreamingConfig newConfig = new DataStreamingConfig();
        newConfig.setAccessor(accessor);
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
        
        DataStreamingConfig modelConfig = configuration;
        int configId = 0;
        
        if (configuration.isNewConfiguration()) {
            modelConfig.getAttributes().forEach(attribute -> attribute.setInterval(configuration.getSelectedInterval()));
            configId = dataStreamingService.saveConfig(modelConfig);
            modelConfig.setId(configId);
        } else {
            configId = configuration.getSelectedConfiguration();
            modelConfig = dataStreamingService.findDataStreamingConfiguration(configId);
            modelConfig.setId(configId);
        }
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        modelConfig.setAccessor(accessor);
        
        model.addAttribute("configuration", modelConfig);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));

        VerificationInformation verifyInfo = dataStreamingService.verifyConfiguration(configId, deviceIds);
        verifyInfo.setConfiguration(modelConfig);
        verifyInfo.getDeviceUnsupported().forEach(device -> device.setDeviceCollection(dcProducer.createDeviceCollection(device.getDeviceIds(), null)));
        
        model.addAttribute("verificationInfo", verifyInfo);

        return "dataStreaming/verification.jsp";
    }
    
    @RequestMapping("remove")
    public String remove(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
                
        return "dataStreaming/remove.jsp";
    }
    
    @RequestMapping(value="remove", method=RequestMethod.POST)
    public String removeSubmit(ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        LiteYukonUser user = userContext.getYukonUser();

        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));
        
        DataStreamingConfigResult result = dataStreamingService.unassignDataStreamingConfig(deviceCollection, user);
        model.addAttribute("resultsId", result.getResultsId());

        return "redirect:dataStreamingResults";
    }
    
    @RequestMapping(value="verification", method=RequestMethod.POST)
    public String verificationSubmit(@ModelAttribute("verificationInfo") VerificationInformation verificationInfo, ModelMap model, HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        LiteYukonUser user = userContext.getYukonUser();
        
        List<Integer> deviceIds = new ArrayList<>();
        deviceCollection.getDeviceList().forEach(device->deviceIds.add(device.getDeviceId()));
        
        int configId = verificationInfo.getConfiguration().getId();
        DataStreamingConfigResult result = dataStreamingService.assignDataStreamingConfig(configId, deviceCollection, user);
        result.setConfigId(configId);
        
        model.addAttribute("resultsId", result.getResultsId());

        return "redirect:dataStreamingResults";
    }
    
    @RequestMapping("dataStreamingResults")
    public String dataStreamingResults(HttpServletRequest request, ModelMap model, YukonUserContext userContext) throws ServletException {
        retrieveResults(request, model, userContext);
        return "dataStreaming/results.jsp";
    }
    
    /**
     * Helper method for the results
     */
    private void retrieveResults(HttpServletRequest request, ModelMap model, YukonUserContext userContext) throws ServletException {
        String resultsId = ServletRequestUtils.getRequiredStringParameter(request, "resultsId");
        DataStreamingConfigResult streamingResult = dataStreamingService.findDataStreamingResult(resultsId);
        model.addAttribute("result", streamingResult);
        int configId = streamingResult.getConfigId();
        if(configId > 0) {
            DataStreamingConfig config = dataStreamingService.findDataStreamingConfiguration(configId);
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            config.setAccessor(accessor);
            model.addAttribute("config", config);
        }
    }
    
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public void cancel(HttpServletResponse resp, String key, YukonUserContext userContext) {
        dataStreamingService.cancel(key, userContext.getYukonUser());
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
}