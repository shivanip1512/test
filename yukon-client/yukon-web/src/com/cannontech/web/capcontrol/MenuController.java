package com.cannontech.web.capcontrol;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;
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
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
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

@Controller("/menu/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class MenuController {
    
    private CapControlCache cache;
    private PaoDao paoDao;
    private RolePropertyDao rolePropertyDao;
    private CapControlCommentService capControlCommentService;
    private VoltageRegulatorService voltageRegulatorService;
    private static final BankOpState[] allowedOperationStates;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    static {
        allowedOperationStates  = new BankOpState[] {
                                                                 BankOpState.FIXED,
                                                                 BankOpState.STANDALONE,
                                                                 BankOpState.SWITCHED};
    }
    
    @RequestMapping
    public String commandMenu(ModelMap model, int id, LiteYukonUser user) {
        StreamableCapObject object = cache.getObject(id);
        model.addAttribute("paoId", object.getCcId());
        String paoName = object.getCcName();
        model.addAttribute("paoName", paoName);
        
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
            commands.add(CommandType.VERIFY_ALL_BANKS);
            commands.add(CommandType.VERIFY_FQ_BANKS);
            commands.add(CommandType.VERIFY_FAILED_BANKS);
            commands.add(CommandType.VERIFY_Q_BANKS);
            commands.add(CommandType.VERIFY_SA_BANKS);
        } else {
            commands.add(CommandType.STOP_VERIFICATION);
            commands.add(CommandType.EMERGENCY_VERIFICATION_STOP);
        }
        model.addAttribute("commands", commands);
    }
    
    public void setupFeederModel(ModelMap model, Feeder feeder) {
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

    public void setupCapBankModel(ModelMap model, LiteYukonUser user, CapBankDevice capBank) {
        int cbcDeviceId = capBank.getControlDeviceID();
        LiteYukonPAObject cbcPaoObject = paoDao.getLiteYukonPAO(cbcDeviceId);
        
        boolean isClosed = CapBankDevice.isInAnyCloseState(capBank);
        boolean isTwoWay = CapControlUtils.isTwoWay(cbcPaoObject);
        boolean is702xDevice = CapControlUtils.is702xDevice(cbcPaoObject.getPaoType().getDeviceTypeId());
        boolean is701xDevice = CapControlUtils.is701xDevice(cbcPaoObject);
        boolean allowFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        
        List<CommandType> commands = Lists.newArrayList();
        if (isClosed) {
            commands.add(CommandType.CONFIRM_CLOSE);
        } else {
            commands.add(CommandType.CONFIRM_OPEN);
        }
        commands.add(CommandType.SEND_OPEN_CAPBANK);
        commands.add(CommandType.SEND_CLOSE_CAPBANK);
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

    @RequestMapping //TODO MOVE
    public String regulatorPointList(ModelMap model, int regulatorId) {
        String regulatorName = paoDao.getYukonPAOName(regulatorId);
        List<VoltageRegulatorPointMapping> pointMappings = voltageRegulatorService.getPointMappings(regulatorId);
        Collections.sort(pointMappings);
        model.addAttribute("mappings", pointMappings);
        model.addAttribute("regulatorName", regulatorName);
        
        return "tier/popupmenu/regulatorPointList.jsp";
    }

    @RequestMapping
    public String localControl(ModelMap model, int id, LiteYukonUser user) {
        StreamableCapObject capObject = cache.getCapControlPAO(id);
        
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

    @RequestMapping
    public String capBankState(ModelMap model, int id, LiteYukonUser user) {
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
        
        LiteState[] states = CapControlUtils.getCBCStateNames();
        model.addAttribute("states", states);

        return "tier/popupmenu/menu.jsp";
    }

    @RequestMapping
    public String resetBankOpCount(ModelMap model, int bankId, LiteYukonUser user) {
        model.addAttribute("title", cache.getCapBankDevice(bankId).getCcName());
        model.addAttribute("bankId", bankId);
        return "tier/popupmenu/resetBankOpCountMenu.jsp";
    }
    
    @RequestMapping
    public String opStateChange(ModelMap model, int bankId, LiteYukonUser user) {
        CapBankDevice capBank = cache.getCapBankDevice(bankId);
        String reason = capControlCommentService.getReason(bankId, CommentAction.STANDALONE_REASON, CapControlType.CAPBANK);
        BankOpState currentState = BankOpState.getStateByName(capBank.getOperationalState());
        model.addAttribute("bankId", bankId);
        
        String paoName = capBank.getCcName();
        model.addAttribute("title", paoName);
        
        List<String> comments = capControlCommentService.getLastTenCommentsForActionAndType(bankId, CommandType.CHANGE_OP_STATE.getCommandId());
        model.addAttribute("comments", comments);
        
        model.addAttribute("changeOpStateCmdHolder", CommandType.CHANGE_OP_STATE);
        
        model.addAttribute("currentState", currentState);
        model.addAttribute("allowedOperationStates", allowedOperationStates);
        
        model.addAttribute("reason", reason);
        
        return "tier/popupmenu/opStateChangeMenu.jsp";
    }

    @RequestMapping
    public String movedBankMenu(HttpServletRequest request, ModelMap model, int id, YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        CapBankDevice capBank = cache.getCapBankDevice(id);
        
        model.addAttribute("paoId", id);
        String paoName = capBank.getCcName();
        model.addAttribute("title", paoName);
        
        model.addAttribute("assignLabel", accessor.getMessage("yukon.web.modules.capcontrol.command.assignBankHere"));
        model.addAttribute("returnLabel", accessor.getMessage(CommandType.RETURN_CAP_TO_ORIGINAL_FEEDER));
        
        return "tier/popupmenu/movedBankMenu.jsp";
    }

    @Autowired
    public void setCapControlCache(CapControlCache capControlCache) {
        this.cache = capControlCache;
    }

    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setVoltageRegulatorService(VoltageRegulatorService voltageRegulatorService) {
        this.voltageRegulatorService = voltageRegulatorService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setCapControlCommentService(CapControlCommentService capControlCommentService) {
        this.capControlCommentService = capControlCommentService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}