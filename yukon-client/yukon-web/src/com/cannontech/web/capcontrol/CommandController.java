package com.cannontech.web.capcontrol;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.messaging.message.capcontrol.BankMoveMessage;
import com.cannontech.messaging.message.capcontrol.ChangeOpStateMessage;
import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.cannontech.messaging.message.capcontrol.CommandType;
import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.cannontech.messaging.message.capcontrol.ServerResponseMessage;
import com.cannontech.messaging.message.capcontrol.VerifyBanksMessage;
import com.cannontech.messaging.message.capcontrol.VerifyInactiveBanksMessage;
import com.cannontech.messaging.message.capcontrol.VerifySelectedBankMessage;
import com.cannontech.messaging.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.messaging.message.capcontrol.streamable.Feeder;
import com.cannontech.messaging.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.messaging.message.dispatch.PointDataMessage;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@Controller("/command/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CommandController {
    
    @Autowired private CapControlCommentDao commentDao;
    @Autowired private CapControlCache cache;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlCommandExecutor executor;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PaoDao paoDao;
    @Autowired private CapControlCommentService capControlCommentService;
    
    private static final Logger log = YukonLogManager.getLogger(CommandController.class);

    private static ImmutableMap<CapControlType, YukonRoleProperty> permissions;
    
    static {
        Builder<CapControlType, YukonRoleProperty> builder = ImmutableMap.builder();
        builder.put(CapControlType.AREA, YukonRoleProperty.ALLOW_AREA_CONTROLS);
        builder.put(CapControlType.SPECIAL_AREA, YukonRoleProperty.ALLOW_AREA_CONTROLS);
        builder.put(CapControlType.SUBSTATION, YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS);
        builder.put(CapControlType.SUBBUS, YukonRoleProperty.ALLOW_SUBBUS_CONTROLS);
        builder.put(CapControlType.FEEDER, YukonRoleProperty.ALLOW_FEEDER_CONTROLS);
        builder.put(CapControlType.CAPBANK, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS);
        builder.put(CapControlType.LTC, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS);
        builder.put(CapControlType.GO_REGULATOR, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS);
        builder.put(CapControlType.PO_REGULATOR, YukonRoleProperty.ALLOW_CAPBANK_CONTROLS);
        permissions = builder.build();
    }
    
    /* NORMAL COMMANDS */
    @RequestMapping
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
        CommandMessage command = CommandHelper.buildCommand(commandType, context.getYukonUser());
        try {
            executor.execute(command);
            success = true;
        } catch (CommandExecutionException e) {
            log.error("Could not send system command", e);
        }
        
        return sendStatusResponse(response, accessor, commandType, null, model, success);
    }
    
    @RequestMapping
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
        if (!rolePropertyDao.checkProperty(permissions.get(type), user)) {
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
            CommandMessage command;
            if (commandType.isVerifyCommand()) {
                command = CommandHelper.buildVerifyBanks(user, commandType, itemId, false);
            } else if (commandId == CommandType.VERIFY_SELECTED_BANK.getCommandId()  ) {
                int bankId = itemId;
                int busId = cache.getParentSubBusID(bankId);
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
    @RequestMapping
    public String changeOpState(HttpServletResponse response, 
                                       ModelMap model, 
                                       YukonUserContext context, 
                                       int bankId, 
                                       String opState,
                                       String reason) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        
        if (!rolePropertyDao.checkProperty(permissions.get(CapControlType.CAPBANK), user)){
            return sendNotAuthorizedResponse(response, accessor, model, user, CommandType.CHANGE_OP_STATE);
        }
        
        String bankName = cache.getCapBankDevice(bankId).getCcName();
        BankOpState state = BankOpState.valueOf(opState);
        ChangeOpStateMessage command = CommandHelper.buildChangeOpStateCommand(user, bankId, state);
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
    
    @RequestMapping
    public String bankMove(ModelMap model, 
                           LiteYukonUser user,
                           FlashScope flash,
                           int substationId,
                           boolean oneline,
                           String tempMove,
                           @ModelAttribute("bankMoveBean") BankMoveBean bankMoveBean) {
        
        rolePropertyDao.verifyProperty(permissions.get(CapControlType.CAPBANK), user);
        model.addAttribute("substationId", substationId);
        model.addAttribute("areaId", cache.getParentAreaID(substationId));
        
        CapBankDevice bank = cache.getCapBankDevice(bankMoveBean.getBankId());
        
        if (bankMoveBean.getNewFeederId() == 0) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.noFeederSelected", bank.getCcName()));
            if (oneline) {
                model.addAttribute("bankid", bank.getCcId());
                model.addAttribute("oneline", oneline);
                return "redirect:/capcontrol/move/bankMove";
            } else {
                return "redirect:/capcontrol/tier/feeders";
            }
        }
        
        Feeder newFeeder = cache.getFeeder(bankMoveBean.getNewFeederId());
        BankMoveMessage command = CommandHelper.buildBankMove(user, bankMoveBean, tempMove == null ? true : false);
        
        CommandResultCallback callback = new CommandResultCallback() {
            private ServerResponseMessage response;
            private String errorMessage;
            @Override
            public void processingExceptionOccured(String errorMessage) {
                this.errorMessage = errorMessage;
            }
            @Override
            public ServerResponseMessage getResponse() {
                return response;
            }
            @Override
            public void recievedResponse(ServerResponseMessage message) {
                if (!message.isSuccess()) {
                    this.errorMessage = message.getResponse();
                }
                this.response = message;
            }
            @Override
            public String getErrorMessage() {
                return errorMessage;
            }
        };
        
        ServerResponseMessage response = executor.blockingExecute(command, ServerResponseMessage.class, callback);
        if (response.isTimeout()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveTimeout", bank.getCcName(), newFeeder.getCcName()));
        } else if (!response.isSuccess()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveFailed", bank.getCcName(), newFeeder.getCcName(), callback.getErrorMessage()));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveSuccess", bank.getCcName(), newFeeder.getCcName()));
        }
        
        if (oneline) {
            model.addAttribute("bankid", bank.getCcId());
            model.addAttribute("oneline", oneline);
            return "redirect:/capcontrol/move/bankMove";
        } else {
            return "redirect:/capcontrol/tier/feeders";
        }
    }
    
    @RequestMapping
    public String returnBank(ModelMap model, FlashScope flash, LiteYukonUser user, int bankId, boolean assignHere) {
        
        rolePropertyDao.verifyProperty(permissions.get(CapControlType.CAPBANK), user);
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        Feeder feeder;
        ItemCommandMessage command;
        if (!assignHere) {
            feeder = cache.getFeeder(bank.getOrigFeederId());
            command = CommandHelper.buildItemCommand(CommandType.RETURN_CAP_TO_ORIGINAL_FEEDER.getCommandId(), bankId, user);
        } else {
            feeder = cache.getFeeder(bank.getParentId());
            BankMoveBean bean = new BankMoveBean();
            bean.setBankId(bankId);
            bean.setOldFeederId(bank.getOrigFeederId());
            bean.setNewFeederId(bank.getParentId());
            bean.setDisplayOrder(bank.getControlOrder()); // IS THIS RIGHT?
            bean.setCloseOrder(bank.getCloseOrder());
            bean.setTripOrder(bank.getTripOrder());
            command = CommandHelper.buildBankMove(user, bean, true); 
        }
        
        CommandResultCallback callback = new CommandResultCallback() {
            private ServerResponseMessage response;
            private String errorMessage;
            @Override
            public void processingExceptionOccured(String errorMessage) {
                this.errorMessage = errorMessage;
            }
            @Override
            public ServerResponseMessage getResponse() {
                return response;
            }
            @Override
            public void recievedResponse(ServerResponseMessage message) {
                if (!message.isSuccess()) {
                    this.errorMessage = message.getResponse();
                }
                this.response = message;
            }
            @Override
            public String getErrorMessage() {
                return errorMessage;
            }
        };
        
        ServerResponseMessage response = executor.blockingExecute(command, ServerResponseMessage.class, callback);
        if (response.isTimeout()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveTimeout", bank.getCcName(), feeder.getCcName()));
        } else if (!response.isSuccess()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveFailed", bank.getCcName(), feeder.getCcName(), callback.getErrorMessage()));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveSuccess", bank.getCcName(), feeder.getCcName()));
        }
        
        model.addAttribute("substationId", cache.getParentSubStationId(bankId));
        model.addAttribute("areaId", cache.getParentAreaID(bankId));
        return "redirect:/capcontrol/tier/feeders";
    }
    
    @RequestMapping
	public String manualStateChange(HttpServletResponse response, 
	                                       ModelMap model, 
	                                       YukonUserContext context, 
	                                       int paoId, 
	                                       int rawStateId) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        String bankName = cache.getCapBankDevice(paoId).getCcName();
        
        if (!rolePropertyDao.checkProperty(permissions.get(CapControlType.CAPBANK), user)){
            return sendNotAuthorizedResponse(response, accessor, model, user, CommandType.CHANGE_OP_STATE);
        }
        
	    int pointId = cache.getCapBankDevice(paoId).getStatusPointId();
	    PointDataMessage command = CommandHelper.buildManualStateChange(user, pointId, paoId, rawStateId);
	    boolean success = true;
	    try {
	        executor.execute(command);
	    } catch (CommandExecutionException e) {
	        success = false;
	    }
        
	    return sendStatusResponse(response, accessor, CommandType.MANUAL_ENTRY, bankName, model, success);
	}
    
    @RequestMapping
    public void commandOneLine(HttpServletResponse response, YukonUserContext context, int paoId, int cmdId) {
        LiteYukonUser user = context.getYukonUser();
        ItemCommandMessage command = CommandHelper.buildItemCommand(cmdId, paoId, user);
        
        try {
            executor.execute(command);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (CommandExecutionException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
        }
    }
    
    @RequestMapping
    public String commandOneLineTag(HttpServletRequest request,
                                           HttpServletResponse response,
                                           ModelMap model,
                                           YukonUserContext context, 
                                           int paoId) throws ServletRequestBindingException {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        
        boolean disableChange = ServletRequestUtils.getBooleanParameter(request, "disableChange", false);
        boolean disableOVUVChange = ServletRequestUtils.getBooleanParameter(request, "disableOvUvChange", false);
        boolean operationalStateChange = ServletRequestUtils.getBooleanParameter(request, "operationalStateChange", false);
        
        boolean disableValue = ServletRequestUtils.getBooleanParameter(request, "disableValue", false);
        boolean disableOVUVValue = ServletRequestUtils.getBooleanParameter(request, "disableOvUvValue", false);
        boolean success = true;
        
        if (disableChange) {
            int commandId;
            if (disableValue) {
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "disableCommandId");
            } else {
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "enableCommandId");
            }
            CommandType commandType = CommandType.getForId(commandId);

            ItemCommandMessage command = CommandHelper.buildItemCommand(commandId, paoId, user);
            boolean disableSuccess = true;
            try {
                executor.execute(command);
            } catch (CommandExecutionException e) {
                disableSuccess = false;
                success = false;
            }
            
            if (disableSuccess && disableValue) {
                String reason = ServletRequestUtils.getStringParameter(request, "disableReason", "");
                if (StringUtils.isBlank(reason)) reason = generateUpdateComment(commandType, paoId, user, accessor);
                insertComment(paoId, commandType, user, reason);
            }
        }
        
        if (disableOVUVChange) {
            int commandId;
            if (disableOVUVValue) {
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "disableOvUvCommandId");
            } else {
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "enableOvUvCommandId");
            }
            CommandType commandType = CommandType.getForId(commandId);

            ItemCommandMessage command = CommandHelper.buildItemCommand(commandId, paoId, user);
            boolean disableOvUvSuccess = true;
            try {
                executor.execute(command);
            } catch (CommandExecutionException e) {
                disableOvUvSuccess = false;
                success = false;
            }
            
            if (disableOvUvSuccess && disableOVUVValue) {
                String reason = ServletRequestUtils.getStringParameter(request, "disableOvUvReason", "");
                if (StringUtils.isBlank(reason)) reason = generateUpdateComment(commandType, paoId, user, accessor);
                insertComment(paoId, commandType, user, reason);
            }
        }
        
        if (operationalStateChange) {
            CommandType commandType = CommandType.CHANGE_OP_STATE;
            String reason = ServletRequestUtils.getStringParameter(request, "operationalStateReason", "");
            if (StringUtils.isBlank(reason)) reason = generateUpdateComment(commandType, paoId, user, accessor);
            
            String rawState = ServletRequestUtils.getRequiredStringParameter(request, "operationalStateValue");
            ChangeOpStateMessage command = CommandHelper.buildChangeOpStateCommand(user, paoId, BankOpState.valueOf(rawState));
            try {
                executor.execute(command);
                insertComment(paoId, commandType, user, reason);
            } catch (CommandExecutionException e) {
                success = false;
            }
        }
        if (success) {
            model.addAttribute("success", true);
            model.addAttribute("message", accessor.getMessage("yukon.web.modules.capcontrol.oneline.tagMenuCommandSuccess"));
        } else {
            model.addAttribute("success", false);
            model.addAttribute("message", accessor.getMessage("yukon.web.modules.capcontrol.oneline.tagMenuCommandFailed"));
        }
        model.addAttribute("finished", true);
        return "tier/popupmenu/statusMessage.jsp";
    }

    @RequestMapping
    public String resetBankOpCount(HttpServletResponse response, ModelMap model, YukonUserContext context, int bankId, int newOpCount) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        if (!rolePropertyDao.checkProperty(permissions.get(CapControlType.CAPBANK), user)) {
            return sendNotAuthorizedResponse(response, accessor, model, context.getYukonUser(), CommandType.RESET_DAILY_OPERATIONS);
        }
        
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        int pointId = bank.getOperationAnalogPointId();
        PointDataMessage command = CommandHelper.buildResetOpCount(user, pointId, bankId, newOpCount);
        boolean success = true;
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        
        return sendStatusResponse(response, accessor, CommandType.MANUAL_ENTRY, bank.getCcName(), model, success);
    }
    
    @RequestMapping
    public String verifyBanks(HttpServletResponse response, ModelMap model, YukonUserContext context, int commandId, int itemId, boolean disableOvUv) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        StreamableCapObject streamable = cache.getObject(itemId);
        CommandType type = CommandType.getForId(commandId);
        VerifyBanksMessage command = CommandHelper.buildVerifyBanks(context.getYukonUser(), type, itemId, disableOvUv);
        boolean success = true;
        
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        return sendStatusResponse(response, accessor, type, streamable.getCcName(), model, success);
    }
    
    @RequestMapping
    public String verifyInactiveBanks(HttpServletResponse response, ModelMap model, YukonUserContext context, int itemId, boolean disableOvUv, long inactivityTime) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        StreamableCapObject streamable = cache.getObject(itemId);
        CommandType type = CommandType.VERIFY_INACTIVE_BANKS;
        VerifyInactiveBanksMessage command = CommandHelper.buildVerifyInactiveBanks(context.getYukonUser(), type, itemId, disableOvUv, inactivityTime);
        boolean success = true;
        
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        return sendStatusResponse(response, accessor, type, streamable.getCcName(), model, success); 
    }
    
    @RequestMapping
    public String verifySelectedBank(HttpServletResponse response, ModelMap model, YukonUserContext context, int itemId, boolean disableOvUv, int bankId) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        StreamableCapObject streamable = cache.getObject(itemId);
        CommandType type = CommandType.VERIFY_SELECTED_BANK;
        VerifySelectedBankMessage command = CommandHelper.buildVerifySelectedBank(context.getYukonUser(), type, itemId, bankId, disableOvUv);
        boolean success = true;
        
        try {
            executor.execute(command);
        } catch (CommandExecutionException e) {
            success = false;
        }
        return sendStatusResponse(response, accessor, type, streamable.getCcName(), model, success); 
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
    
    private String generateUpdateComment(CommandType type, 
                                         int paoId, 
                                         LiteYukonUser user, 
                                         MessageSourceAccessor accessor) {
        
        String paoName = cache.getCapControlPAO(paoId).getCcName();
        String commandName = accessor.getMessage(type);
        return accessor.getMessage("yukon.web.modules.capcontrol.command.comment", commandName, paoName, user.getUsername());
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