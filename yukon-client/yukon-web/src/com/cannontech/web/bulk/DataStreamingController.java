package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingAttribute;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfig;
import com.cannontech.amr.rfn.dataStreaming.model.DataStreamingConfigException;
import com.cannontech.amr.rfn.dataStreaming.model.VerificationInformation;
import com.cannontech.amr.rfn.dataStreaming.service.DataStreamingService;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparmLicense;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping("/dataStreaming/*")
@CheckCparmLicense(license = MasterConfigLicenseKey.RF_DATA_STREAMING_ENABLED)
@CheckRoleProperty(YukonRoleProperty.RF_DATA_STREAMING)
public class DataStreamingController {

    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DataStreamingService dataStreamingService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private DataStreamingAttributeHelper dataStreamingAttributeHelper;
    @Autowired private AlertService alertService;
    private static final List<Integer> intervals = ImmutableList.of(1, 3, 5, 15, 30);

    @RequestMapping(value="configure", method=RequestMethod.GET)
    public String configure(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        setupModel(deviceCollection, model, userContext);
        model.addAttribute("action", CollectionAction.CONFIGURE_DATA_STREAMING);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/dataStreaming/configure.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value="configureInputs", method=RequestMethod.GET)
    public String configureInputs(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) throws ServletException {
        setupModel(deviceCollection, model, userContext);
        return "dataStreaming/configure.jsp";
    }
    
    private void setupModel(DeviceCollection deviceCollection, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("deviceCollection", deviceCollection);

        //check if the selected devices support data streaming
        Set<PaoType> types = deviceCollection.getDeviceList().stream()
                .map(SimpleDevice::getDeviceType)
                .collect(Collectors.toSet());
        List<BuiltInAttribute> attributes = new ArrayList<>(dataStreamingAttributeHelper.getAllSupportedAttributes(types));
        attributes.sort((a1, a2) -> a1.getDescription().compareTo(a2.getDescription()));
        boolean dsNotSupported = false;
        if (attributes.size() == 0) {
            dsNotSupported = true;
        }
        model.addAttribute("dataStreamingNotSupported", dsNotSupported);

        DataStreamingConfig newConfig = new DataStreamingConfig();
        newConfig.setAccessor(accessor);
        newConfig.setSelectedInterval(5);
        attributes.forEach(a -> {
            DataStreamingAttribute attribute = new DataStreamingAttribute();
            attribute.setAttribute(a);
            newConfig.addAttribute(attribute);
        });

        List<DataStreamingConfig> supportedConfigs = new ArrayList<>();
        List<DataStreamingConfig> existingConfigs = dataStreamingService.getAllDataStreamingConfigurations();

        //only display configs that have attributes that the selected devices support
        existingConfigs.forEach(config -> {
            config.setAccessor(accessor);
            config.getAttributes().forEach(att ->{
                if (attributes.contains(att.getAttribute())) {
                    if (!supportedConfigs.contains(config)) {
                        supportedConfigs.add(config);
                    }
                }
            });
        });
        model.addAttribute("existingConfigs", supportedConfigs);

        if (supportedConfigs.size() == 0) {
            newConfig.setNewConfiguration(true);
        }
        model.addAttribute("configuration", newConfig);
        model.addAttribute("intervals", intervals);
        dataStreamingAttributeHelper.buildMatrixModel(model);
    }

    @RequestMapping(value="configure", method=RequestMethod.POST)
    public String configureSubmit(@ModelAttribute("configuration") DataStreamingConfig configuration, ModelMap model, 
                                  HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);

        if (configuration.isNewConfiguration()) {
            configuration.getAttributes().forEach(attribute -> attribute.setInterval(configuration.getSelectedInterval()));
        }

        //  Don't save the config yet - just do a verification check.
        //    The config will be saved by dataStreamingService.assignDataStreamingConfig() during the verification POST.
        VerificationInformation verifyInfo = dataStreamingService.verifyConfiguration(configuration, deviceCollection.getDeviceList());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        verifyInfo.getConfiguration().setAccessor(accessor);

        verifyInfo.getDeviceUnsupported().forEach(unsupported -> {
            unsupported.setDeviceCollection(dcProducer.createDeviceCollection(unsupported.getDeviceIds(), null));
            unsupported.setAccessor(accessor);
        });
        verifyInfo.getGatewayLoadingInfo().forEach(gateway -> gateway.setAccessor(accessor));

        model.addAttribute("verificationInfo", verifyInfo);
        
        return "dataStreaming/verification.jsp";
    }

    @RequestMapping(value="remove", method=RequestMethod.GET)
    public String remove(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        model.addAttribute("action", CollectionAction.REMOVE_DATA_STREAMING);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/dataStreaming/remove.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value="removeInputs", method=RequestMethod.GET)
    public String removeInputs(DeviceCollection deviceCollection, ModelMap model) throws ServletException {
        model.addAttribute("deviceCollection", deviceCollection);
        return "dataStreaming/remove.jsp";
    }

    @RequestMapping(value="remove", method=RequestMethod.POST)
    public String removeSubmit(ModelMap model, HttpServletRequest request, HttpServletResponse resp,
                               YukonUserContext userContext, FlashScope flash) throws ServletException {

        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        try {
            SimpleCallback<CollectionActionResult> alertCallback =
                    CollectionActionAlertHelper.createAlert(AlertType.DATA_STREAMING, alertService,
                    messageSourceResolver.getMessageSourceAccessor(userContext), request);
            int cacheKey = dataStreamingService.unassignDataStreamingConfig(deviceCollection, alertCallback, userContext);
            return "redirect:/collectionActions/progressReport/detail?key=" + cacheKey;
        } catch (DataStreamingConfigException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            flash.setError(e.getMessageSourceResolvable());
            return "dataStreaming/remove.jsp";
        }
    }

    @RequestMapping(value="verification", method=RequestMethod.POST)
    public String verificationSubmit(@ModelAttribute("verificationInfo") VerificationInformation verificationInfo, 
            ModelMap model, HttpServletRequest request, HttpServletResponse resp, YukonUserContext userContext,
            FlashScope flash) throws ServletException {

        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);

        DataStreamingConfig config = verificationInfo.getConfiguration();
        config.setSelectedInterval(config.getAttributes().get(0).getInterval());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        config.setAccessor(accessor);

        try {
            SimpleCallback<CollectionActionResult> alertCallback =
                    CollectionActionAlertHelper.createAlert(AlertType.DATA_STREAMING, alertService,
                        messageSourceResolver.getMessageSourceAccessor(userContext), request);
            List<Integer> failedVerificationDevices = verificationInfo.getFailedVerificationDevices();
            int cacheKey = dataStreamingService.assignDataStreamingConfig(config, deviceCollection,
                failedVerificationDevices, alertCallback, userContext);
            return "redirect:/collectionActions/progressReport/detail?key=" + cacheKey;
        } catch (DataStreamingConfigException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            flash.setError(e.getMessageSourceResolvable());
            model.addAttribute("configuration", config);
            return "dataStreaming/verification.jsp";
        }
    }
}