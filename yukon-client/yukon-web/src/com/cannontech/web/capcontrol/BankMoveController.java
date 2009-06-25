package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.util.ParamUtil;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.Feeder;

@Controller
@RequestMapping("/move/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class BankMoveController {
    
    private FilterCacheFactory cacheFactory;
    private CapControlCache capControlCache;
    private PaoDao paoDao;
    
    @RequestMapping
    public ModelAndView bankMove(HttpServletRequest request, LiteYukonUser user) {
        final ModelAndView mav = new ModelAndView();
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
        int bankId = ParamUtil.getInteger(request, "bankid");
        int subBusId = 0;
        boolean oneline = ParamUtil.getBoolean(request,"oneline",false);
        List<ViewableArea> viewableAreas = CapControlWebUtils.createViewableAreas(filterCapControlCache.getCbcAreas(), filterCapControlCache, false);
        mav.addObject("allAreas", viewableAreas);
        CapBankDevice capBank = filterCapControlCache.getCapBankDevice( new Integer(bankId) );
        
        int oldFeederId = 0;
        if( oneline ){
            subBusId = filterCapControlCache.getParentSubBusID(bankId);
        }
        if( capBank != null ){
            oldFeederId = capBank.getParentID();
        }
        mav.addObject("oneline", oneline);
        mav.addObject("bankId", bankId);
        mav.addObject("oldFeederId", oldFeederId);
        mav.addObject("subBusId", subBusId);
        
        String path = capBank.getCcName(); 
        path += " > ";
        path += paoDao.getYukonPAOName(capBank.getParentID());
        path += " > ";
        path += paoDao.getYukonPAOName(capControlCache.getFeeder(capBank.getParentID()).getParentID());
        path += " > ";
        path += paoDao.getYukonPAOName(capControlCache.getSubBus(capControlCache.getFeeder(capBank.getParentID()).getParentID()).getParentID());
        path += " > ";
        path += paoDao.getYukonPAOName(capControlCache.getSubstation(capControlCache.getSubBus(capControlCache.getFeeder(capBank.getParentID()).getParentID()).getParentID()).getParentID());
        
        mav.addObject("path", path);
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.addObject("commandId", CapControlCommand.CMD_BANK_TEMP_MOVE);
        
        mav.setViewName("move/bankMove.jsp");
        return mav;
    }
    
    @RequestMapping
    public ModelAndView feederBankInfo(HttpServletRequest request, LiteYukonUser user) {
        final ModelAndView mav = new ModelAndView();
        
        String feederid = request.getParameter("FeederID");
        int id = Integer.valueOf(feederid);
        Feeder feederobj = capControlCache.getFeeder(id);
        String feederName = feederobj.getCcName();
        List<CapBankDevice> capBankList = capControlCache.getCapBanksByFeeder(id);
        
        mav.addObject("feederName", feederName);
        mav.addObject("capBankList", capBankList);
        
        mav.setViewName("move/feederBankInfo.jsp");
        return mav;
    }
    
    @Autowired
    public void setFilterCacheFactory (FilterCacheFactory filterCacheFactory) {
        this.cacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setCapControlCache (CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
