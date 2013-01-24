package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.model.BankMoveBean;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.UpdaterHelper;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.models.MovedBank;
import com.cannontech.web.capcontrol.models.NavigableArea;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/move/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class BankMoveController {
    
    @Autowired private FilterCacheFactory cacheFactory;
    @Autowired private UpdaterHelper updaterHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;
    
    @RequestMapping
    public String bankMove(ModelMap model, YukonUserContext context, int bankid, Boolean oneline) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        if (oneline == null) {
            oneline = false;
        }
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(context.getYukonUser());
        int subBusId = 0;
        List<NavigableArea> navigableAreas = capControlWebUtilsService.buildSimpleHierarchy();
        model.addAttribute("allAreas", navigableAreas);
        CapBankDevice capBank = filterCapControlCache.getCapBankDevice( new Integer(bankid) );
        
        int oldFeederId = 0;
        if (oneline) {
            subBusId = filterCapControlCache.getParentSubBusID(bankid);
        }
        if (capBank != null) {
            oldFeederId = capBank.getParentID();
        }
        model.addAttribute("oneline", oneline);
        model.addAttribute("bankId", bankid);
        model.addAttribute("oldFeederId", oldFeederId);
        model.addAttribute("subBusId", subBusId);
        
        Feeder feeder = filterCapControlCache.getFeeder(capBank.getParentID()); 
        SubBus subBus = filterCapControlCache.getSubBus(feeder.getParentID());
        SubStation substation = filterCapControlCache.getSubstation(subBus.getParentID());
        StreamableCapObject area = filterCapControlCache.getArea(substation.getParentID());
        
        model.addAttribute("substationId", substation.getCcId());
        
        String path = accessor.getMessage("yukon.web.modules.capcontrol.bankMove.path"
                                          , area.getCcName()
                                          , substation.getCcName()
                                          , subBus.getCcName()
                                          , feeder.getCcName());
        
        model.addAttribute("path", path);
        model.addAttribute("bankName", capBank.getCcName());
        model.addAttribute("controlType", CapControlType.CAPBANK);
        model.addAttribute("commandId", CommandType.MOVE_BANK.getCommandId());
        
        BankMoveBean bankMoveBean = new BankMoveBean();
        bankMoveBean.setBankId(capBank.getCcId());
        bankMoveBean.setOldFeederId(capBank.getParentID());
        model.addAttribute("bankMoveBean", bankMoveBean);
        
        if (oneline) {
            return "move/onelineBankMove.jsp";
        } else {
            return "move/bankMove.jsp";
        }
    }
    
    @RequestMapping
    public String feederBankInfo(ModelMap model, LiteYukonUser user, int feederId) {
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
        Feeder feederobj = filterCapControlCache.getFeeder(feederId);
        String feederName = feederobj.getCcName();
        List<CapBankDevice> capBankList = filterCapControlCache.getCapBanksByFeeder(feederId);
        
        model.addAttribute("feederName", feederName);
        model.addAttribute("capBankList", capBankList);
        
        return "move/feederBankInfo.jsp";
    }
    
    @RequestMapping
    public String movedCapBanks(HttpServletRequest request, ModelMap model, YukonUserContext context) {
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(context.getYukonUser());
        
        List<Area> areas = filterCapControlCache.getCbcAreas();
        List<MovedBank> movedCaps = Lists.newArrayList();   
        for (Area area : areas) {
            List<CapBankDevice> capBanks = filterCapControlCache.getCapBanksByArea(area.getPaoId());
            for (CapBankDevice capBank : capBanks) {
                if (capBank.isBankMoved()) {
                    MovedBank movedBank = new MovedBank(capBank);
                    movedBank.setCurrentFeederName(updaterHelper.getCapBankValueAt(capBank, UpdaterHelper.UpdaterDataType.CB_PARENT_COLUMN, context).toString());
                    movedBank.setOriginalFeederName(filterCapControlCache.getFeeder(capBank.getOrigFeederID()).getCcName());
                    movedCaps.add(movedBank);
                }
            }
        }
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = movedCaps.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        movedCaps = movedCaps.subList(startIndex, toIndex);
        
        SearchResult<MovedBank> result = new SearchResult<MovedBank>();
        result.setResultList(movedCaps);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        model.addAttribute("searchResult", result);
        model.addAttribute("itemList", result.getResultList());
        
        model.addAttribute("isFiltered", false);
        
        
        model.addAttribute("movedCaps", movedCaps);
        
        return "move/movedCapBanks.jsp";
    }
}