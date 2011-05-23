package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class TierPopupMenuController extends MultiActionController {
    private CapControlCache capControlCache;
    private PaoDao paoDao;
    private RolePropertyDao rolePropertyDao;
    private CapControlCommentService capControlCommentService;
    private VoltageRegulatorService voltageRegulatorService;
    private static final CapBankOperationalState[] allowedOperationStates;
    
    static {
        allowedOperationStates  = new CapBankOperationalState[] {
                                                                 CapBankOperationalState.Fixed,
                                                                 CapBankOperationalState.StandAlone,
                                                                 CapBankOperationalState.Switched};
    }
    
    public ModelAndView specialAreaMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CCSpecialArea area = capControlCache.getCBCSpecialArea(id);
        
        boolean isDisabled = area.getCcDisableFlag();
        
        ModelAndView mav = createAreaMAV(user, area, CapControlType.SPECIAL_AREA, isDisabled);
        return mav;    
    }
    
    public ModelAndView areaMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CCArea area = capControlCache.getCBCArea(id);
        
        boolean isDisabled = area.getCcDisableFlag();

        ModelAndView mav = createAreaMAV(user, area, CapControlType.AREA, isDisabled );
        return mav;
    }
    
    public ModelAndView subStationMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final SubStation subStation = capControlCache.getSubstation(id);
        
        mav.addObject("paoId", id);
        
        String paoName = subStation.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = subStation.getCcDisableFlag();
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        mav.addObject("allowLocalControl", allowLocalControl);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        list.add(CommandHolder.CONFIRM_SUBSTATION);
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_SUBSTATION);
        } else {
            list.add(CommandHolder.DISABLE_SUBSTATION);
        }
        
        list.add(CommandHolder.RESET_OP_COUNTS);
        list.add(CommandHolder.SEND_ALL_OPEN);
        list.add(CommandHolder.SEND_ALL_CLOSED);

        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.SUBSTATION);
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;
    }
    
    public ModelAndView subBusMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final SubBus subBus = capControlCache.getSubBus(id);
        
        mav.addObject("paoId", id);
        
        String paoName = subBus.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = subBus.getCcDisableFlag();
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        boolean isVerify = subBus.getVerificationFlag();
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        mav.addObject("allowLocalControl", allowLocalControl);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        list.add(CommandHolder.CONFIRM_SUBBUS);
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_SUBBUS);
        } else {
            list.add(CommandHolder.DISABLE_SUBBUS);
        }
        
        list.add(CommandHolder.RESET_OP_COUNTS);
        list.add(CommandHolder.SEND_ALL_OPEN);
        list.add(CommandHolder.SEND_ALL_CLOSED);

        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        
        if (!isVerify) {
            list.add(CommandHolder.VERIFY_ALL_BANKS);
            list.add(CommandHolder.VERIFY_FQ_BANKS);
            list.add(CommandHolder.VERIFY_FAILED_BANKS);
            list.add(CommandHolder.VERIFY_Q_BANKS);
            list.add(CommandHolder.VERIFY_SA_BANKS);
        } else {
            list.add(CommandHolder.VERIFY_STOP);
            list.add(CommandHolder.VERIFY_EMERGENCY_STOP);
        }
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.SUBBUS);
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;
    }
    
    @Deprecated
    public ModelAndView ltcMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final int id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        
        mav.addObject("paoId", id);
        
        String paoName = paoDao.getYukonPAOName(id);
        mav.addObject("paoName", paoName);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        list.add(CommandHolder.LTC_SCAN_INTEGRITY);
        list.add(CommandHolder.LTC_REMOTE_ENABLE);
        list.add(CommandHolder.LTC_REMOTE_DISABLE);
        
        mav.addObject("list", list);
        mav.addObject("hideRecentCommands", true);
        mav.addObject("hideComments",true);
        
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;
    }
    
    public ModelAndView regulatorPointList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final int regulatorId = ServletRequestUtils.getRequiredIntParameter(request, "regulatorId");
        
        String regulatorName = paoDao.getYukonPAOName(regulatorId);
        List<VoltageRegulatorPointMapping> pointMappings = voltageRegulatorService.getPointMappings(regulatorId);
        Collections.sort(pointMappings);
        mav.addObject("mappings", pointMappings);
        mav.addObject("regulatorName", regulatorName);
        mav.setViewName("tier/popupmenu/regulatorPointList.jsp");
        return mav;
    }
    
    public ModelAndView feederMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final Feeder feeder = capControlCache.getFeeder(id);
        
        mav.addObject("paoId", id);
        
        String paoName = feeder.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = feeder.getCcDisableFlag();
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        mav.addObject("allowLocalControl", allowLocalControl);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        list.add(CommandHolder.CONFIRM_FEEDER);
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_FEEDER);
        } else {
            list.add(CommandHolder.DISABLE_FEEDER);
        }
        
        list.add(CommandHolder.RESET_OP_COUNTS);
        list.add(CommandHolder.SEND_ALL_OPEN);
        list.add(CommandHolder.SEND_ALL_CLOSED);
        
        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.FEEDER);
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;
    }
    
    public ModelAndView capBankMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        final int cbcDeviceId = capBank.getControlDeviceID();
        final LiteYukonPAObject cbcPaoObject = paoDao.getLiteYukonPAO(cbcDeviceId);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isClosed = CapBankDevice.isInAnyCloseState(capBank);
        boolean isTwoWay = CBCUtils.isTwoWay(cbcPaoObject);
        boolean is702xDevice = CBCUtils.is702xDevice(cbcPaoObject.getPaoType().getDeviceTypeId());
        boolean is701xDevice = CBCUtils.is701xDevice(cbcPaoObject);
        boolean allowFlip = rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_FLIP_COMMAND, user);
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        if (isClosed) {
            list.add(CommandHolder.CBC_CONFIRM_CLOSE);
        } else {
            list.add(CommandHolder.CBC_CONFIRM_OPEN);
        }
        list.add(CommandHolder.CBC_OPEN);
        list.add(CommandHolder.CBC_CLOSE);

        if (isTwoWay) {
            list.add(CommandHolder.CBC_SCAN_2WAY);
        }
        
        list.add(CommandHolder.CBC_TIMESYNC);

        if (is702xDevice) {
            mav.addObject("allowLocalControl", allowLocalControl);
            mav.addObject("localControlTypeCBC", true);
        } else if( allowLocalControl ) {
            list.add(CommandHolder.CBC_ENABLE_OVUV);
            list.add(CommandHolder.CBC_DISABLE_OVUV);
        }
        
        if (allowFlip && is701xDevice) {
            list.add(CommandHolder.CBC_FLIP);
        }
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;
    }
    
    public ModelAndView localControlMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final int id = ServletRequestUtils.getRequiredIntParameter(request,"id");
        final boolean capBankType = ServletRequestUtils.getBooleanParameter(request,"capBankType",false);
        final String controlType = ServletRequestUtils.getRequiredStringParameter(request, "controlType");
         
        final StreamableCapObject capObject = capControlCache.getCapControlPAO(id);
        
        Validate.notNull(capObject);
        
        mav.addObject("paoId", id);
        
        String paoName = capObject.getCcName();
        mav.addObject("paoName", paoName);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        if (capBankType) {
            list.add(CommandHolder.CBC_ENABLE_OVUV);
            list.add(CommandHolder.CBC_DISABLE_OVUV);
            list.add(CommandHolder.CBC_ENABLE_TEMPCONTROL);
            list.add(CommandHolder.CBC_DISABLE_TEMPCONTROL);
            list.add(CommandHolder.CBC_ENABLE_VARCONTROL);
            list.add(CommandHolder.CBC_DISABLE_VARCONTROL);
            list.add(CommandHolder.CBC_ENABLE_TIMECONTROL);
            list.add(CommandHolder.CBC_DISABLE_TIMECONTROL);
        } else {
            list.add(CommandHolder.SEND_ENABLE_OVUV);
            list.add(CommandHolder.SEND_DISABLE_OVUV);
            list.add(CommandHolder.SEND_ENABLE_TEMPCONTROL);
            list.add(CommandHolder.SEND_DISABLE_TEMPCONTROL);
            list.add(CommandHolder.SEND_ENABLE_VARCONTROL);
            list.add(CommandHolder.SEND_DISABLE_VARCONTROL);
            list.add(CommandHolder.SEND_ENABLE_TIMECONTROL);
            list.add(CommandHolder.SEND_DISABLE_TIMECONTROL);
        }
        
        mav.addObject("list", list);
        mav.addObject("controlType", CapControlType.valueOf(controlType));
        mav.addObject("hideRecentCommands", true);
        mav.addObject("hideComments",true);
        
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;    
    }
    
    public ModelAndView capBankSystemMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = capBank.getCcDisableFlag();
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_CAPBANK);
        } else {
            list.add(CommandHolder.DISABLE_CAPBANK);
        }
        mav.addObject("list", list);
        
        mav.addObject("resetOpsCmdHolder", CommandHolder.RESET_OP_COUNTS);
        mav.addObject("changeOpStateCmdHolder", CommandHolder.OPERATIONAL_STATECHANGE);

        LiteState[] states = CBCUtils.getCBCStateNames();
        mav.addObject("states", states);

        mav.addObject("isCapBankSystemMenu", true);
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;    
    }
    
    public ModelAndView opStateChangeMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(paoId);
        String operationalStateReason = capControlCommentService.getReason(paoId, CommentAction.STANDALONE_REASON, CapControlType.CAPBANK);
        CapBankOperationalState operationalState = CapBankOperationalState.valueOf(capBank.getOperationalState());
        mav.addObject("paoId", paoId);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        
        List<String> comments = capControlCommentService.getLastTenCommentsForActionAndType(paoId, CapControlCommand.OPERATIONAL_STATECHANGE);
        mav.addObject("comments", comments);
        
        mav.addObject("changeOpStateCmdHolder", CommandHolder.OPERATIONAL_STATECHANGE);
        
        mav.addObject("operationalState", operationalState);
        mav.addObject("allowedOperationStates", allowedOperationStates);
        
        mav.addObject("operationalStateReason", operationalStateReason);
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("tier/popupmenu/opStateChangeMenu.jsp");
        return mav;    
    }
    
    public ModelAndView capBankTempMoveBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        String redirectURL = ServletRequestUtils.getStringParameter(request, "redirectURL", "/spring/capcontrol/tier/areas");
        redirectURL = ServletUtil.createSafeRedirectUrl(request, redirectURL);
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        mav.addObject("redirectURL", redirectURL);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        int moveBackCmdId = CapControlCommand.RETURN_BANK_TO_FEEDER;
        int assignHereCmdId = CapControlCommand.CMD_BANK_TEMP_MOVE;
        mav.addObject("moveBackCmdId", moveBackCmdId);
        mav.addObject("assignHereCmdId", assignHereCmdId);
        
        String moveBack = "Temp Move Back";
        String assignHere = "Assign Bank Here";
        mav.addObject("moveBack", moveBack);
        mav.addObject("assignHere", assignHere);
        
        String opts = "[" + capBank.getParentID() + ", " + capBank.getParentID()
            + ", " + capBank.getCloseOrder() + ", " + capBank.getCloseOrder()
            + ", " + capBank.getTripOrder() + ", " + 1.0 + "]";
        mav.addObject("opts", opts);
        
        mav.setViewName("tier/popupmenu/tempMoveMenu.jsp");
        return mav;
    }
    
    public ModelAndView reasonMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer paoId = ServletRequestUtils.getRequiredIntParameter(request, "paoId");
        final Integer cmdId = ServletRequestUtils.getRequiredIntParameter(request, "cmdId");
        final String commandName = ServletRequestUtils.getRequiredStringParameter(request, "commandName");
        final String controlType = ServletRequestUtils.getRequiredStringParameter(request, "controlType");
        
        final StreamableCapObject capObject = capControlCache.getCapControlPAO(paoId);
        String paoName = capObject.getCcName();
        mav.addObject("paoName", paoName);

        mav.addObject("paoId", paoId);
        mav.addObject("cmdId", cmdId);
        mav.addObject("commandName", commandName);
        mav.addObject("controlType", controlType);
        
        List<String> comments = capControlCommentService.getLastTenCommentsForActionAndType(paoId, cmdId);
        mav.addObject("comments", comments);
        
        mav.setViewName("tier/popupmenu/reasonMenu.jsp");
        return mav;
    }
    
    private ModelAndView createAreaMAV(LiteYukonUser user, StreamableCapObject capObject, 
                                       CapControlType controlType, boolean isDisabled) {
        
        final ModelAndView mav = new ModelAndView();
        
        int paoId = capObject.getCcId();
        mav.addObject("paoId", paoId);
        
        String paoName = capObject.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean allowLocalControl = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_ALLOW_OVUV, user);
        boolean allowAddComments = rolePropertyDao.checkProperty(YukonRoleProperty.ADD_COMMENTS, user);
        mav.addObject("allowAddComments", allowAddComments);
        mav.addObject("allowLocalControl", allowLocalControl);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        list.add(CommandHolder.CONFIRM_AREA);
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_AREA);
        } else {    
            list.add(CommandHolder.DISABLE_AREA);
        }
        
        list.add(CommandHolder.SEND_ALL_OPEN);
        list.add(CommandHolder.SEND_ALL_CLOSED);
        
        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        list.add(CommandHolder.VERIFY_EMERGENCY_STOP);
        mav.addObject("list", list);
        
        mav.addObject("controlType", controlType);
        mav.setViewName("tier/popupmenu/menu.jsp");
        return mav;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
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
}