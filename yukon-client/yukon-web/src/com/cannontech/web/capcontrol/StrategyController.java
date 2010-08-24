package com.cannontech.web.capcontrol;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.ViewableStrategy;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;


@Controller
@RequestMapping("/strategy/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class StrategyController {
    
    private RolePropertyDao rolePropertyDao = null;
    private StrategyDao strategyDao = null;
    
    @RequestMapping
    public String strategies(HttpServletRequest request, YukonUserContext userContext, ModelMap mav) {
        List<ViewableStrategy> strategies = strategyDao.getAllViewableStrategies(userContext);
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 25);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = strategies.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        strategies = strategies.subList(startIndex, toIndex);
        
        SearchResult<ViewableStrategy> result = new SearchResult<ViewableStrategy>();
        result.setResultList(strategies);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        mav.addAttribute("searchResult", result);
        mav.addAttribute("strategies", result.getResultList());
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
        mav.addAttribute("hasEditingRole", hasEditingRole);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "strategy/strategies.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public View deleteStrategy(HttpServletRequest request, Integer strategyId, ModelMap map) throws ServletException, Exception {
        boolean success = true;
        String resultString = "Strategy deleted successfully.";
        if( strategyId == null) {
            success = false;
            resultString = "Delete failed: StrategyId was NULL";
        } else {
            List<String> otherPaosUsingStrategy = strategyDao.getAllOtherPaoNamesUsingStrategyAssignment(strategyId, 0);
            if(!otherPaosUsingStrategy.isEmpty()){
                success = false;
                if(otherPaosUsingStrategy.size() > 4) {
                    resultString = "Delete failed: Strategy used by: " + otherPaosUsingStrategy.get(0) 
                                       + ", " +otherPaosUsingStrategy.get(1)
                                       + ", " +otherPaosUsingStrategy.get(2)
                                       + ", " +otherPaosUsingStrategy.get(3) + " ..." + Integer.toString(otherPaosUsingStrategy.size() - 4) + " more.";
                } else {
                    resultString = "Delete failed: Strategy used by: " + otherPaosUsingStrategy;
                }
            } else {
                /* Try to delete the strategy */
                strategyDao.delete(strategyId);
            }
        }
        map.addAttribute("success", success);
        map.addAttribute("resultText" , resultString);
        return new JsonView();
    }
    
    @Autowired
    public void setStrategyDao(StrategyDao strategyDao) {
        this.strategyDao = strategyDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}