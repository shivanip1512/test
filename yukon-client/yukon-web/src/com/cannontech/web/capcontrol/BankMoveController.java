package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ParamUtil;
import com.cannontech.web.capcontrol.models.MovedBank;
import com.cannontech.web.capcontrol.models.NavigableArea;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlCommand;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

@Controller
@RequestMapping("/move/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class BankMoveController {
    
    private FilterCacheFactory cacheFactory;
    private RolePropertyDao rolePropertyDao;
    
    @RequestMapping
    public ModelAndView bankMove(HttpServletRequest request, LiteYukonUser user) {
        final ModelAndView mav = new ModelAndView();
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
        int bankId = ParamUtil.getInteger(request, "bankid");
        int subBusId = 0;
        boolean oneline = ParamUtil.getBoolean(request,"oneline",false);
        List<NavigableArea> navigableAreas = CapControlWebUtils.buildSimpleHierarchy();
        mav.addObject("allAreas", navigableAreas);
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
        
        Feeder feeder = filterCapControlCache.getFeeder(capBank.getParentID()); 
        SubBus subBus = filterCapControlCache.getSubBus(feeder.getParentID());
        SubStation substation = filterCapControlCache.getSubstation(subBus.getParentID());
        StreamableCapObject area = filterCapControlCache.getArea(substation.getParentID());
        
        String path = capBank.getCcName(); 
        path += " > ";
        path += feeder.getCcName();
        path += " > ";
        path += subBus.getCcName();
        path += " > ";
        path += substation.getCcName();
        path += " > ";
        path += area.getCcName();
        
        mav.addObject("path", path);
        mav.addObject("controlType", CapControlType.CAPBANK);
        mav.addObject("commandId", CapControlCommand.CMD_BANK_TEMP_MOVE);
        
        mav.setViewName("move/bankMove.jsp");
        return mav;
    }
    
    @RequestMapping
    public ModelAndView feederBankInfo(HttpServletRequest request, LiteYukonUser user) {
        final ModelAndView mav = new ModelAndView();
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
        String feederid = request.getParameter("FeederID");
        int id = Integer.valueOf(feederid);
        Feeder feederobj = filterCapControlCache.getFeeder(id);
        String feederName = feederobj.getCcName();
        List<CapBankDevice> capBankList = filterCapControlCache.getCapBanksByFeeder(id);
        
        mav.addObject("feederName", feederName);
        mav.addObject("capBankList", capBankList);
        
        mav.setViewName("move/feederBankInfo.jsp");
        return mav;
    }
    
    @RequestMapping
    public ModelAndView movedCapBanks(HttpServletRequest request) {
        final ModelAndView mav = new ModelAndView();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        final CBCDisplay cbcDisplay = new CBCDisplay(userContext);
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(userContext.getYukonUser());
        String popupEvent = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.POPUP_APPEAR_STYLE, userContext.getYukonUser());
        if (popupEvent == null) {
            popupEvent = "onclick";
        }
        mav.addObject("popupEvent", popupEvent);
        
        List<CCArea> areas = filterCapControlCache.getCbcAreas();
        List<MovedBank> movedCaps = new ArrayList<MovedBank>();   
        for (CCArea area : areas) {
            List<CapBankDevice> capBanks = filterCapControlCache.getCapBanksByArea(area.getPaoId());
            for (CapBankDevice capBank : capBanks) {
                if (capBank.isBankMoved()) {
                    MovedBank movedBank = new MovedBank(capBank);
                    movedBank.setCurrentFeederName(cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_PARENT_COLUMN).toString());
                    movedBank.setOriginalFeederName(filterCapControlCache.getFeeder(capBank.getOrigFeederID()).getCcName());
                    movedCaps.add(movedBank);
                }
            }
        }
        
        int itemsPerPage = 25;
        int currentPage = 1;
        
        String temp = request.getParameter("itemsPerPage");
        if(!StringUtils.isEmpty(temp)) itemsPerPage = Integer.valueOf(temp);
        
        temp = request.getParameter("page");
        if(!StringUtils.isEmpty(temp)) currentPage = Integer.valueOf(temp);
        
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = movedCaps.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        movedCaps = movedCaps.subList(startIndex, toIndex);
        
        SearchResult<MovedBank> result = new SearchResult<MovedBank>();
        result.setResultList(movedCaps);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        mav.addObject("searchResult", result);
        mav.addObject("itemList", result.getResultList());
        
        mav.addObject("isFiltered", false);
        
        
        mav.addObject("movedCaps", movedCaps);
        
        mav.setViewName("move/movedCapBanks.jsp");
        return mav;
    }
    
    @Autowired
    public void setFilterCacheFactory (FilterCacheFactory filterCacheFactory) {
        this.cacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}