package com.cannontech.web.widget;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.DeviceReadResult;
import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.meter.service.MeterService;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DisconnectEventLogService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/disconnectMeterWidget/*")
public class DisconnectMeterWidget extends AdvancedWidgetControllerBase {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private MeterDao meterDao;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectService disconnectService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired MeterService meterService;
    @Autowired private ServerDatabaseCache serverDatabaseCache;
    @Autowired private DisconnectEventLogService disconnectEventLogService;
    @Autowired private CommandExecutionService commandExecutionService;

    private final Set<BuiltInAttribute> disconnectAttribute = Sets.newHashSet(BuiltInAttribute.DISCONNECT_STATUS);
    private final static String baseKey = "yukon.web.widgets.disconnectMeterWidget.";

    @Autowired
    public DisconnectMeterWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = YukonRole.METERING.name();
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }

    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        
        return "disconnectMeterWidget/render.jsp";
    }

    private final Validator validator = new SimpleValidator<CollarAddressEditorBean>(CollarAddressEditorBean.class) {

        private final static String key = baseKey + "error.";

        @Override
        protected void doValidation(CollarAddressEditorBean addressEditorBean, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "disconnectAddress", key
                + "disconnectAddress.required");
            if (!errors.hasFieldErrors("disconnectAddress")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "disconnectAddress",
                    addressEditorBean.getDisconnectAddress().toString(), 7);
            }
            if (!errors.hasFieldErrors("disconnectAddress")) {
                YukonValidationUtils.checkRange(errors, "disconnectAddress", addressEditorBean.getDisconnectAddress(),
                    0, 4194304, true);
            }
            if (!errors.hasFieldErrors("disconnectAddress")) {
                // Uniqueness check
                List<String> devices =
                    MCT400SeriesBase.isDiscAddressUnique(
                        Integer.valueOf(addressEditorBean.getDisconnectAddress().toString()),
                        addressEditorBean.getDeviceId());
                if (!CollectionUtils.isEmpty(devices)) {
                    Object[] args = devices.toArray();
                    errors.rejectValue("disconnectAddress", key + "disconnectAddress.unique", args, "");
                }
            }
        }
    };

    @RequestMapping(value = "query", method = RequestMethod.POST)
    public String query(ModelMap model, YukonUserContext userContext, Integer deviceId) {
    	
    	RfnMeter meter = (RfnMeter) meterDao.getForId(deviceId);
        initModel(model, userContext, meter);

        class WaitableCallback implements RfnMeterDisconnectCallback {
        	private final DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE);
            private final Logger log = YukonLogManager.getLogger(RfnMeterDisconnectCallback.class);
            private final CountDownLatch completeLatch = new CountDownLatch(1);
            private final Set<SpecificDeviceErrorDescription> errors = new HashSet<>();

            @Override
            public final void complete() {
                completeLatch.countDown();
            }

            public void waitForCompletion() throws InterruptedException {
                log.debug("Starting await completion");
                completeLatch.await();
                log.debug("Finished await completion");
            }
            
            @Override
            public void processingExceptionOccurred(MessageSourceResolvable message) {
                errors.add(new SpecificDeviceErrorDescription(errorDescription, message));
            }

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                model.addAttribute("success", true);
            }

            @Override
            public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state, RfnMeterDisconnectConfirmationReplyType replyType) {
                if (replyType == RfnMeterDisconnectConfirmationReplyType.FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT) {
                    disconnectEventLogService.loadSideVoltageDetectedWhileDisconnected(UserUtils.getYukonUser(), meter.getName());
                    errors.add(new SpecificDeviceErrorDescription(deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT),
                                                                  YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.disconnectMeterWidget.error.loadSideVoltageDetectedWhileDisconnected")));
                    return;
                } else if (replyType == RfnMeterDisconnectConfirmationReplyType.FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT) {
                    errors.add(new SpecificDeviceErrorDescription(deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT), 
                                                                  YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.disconnectMeterWidget.error.noLoadSideVoltageDetectedWhileConnected")));
                    return;
                } else if (replyType == RfnMeterDisconnectConfirmationReplyType.FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD) {
                    errors.add(new SpecificDeviceErrorDescription(deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD), 
                                                                  YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.web.widgets.disconnectMeterWidget.error.loadSideVoltageHigherThanThreshold")));
                    return;
                }
                errors.add(new SpecificDeviceErrorDescription(errorDescription, message));
            }

			public Set<SpecificDeviceErrorDescription> getErrors() {
				return errors;
			}
			
        };
        
        WaitableCallback callback = new WaitableCallback();
		rfnMeterDisconnectService.send(meter, RfnMeterDisconnectCmdType.QUERY, callback);
	    try {
	    	callback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
		model.addAttribute("isQuery", true);
		model.addAttribute("isRead", false);
		model.addAttribute("errors", callback.getErrors());
        
        return "disconnectMeterWidget/render.jsp";
    }

    @RequestMapping(value = "read", method = RequestMethod.POST)
    public String read(ModelMap model, YukonUserContext userContext, Integer deviceId) {
    	
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
		DeviceReadResult result = deviceAttributeReadService.initiateReadAndWait(meter, disconnectAttribute,
				DeviceRequestType.DISCONNECT_STATUS_ATTRIBUTE_READ, userContext.getYukonUser());
		if (result.isSuccess()) {
			model.addAttribute("success", true);
			if(!StringUtils.isEmpty(result.getLastResultString())){
				model.addAttribute("configString", result.getLastResultString());
			}
		}
		model.addAttribute("errors", result.getErrors());
		model.addAttribute("isRead", true);
        
        return "disconnectMeterWidget/render.jsp";
    }

    @RequestMapping("helpInfo")
    public String helpInfo(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        
        return "disconnectMeterWidget/helpInfo.jsp";
    }

    @RequestMapping(value = "connect", method = RequestMethod.POST)
    public String connect(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, userContext.getYukonUser());
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        DisconnectMeterResult result = disconnectService.execute(
                DisconnectCommand.CONNECT,
                DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                userContext.getYukonUser());
        addDisconnectResultToModel(model, result);
        
        return "disconnectMeterWidget/render.jsp";
    }

    @RequestMapping(value = "disconnect", method = RequestMethod.POST)
    public String disconnect(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, userContext.getYukonUser());
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        DisconnectMeterResult result = disconnectService.execute(
                DisconnectCommand.DISCONNECT,
                DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                userContext.getYukonUser());
        addDisconnectResultToModel(model, result);
        return "disconnectMeterWidget/render.jsp";
    }

    @RequestMapping(value = "arm", method = RequestMethod.POST)
    public String arm(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, userContext.getYukonUser());
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        DisconnectMeterResult result = disconnectService.execute(
                DisconnectCommand.ARM,
                DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                userContext.getYukonUser());
        addDisconnectResultToModel(model, result);
        
        return "disconnectMeterWidget/render.jsp";
    }

    private void addDisconnectResultToModel(ModelMap model, DisconnectMeterResult result) {
        if (result.getError() != null) {
            model.addAttribute("errors", Lists.newArrayList(result.getError()));
        }
        if (StringUtils.isNotEmpty(result.getProcessingException())) {
            model.addAttribute("exceptionReason", result.getProcessingException());
        }
        
        model.addAttribute("success", result.isSuccess());
        model.addAttribute("command", result.getCommand());
        model.addAttribute("isRead", false);
    }

    private void initModel(ModelMap model, YukonUserContext userContext, YukonMeter meter) {
        boolean isDisconnectStatusFound = true;
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            model.addAttribute("pointId", litePoint.getPointID());
            model.addAttribute("isConfigured", true);
            List<LiteState> liteStates = stateGroupDao.getLiteStates(litePoint.getStateGroupID());
            model.addAttribute("stateGroups", liteStates);
            model.addAttribute("pointId", litePoint.getLiteID());
            boolean is410Supported = paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_410);
            model.addAttribute("is410Supported", is410Supported);
            boolean is310Supported = paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_310);
            model.addAttribute("is310Supported", is310Supported);
            boolean is213Supported = paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_213);
            model.addAttribute("is213Supported", is213Supported);
        } catch (IllegalUseOfAttribute e) {
            model.addAttribute("isConfigured", false);
            isDisconnectStatusFound = false;
        }
        
        model.addAttribute("device", meter);
        
        boolean supportsArm = disconnectService.supportsArm(Lists.newArrayList(new SimpleDevice(meter)));
        model.addAttribute("supportsArm", supportsArm);
        
        boolean isDisconnectCollarSupported = paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        model.addAttribute("isDisconnectCollarSupported", isDisconnectCollarSupported);
        if (isDisconnectCollarSupported) {
            Integer disconnectAddress = meterDao.getDisconnectAddress(meter.getDeviceId());
            model.addAttribute("disconnectAddress", disconnectAddress);
            CollarAddressEditorBean addressEditorBean = new CollarAddressEditorBean(meter.getDeviceId(), disconnectAddress);
            model.addAttribute("addressEditorBean", addressEditorBean);
            
            model.addAttribute("showActions", disconnectAddress != null);
            model.addAttribute("isConfigured", disconnectAddress != null && isDisconnectStatusFound);
        } else {
            model.addAttribute("disconnectAddress", null);
            model.addAttribute("showActions", true);
        }
        model.addAttribute("attribute", BuiltInAttribute.DISCONNECT_STATUS);
        if (meter.getPaoType().isRfn()) {
            model.addAttribute("supportsQuery", true);
        } else {
            model.addAttribute("supportsRead", true);
        }
    }

    @RequestMapping(value = "edit", method = RequestMethod.GET)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String edit(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {
        Integer disconnectAddress = meterDao.getDisconnectAddress(deviceId);
        CollarAddressEditorBean addressEditorBean = new CollarAddressEditorBean(deviceId, disconnectAddress);
        model.addAttribute("addressEditorBean", addressEditorBean);
        return "disconnectMeterWidget/edit.jsp";
    }

    @RequestMapping(value = "saveDisconnectCollar", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String saveDisconnectCollar(HttpServletResponse resp,
            @ModelAttribute("addressEditorBean") CollarAddressEditorBean addressEditorBean, BindingResult result,
            ModelMap model, FlashScope flash, LiteYukonUser user) throws Exception {

        validator.validate(addressEditorBean, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "disconnectMeterWidget/edit.jsp";
        }

        // Update configuration to DB and Points
        YukonMeter meter = meterDao.getForId(addressEditorBean.getDeviceId());
        SimpleDevice device = SimpleDevice.of(meter.getPaoIdentifier());
        meterService.addDisconnectAddress(device, addressEditorBean.getDisconnectAddress());

        flash.setMessage(new YukonMessageSourceResolvable("yukon.web.widgets.disconnectMeterWidget.update.successful"),
            FlashScopeMessageType.SUCCESS);
        return "disconnectMeterWidget/edit.jsp";
    }

    @RequestMapping(value = "removeDisconnectCollar", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String removeDisconnectCollar(HttpServletResponse resp,
            @ModelAttribute("disconnectAddress") Integer disconnectAddress,
            @ModelAttribute("deviceId") Integer deviceId, BindingResult result, ModelMap model, FlashScope flash,
            LiteYukonUser user) throws Exception {
        YukonMeter meter = meterDao.getForId(deviceId);
        SimpleDevice device = SimpleDevice.of(meter.getPaoIdentifier());
        try {
            meterService.removeDisconnectAddress(device);
        } catch (IllegalArgumentException | TransactionException | IllegalUseOfAttribute e) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.widgets.disconnectMeterWidget.delete.failed"));
            return "disconnectMeterWidget/render.jsp";
        }
        Object args = disconnectAddress;
        flash.setMessage(new YukonMessageSourceResolvable("yukon.web.widgets.disconnectMeterWidget.delete.successful",
            args), FlashScopeMessageType.SUCCESS);
        return "disconnectMeterWidget/render.jsp";
    }
    
    @RequestMapping(value = "uploadConfig", method = RequestMethod.POST)
    public String uploadConfig(ModelMap model, LiteYukonUser user, int deviceId) throws Exception {

        SimpleMeter meter = serverDatabaseCache.getAllMeters().get(deviceId);
        CommandRequestDevice request =
            new CommandRequestDevice("putconfig emetcon disconnect", new SimpleDevice(meter.getPaoIdentifier()));
        CommandResultHolder result =
            commandExecutionService.execute(request, DeviceRequestType.DISCONNECT_COLLAR_PUT_CONFIG_COMMAND, user);

        model.addAttribute("result", result);
        model.addAttribute("errors", result.getErrors());
        return "common/meterReadingsResult.jsp";
    }

    public static class CollarAddressEditorBean {

        private int deviceId;
        private Integer disconnectAddress;

        public int getDeviceId() {
            return deviceId;
        }

        public CollarAddressEditorBean(int deviceId, Integer disconnectAddress) {
            super();
            this.deviceId = deviceId;
            this.disconnectAddress = disconnectAddress;
        }

        public CollarAddressEditorBean() {
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public Integer getDisconnectAddress() {
            return disconnectAddress;
        }

        public void setDisconnectAddress(Integer collarAddress) {
            this.disconnectAddress = collarAddress;
        }
    }
}