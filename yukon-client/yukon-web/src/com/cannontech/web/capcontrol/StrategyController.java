package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.ViewableStrategy;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@RequestMapping("/strategy/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class StrategyController {
    
    private StrategyDao strategyDao = null;
    
    @RequestMapping
    public String strategies(HttpServletRequest request, YukonUserContext userContext, ModelMap mav) {
        List<ViewableStrategy> strategies = strategyDao.getAllViewableStrategies(userContext);
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", CtiUtilities.DEFAULT_ITEMS_PER_PAGE);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        if (itemsPerPage > CtiUtilities.MAX_ITEMS_PER_PAGE) {
            // Limit the maximum items per page
            itemsPerPage = CtiUtilities.MAX_ITEMS_PER_PAGE;
        }
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = strategies.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        strategies = strategies.subList(startIndex, toIndex);
        
        SearchResults<ViewableStrategy> result = new SearchResults<ViewableStrategy>();
        result.setResultList(strategies);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        mav.addAttribute("searchResult", result);
        mav.addAttribute("strategies", result.getResultList());
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "strategy/strategies.jsp";
    }
    
    @Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
    
}