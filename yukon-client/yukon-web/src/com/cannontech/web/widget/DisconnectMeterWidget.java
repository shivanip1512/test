package com.cannontech.web.widget;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.amr.rfn.service.WaitableRfnMeterDisconnectCallback;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.service.NetworkManagerError;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DisconnectMeterWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private MeterDao meterDao;
    @Autowired private PlcDeviceAttributeReadService readService;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectService disconnectService;  
    @Autowired private RfnMeterDisconnectService rfnDisconnectService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    @Autowired private StateDao stateDao;
    
    private final Set<BuiltInAttribute> disconnectAttribute = Sets.newHashSet(BuiltInAttribute.DISCONNECT_STATUS);

    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, Integer deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        
        return "disconnectMeterWidget/render.jsp";
    }
    
    @RequestMapping("read")
    public String read(final ModelMap model, final YukonUserContext userContext, Integer deviceId) {
        // This method will be changed by YUK-12715 Refactor PlcDeviceAttributeReadService
        // It should just call DeviceAttributeReadService.readMeter without checking if the device is PLC or RF
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        if(meter.getPaoType().isRfn()) {
            final MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            WaitableRfnMeterDisconnectCallback callback = new WaitableRfnMeterDisconnectCallback() {
    
                @Override
                public void processingExceptionOccured(MessageSourceResolvable message) {
                    SpecificDeviceErrorDescription error = getErrorDescription(NetworkManagerError.FAILURE, message);
                    model.addAttribute("errors", Lists.newArrayList(error));
                }
    
                @Override
                public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                    model.addAttribute("success", true);
                }
    
                @Override
                public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state) {
                    SpecificDeviceErrorDescription error = getErrorDescription(NetworkManagerError.FAILURE, message);
                    model.addAttribute("errors", Lists.newArrayList(error));
                }
                
                private SpecificDeviceErrorDescription getErrorDescription(NetworkManagerError errorType,
                                                                           MessageSourceResolvable message) {
                    String detail = accessor.getMessage(message);
                    DeviceErrorDescription errorDescription =
                        deviceErrorTranslatorDao.translateErrorCode(errorType.getErrorCode(), userContext);
                    SpecificDeviceErrorDescription deviceErrorDescription =
                        new SpecificDeviceErrorDescription(errorDescription, detail);
                    
                    return deviceErrorDescription;
                }
            };
            
            RfnMeter rfnMeter = meterDao.getRfnMeterForId(deviceId);
            rfnDisconnectService.send(rfnMeter,  RfnMeterDisconnectStatusType.QUERY, callback);
            
            try {
                callback.waitForCompletion();
            } catch (InterruptedException e) { /* Ignore */ }
            
        } else if (meter.getPaoType().isPlc()) {
            CommandResultHolder result = readService.readMeter(meter,
                    disconnectAttribute,
                    DeviceRequestType.DISCONNECT_STATUS_ATTRIBUTE_READ,
                    userContext.getYukonUser());
            
            if (result.getErrors().isEmpty() && StringUtils.isEmpty(result.getExceptionReason())) {
                String configStr = result.getLastResultString();
                model.addAttribute("configString", configStr);  
                model.addAttribute("success", true);
            }
            
            model.addAttribute("errors", result.getErrors());
            if (StringUtils.isNotEmpty(result.getExceptionReason())) {
                model.addAttribute("exceptionReason", result.getExceptionReason());
            }
            
        }
        
        return "disconnectMeterWidget/render.jsp";
    }
    
    @RequestMapping("helpInfo")
    public String helpInfo(ModelMap model, YukonUserContext userContext, int deviceId) {
        
        YukonMeter meter = meterDao.getForId(deviceId);
        initModel(model, userContext, meter);
        
        boolean is410Supported =
            paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_410);
        model.addAttribute("is410Supported", is410Supported);

        boolean is310Supported =
            paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_310);
        model.addAttribute("is310Supported", is310Supported);
        
        boolean is213Supported =
            paoDefinitionDao.isTagSupported(meter.getPaoType(), PaoTag.DISCONNECT_213);
        model.addAttribute("is213Supported",is213Supported);

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
    }
    
    private void initModel(ModelMap model, YukonUserContext userContext, YukonMeter meter) {
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(meter, BuiltInAttribute.DISCONNECT_STATUS);
            model.addAttribute("pointId", litePoint.getPointID());
            model.addAttribute("isConfigured", true);
            LiteState[] liteStates = stateDao.getLiteStates(litePoint.getStateGroupID());
            model.addAttribute("stateGroups", liteStates);
            model.addAttribute("pointId", litePoint.getLiteID());
        } catch (IllegalUseOfAttribute e) {
            model.addAttribute("isConfigured", false);
        }
        boolean supportsArm = disconnectService.supportsArm(Lists.newArrayList(new SimpleDevice(meter)));
        model.addAttribute("device", meter);
        model.addAttribute("supportsArm", supportsArm);
        model.addAttribute("attribute", BuiltInAttribute.DISCONNECT_STATUS);
    }
    
}