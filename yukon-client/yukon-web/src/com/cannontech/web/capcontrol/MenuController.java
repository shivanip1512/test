package com.cannontech.web.capcontrol;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/menu/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class MenuController {
    @Autowired private CapControlCache cache;
    @Autowired private PaoDao paoDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlCommentService ccCommentService;
    @Autowired private VoltageRegulatorService voltageRegulatorService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    private final static BankOpState[] allowedOperationStates =
            new BankOpState[] {BankOpState.FIXED,BankOpState.STANDALONE,BankOpState.SWITCHED};

    @RequestMapping("commandMenu")
    public String commandMenu(ModelMap model, int id, LiteYukonUser user) {
        StreamableCapObject object = cache.getObject(id);
        model.addAttribute("paoId", object.getCcId());
        String paoName = object.getCcName();
        
        // Need to escape this here AND in the jsp since jquery ui dialog call
        // will unescape it. Double escaping solves this.
        model.addAttribute("paoName", StringEscapeUtils.escapeXml11(paoName));
        
        model.addAttribute("showChangeOpState", false);
        model.addAttribute("showComments", true);
        model.addAttribute("showRecentCommands", true);
        model.addAttribute("showLocalControl", rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user));
        
        if (object instanceof Area || object instanceof SpecialArea) {
            setupAreaMenuModel(model, object);
        } else if (object instanceof SubStation) {
            setupSubstationModel(model, (SubStation)object);
        } else if (object instanceof SubBus) {
            setupSubBusModel(model, (SubBus)object);
        } else if (object instanceof Feeder) {
            setupFeederModel(model, (Feeder)object);
        } else if (object instanceof CapBankDevice) {
            setupCapBankModel(model, user, (CapBankDevice)object);
        }
        
        return "tier/popupmenu/menu.jsp";
    }
    
    private void setupAreaMenuModel(ModelMap model, StreamableCapObject area) {
        List<CommandType> commands = Lists.newArrayList();
        commands.add(CommandType.CONFIRM_AREA);
        if (area.getCcDisableFlag()) {
            commands.add(CommandType.ENABLE_AREA);
        } else {
            commands.add(CommandType.DISABLE_AREA);
        }
        commands.add(CommandType.SEND_OPEN_CAPBANK);
        commands.add(CommandType.SEND_CLOSE_CAPBANK);
        commands.add(CommandType.SEND_SCAN_2WAY_DEVICE);
        commands.add(CommandType.SEND_TIME_SYNC);
        commands.add(CommandType.SEND_SYNC_CBC_CAPBANK_STATE);
        commands.add(CommandType.EMERGENCY_VERIFICATION_STOP);
        model.addAttribute("commands", commands);
    }

    private void setupSubstationModel(ModelMap model, SubStation substation) {
        List<CommandType> commands = Lists.newArrayList();
        commands.add(CommandType.CONFIRM_SUBSTATION);
        if (substation.getCcDisableFlag()) {
            commands.add(CommandType.ENABLE_SUBSTATION);
        } else {
            commands.add(CommandType.DISABLE_SUBSTATION);
        }
        commands.add(CommandType.RESET_DAILY_OPERATIONS);
        commands.add(CommandType.SEND_OPEN_CAPBANK);
        commands.add(CommandType.SEND_CLOSE_CAPBANK);
        commands.add(CommandType.SEND_SCAN_2WAY_DEVICE);
        commands.add(CommandType.SEND_TIME_SYNC);
        commands.add(CommandType.SEND_SYNC_CBC_CAPBANK_STATE);
        model.addAttribute("commands", commands);
    }

    private void setupSubBusModel(ModelMap model, SubBus subBus) {
        List<CommandType> commands = Lists.newArrayList();
        commands.add(CommandType.CONFIRM_SUBSTATION_BUS);
        if (subBus.getCcDisableFlag()) {
            commands.add(CommandType.ENABLE_SUBSTATION_BUS);
        } else {
            commands.add(CommandType.DISABLE_SUBSTATION_BUS);
        }
        
        commands.add(CommandType.RESET_DAILY_OPERATIONS);
        commands.add(CommandType.SEND_OPEN_CAPBANK);
        commands.add(CommandType.SEND_CLOSE_CAPBANK);
        commands.add(CommandType.SEND_SCAN_2WAY_DEVICE);
        commands.add(CommandType.SEND_TIME_SYNC);
        commands.add(CommandType.SEND_SYNC_CBC_CAPBANK_STATE);
        if (!subBus.getVerificationFlag()) {
            /*
             * You shouldn't be able to Initiated any "Verify ..." commands from the subbus level
             * without a strategy attached. They require a strategy and rely on knowing VAR change
             * to determine the outcome of a control sent.
             */
            if (CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(subBus)) {
                commands.add(CommandType.VERIFY_ALL_BANKS);
                commands.add(CommandType.VERIFY_FQ_BANKS);
                commands.add(CommandType.VERIFY_FAILED_BANKS);
                commands.add(CommandType.VERIFY_Q_BANKS);
                commands.add(CommandType.VERIFY_SA_BANKS);
            }
        } else {
            commands.add(CommandType.STOP_VERIFICATION);
            commands.add(CommandType.EMERGENCY_VERIFICATION_STOP);
        }
        model.addAttribute("commands", commands);
    }
    
    private void setupFeederModel(ModelMap model, Feeder feeder) {
        List<CommandType> commands = Lists.newArrayList();
        commands.add(CommandType.CONFIRM_FEEDER);
        if (feeder.getCcDisableFlag()) {
            commands.add(CommandType.ENABLE_FEEDER);
        } else {
            commands.add(CommandType.DISABLE_FEEDER);
        }
        commands.add(CommandType.RESET_DAILY_OPERATIONS);
        commands.add(CommandType.SEND_OPEN_CAPBANK);
        commands.add(CommandType.SEND_CLOSE_CAPBANK);
        commands.add(CommandType.SEND_SCAN_2WAY_DEVICE);
        commands.add(CommandType.SEND_TIME_SYNC);
        commands.add(CommandType.SEND_SYNC_CBC_CAPBANK_STATE);
        model.addAttribute("commands", commands);
    }

    private void setupCapBankModel(ModelMap model, LiteYukonUser user, CapBankDevice capBank) {
        int cbcDeviceId = capBank.getControlDeviceID();
        LiteYukonPAObject cbcPaoObject = paoDao.getLiteYukonPAO(cbcDeviceId);
        
        boolean isClosed = CapBankDevice.isInAnyCloseState(capBank);
        boolean isTwoWay = DeviceTypesFuncs.isCBCTwoWay(cbcPaoObject.getPaoType());
        boolean is702xDevice = DeviceTypesFuncs.is702xDevice(cbcPaoObject.getPaoType());
        boolean is701xDevice = DeviceTypesFuncs.is701xDevice(cbcPaoObject.getPaoType());
        boolean allowFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        
        List<CommandType> commands = Lists.newArrayList();
        if (isClosed) {
            commands.add(CommandType.CONFIRM_CLOSE);
        } else {
            commands.add(CommandType.CONFIRM_OPEN);
        }
        
        boolean strategyAttachedToSubBus = true;
        try {
            SubBus parentSubBus = cache.getParentSubBus(capBank.getCcId());
            strategyAttachedToSubBus = CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(parentSubBus);
        } catch (NotFoundException nfe) {/* no parent subbus assigned - that's fine, move along */}
        if (strategyAttachedToSubBus) {
            commands.add(CommandType.SEND_OPEN_CAPBANK);
            commands.add(CommandType.SEND_CLOSE_CAPBANK);
        }
        if (isTwoWay) {
            commands.add(CommandType.SEND_SCAN_2WAY_DEVICE);
        }
        commands.add(CommandType.SEND_TIME_SYNC);
        commands.add(CommandType.SEND_SYNC_CBC_CAPBANK_STATE);
        
        if (is702xDevice) {
            model.addAttribute("allowLocalControl", allowLocalControl);
            model.addAttribute("localControlTypeCBC", true);
        }
        if (allowLocalControl && !is702xDevice) {
            commands.add(CommandType.SEND_ENABLE_OVUV);
            commands.add(CommandType.SEND_DISABLE_OVUV);
        }
        if (allowFlip && is701xDevice) {
            commands.add(CommandType.FLIP_7010_CAPBANK);
        }
        commands.add(CommandType.VERIFY_SELECTED_BANK);
       
        model.addAttribute("commands", commands);
    }

    @RequestMapping("localControl")
    public String localControl(ModelMap model, int id) {
        StreamableCapObject capObject = cache.getCapControlPao(id);
        
        Validate.notNull(capObject);
        
        model.addAttribute("paoId", id);
        String paoName = capObject.getCcName();
        model.addAttribute("paoName", paoName);
        
        List<CommandType> commands = Lists.newArrayList();
        commands.add(CommandType.SEND_ENABLE_OVUV);
        commands.add(CommandType.SEND_DISABLE_OVUV);
        commands.add(CommandType.SEND_ENABLE_TEMPCONTROL);
        commands.add(CommandType.SEND_DISABLE_TEMPCONTROL);
        commands.add(CommandType.SEND_ENABLE_VARCONTROL);
        commands.add(CommandType.SEND_DISABLE_VARCONTROL);
        commands.add(CommandType.SEND_ENABLE_TIMECONTROL);
        commands.add(CommandType.SEND_DISABLE_TIMECONTROL);
        
        model.addAttribute("commands", commands);
        model.addAttribute("showRecentCommands", false);
        model.addAttribute("showComments", false);
        
        return "tier/popupmenu/menu.jsp";
    }

    @RequestMapping("capBankState")
    public String capBankState(ModelMap model, int id) {
        CapBankDevice capBank = cache.getCapBankDevice(id);
        model.addAttribute("paoId", id);
        String paoName = capBank.getCcName();
        model.addAttribute("paoName", paoName);
        
        List<CommandType> commands = Lists.newArrayList();
        if (capBank.getCcDisableFlag()) {
            commands.add(CommandType.ENABLE_CAPBANK);
        } else {
            commands.add(CommandType.DISABLE_CAPBANK);
        }
        model.addAttribute("commands", commands);
        
        model.addAttribute("showResetBankOpCount", true);
        model.addAttribute("resetBankOpCount", CommandType.RESET_DAILY_OPERATIONS);
        
        model.addAttribute("showChangeOpState", true);
        model.addAttribute("changeOpState", CommandType.CHANGE_OP_STATE);
        
        List<LiteState> states = CapControlUtils.getCBCStateNames();
        model.addAttribute("states", states);

        return "tier/popupmenu/menu.jsp";
    }

    @RequestMapping("resetBankOpCount")
    public String resetBankOpCount(ModelMap model, int bankId) {
        model.addAttribute("title", cache.getCapBankDevice(bankId).getCcName());
        model.addAttribute("bankId", bankId);
        return "tier/popupmenu/resetBankOpCountMenu.jsp";
    }
    
    @RequestMapping("opStateChange")
    public String opStateChange(ModelMap model, int bankId) {
        CapBankDevice capBank = cache.getCapBankDevice(bankId);
        String reason = ccCommentService.getReason(bankId, CommentAction.STANDALONE_REASON, CapControlType.CAPBANK);
        BankOpState currentState = BankOpState.getStateByName(capBank.getOperationalState());
        model.addAttribute("bankId", bankId);
        
        String paoName = capBank.getCcName();
        model.addAttribute("title", paoName);
        
        List<String> comments = ccCommentService.getLastTenCommentsForActionAndType(bankId, CommandType.CHANGE_OP_STATE.getCommandId());
        model.addAttribute("comments", comments);
        
        model.addAttribute("changeOpStateCmdHolder", CommandType.CHANGE_OP_STATE);
        
        model.addAttribute("currentState", currentState);
        model.addAttribute("allowedOperationStates", allowedOperationStates);
        
        model.addAttribute("reason", reason);
        
        return "tier/popupmenu/opStateChangeMenu.jsp";
    }

    @RequestMapping("create")
    public String create(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String title = accessor.getMessage("yukon.web.modules.capcontrol.create.title");
        model.addAttribute("title", title);
        
        return "tier/popupmenu/create.jsp";
    }
}
