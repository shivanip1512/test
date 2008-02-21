package com.cannontech.web.cbc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public class TierPopupMenuController extends MultiActionController {
    private CapControlCache capControlCache;
    private PaoDao paoDao;
    private AuthDao authDao;
    
    public ModelAndView specialAreaMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CBCSpecialArea area = capControlCache.getCBCSpecialArea(id);
        
        boolean isDisabled = area.getCcDisableFlag();
        boolean isDisabledOVUV = area.getOvUvDisabledFlag();
        String methodName = "execute_SpecialAreaCommand";
        
        ModelAndView mav = createAreaMAV(user, area, methodName, isDisabled, isDisabledOVUV);
        return mav;    
    }
    
    public ModelAndView areaMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        
        final CBCArea area = capControlCache.getCBCArea(id);
        
        boolean isDisabled = area.getCcDisableFlag();
        boolean isDisabledOVUV = area.getOvUvDisabledFlag();
        String methodName = "execute_SubAreaCommand";
        ModelAndView mav = createAreaMAV(user, area, methodName, isDisabled, isDisabledOVUV );
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

        list.add(CommandHolder.SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("executeMethodName", "executeSubstationCommand");
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

        list.add(CommandHolder.SCAN_2WAY);
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
        
        mav.addObject("executeMethodName", "executeSubCommand");
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
        
        list.add(CommandHolder.SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("executeMethodName", "executeFeederCommand");
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
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        if (isClosed) {
            list.add(CommandHolder.CAPBANK_CONFIRM_CLOSE);
        } else {
            list.add(CommandHolder.CAPBANK_CONFIRM_OPEN);
        }
        list.add(CommandHolder.CAPBANK_OPEN);
        list.add(CommandHolder.CAPBANK_CLOSE);

        if (isTwoWay) {
            list.add(CommandHolder.CAPBANK_SCAN_2WAY);
        }
        
        list.add(CommandHolder.CAPBANK_TIMESYNC);
        
        if (allowOVUV) {
            list.add(CommandHolder.CAPBANK_ENABLE_OVUV);
            list.add(CommandHolder.CAPBANK_DISABLE_OVUV);
        }
        
        if (allowFlip && is701xDevice) {
            list.add(CommandHolder.CAPBANK_FLIP);
        }
        mav.addObject("list", list);
        
        mav.addObject("executeMethodName", "executeCapBankCommand");
        mav.setViewName("tier/popupmenu/menu");
        return mav;
    }
    
    public ModelAndView capBankSystemMenu(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getRequiredIntParameter(request, "id");
        
        final CapBankDevice capBank = capControlCache.getCapBankDevice(id);
        
        mav.addObject("paoId", id);
        
        String paoName = capBank.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean isDisabled = capBank.getCcDisableFlag();
        
        final List<CommandHolder> list = new ArrayList<CommandHolder>();
        
        
        if (isDisabled) {
            list.add(CommandHolder.ENABLE_CAPBANK);
        } else {
            list.add(CommandHolder.DISABLE_CAPBANK);
        }
        mav.addObject("list", list);
        
        mav.addObject("resetOpsCmdHolder", CommandHolder.RESET_OP_COUNTS);

        LiteState[] states = CBCUtils.getCBCStateNames();
        mav.addObject("states", states);

        mav.addObject("isCapBankSystemMenu", true);
        mav.addObject("executeMethodName", "executeCapBankCommand");
        mav.setViewName("tier/popupmenu/menu");
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
        
        int cmdId = CBCCommand.RETURN_BANK_TO_FEEDER;
        mav.addObject("cmdId", cmdId);
        
        String displayName = "Temp Move Back";
        mav.addObject("displayName", displayName);
        
        mav.setViewName("tier/popupmenu/tempMoveMenu");
        return mav;
    }
    
    private ModelAndView createAreaMAV(LiteYukonUser user, StreamableCapObject capObject, String executeMethodName,
            boolean isDisabled, boolean isDisabledOVUV) {
        
        final ModelAndView mav = new ModelAndView();
        
        int paoId = capObject.getCcId();
        mav.addObject("paoId", paoId);
        
        String paoName = capObject.getCcName();
        mav.addObject("paoName", paoName);
        
        boolean allowOVUV = authDao.checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
        
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
        
        list.add(CommandHolder.SCAN_2WAY);
        list.add(CommandHolder.SEND_ALL_TIMESYNC);
        mav.addObject("list", list);
        
        mav.addObject("executeMethodName", executeMethodName);
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
