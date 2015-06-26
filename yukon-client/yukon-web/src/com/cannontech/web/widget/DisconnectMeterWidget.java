package com.cannontech.web.widget;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetInput;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.log4j.Logger;

@Controller
@RequestMapping("/disconnectMeterWidget/*")
public class DisconnectMeterWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private MeterDao meterDao;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectService disconnectService;  
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private StateDao stateDao;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    
    private final Set<BuiltInAttribute> disconnectAttribute = Sets.newHashSet(BuiltInAttribute.DISCONNECT_STATUS);
    
    @Autowired
    public DisconnectMeterWidget(@Qualifier("widgetInput.deviceId")
            SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        Set<WidgetInput> simpleWidgetInputSet = new HashSet<WidgetInput>();
        simpleWidgetInputSet.add(simpleWidgetInput);
        
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setInputs(simpleWidgetInputSet);
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile("METERING"));
    }

    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        
        return "disconnectMeterWidget/render.jsp";
    }
    
    @RequestMapping("query")
    public String query(ModelMap model, YukonUserContext userContext, Integer deviceId) {
    	
    	RfnMeter meter = (RfnMeter) meterDao.getForId(deviceId);
        initModel(model, userContext, meter);

        class WaitableCallback implements RfnMeterDisconnectCallback {
        	private final DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.FAILURE);
            private Logger log = YukonLogManager.getLogger(RfnMeterDisconnectCallback.class);
            private CountDownLatch completeLatch = new CountDownLatch(1);
            private Set<SpecificDeviceErrorDescription> errors = new HashSet<>();

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
            public void processingExceptionOccured(MessageSourceResolvable message) {
                errors.add(new SpecificDeviceErrorDescription(errorDescription, message));
            }

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                model.addAttribute("success", true);
            }

            @Override
            public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state) {
                errors.add(new SpecificDeviceErrorDescription(errorDescription, message));
            }

			public Set<SpecificDeviceErrorDescription> getErrors() {
				return errors;
			}
        };
        
        WaitableCallback callback = new WaitableCallback();
		rfnMeterDisconnectService.send(meter, RfnMeterDisconnectStatusType.QUERY, callback);
	    try {
	    	callback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
		model.addAttribute("isQuery", true);
		model.addAttribute("isRead", false);
		model.addAttribute("errors", callback.getErrors());
        
        return "disconnectMeterWidget/render.jsp";
    }
    
    @RequestMapping("read")
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
    
    @RequestMapping("connect")
    public String connect(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, userContext.getYukonUser());
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        DisconnectMeterResult result = disconnectService.execute(
                DisconnectCommand.CONNECT,
                DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                userContext);
        addDisconnectResultToModel(model, result);
        
        return "disconnectMeterWidget/render.jsp";
    }

    @RequestMapping("disconnect")
    public String disconnect(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, userContext.getYukonUser());
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        DisconnectMeterResult result = disconnectService.execute(
                DisconnectCommand.DISCONNECT,
                DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                userContext);
        addDisconnectResultToModel(model, result);
        
        return "disconnectMeterWidget/render.jsp";
    }
    
    @RequestMapping("arm")
    public String arm(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ALLOW_DISCONNECT_CONTROL, userContext.getYukonUser());
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        DisconnectMeterResult result = disconnectService.execute(
                DisconnectCommand.ARM,
                DeviceRequestType.METER_CONNECT_DISCONNECT_WIDGET, meter,
                userContext);
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
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            model.addAttribute("pointId", litePoint.getPointID());
            model.addAttribute("isConfigured", true);
            LiteState[] liteStates = stateDao.getLiteStates(litePoint.getStateGroupID());
            model.addAttribute("stateGroups", liteStates);
            model.addAttribute("pointId", litePoint.getLiteID());
            boolean is410Supported =
                paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_410);
            model.addAttribute("is410Supported", is410Supported);

            boolean is310Supported =
                paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_310);
            model.addAttribute("is310Supported", is310Supported);

            boolean is213Supported =
                paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_213);
            model.addAttribute("is213Supported", is213Supported);
        } catch (IllegalUseOfAttribute e) {
            model.addAttribute("isConfigured", false);
        }
        boolean supportsArm = disconnectService.supportsArm(Lists.newArrayList(new SimpleDevice(meter)));
        model.addAttribute("device", meter);
        model.addAttribute("supportsArm", supportsArm);
        model.addAttribute("attribute", BuiltInAttribute.DISCONNECT_STATUS);
		if (meter.getPaoType().isRfn()) {
			model.addAttribute("supportsQuery", true);
		} else {
			model.addAttribute("supportsRead", true);
		}
	}
}