package com.cannontech.web.capcontrol;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SpecialArea;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.models.CommandOption;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/menu/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class MenuController {
    @Autowired private CapControlCache cache;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlCommentService ccCommentService;
    @Autowired private AttributeService attributeService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ServerDatabaseCache dbcache;
    @Autowired private ConfigurationSource configurationSource;
    
    private final static BankOpState[] allowedOperationStates =
            new BankOpState[] {BankOpState.FIXED,BankOpState.STANDALONE,BankOpState.SWITCHED};

    @GetMapping("commandMenu")
    public String commandMenu(ModelMap model, int id, LiteYukonUser user) {
        StreamableCapObject object = cache.getObject(id);
        model.addAttribute("paoId", object.getCcId());
        String paoName = object.getCcName();
        
        // Need to escape this here AND in the jsp since jquery ui dialog call
        // will unescape it. Double escaping solves this.
        model.addAttribute("paoName", StringEscapeUtils.escapeXml11(paoName));
        
        model.addAttribute("showChangeOpState", false);
        model.addAttribute("showLocalControl", rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user));
        
        if (object instanceof Area || object instanceof SpecialArea) {
            setupAreaMenuModel(model, object, user);
        } else if (object instanceof SubStation) {
            setupSubstationModel(model, (SubStation)object, user);
        } else if (object instanceof SubBus) {
            setupSubBusModel(model, (SubBus)object, user);
        } else if (object instanceof Feeder) {
            setupFeederModel(model, (Feeder)object, user);
        } else if (object instanceof CapBankDevice) {
            setupCapBankModel(model, user, (CapBankDevice)object);
        }
        
        return "tier/popupmenu/menu.jsp";
    }
    
    private boolean hasYukonActionsPermission(YukonRoleProperty roleProperty, LiteYukonUser user) {
        return rolePropertyDao.checkAnyLevel(roleProperty, CapControlCommandsAccessLevel.getYukonActionsLevels(), user);
/*        if (rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS, user) || 
                rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS, user) ||
                rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.YUKON_ACTIONS_ONLY, user)) {
            return true;
        }
        return false;*/
    }
    
    private boolean hasNonOperationCommandsPermission(YukonRoleProperty roleProperty, LiteYukonUser user) {
        return rolePropertyDao.checkAnyLevel(roleProperty, CapControlCommandsAccessLevel.getNonOperationLevels(), user);
/*
        if (rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS, user) || 
                rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS, user) ||
                rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS, user) ||
                rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS, user)) {
            return true;
        }
        return false;*/
    }
    
    private boolean hasFieldOperationCommandsPermission(YukonRoleProperty roleProperty, LiteYukonUser user) {
        return rolePropertyDao.checkAnyLevel(roleProperty, CapControlCommandsAccessLevel.getFieldOperationLevels(), user);

/*        if (rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS, user) || 
                rolePropertyDao.checkLevel(roleProperty, CapControlCommandsAccessLevel.ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS, user)) {
            return true;
        }
        return false;*/
    }
    
    private void setupAreaMenuModel(ModelMap model, StreamableCapObject area, LiteYukonUser user) {
        List<CommandOption> yukonActions = Lists.newArrayList();
        List<CommandOption> nonOperationCommands = Lists.newArrayList();
        List<CommandOption> fieldOperationCommands = Lists.newArrayList();
        
        //check if user has access to Area Yukon Actions
        if (hasYukonActionsPermission(YukonRoleProperty.AREA_COMMANDS_AND_ACTIONS, user)) {
            if (area.getCcDisableFlag()) {
                yukonActions.add(new CommandOption(CommandType.ENABLE_AREA));
            } else {
                yukonActions.add(new CommandOption(CommandType.DISABLE_AREA));
            }
            yukonActions.add(new CommandOption(CommandType.SEND_SYNC_CBC_CAPBANK_STATE));
        }

        //check if user has access to Area Non Operation Commands
        if (hasNonOperationCommandsPermission(YukonRoleProperty.AREA_COMMANDS_AND_ACTIONS, user)) {
            nonOperationCommands.add(new CommandOption(CommandType.SEND_SCAN_2WAY_DEVICE));
            nonOperationCommands.add(new CommandOption(CommandType.SEND_TIME_SYNC));
        }

        //check if user has access to Area Field Operation Commands
        if (hasFieldOperationCommandsPermission(YukonRoleProperty.AREA_COMMANDS_AND_ACTIONS, user)) {
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_OPEN_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_CLOSE_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.CONFIRM_AREA));
            fieldOperationCommands.add(new CommandOption(CommandType.EMERGENCY_VERIFICATION_STOP));
        }

        model.addAttribute("capControlType", CapControlType.AREA.getDisplayValue());
        model.addAttribute("yukonActions", yukonActions);
        model.addAttribute("nonOperationCommands", nonOperationCommands);
        model.addAttribute("fieldOperationCommands", fieldOperationCommands);
    }

    private void setupSubstationModel(ModelMap model, SubStation substation, LiteYukonUser user) {
        List<CommandOption> yukonActions = Lists.newArrayList();
        List<CommandOption> nonOperationCommands = Lists.newArrayList();
        List<CommandOption> fieldOperationCommands = Lists.newArrayList();
        
        //check if user has access to Substation Yukon Actions
        if (hasYukonActionsPermission(YukonRoleProperty.SUBSTATION_COMMANDS_AND_ACTIONS, user)) {
            if (substation.getCcDisableFlag()) {
                yukonActions.add(new CommandOption(CommandType.ENABLE_SUBSTATION));
            } else {
                yukonActions.add(new CommandOption(CommandType.DISABLE_SUBSTATION));
            }
            yukonActions.add(new CommandOption(CommandType.RESET_DAILY_OPERATIONS));
            yukonActions.add(new CommandOption(CommandType.SEND_SYNC_CBC_CAPBANK_STATE));
        }

        //check if user has access to Substation Non Operation Commands
        if (hasNonOperationCommandsPermission(YukonRoleProperty.SUBSTATION_COMMANDS_AND_ACTIONS, user)) {
            nonOperationCommands.add(new CommandOption(CommandType.SEND_SCAN_2WAY_DEVICE));
            nonOperationCommands.add(new CommandOption(CommandType.SEND_TIME_SYNC));
        }

        //check if user has access to Substation Field Operation Commands
        if (hasFieldOperationCommandsPermission(YukonRoleProperty.SUBSTATION_COMMANDS_AND_ACTIONS, user)) {
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_OPEN_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_CLOSE_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.CONFIRM_SUBSTATION));
        }

        model.addAttribute("capControlType", CapControlType.SUBSTATION.getDisplayValue());
        model.addAttribute("yukonActions", yukonActions);
        model.addAttribute("nonOperationCommands", nonOperationCommands);
        model.addAttribute("fieldOperationCommands", fieldOperationCommands);
    }

    private void setupSubBusModel(ModelMap model, SubBus subBus, LiteYukonUser user) {
        List<CommandOption> yukonActions = Lists.newArrayList();
        List<CommandOption> nonOperationCommands = Lists.newArrayList();
        List<CommandOption> fieldOperationCommands = Lists.newArrayList();
        
        //check if user has access to SubBus Yukon Actions
        if (hasYukonActionsPermission(YukonRoleProperty.SUBBUS_COMMANDS_AND_ACTIONS, user)) {
            if (subBus.getCcDisableFlag()) {
                //disable the ability to re-enable a bus during verification
                String disabledTextKey = "yukon.web.modules.capcontrol.bus.verificationStatusDisabled";
                yukonActions.add(new CommandOption(CommandType.ENABLE_SUBSTATION_BUS, !subBus.getVerificationFlag(), disabledTextKey));
            } else {
                yukonActions.add(new CommandOption(CommandType.DISABLE_SUBSTATION_BUS));
            }
            yukonActions.add(new CommandOption(CommandType.RESET_DAILY_OPERATIONS));
            yukonActions.add(new CommandOption(CommandType.SEND_SYNC_CBC_CAPBANK_STATE));
        }

        //check if user has access to SubBus Non Operation Commands
        if (hasNonOperationCommandsPermission(YukonRoleProperty.SUBBUS_COMMANDS_AND_ACTIONS, user)) {
            nonOperationCommands.add(new CommandOption(CommandType.SEND_SCAN_2WAY_DEVICE));
            nonOperationCommands.add(new CommandOption(CommandType.SEND_TIME_SYNC));
        }
        
        //check if user has access to SubBus Field Operation Commands
        if (hasFieldOperationCommandsPermission(YukonRoleProperty.SUBBUS_COMMANDS_AND_ACTIONS, user)) {
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_OPEN_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_CLOSE_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.CONFIRM_SUBSTATION_BUS));

            if (!subBus.getVerificationFlag()) {
    
                /*
                 * You shouldn't be able to Initiated any "Verify ..." commands from the subbus level
                 * without a strategy attached. They require a strategy and rely on knowing VAR change
                 * to determine the outcome of a control sent.
                 */
    
                if (CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(subBus)) {
                    fieldOperationCommands.add(new CommandOption(CommandType.VERIFY_ALL_BANKS));
                    fieldOperationCommands.add(new CommandOption(CommandType.VERIFY_FQ_BANKS));
                    fieldOperationCommands.add(new CommandOption(CommandType.VERIFY_FAILED_BANKS));
                    fieldOperationCommands.add(new CommandOption(CommandType.VERIFY_Q_BANKS));
                    fieldOperationCommands.add(new CommandOption(CommandType.VERIFY_SA_BANKS));
                }
            } else {
                fieldOperationCommands.add(new CommandOption(CommandType.STOP_VERIFICATION));
                fieldOperationCommands.add(new CommandOption(CommandType.EMERGENCY_VERIFICATION_STOP));
            }
        }
        
        model.addAttribute("capControlType", CapControlType.SUBBUS.getDisplayValue());
        model.addAttribute("yukonActions", yukonActions);
        model.addAttribute("nonOperationCommands", nonOperationCommands);
        model.addAttribute("fieldOperationCommands", fieldOperationCommands);    
    }
    
    private void setupFeederModel(ModelMap model, Feeder feeder, LiteYukonUser user) {
        List<CommandOption> yukonActions = Lists.newArrayList();
        List<CommandOption> nonOperationCommands = Lists.newArrayList();
        List<CommandOption> fieldOperationCommands = Lists.newArrayList();

        //check if user has access to Feeder Yukon Actions
        if (hasYukonActionsPermission(YukonRoleProperty.FEEDER_COMMANDS_AND_ACTIONS, user)) {
            if (feeder.getCcDisableFlag()) {
                yukonActions.add(new CommandOption(CommandType.ENABLE_FEEDER));
            } else {
                yukonActions.add(new CommandOption(CommandType.DISABLE_FEEDER));
            }
            yukonActions.add(new CommandOption(CommandType.RESET_DAILY_OPERATIONS));
            yukonActions.add(new CommandOption(CommandType.SEND_SYNC_CBC_CAPBANK_STATE));
        }

        //check if user has access to Feeder Non Operation Commands
        if (hasNonOperationCommandsPermission(YukonRoleProperty.FEEDER_COMMANDS_AND_ACTIONS, user)) {
            nonOperationCommands.add(new CommandOption(CommandType.SEND_SCAN_2WAY_DEVICE));
            nonOperationCommands.add(new CommandOption(CommandType.SEND_TIME_SYNC));
        }

        //check if user has access to Feeder Field Operation Commands
        if (hasFieldOperationCommandsPermission(YukonRoleProperty.FEEDER_COMMANDS_AND_ACTIONS, user)) {
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_OPEN_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.SEND_CLOSE_ALL_CAPBANKS));
            fieldOperationCommands.add(new CommandOption(CommandType.CONFIRM_FEEDER));
        }
        
        model.addAttribute("capControlType", CapControlType.FEEDER.getDisplayValue());
        model.addAttribute("yukonActions", yukonActions);
        model.addAttribute("nonOperationCommands", nonOperationCommands);
        model.addAttribute("fieldOperationCommands", fieldOperationCommands);    
    }

    private void setupCapBankModel(ModelMap model, LiteYukonUser user, CapBankDevice capBank) {
        List<CommandOption> yukonActions = Lists.newArrayList();
        List<CommandOption> nonOperationCommands = Lists.newArrayList();
        List<CommandOption> fieldOperationCommands = Lists.newArrayList();

        int cbcDeviceId = capBank.getControlDeviceID();
        LiteYukonPAObject cbcPaoObject = paoDao.getLiteYukonPAO(cbcDeviceId);
        boolean isClosed = CapBankDevice.isInAnyCloseState(capBank);
        boolean isTwoWay = paoDefinitionDao.isTagSupported(cbcPaoObject.getPaoType(), PaoTag.TWO_WAY_DEVICE);
        boolean isLogical = cbcPaoObject.getPaoType().isLogicalCBC();
        boolean is702xDevice = DeviceTypesFuncs.is702xDevice(cbcPaoObject.getPaoType());
        boolean is701xDevice = DeviceTypesFuncs.is701xDevice(cbcPaoObject.getPaoType());
        boolean allowFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        
        //check if user has access to CapBank Yukon Actions
        if (hasYukonActionsPermission(YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, user)) {
            yukonActions.add(new CommandOption(CommandType.SEND_SYNC_CBC_CAPBANK_STATE));
        }
        
        //check if user has access to CapBank Non Operation Commands
        if (hasNonOperationCommandsPermission(YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, user)) {
            if (isTwoWay || isLogical) {
                nonOperationCommands.add(new CommandOption(CommandType.SEND_SCAN_2WAY_DEVICE));
            }
            nonOperationCommands.add(new CommandOption(CommandType.SEND_TIME_SYNC));
        }
        
        //check if user has access to CapBank Field Operation Commands
        if (hasFieldOperationCommandsPermission(YukonRoleProperty.CAPBANK_COMMANDS_AND_ACTIONS, user)) {
        
            boolean strategyAttachedToSubBus = true;
            try {
                SubBus parentSubBus = cache.getParentSubBus(capBank.getCcId());
                strategyAttachedToSubBus = CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(parentSubBus);
            } catch (NotFoundException nfe) {/* no parent subbus assigned - that's fine, move along */}
            if (strategyAttachedToSubBus) {
                fieldOperationCommands.add(new CommandOption(CommandType.SEND_OPEN_CAPBANK));
                fieldOperationCommands.add(new CommandOption(CommandType.SEND_CLOSE_CAPBANK));
            }
    
            if (isClosed) {
                fieldOperationCommands.add(new CommandOption(CommandType.CONFIRM_CLOSE));
            } else {
                fieldOperationCommands.add(new CommandOption(CommandType.CONFIRM_OPEN));
            }
    
            if (is702xDevice) {
                model.addAttribute("allowLocalControl", allowLocalControl);
                model.addAttribute("localControlTypeCBC", true);
            }
            if (allowLocalControl && !is702xDevice) {
                if (cbcPaoObject != null && (cbcPaoObject.getPaoIdentifier().getPaoType() == PaoType.CBC_8020
                    || cbcPaoObject.getPaoIdentifier().getPaoType() == PaoType.CBC_8024)) {
                    Set<Attribute> attributes = Sets.newHashSet(BuiltInAttribute.ENABLE_OVUV_CONTROL);
                    Set<Attribute> allExistingAttributes = attributeService.getExistingAttributes(cbcPaoObject, attributes);
                    String disabledTextKey = "yukon.web.modules.capcontrol.menu.pointNotFound";
                    fieldOperationCommands.add(new CommandOption(CommandType.SEND_ENABLE_OVUV,
                        allExistingAttributes.contains(BuiltInAttribute.ENABLE_OVUV_CONTROL), disabledTextKey));
                    fieldOperationCommands.add(new CommandOption(CommandType.SEND_DISABLE_OVUV,
                        allExistingAttributes.contains(BuiltInAttribute.ENABLE_OVUV_CONTROL), disabledTextKey));
                }
            }
            if (allowFlip && is701xDevice) {
                fieldOperationCommands.add(new CommandOption(CommandType.FLIP_7010_CAPBANK));
            }
            String disabledTextKey = "yukon.web.modules.capcontrol.menu.bankInLocalControl";
            fieldOperationCommands.add(new CommandOption(CommandType.VERIFY_SELECTED_BANK, !capBank.getLocalControlFlag(), disabledTextKey));
        }
        
        model.addAttribute("capControlType", CapControlType.CAPBANK.getDisplayValue());
        model.addAttribute("yukonActions", yukonActions);
        model.addAttribute("nonOperationCommands", nonOperationCommands);
        model.addAttribute("fieldOperationCommands", fieldOperationCommands);   
    }

    @GetMapping("localControl")
    public String localControl(ModelMap model, int id) {
        YukonPao pao = null;
        StreamableCapObject capObject = cache.getCapControlPao(id);

        Validate.notNull(capObject);

        model.addAttribute("paoId", id);
        String paoName = capObject.getCcName();
        model.addAttribute("paoName", paoName);

        CapControlType ccType = CapControlType.getCapControlType(capObject.getCcType());
        if (ccType == CapControlType.CAPBANK) {
            CapBankDevice capBankDevice = (CapBankDevice) capObject;
            pao = dbcache.getAllPaosMap().get(capBankDevice.getControlDeviceID());
        }
        boolean isEnabledOvUv = true;
        boolean isEnabledTemperature = true;
        boolean isEnabledVar = true;
        boolean isEnabledTime = true;

        List<CommandOption> commands = Lists.newArrayList();

        if (pao != null && (pao.getPaoIdentifier().getPaoType() == PaoType.CBC_8020
            || pao.getPaoIdentifier().getPaoType() == PaoType.CBC_8024)) {
            Set<Attribute> attributes =
                Sets.newHashSet(BuiltInAttribute.ENABLE_TEMPERATURE_CONTROL, BuiltInAttribute.ENABLE_VAR_CONTROL,
                    BuiltInAttribute.ENABLE_OVUV_CONTROL, BuiltInAttribute.ENABLE_TIME_CONTROL);
            Set<Attribute> allExistingAttributes = attributeService.getExistingAttributes(pao, attributes);
            isEnabledOvUv = allExistingAttributes.contains(BuiltInAttribute.ENABLE_OVUV_CONTROL);
            isEnabledTemperature = allExistingAttributes.contains(BuiltInAttribute.ENABLE_TEMPERATURE_CONTROL);
            isEnabledVar = allExistingAttributes.contains(BuiltInAttribute.ENABLE_VAR_CONTROL);
            isEnabledTime = allExistingAttributes.contains(BuiltInAttribute.ENABLE_TIME_CONTROL);
        }

        String disabledTextKey = "yukon.web.modules.capcontrol.menu.pointNotFound";
        commands.add(new CommandOption(CommandType.SEND_ENABLE_OVUV, isEnabledOvUv, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_DISABLE_OVUV, isEnabledOvUv, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_ENABLE_TEMPCONTROL, isEnabledTemperature, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_DISABLE_TEMPCONTROL, isEnabledTemperature, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_ENABLE_VARCONTROL, isEnabledVar, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_DISABLE_VARCONTROL, isEnabledVar, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_ENABLE_TIMECONTROL, isEnabledTime, disabledTextKey));
        commands.add(new CommandOption(CommandType.SEND_DISABLE_TIMECONTROL, isEnabledTime, disabledTextKey));

        model.addAttribute("commands", commands);

        return "tier/popupmenu/menu.jsp";
    }

    @GetMapping("capBankState")
    public String capBankState(ModelMap model, int id) {
        CapBankDevice capBank = cache.getCapBankDevice(id);
        model.addAttribute("paoId", id);
        String paoName = capBank.getCcName();
        model.addAttribute("paoName", paoName);
        List<CommandOption> commands = Lists.newArrayList();
        if (capBank.getCcDisableFlag()) {
            commands.add(new CommandOption(CommandType.ENABLE_CAPBANK));
        } else {
            commands.add(new CommandOption(CommandType.DISABLE_CAPBANK));
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

    @GetMapping("resetBankOpCount")
    public String resetBankOpCount(ModelMap model, int bankId) {
        model.addAttribute("title", cache.getCapBankDevice(bankId).getCcName());
        model.addAttribute("bankId", bankId);
        return "tier/popupmenu/resetBankOpCountMenu.jsp";
    }
    
    @GetMapping("opStateChange")
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

    @GetMapping("create")
    public String create(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String title = accessor.getMessage("yukon.web.modules.capcontrol.create.title");
        model.addAttribute("title", title);
        
        boolean usesDmvTest = MasterConfigLicenseKey.DEMAND_MEASUREMENT_VERIFICATION_ENABLED.getKey().equals(configurationSource.getString("DEMAND_MEASUREMENT_VERIFICATION_ENABLED"));
        model.addAttribute("usesDmvTest", usesDmvTest);
        
        return "tier/popupmenu/create.jsp";
    }
}
