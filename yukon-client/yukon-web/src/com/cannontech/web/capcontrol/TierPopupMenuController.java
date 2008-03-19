package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.capcontrol.CapBankOperationalState;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.service.CapControlCommentService;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CapControlType;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public class TierPopupMenuController extends MultiActionController {
    private CapControlCache capControlCache;
    private PaoDao paoDao;
    private AuthDao authDao;
    private CapControlCommentService capControlCommentService;
    private static final CapBankOperationalState[] allowedOperationStates;
    
    static {
        allowedOperationStates  = new CapBankOperationalState[] {
                                                                 CapBankOperationalState.Fixed,
                                                                 CapBankOperationalState.StandAlone,
                                                                 CapBankOperationalState.Switched};
    }
    
    public void setCapControlCommentService(
            CapControlCommentService capControlCommentService) {
        this.capControlCommentService = capControlCommentService;
    }

    public ModelAndView specialAreaMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CCSpecialArea area = capControlCache.getCBCSpecialArea(id);
        
        boolean isDisabled = area.getCcDisableFlag();
        boolean isDisabledOVUV = area.getOvUvDisabledFlag();
        
        ModelAndView mav = createAreaMAV(user, area, CapControlType.SPECIAL_AREA, isDisabled, isDisabledOVUV);
        return mav;    
    }
    
    public ModelAndView areaMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CCArea area = capControlCache.getCBCArea(id);
        
        boolean isDisabled = area.getCcDisableFlag();
        boolean isDisabledOVUV = area.getOvUvDisabledFlag();

        ModelAndView mav = createAreaMAV(user, area, CapControlType.AREA, isDisabled, isDisabledOVUV );
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
        boolean allowOVUV = authDao.checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
        mav.addObject("allowAddComments", allowAddComments);
        
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
        
        if (allowOVUV) {
            list.add(CommandHolder.SEND_ENABLE_OVUV);
            list.add(CommandHolder.SEND_DISABLE_OVUV);
        }

        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.SUBSTATION);
        mav.setViewName("tier/popupmenu/menu");
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
        boolean allowOVUV = authDao.checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
        boolean isVerify = subBus.getVerificationFlag();
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
        mav.addObject("allowAddComments", allowAddComments);
        
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

        if (allowOVUV) {
            list.add(CommandHolder.SEND_ENABLE_OVUV);
            list.add(CommandHolder.SEND_DISABLE_OVUV);
        }

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
        }
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.SUBBUS);
        mav.setViewName("tier/popupmenu/menu");
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
        boolean allowOVUV = authDao.checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
        mav.addObject("allowAddComments", allowAddComments);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_FEEDER);
        } else {
            list.add(CommandHolder.DISABLE_FEEDER);
        }
        
        list.add(CommandHolder.RESET_OP_COUNTS);
        list.add(CommandHolder.SEND_ALL_OPEN);
        list.add(CommandHolder.SEND_ALL_CLOSED);

        if (allowOVUV) {
            list.add(CommandHolder.SEND_ENABLE_OVUV);
            list.add(CommandHolder.SEND_DISABLE_OVUV);
        }
        
        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.FEEDER);
        mav.setViewName("tier/popupmenu/menu");
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
        boolean is701xDevice = CBCUtils.is701xDevice(cbcPaoObject);
        boolean allowOVUV = authDao.checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
        boolean allowFlip = authDao.checkRoleProperty(user, CBCSettingsRole.SHOW_FLIP_COMMAND);
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
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
        
        if (allowOVUV) {
            list.add(CommandHolder.CBC_ENABLE_OVUV);
            list.add(CommandHolder.CBC_DISABLE_OVUV);
        }
        
        if (allowFlip && is701xDevice) {
            list.add(CommandHolder.CBC_FLIP);
        }
        mav.addObject("list", list);
        
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("tier/popupmenu/menu");
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
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
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
        mav.setViewName("tier/popupmenu/menu");
        return mav;    
    }
    
    public ModelAndView opStateChangeMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        String operationalStateReason = capControlCommentService.getReason(id, CommentAction.STANDALONE_REASON, CapControlType.CAPBANK);
        CapBankOperationalState operationalState = CapBankOperationalState.valueOf(capBank.getOperationalState());
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
        mav.addObject("allowAddComments", allowAddComments);
        
        List<String> comments = capControlCommentService.getComments(id, 15);
        mav.addObject("comments", comments);
        
        mav.addObject("changeOpStateCmdHolder", CommandHolder.OPERATIONAL_STATECHANGE);
        
        mav.addObject("operationalState", operationalState);
        mav.addObject("allowedOperationStates", allowedOperationStates);
        
        mav.addObject("operationalStateReason", operationalStateReason);
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.setViewName("tier/popupmenu/opStateChangeMenu");
        return mav;    
    }
    
    public ModelAndView capBankTempMoveBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final String redirectURL = ServletRequestUtils.getStringParameter(request, "redirectURL", "/capcontrol/feeders.jsp");
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        mav.addObject("redirectURL", redirectURL);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        int cmdId = CapControlCommand.RETURN_BANK_TO_FEEDER;
        mav.addObject("cmdId", cmdId);
        
        String displayName = "Temp Move Back";
        mav.addObject("displayName", displayName);
        
        mav.setViewName("tier/popupmenu/tempMoveMenu");
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
        
        List<String> comments = capControlCommentService.getComments(paoId, 15);
        mav.addObject("comments", comments);
        
        mav.setViewName("tier/popupmenu/reasonMenu");
        return mav;
    }
    
    private ModelAndView createAreaMAV(LiteYukonUser user, StreamableCapObject capObject, CapControlType controlType,
            boolean isDisabled, boolean isDisabledOVUV) {
        
        final ModelAndView mav = new ModelAndView();
        
        int paoId = capObject.getCcId();
        mav.addObject("paoId", paoId);
        
        String paoName = capObject.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean allowOVUV = authDao.checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
        boolean allowAddComments = authDao.checkRoleProperty(user, CBCSettingsRole.ADD_COMMENTS);
        mav.addObject("allowAddComments", allowAddComments);
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        list.add(CommandHolder.CONFIRM_AREA);
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_AREA);
        } else {    
            list.add(CommandHolder.DISABLE_AREA);
        }
        
        list.add(CommandHolder.SEND_ALL_OPEN);
        list.add(CommandHolder.SEND_ALL_CLOSED);

        if (allowOVUV) {
            list.add(CommandHolder.SEND_ENABLE_OVUV);
            list.add(CommandHolder.SEND_DISABLE_OVUV);
        }
        
        list.add(CommandHolder.SEND_ALL_SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("controlType", controlType);
        mav.setViewName("tier/popupmenu/menu");
        return mav;
    }
    
    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
}
