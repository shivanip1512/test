package com.cannontech.web.stars.dr.operator.hardware;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.ZigbeeEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.ZigbeeTextMessageDto;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.point.stategroup.Commissioned;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.thirdparty.digi.dao.GatewayDeviceDao;
import com.cannontech.thirdparty.digi.dao.ZigbeeDeviceDao;
import com.cannontech.thirdparty.digi.exception.DigiNotConfiguredException;
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
import com.google.common.collect.Sets;

@RequestMapping("/operator/hardware/zb/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES)
public class ZigBeeHardwareController {
    
    private static final Logger log = YukonLogManager.getLogger(ZigBeeHardwareController.class);
    
    private static final String keyPrefix = "yukon.web.modules.operator.hardware.";
    
    @Autowired private ZigbeeWebService zigbeeWebService;
    @Autowired private ZigbeeStateUpdaterService zigbeeStateUpdaterService;
    @Autowired private ZigbeeDeviceDao zigbeeDeviceDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private GatewayDeviceDao gatewayDeviceDao;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ZigbeeEventLogService zigbeeEventLogService;
    @Autowired private PaoDao paoDao;
    @Autowired private NextValueHelper nextValueHelper;
    
    @RequestMapping
    public void readNow(HttpServletResponse resp, YukonUserContext context, int deviceId) throws IOException {
        final MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        final ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
        final JSONObject json = new JSONObject();
        
        try {
            zigbeeWebService.readLoadGroupAddressing(device); // TODO this should take a callback similar to rf and support timeout
            
            log.debug("Read now initiated for " + device);
            json.put("success", true);
            json.put("message", accessor.getMessage(keyPrefix + "readNowSuccess"));
            
        } catch (DigiWebServiceException e) {
            log.debug("Read now failed for " + device);
            json.put("success", false);
            json.put("message", accessor.getMessage(keyPrefix + "error.readNowFailed", e.getMessage()));
        }
        
        resp.setContentType("application/json");
        resp.getWriter().print(json.toString());
        resp.getWriter().close();
    }
    
    @RequestMapping
    public void refresh(HttpServletResponse resp, YukonUserContext context, int deviceId) throws NoSuchMessageException, IOException {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
        zigbeeEventLogService.zigbeeDeviceRefresh(context.getYukonUser(), pao.getPaoName(), EventSource.OPERATOR);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        InventoryIdentifier id = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = id.getHardwareType();
        
        ZigbeePingResponse ping;
        
        try {
            ZigbeeDevice device;
            if (type.isGateway()) {
                device = gatewayDeviceDao.getZigbeeGateway(deviceId);
                try {
                    ping = zigbeeStateUpdaterService.updateGatewayStatus(device);
                } catch (DigiNotConfiguredException e) {
                    String notConfigured = "yukon.web.modules.operator.hardware.zigbeeNotEnabled";
                    MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(notConfigured);
                    ping = new ZigbeePingResponse(false, Commissioned.DECOMMISSIONED, resolvable);
                }
                
            } else {
                device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
                try {
                    ping = zigbeeStateUpdaterService.updateEndPointStatus(device);
                } catch (DigiNotConfiguredException e) {
                    String notConfigured = "yukon.web.modules.operator.hardware.zigbeeNotEnabled";
                    MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCode(notConfigured);
                    ping = new ZigbeePingResponse(false, Commissioned.DECOMMISSIONED, resolvable);    
                }
            }
            
            zigbeeEventLogService.zigbeeDeviceRefreshed(pao.getPaoName());
            zigbeeStateUpdaterService.activateSmartPolling(device);

            returnJson(resp, ping.isSuccess(), accessor.getMessage(ping.getPingResultResolvable()));
        } catch (DigiNotConfiguredException e) {
            commandFailed(resp, accessor, e.getMessage());
        } catch (DigiWebServiceException e) {
            commandFailed(resp, accessor, e.getMessage());
        }
        
    }
    
    private void returnJson(HttpServletResponse response, boolean success, String message) throws IOException {
        JSONObject object = new JSONObject();
        object.put("success", success);
        object.put("message", message);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(object.toString());
        out.close();
    }

    @RequestMapping
    public ModelAndView commission(HttpServletResponse resp,YukonUserContext context, int deviceId) throws IOException {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
        zigbeeEventLogService.zigbeeDeviceCommission(context.getYukonUser(), pao.getPaoName(), EventSource.OPERATOR);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        ModelAndView mav = new ModelAndView(new JsonView());
        InventoryIdentifier id = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = id.getHardwareType();
        
        if (type.isGateway()) {
            commissionGateway(resp, accessor, deviceId);
        } else {
            commissionNode(resp, accessor, deviceId, id.getInventoryId());
        }

        return mav;
    }
    
    @RequestMapping
    public ModelAndView decommission(HttpServletResponse resp, YukonUserContext context, int deviceId) throws IOException {
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(deviceId);
        zigbeeEventLogService.zigbeeDeviceDecommission(context.getYukonUser(), pao.getPaoName(), EventSource.OPERATOR);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        ModelAndView mav = new ModelAndView(new JsonView());
        InventoryIdentifier id = inventoryDao.getYukonInventoryForDeviceId(deviceId);
        HardwareType type = id.getHardwareType();
        
        if (type.isGateway()) {
            decommissionGateway(resp, accessor, deviceId);
        } else {
            decommissionNode(resp, accessor, deviceId, id.getInventoryId());
        }
        
        return mav;
    }
    
    private void commissionNode(HttpServletResponse resp, MessageSourceAccessor accessor, int deviceId, int inventoryId) throws IOException {
        boolean messageFailed = false;
        String errorMessage = null;
        MessageSourceResolvable errorResolvable = null;
        
        try {
            // This gatewayId is expected to be not null since this action wouldn't have been available.
            int gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(inventoryId);
            zigbeeWebService.installEndPoint(gatewayId, deviceId);

            ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
            zigbeeStateUpdaterService.activateSmartPolling(device);

            zigbeeEventLogService.zigbeeDeviceCommissioned(device.getName());
        } catch (DigiNotConfiguredException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (ZigbeeCommissionException e2) {
            messageFailed = true;
            errorResolvable = e2.getDescription();            
        }
        
        if (messageFailed) {
            if (errorResolvable != null) {
                errorMessage = accessor.getMessage(errorResolvable);
                commandFailed(resp, errorMessage);
            } else {
                commandFailed(resp, accessor, errorMessage);
            }
        } else {
            commandSucceeded(resp, accessor, "thermostatCommissioned", deviceId);
        }
        
    }

    private void commandSucceeded(HttpServletResponse resp, MessageSourceAccessor accessor, String keySuffix, int deviceId) throws IOException {
        String deviceSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(deviceId);
        String message = accessor.getMessage(new YukonMessageSourceResolvable(keyPrefix + keySuffix, deviceSerialNumber));
        commandResults(resp, true, message);
    }
    
    private void commandFailed(HttpServletResponse resp, MessageSourceAccessor accessor, String errorMessage) throws IOException {
        String message = accessor.getMessage(new YukonMessageSourceResolvable(keyPrefix + "commandFailed", errorMessage));
        commandResults(resp, false, message);
    }
    
    private void commandFailed(HttpServletResponse resp, String errorMessage) throws IOException {
        commandResults(resp, false, errorMessage);
    }
    
    private void commandResults(HttpServletResponse resp, boolean success, String message) throws IOException {
        returnJson(resp, success, message);
    }
    
    private void commissionGateway(HttpServletResponse resp, MessageSourceAccessor accessor, int gatewayId) throws IOException {
        boolean messageFailed = false;
        String errorMessage = null;
        MessageSourceResolvable errorResolvable = null;
        
        
        try {
            zigbeeWebService.installGateway(gatewayId);
            ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
            zigbeeStateUpdaterService.activateSmartPolling(gateway);

            zigbeeEventLogService.zigbeeDeviceCommissioned(gateway.getName());
        } catch (DigiNotConfiguredException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (ZigbeeCommissionException e) {
            messageFailed = true;
            errorResolvable = e.getDescription();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            if (errorResolvable != null) {
                errorMessage = accessor.getMessage(errorResolvable);
                commandFailed(resp, errorMessage);
            } else {
                commandFailed(resp, accessor, errorMessage);
            }
        } else {
            commandSucceeded(resp, accessor, "gatewayCommissioned", gatewayId);
        }
    }
    
    private void decommissionNode(HttpServletResponse resp, MessageSourceAccessor accessor, int deviceId, int inventoryId) throws IOException {
        boolean messageFailed = false;
        String errorMessage = null;
        try {
            // This gatewayId is expected to be not null since this action wouldn't have been available.
            int gatewayId = zigbeeDeviceDao.findGatewayIdForInventory(inventoryId);
            zigbeeWebService.uninstallEndPoint(gatewayId, deviceId);
            
            ZigbeeDevice device = zigbeeDeviceDao.getZigbeeDevice(deviceId);
            zigbeeEventLogService.zigbeeDeviceDecommissioned(device.getName());
        } catch (DigiNotConfiguredException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            commandFailed(resp, accessor, errorMessage);
        } else {
            commandSucceeded(resp, accessor, "thermostatDecommissioned", deviceId);
        }
    }
    
    private void decommissionGateway(HttpServletResponse resp, MessageSourceAccessor accessor, int gatewayId) throws IOException {
        boolean messageFailed = false;
        String errorMessage = null;
        try {
            zigbeeWebService.removeGateway(gatewayId);
            ZigbeeDevice gateway = gatewayDeviceDao.getZigbeeGateway(gatewayId);
            
            zigbeeEventLogService.zigbeeDeviceDecommissioned(gateway.getName());
        } catch (DigiNotConfiguredException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        }
        
        if (messageFailed) {
            commandFailed(resp, accessor, errorMessage);
        } else {
            commandSucceeded(resp, accessor, "gatewayDecommissioned", gatewayId);
        }
    }
    
    @RequestMapping
    public String showTextMessage(ModelMap model, int accountId, int inventoryId, int gatewayId) {
        
        ZigbeeTextMessageDto textMessage = new ZigbeeTextMessageDto();
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
                                  @ModelAttribute("textMessage") ZigbeeTextMessageDto textMessage,
                                  BindingResult result,
                                  AccountInfoFragment fragment,
                                  YukonUserContext context,
                                  HttpServletResponse resp) throws IOException {
        
        int inventoryId = textMessage.getInventoryId();
        int gatewayId = textMessage.getGatewayId();
        int accountId = fragment.getAccountId();

        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(gatewayId);
        zigbeeEventLogService.zigbeeSendText(context.getYukonUser(), pao.getPaoName(),textMessage.getMessage(), EventSource.OPERATOR);
        
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
            Set<Integer> inventoryIds = Sets.newHashSet();
            inventoryIds.add(inventoryId);
            textMessage.setInventoryIds(inventoryIds);
            
            long yukonMessageId = nextValueHelper.getNextValue("ExternalToYukonMessageIdMapping");
            textMessage.setMessageId(yukonMessageId);
            zigbeeWebService.sendTextMessage(textMessage);

            zigbeeEventLogService.zigbeeSentText(pao.getPaoName(), textMessage.getMessage());
        } catch (DigiNotConfiguredException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (DigiWebServiceException e) {
            messageFailed = true;
            errorMessage = e.getMessage();
        } catch (ZigbeeClusterLibraryException e) {
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
        zigbeeEventLogService.zigbeeDeviceAssign(context.getYukonUser(), device.getPaoName(), gateway.getPaoName(), EventSource.OPERATOR);
        
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
        zigbeeEventLogService.zigbeeDeviceUnassign(context.getYukonUser(), device.getPaoName(), gateway.getPaoName(), EventSource.OPERATOR);
        
        try {
            zigbeeWebService.uninstallEndPoint(gatewayId, deviceId);
        } catch (DigiNotConfiguredException e) {
            log.warn("Problem while sending uninstall command to the device. Device will still be unassigned from the gateway.",e);
        } catch (DigiWebServiceException e) {
            log.warn("Problem while sending uninstall command to the device. Device will still be unassigned from the gateway.");
        }
        
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
        
        return "redirect:/stars/operator/hardware/view";
    }
}