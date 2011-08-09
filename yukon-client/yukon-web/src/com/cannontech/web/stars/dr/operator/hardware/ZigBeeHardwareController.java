package com.cannontech.web.stars.dr.operator.hardware;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.events.loggers.ZigbeeEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.ZigbeeTextMessage;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiWebServiceException;
import com.cannontech.thirdparty.digi.service.errors.ZigbeePingResponse;
import com.cannontech.thirdparty.exception.ZigbeeClusterLibraryException;
import com.cannontech.thirdparty.exception.ZigbeeCommissionException;
import com.cannontech.thirdparty.model.ZigbeeDevice;
import com.cannontech.thirdparty.service.ZigbeeStateUpdaterService;
import com.cannontech.thirdparty.service.ZigbeeWebService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Lists;

@RequestMapping("/operator/hardware/zb/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class ZigBeeHardwareController {
    
    private static final String keyPrefix = "yukon.web.modules.operator.hardware.";
    private ZigbeeWebService zigbeeWebService;
    private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    private ZigbeeDeviceDao zigbeeDeviceDao;
    private LMHardwareBaseDao lmHardwareBaseDao;
    private GatewayDeviceDao gatewayDeviceDao;
    private DatePropertyEditorFactory datePropertyEditorFactory;
    private InventoryDao inventoryDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private ZigbeeEventLogService zigbeeEventLogService;
    private PaoDao paoDao;

    @RequestMapping
    public ModelAndView refresh(YukonUserContext context, int deviceId) {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
        zigbeeEventLogService.zigbeeDeviceRefreshAttemptedByOperator(context.getYukonUser(), pao.getPaoName());
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        ModelAndView mav = new ModelAndView(new JsonView());

        InventoryIdentifier id = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = id.getHardwareType();
        
        ZigbeePingResponse ping;
        
        try {
            ZigbeeDevice device;
            if (type.isGateway()) {
                device = gatewayDeviceDao.getZigbeeGateway(deviceId);
                ping = zigbeeStateUpdaterService.updateGatewayStatus(device);
                
            } else {
                device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
                ping = zigbeeStateUpdaterService.updateEndPointStatus(device);
            }
            
            zigbeeStateUpdaterService.activateSmartPolling(device);
        } catch (DigiWebServiceException e) {
            commandFailed(mav.getModelMap(), accessor, e.getMessage());
            return mav;
        }

        mav.addObject("success", ping.isSuccess());
        mav.addObject("message", accessor.getMessage(ping.getMessageSourceResolvable()));

        return mav;
    }

    @RequestMapping
    public ModelAndView commission(YukonUserContext context, int deviceId) {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
        zigbeeEventLogService.zigbeeDeviceCommissionAttemptedByOperator(context.getYukonUser(), pao.getPaoName());
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        ModelAndView mav = new ModelAndView(new JsonView());
        InventoryIdentifier id = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = id.getHardwareType();
        
        if (type.isGateway()) {
            commissionGateway(mav.getModelMap(), accessor, deviceId);
        } else {
            commissionNode(mav.getModelMap(), accessor, deviceId, id.getInventoryId());
        }

        return mav;
    }
    
    @RequestMapping
    public ModelAndView decommission(YukonUserContext context, int deviceId) {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
        zigbeeEventLogService.zigbeeDeviceDecommissionAttemptedByOperator(context.getYukonUser(), pao.getPaoName());
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        ModelAndView mav = new ModelAndView(new JsonView());
        InventoryIdentifier id = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = id.getHardwareType();
        
        if (type.isGateway()) {
            decommissionGateway(mav.getModelMap(), accessor, deviceId);
        } else {
            decommissionNode(mav.getModelMap(), accessor, deviceId, id.getInventoryId());
        }
        
        return mav;
    }
    
    private void commissionNode(ModelMap model, MessageSourceAccessor accessor, int deviceId, int inventoryId) {
        boolean messageFailed = false;
        String errorMessage = null;
        try {
            // This gatewayId is expected to be not null since this action wouldn't have been available.
            int gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(inventoryId);
            zigbeeWebService.installEndPoint(gatewayId, deviceId);

            ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
            zigbeeStateUpdaterService.activateSmartPolling(device);
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            commandFailed(model, accessor, errorMessage);
        } else {
            commandSucceeded(model, accessor, "thermostatCommissioned", deviceId);
        }
        
    }

    private void commandSucceeded(ModelMap model, MessageSourceAccessor accessor, String keySuffix, int deviceId) {
        String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        String message = accessor.getMessage(new YukonMessageSourceResolvable(keyPrefix + keySuffix, deviceSerialNumber));
        commandResults(model,true,message);
    }
    
    private void commandFailed(ModelMap model, MessageSourceAccessor accessor, String errorMessage) {
        String message = accessor.getMessage(new YukonMessageSourceResolvable(keyPrefix + "commandFailed", errorMessage));
        commandResults(model,false,message);
    }
    
    private void commandFailed(ModelMap model, String errorMessage) {
        commandResults(model,false,errorMessage);
    }
    
    private void commandResults(ModelMap model, boolean success, String message) {
        model.addAttribute("success", success);        
        model.addAttribute("message", message);
    }
    
    private void commissionGateway(ModelMap model, MessageSourceAccessor accessor, int gatewayId) {
        boolean messageFailed = false;
        String errorMessage = null;
        MessageSourceResolvable errorResolvable = null;
        try {
            zigbeeWebService.installGateway(gatewayId);
            
            ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
            zigbeeStateUpdaterService.activateSmartPolling(gateway);
        } catch (ZigbeeCommissionException e) {
            messageFailed = true;
            errorResolvable = e.getMessageSourceResolvable();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = ExceptionUtils.getRootCauseMessage(e);
        }
        
        if (messageFailed) {
            if (errorResolvable != null) {
                errorMessage = accessor.getMessage(errorResolvable);
                commandFailed(model, errorMessage);
            } else {
                commandFailed(model, accessor, errorMessage);
            }
        } else {
            commandSucceeded(model, accessor, "gatewayCommissioned", gatewayId);
        }
    }
    
    private void decommissionNode(ModelMap model, MessageSourceAccessor accessor, int deviceId, int inventoryId) {
        boolean messageFailed = false;
        String errorMessage = null;
        try {
            // This gatewayId is expected to be not null since this action wouldn't have been available.
            int gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(inventoryId);
            zigbeeWebService.uninstallEndPoint(gatewayId, deviceId);
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            commandFailed(model, accessor, errorMessage);
        } else {
            commandSucceeded(model, accessor, "thermostatDecommissioned", deviceId);
        }
    }
    
    private void decommissionGateway(ModelMap model, MessageSourceAccessor accessor, int gatewayId) {
        boolean messageFailed = false;
        String errorMessage = null;
        try {
            zigbeeWebService.removeGateway(gatewayId);
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            commandFailed(model, accessor, errorMessage);
        } else {
            commandSucceeded(model, accessor, "gatewayDecommissioned", gatewayId);
        }
    }
    
    @RequestMapping
    public String showTextMessage(ModelMap model, int accountId, int inventoryId, int gatewayId) {
        
        ZigbeeTextMessage textMessage = new ZigbeeTextMessage();
        textMessage.setAccountId(accountId);
        textMessage.setInventoryId(inventoryId);
        textMessage.setGatewayId(gatewayId);
        textMessage.setStartTime(new Instant());
        textMessage.setDisplayDuration(Duration.standardMinutes(2));
        model.addAttribute("textMessage", textMessage);

        return setupTextMsgModel(model, accountId, inventoryId, gatewayId);
    }

    private String setupTextMsgModel(ModelMap model, int accountId, int inventoryId, int gatewayId) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        
        model.addAttribute("accountId", accountId);
        model.addAttribute("inventoryId", inventoryId);
        model.addAttribute("gatewayId", gatewayId);
        
        List<Duration> durations = Lists.newArrayList();
        durations.add(Duration.standardMinutes(2));
        durations.add(Duration.standardMinutes(30));
        durations.add(Duration.standardHours(1));
        durations.add(Duration.standardHours(6));
        durations.add(Duration.standardHours(12));
        durations.add(Duration.standardHours(24));
        durations.add(Duration.standardDays(2));
        durations.add(Duration.standardDays(7));
        durations.add(Duration.standardDays(14));
        model.addAttribute("durations", durations);
        
        return "operator/hardware/textMessage.jsp";
    }
    
    @RequestMapping
    public String sendTextMessage(ModelMap model, FlashScope flash, 
                                  @ModelAttribute("textMessage") ZigbeeTextMessage textMessage,
                                  BindingResult result,
                                  AccountInfoFragment fragment,
                                  YukonUserContext context,
                                  HttpServletResponse resp) throws IOException {
        
        int inventoryId = textMessage.getInventoryId();
        int gatewayId = textMessage.getGatewayId();
        int accountId = fragment.getAccountId();

        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(gatewayId);
        zigbeeEventLogService.zigbeeSendTextAttemptedByOperator(context.getYukonUser(), pao.getPaoName(),textMessage.getMessage());
        
        YukonValidationUtils.checkExceedsMaxLength(result, "message", textMessage.getMessage(), 21);
        if (result.hasErrors()) {
            /* Add errors to flash scope */
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return setupTextMsgModel(model, accountId, inventoryId, gatewayId);
        }
        
        boolean messageFailed = false;
        String errorMessage = null;
        try {
            zigbeeWebService.sendTextMessage(textMessage);
        } catch (ZigbeeClusterLibraryException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            flash.setError(new YukonMessageSourceResolvable(keyPrefix + "commandFailed", errorMessage));
        } else {
            String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(textMessage.getGatewayId());
            flash.setConfirm(new YukonMessageSourceResolvable(keyPrefix + "messageSent", gatewaySerialNumber));
        }
        
        ServletUtils.closePopup(resp, "ajaxDialog");
        return null;
    }
    
    /* TSTAT ACTIONS */
    
    @RequestMapping(value="addDeviceToGateway", method=RequestMethod.POST)
    public String addDeviceToGateway(YukonUserContext context, ModelMap model, FlashScope flash, int deviceId, int gatewayId, int accountId, int inventoryId) {
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        LiteYukonPAObject gateway = paoDao.getLiteYukonPAO(gatewayId);
        zigbeeEventLogService.zigbeeDeviceAssignAttemptedByOperator(context.getYukonUser(), device.getPaoName(), gateway.getPaoName());
        
        gatewayDeviceDao.updateDeviceToGatewayAssignment(deviceId, gatewayId);

        zigbeeEventLogService.zigbeeDeviceAssigned(device.getPaoName(), gateway.getPaoName());
        
        String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
        flash.setConfirm(new YukonMessageSourceResolvable(keyPrefix + "thermostatAdded", deviceSerialNumber, gatewaySerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }
    
    @RequestMapping
    public String removeDeviceFromGateway(YukonUserContext context, ModelMap model, FlashScope flash, int deviceId, int gatewayId, int accountId, int inventoryId) {
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        LiteYukonPAObject gateway = paoDao.getLiteYukonPAO(gatewayId);
        zigbeeEventLogService.zigbeeDeviceUnassignAttemptedByOperator(context.getYukonUser(), device.getPaoName(), gateway.getPaoName());
        
        zigbeeWebService.uninstallEndPoint(gatewayId, deviceId);
        gatewayDeviceDao.unassignDeviceFromGateway(deviceId);
        
        zigbeeEventLogService.zigbeeDeviceUnassigned(device.getPaoName(), gateway.getPaoName());
        
        String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        String gatewaySerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(gatewayId);
        flash.setConfirm(new YukonMessageSourceResolvable(keyPrefix + "thermostatRemoved", deviceSerialNumber, gatewaySerialNumber));
        
        return redirectView(model, accountId, inventoryId);
    }
    
    /* INIT BINDER */
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext context) {
        binder.registerCustomEditor(Instant.class, "startTime", datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATEHM, context, BlankMode.CURRENT));
        
        binder.registerCustomEditor(Duration.class, "displayDuration", new PropertyEditorSupport() {
            @Override
            public void setAsText(String duration) throws IllegalArgumentException {
                long durationInMillis = Long.parseLong(duration);
                setValue(new Duration(durationInMillis));
            }
            @Override
            public String getAsText() {
                Duration duration = (Duration) getValue();
                return String.valueOf(duration.getMillis());
            }
        });
    }
    
    /* HELPERS */
    private String redirectView(ModelMap model, int accountId, int inventoryId) {
        model.addAttribute("inventoryId", inventoryId);
        model.addAttribute("accountId", accountId);
        
        return "redirect:/spring/stars/operator/hardware/view";
    }
    
    @Autowired
    public void setZigbeeDeviceDao(ZigbeeDeviceDao zigbeeDeviceDao) {
        this.zigbeeDeviceDao = zigbeeDeviceDao;
    }
    
    @Autowired
    public void setZigbeeStateUpdaterService(ZigbeeStateUpdaterService zigbeeStateUpdaterService) {
        this.zigbeeStateUpdaterService = zigbeeStateUpdaterService;
    }
    
    @Autowired
    public void setZigbeeWebService(ZigbeeWebService zigbeeWebService) {
        this.zigbeeWebService = zigbeeWebService;
    }
    
    @Autowired
    public void setLmHardwareBaseDao(LMHardwareBaseDao lmHardwareBaseDao) {
        this.lmHardwareBaseDao = lmHardwareBaseDao;
    }
    
    @Autowired
    public void setGatewayDeviceDao(GatewayDeviceDao gatewayDeviceDao) {
        this.gatewayDeviceDao = gatewayDeviceDao;
    }
    
    @Autowired
    public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setZigbeeEventLogService(ZigbeeEventLogService zigbeeEventLogService) {
        this.zigbeeEventLogService = zigbeeEventLogService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}