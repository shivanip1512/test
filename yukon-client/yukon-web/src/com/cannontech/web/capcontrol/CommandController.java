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
import com.cannontech.message.capcontrol.model.BankMove;
import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.cannontech.message.capcontrol.model.ChangeOpState;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.message.capcontrol.model.VerifyBanks;
import com.cannontech.message.capcontrol.model.VerifyInactiveBanks;
import com.cannontech.message.capcontrol.model.VerifySelectedBank;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@Controller("/command/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class CommandController {
    
    private CapControlCommentDao commentDao;
    private CapControlCache cache;
    private RolePropertyDao rolePropertyDao;
    private CapControlCommandExecutor executor;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private PaoDao paoDao;
    private static final Logger log = YukonLogManager.getLogger(CommandController.class);
    private CapControlCommentService capControlCommentService;

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
        CapControlCommand command = CommandHelper.buildCommand(commandType, context.getYukonUser());
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
            CapControlCommand command;
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
    
    @RequestMapping
    public String bankMove(ModelMap model, 
                           LiteYukonUser user,
                           FlashScope flash,
                           int substationId,
                           String tempMove,
                           @ModelAttribute("bankMoveBean") BankMoveBean bankMoveBean) {
        
        rolePropertyDao.verifyProperty(permissions.get(CapControlType.CAPBANK), user);
        model.addAttribute("substationId", substationId);
        model.addAttribute("areaId", cache.getParentAreaID(substationId));
        
        CapBankDevice bank = cache.getCapBankDevice(bankMoveBean.getBankId());
        
        if(bankMoveBean.getNewFeederId() == 0) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.noFeederSelected", bank.getCcName()));
            return "redirect:/spring/capcontrol/tier/feeders";
        }
        
        Feeder newFeeder = cache.getFeeder(bankMoveBean.getNewFeederId());
        BankMove command = CommandHelper.buildBankMove(user, bankMoveBean, tempMove == null ? true : false);
        
        CommandResultCallback callback = new CommandResultCallback() {
            private CapControlServerResponse response;
            private String errorMessage;
            @Override
            public void processingExceptionOccured(String errorMessage) {
                this.errorMessage = errorMessage;
            }
            @Override
            public CapControlServerResponse getResponse() {
                return response;
            }
            @Override
            public void recievedResponse(CapControlServerResponse message) {
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
        
        CapControlServerResponse response = executor.blockingExecute(command, CapControlServerResponse.class, callback);
        if (response.isTimeout()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveTimeout", bank.getCcName(), newFeeder.getCcName()));
        } else if (!response.isSuccess()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveFailed", bank.getCcName(), newFeeder.getCcName(), callback.getErrorMessage()));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveSuccess", bank.getCcName(), newFeeder.getCcName()));
        }
        
        return "redirect:/spring/capcontrol/tier/feeders";
    }
    
    @RequestMapping
    public String returnBank(ModelMap model, FlashScope flash, LiteYukonUser user, int bankId, boolean assignHere) {
        
        rolePropertyDao.verifyProperty(permissions.get(CapControlType.CAPBANK), user);
        CapBankDevice bank = cache.getCapBankDevice(bankId);
        Feeder feeder;
        ItemCommand command;
        if (!assignHere) {
            feeder = cache.getFeeder(bank.getOrigFeederID());
            command = CommandHelper.buildItemCommand(CommandType.RETURN_CAP_TO_ORIGINAL_FEEDER.getCommandId(), bankId, user);
        } else {
            feeder = cache.getFeeder(bank.getParentID());
            BankMoveBean bean = new BankMoveBean();
            bean.setBankId(bankId);
            bean.setOldFeederId(bank.getOrigFeederID());
            bean.setNewFeederId(bank.getParentID());
            bean.setDisplayOrder(bank.getControlOrder()); // IS THIS RIGHT?
            bean.setCloseOrder(bank.getCloseOrder());
            bean.setTripOrder(bank.getTripOrder());
            command = CommandHelper.buildBankMove(user, bean, true); 
        }
        
        CommandResultCallback callback = new CommandResultCallback() {
            private CapControlServerResponse response;
            private String errorMessage;
            @Override
            public void processingExceptionOccured(String errorMessage) {
                this.errorMessage = errorMessage;
            }
            @Override
            public CapControlServerResponse getResponse() {
                return response;
            }
            @Override
            public void recievedResponse(CapControlServerResponse message) {
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
        
        CapControlServerResponse response = executor.blockingExecute(command, CapControlServerResponse.class, callback);
        if (response.isTimeout()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveTimeout", bank.getCcName(), feeder.getCcName()));
        } else if (!response.isSuccess()) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveFailed", bank.getCcName(), feeder.getCcName(), callback.getErrorMessage()));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.bankMoveSuccess", bank.getCcName(), feeder.getCcName()));
        }
        
        model.addAttribute("substationId", cache.getParentSubStationId(bankId));
        model.addAttribute("areaId", cache.getParentAreaID(bankId));
        return "redirect:/spring/capcontrol/tier/feeders";
    }
    
    @RequestMapping
	public String manualStateChange(HttpServletResponse response, 
	                                       ModelMap model, 
	                                       YukonUserContext context, 
	                                       int bankId, 
	                                       int rawStateId) {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        String bankName = cache.getCapBankDevice(bankId).getCcName();
        
        if (!rolePropertyDao.checkProperty(permissions.get(CapControlType.CAPBANK), user)){
            return sendNotAuthorizedResponse(response, accessor, model, user, CommandType.CHANGE_OP_STATE);
        }
        
	    int pointId = cache.getCapBankDevice(bankId).getStatusPointID();
	    PointData command = CommandHelper.buildManualStateChange(user, pointId, bankId, rawStateId);
	    boolean success = true;
	    try {
	        executor.execute(command);
	    } catch (CommandExecutionException e) {
	        success = false;
	    }
        
	    return sendStatusResponse(response, accessor, CommandType.MANUAL_ENTRY, bankName, model, success);
	}
    
    @RequestMapping//TODO FIX ONELINE TO USE NORMAL MSG DIV
    public String commandOneLineTag(HttpServletRequest request,
                                           HttpServletResponse response,
                                           ModelMap model,
                                           YukonUserContext context, 
                                           int paoId) throws ServletRequestBindingException {
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LiteYukonUser user = context.getYukonUser();
        
        boolean disableChange = ServletRequestUtils.getBooleanParameter(request, "disableChange", false);
        boolean disableOVUVChange = ServletRequestUtils.getBooleanParameter(request, "disableOVUVChange", false);
        boolean operationalStateChange = ServletRequestUtils.getBooleanParameter(request, "operationalStateChange", false);
        
        boolean disableValue = ServletRequestUtils.getBooleanParameter(request, "disableValue", false);
        boolean disableOVUVValue = ServletRequestUtils.getBooleanParameter(request, "disableOVUVValue", false);
        boolean success = true;
        
        if (disableChange) {
            int commandId;
            if (disableValue) {
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "disableCommandId");
            } else {
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "enabledCommandId");
            }
            CommandType commandType = CommandType.getForId(commandId);

            ItemCommand command = CommandHelper.buildItemCommand(commandId, paoId, user);
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
                commandId = ServletRequestUtils.getRequiredIntParameter(request, "enabledOvUvCommandId");
            }
            CommandType commandType = CommandType.getForId(commandId);

            ItemCommand command = CommandHelper.buildItemCommand(commandId, paoId, user);
            boolean disableOvUvSuccess = true;
            try {
                executor.execute(command);
            } catch (CommandExecutionException e) {
                disableOvUvSuccess = false;
                success = false;
            }
            
            if (disableOvUvSuccess && disableOVUVValue) {
                String reason = ServletRequestUtils.getStringParameter(request, "disableOVUVReason", "");
                if (StringUtils.isBlank(reason)) reason = generateUpdateComment(commandType, paoId, user, accessor);
                insertComment(paoId, commandType, user, reason);
            }
        }
        
        if (operationalStateChange) {
            CommandType commandType = CommandType.CHANGE_OP_STATE;
            String reason = ServletRequestUtils.getStringParameter(request, "operationalStateReason", "");
            if (StringUtils.isBlank(reason)) reason = generateUpdateComment(commandType, paoId, user, accessor);
            
            String rawState = ServletRequestUtils.getRequiredStringParameter(request, "operationalStateValue");
            ChangeOpState command = CommandHelper.buildChangeOpStateCommand(user, paoId, BankOpState.valueOf(rawState));
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
        int pointId = bank.getOperationAnalogPointID();
        PointData command = CommandHelper.buildResetOpCount(user, pointId, bankId, newOpCount);
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
        VerifyBanks command = CommandHelper.buildVerifyBanks(context.getYukonUser(), type, itemId, disableOvUv);
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
        VerifyInactiveBanks command = CommandHelper.buildVerifyInactiveBanks(context.getYukonUser(), type, itemId, disableOvUv, inactivityTime);
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
        VerifySelectedBank command = CommandHelper.buildVerifySelectedBank(context.getYukonUser(), type, itemId, bankId, disableOvUv);
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
    
    @Autowired
    public void setCommentDao(CapControlCommentDao commentDao) {
        this.commentDao = commentDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache capControlCache) {
        this.cache = capControlCache;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setCapControlCommandExecutor(CapControlCommandExecutor executor) {
        this.executor = executor;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setCapControlCommentService(CapControlCommentService capControlCommentService) {
        this.capControlCommentService = capControlCommentService;
    }
    
}