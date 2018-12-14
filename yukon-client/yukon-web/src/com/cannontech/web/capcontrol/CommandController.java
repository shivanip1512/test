package com.cannontech.web.capcontrol;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.model.BankMoveBean;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.commands.CapControlCommandExecutor;
import com.cannontech.cbc.commands.CommandHelper;
import com.cannontech.cbc.commands.CommandResultCallback;
import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.capcontrol.model.BankMove;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.cannontech.message.capcontrol.model.ChangeOpState;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCapControlCommandsAccessLevel;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@Controller
@RequestMapping("/command/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CommandController {
    
    @Autowired private CapControlCommentDao commentDao;
    @Autowired private CapControlCache cache;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlCommandExecutor executor;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PaoDao paoDao;
    @Autowired private CapControlCommentService capControlCommentService;
    @Autowired private DbChangeManager dbChangeManager;
    
    private static final Logger log = YukonLogManager.getLogger(CommandController.class);
    
    private static ImmutableMap<CapControlType, YukonRoleProperty> permissions;
    
    static {
        Builder<CapControlType, YukonRoleProperty> builder = ImmutableMap.builder();
        builder.put(CapControlType.AREA, YukonRoleProperty.AREA_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.SPECIAL_AREA, YukonRoleProperty.AREA_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.SUBSTATION, YukonRoleProperty.SUBSTATION_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.SUBBUS, YukonRoleProperty.SUBBUS_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.FEEDER, YukonRoleProperty.FEEDER_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.CAPBANK, YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.LTC, YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.GO_REGULATOR, YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS);
        builder.put(CapControlType.PO_REGULATOR, YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS);
        permissions = builder.build();
    }
    
    /* NORMAL COMMANDS */
    @PostMapping("system")
    public String system(HttpServletResponse response,
                         ModelMap model, 
                         YukonUserContext context, 
                         int commandId) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        CommandType commandType = CommandType.getForId(commandId);
        
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.SYSTEM_WIDE_CONTROLS, context.getYukonUser())) {
            log.info("User not authorized to execute command - " + commandType);
            return sendNotAuthorizedResponse(response, accessor, model, context.getYukonUser(), commandType);
        }
        
        if (commandType == CommandType.SYSTEM_STATUS) {
            commandType = cache.getSystemStatusOn() ? CommandType.DISABLE_SYSTEM : CommandType.ENABLE_SYSTEM;
        }
        
        boolean success = false;
        CapControlCommand command = CommandHelper.buildCommand(commandType, context.getYukonUser());
        try {
            executor.execute(command);
            success = true;
        } catch (CommandExecutionException e) {
            log.error("Could not send system command", e);
        }
        
        return sendStatusResponse(response, accessor, commandType, null, model, success);
    }
    
    @PostMapping("itemCommand")
    public String itemCommand(HttpServletResponse response, 
                              ModelMap model, 
                              YukonUserContext context, 
                              int itemId, 
                              int commandId, 
                              String reason,
                              String onReasonMenu) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        StreamableCapObject streamable = cache.getObject(itemId);
        CapControlType type = CapControlType.getCapControlType(streamable.getCcType());
        CommandType commandType = CommandType.getForId(commandId);
        
        /* CHECK USER/COMMAND AUTHORIZATION */
        String commandName = accessor.getMessage(commandType);
        boolean authorized = true;
        if (commandType.isFieldOperationCommand()) {
            if (!rolePropertyDao.checkAnyLevel(permissions.get(type), CapControlCommandsAccessLevel.getFieldOperationLevels(), user)) {
                authorized = false;
            }
        } else if (commandType.isNonOperationCommand()) {
            if (!rolePropertyDao.checkAnyLevel(permissions.get(type), CapControlCommandsAccessLevel.getNonOperationLevels(), user)) {
                authorized = false;
            }
        } else if (commandType.isYukonAction()) {
            if (!rolePropertyDao.checkAnyLevel(permissions.get(type), CapControlCommandsAccessLevel.getYukonActionsLevels(), user)) {
                authorized = false;
            }
        }
        if (!authorized) {
            return sendNotAuthorizedResponse(response, accessor, model, user, commandType);
        }
        
        if (onReasonMenu == null && commandType.isCommentRequired() && StringUtils.isBlank(reason)) {
            /* RETURN REASON MENU */
            model.addAttribute("commandId", commandId);
            model.addAttribute("paoId", itemId);
            String reasonTitle = accessor.getMessage("yukon.web.modules.capcontrol.command.reasonTitle", commandName, streamable.getCcName());
            model.addAttribute("reasonTitle", reasonTitle);
            
            List<String> comments = capControlCommentService.getLastTenCommentsForActionAndType(itemId, commandId);
            model.addAttribute("comments", comments);
            
            model.addAttribute("finished", false);
            return "tier/popupmenu/reasonMenu.jsp";
        } else {
            /* SEND COMMAND AND RETURN STATUS */
            boolean success = true;
            CapControlCommand command;
            if (commandType.isVerifyCommand()) {
                command = CommandHelper.buildVerifyBanks(user, commandType, itemId, false);
            } else if (commandId == CommandType.VERIFY_SELECTED_BANK.getCommandId()  ) {
                int bankId = itemId;
                int busId = cache.getParentSubBusId(bankId);
                command = CommandHelper.buildVerifySelectedBank(user, commandType, busId, bankId, false);
            } else {
                command = CommandHelper.buildItemCommand(commandId, itemId, user);
            }
            try {
                executor.execute(command);
            } catch (CommandExecutionException e) {
                log.error("Could not send command to server: " + commandType, e);
                success = false;
            }
            
            if (success) {
                boolean forceComment = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.FORCE_COMMENTS, user);
                if (forceComment && StringUtils.isBlank(reason)) {
                    String commandLabel = accessor.getMessage(commandType);
                    String deviceName = paoDao.getYukonPAOName(itemId);
                    reason = accessor.getMessage("yukon.web.modules.capcontrol.forcedComment", commandLabel, deviceName);
                }
                
                if (StringUtils.isNotBlank(reason)) {
                    insertComment(itemId, commandType, user, reason);
                }
            }
            
            return sendStatusResponse(response, accessor, commandType, streamable.getCcName(), model, success);
        }
    }

    /* SPECIAL COMMANDS */
    @PostMapping("changeOpState")
    public String changeOpState(HttpServletResponse response, 
                                       ModelMap model, 
                                       YukonUserContext context, 
                                       int bankId, 
                                       String opState,
                                       String reason) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        
        if (!rolePropertyDao.checkAnyLevel(YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, CapControlCommandsAccessLevel.getYukonActionsLevels(), user)) {
            return sendNotAuthorizedResponse(response, accessor, model, user, CommandType.CHANGE_OP_STATE);
        }
        
        String bankName = cache.getCapBankDevice(bankId).getCcName();
        BankOpState state = BankOpState.valueOf(opState);
        ChangeOpState command = CommandHelper.buildChangeOpStateCommand(user, bankId, state);
        boolean success = true;
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        
        if (success) {
            boolean forceComment = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.FORCE_COMMENTS, user);
            if (forceComment && StringUtils.isBlank(reason)) {
                String commandLabel = accessor.getMessage(CommandType.CHANGE_OP_STATE);
                String deviceName = paoDao.getYukonPAOName(bankId);
                reason = accessor.getMessage("yukon.web.modules.capcontrol.forcedComment", commandLabel, deviceName);
            }
            
            if (StringUtils.isNotBlank(reason)) {
                insertComment(bankId, CommandType.CHANGE_OP_STATE, user, reason);
            }
        }
        
        return sendStatusResponse(response, accessor, CommandType.CHANGE_OP_STATE, bankName, model, success);
    }
    
    @CheckCapControlCommandsAccessLevel(property=YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, levels={CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
            CapControlCommandsAccessLevel.NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,CapControlCommandsAccessLevel.YUKON_ACTIONS_ONLY})
    @PostMapping("bankMove")
    public String bankMove(ModelMap model, LiteYukonUser user, FlashScope flash, int substationId, 
                           String tempMove, @ModelAttribute("bankMoveBean") BankMoveBean bankMoveBean) {

        model.addAttribute("substationId", substationId);
        model.addAttribute("areaId", cache.getParentAreaId(substationId));
        
        CapBankDevice bank = cache.getCapBankDevice(bankMoveBean.getBankId());
        
        if (bankMoveBean.getNewFeederId() == 0) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.noFeederSelected", bank.getCcName()));
            return "redirect:/capcontrol/substations/" + substationId;
        }
        
        Feeder newFeeder = cache.getFeeder(bankMoveBean.getNewFeederId());
        BankMove command = CommandHelper.buildBankMove(user, bankMoveBean, tempMove == null ? true : false);
        
        CommandResultCallback callback = new CommandResultCallback() {
            private CapControlServerResponse response;
            private String errorMessage;
            @Override
            public void processingExceptionOccurred(String errorMessage) {
                this.errorMessage = errorMessage;
            }
            @Override
            public CapControlServerResponse getResponse() {
                return response;
            }
            @Override
            public void receivedResponse(CapControlServerResponse message) {
                if (!message.isSuccess()) {
                    errorMessage = message.getResponse();
                }
                response = message;
            }
            @Override
            public String getErrorMessage() {
                return errorMessage;
            }
        };
        
        CapControlServerResponse ccResponse = executor.blockingExecute(command, CapControlServerResponse.class, callback);
        if (ccResponse.isTimeout()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveTimeout", bank.getCcName(), newFeeder.getCcName()));
        } else if (!ccResponse.isSuccess()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveFailed", bank.getCcName(), newFeeder.getCcName(), callback.getErrorMessage()));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveSuccess", bank.getCcName(), newFeeder.getCcName()));
        }
        
        return "redirect:/capcontrol/substations/" + substationId;
    }

    @CheckCapControlCommandsAccessLevel(property=YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, levels={CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
            CapControlCommandsAccessLevel.NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,CapControlCommandsAccessLevel.YUKON_ACTIONS_ONLY})
    @GetMapping(value="{bankId}/move-back")
    public @ResponseBody Map<String, ? extends Object> returnBank(ModelMap model, FlashScope flash, @PathVariable int bankId, LiteYukonUser user) {
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        Feeder feeder = cache.getFeeder(bank.getOrigFeederID());
        ItemCommand command = CommandHelper.buildItemCommand(CommandType.RETURN_CAP_TO_ORIGINAL_FEEDER.getCommandId(), bankId, user);
        return doBankMoveCommand(model, flash, command, feeder, bank);
    }

    @CheckCapControlCommandsAccessLevel(property=YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, levels={CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
            CapControlCommandsAccessLevel.NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,CapControlCommandsAccessLevel.YUKON_ACTIONS_ONLY})
    @GetMapping(value="{bankId}/assign-here")
    public @ResponseBody Map<String, ? extends Object> assignHere(ModelMap model, FlashScope flash, @PathVariable int bankId, LiteYukonUser user) {
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        Feeder feeder = cache.getFeeder(bank.getParentID());

        BankMoveBean bean = new BankMoveBean();
        bean.setBankId(bankId);
        bean.setOldFeederId(bank.getOrigFeederID());
        bean.setNewFeederId(bank.getParentID());
        bean.setDisplayOrder(bank.getControlOrder()); // IS THIS RIGHT?
        bean.setCloseOrder(bank.getCloseOrder());
        bean.setTripOrder(bank.getTripOrder());
        ItemCommand command = CommandHelper.buildBankMove(user, bean, true);

        return doBankMoveCommand(model, flash, command, feeder, bank);
    }

    private Map<String, ? extends Object> doBankMoveCommand(ModelMap model, FlashScope flash, ItemCommand command, Feeder feeder, CapBankDevice bank){
        CommandResultCallback callback = new CommandResultCallback() {
            private CapControlServerResponse response;
            private String errorMessage;
            @Override
            public void processingExceptionOccurred(String errorMessage) {
                this.errorMessage = errorMessage;
            }
            @Override
            public CapControlServerResponse getResponse() {
                return response;
            }
            @Override
            public void receivedResponse(CapControlServerResponse message) {
                if (!message.isSuccess()) {
                    errorMessage = message.getResponse();
                }
                response = message;
            }
            @Override
            public String getErrorMessage() {
                return errorMessage;
            }
        };

        CapControlServerResponse response = executor.blockingExecute(command, CapControlServerResponse.class, callback);
        if (response.isTimeout()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveTimeout", bank.getCcName(), feeder.getCcName()));
            return ImmutableMap.of("success", false, "error", "timeout");
        } else if (!response.isSuccess()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveFailed", bank.getCcName(), feeder.getCcName(), callback.getErrorMessage()));
            return ImmutableMap.of("success", false, "error", callback.getErrorMessage());
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveSuccess", bank.getCcName(), feeder.getCcName()));
            return ImmutableMap.of("success", true);
        }
    }

    @PostMapping("manualStateChange")
    public String manualStateChange(HttpServletResponse response, 
                                           ModelMap model, 
                                           YukonUserContext context, 
                                           int paoId, 
                                           int rawStateId) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        String bankName = cache.getCapBankDevice(paoId).getCcName();
        
        if (!rolePropertyDao.checkAnyLevel(YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, CapControlCommandsAccessLevel.getYukonActionsLevels(), user)) {
            return sendNotAuthorizedResponse(response, accessor, model, user, CommandType.MANUAL_ENTRY);
        }
        
        int pointId = cache.getCapBankDevice(paoId).getStatusPointID();
        PointData command = CommandHelper.buildManualStateChange(user, pointId, paoId, rawStateId);
        boolean success = true;
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        // Added a dbChangemsg for YUK-15684 as the statistics values were not being dynamically updated on the page
        // as our current cap control cache does not get updated unless we receive specific messages.
        int substationId =
            cache.getSubBus(cache.getFeeder(cache.getCapBankDevice(paoId).getParentID()).getParentID()).getParentID();
        dbChangeManager.processPaoDbChange(PaoIdentifier.of(substationId, PaoType.CAP_CONTROL_SUBSTATION),
            DbChangeType.UPDATE);
        return sendStatusResponse(response, accessor, CommandType.MANUAL_ENTRY, bankName, model, success);
    }
    
    @PostMapping("resetBankOpCount")
    public String resetBankOpCount(HttpServletResponse response, ModelMap model, YukonUserContext context, int bankId, int newOpCount) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        if (!rolePropertyDao.checkAnyLevel(YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, CapControlCommandsAccessLevel.getYukonActionsLevels(), user)) {
            return sendNotAuthorizedResponse(response, accessor, model, context.getYukonUser(), CommandType.RESET_DAILY_OPERATIONS);
        }
        
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        int pointId = bank.getOperationAnalogPointID();
        PointData command = CommandHelper.buildResetOpCount(user, pointId, bankId, newOpCount);
        boolean success = true;
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        
        return sendStatusResponse(response, accessor, CommandType.RESET_DAILY_OPERATIONS, bank.getCcName(), model, success);
    }
    
    private String sendNotAuthorizedResponse(HttpServletResponse response, 
                                             MessageSourceAccessor accessor, 
                                             ModelMap model, 
                                             LiteYukonUser user, 
                                             CommandType type) {
        
        String commandName = accessor.getMessage(type);
        String notAuthorized = accessor.getMessage("yukon.web.modules.capcontrol.command.notAuthorized", user.getUsername(), commandName);
        model.addAttribute("success", false);
        model.addAttribute("finished", true);
        model.addAttribute("message", notAuthorized);
        return "tier/popupmenu/statusMessage.jsp";
    }
    
    private String sendStatusResponse(HttpServletResponse response, 
                                      MessageSourceAccessor accessor, 
                                      CommandType type, 
                                      String objectName, 
                                      ModelMap model, 
                                      boolean success) {
        
        String commandName = accessor.getMessage(type);
        String key;
        if (success) {
            if (StringUtils.isBlank(objectName)) {
                key = "yukon.web.modules.capcontrol.commandQueued";
            } else {
                key = "yukon.web.modules.capcontrol.commandQueuedFor";
            }
        } else {
            key = "yukon.web.modules.capcontrol.commandFailed";
        }
        String message = accessor.getMessage(key, commandName, objectName);
        model.addAttribute("success", success);
        model.addAttribute("message", message);
        
        model.addAttribute("finished", true);
        return "tier/popupmenu/statusMessage.jsp";
    }
    
    private void insertComment(int paoId, CommandType type, LiteYukonUser user, String reason) {
        CapControlComment comment = new CapControlComment();
        comment.setPaoId(paoId);
        comment.setUserId(user.getUserID());
        comment.setComment(reason);
        comment.setAltered(false);
        
        Date date = new Date();
        comment.setDate(date);
        
        CommentAction action = CapControlComment.getActionForCommand(type);
        comment.setAction(action.toString());
        
        commentDao.add(comment);
    }
    
}