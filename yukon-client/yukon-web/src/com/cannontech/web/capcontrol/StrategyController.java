package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.ViewableStrategy;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@RequestMapping("/strategy/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class StrategyController {
    
    @Autowired private StrategyDao strategyDao;
    
    @RequestMapping("strategies")
    public String strategies(HttpServletRequest request, YukonUserContext userContext, ModelMap model) throws ServletRequestBindingException {
        List<ViewableStrategy> strategies = strategyDao.getAllViewableStrategies(userContext);
        
        int itemsPerPage = CtiUtilities.itemsPerPage(ServletRequestUtils.getIntParameter(request, "itemsPerPage"));
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = startIndex + itemsPerPage;
        int numberOfResults = strategies.size();
        
        if(numberOfResults < toIndex) toIndex = numberOfResults;
        strategies = strategies.subList(startIndex, toIndex);
        
        SearchResults<ViewableStrategy> result = new SearchResults<ViewableStrategy>();
        result.setResultList(strategies);
        result.setBounds(startIndex, itemsPerPage, numberOfResults);
        model.addAttribute("searchResult", result);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI().substring(request.getContextPath().length()) + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "strategy/strategies.jsp";
    }

    @RequestMapping(value="deleteStrategy")
    public String deleteStrategy(HttpServletRequest request, ModelMap model, int strategyId, FlashScope flash) {
        String name = strategyDao.getForId(strategyId).getStrategyName();
        List<String> otherPaosUsingStrategy = strategyDao.getAllOtherPaoNamesUsingStrategyAssignment(strategyId, strategyId);
        if (otherPaosUsingStrategy.isEmpty()) {
            strategyDao.delete(strategyId);
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.strategies.deleteSuccess", name));
        } else {
            List<Object> params = new ArrayList<>();
            params.add(name);
            params.add(otherPaosUsingStrategy.size());
            params.addAll(otherPaosUsingStrategy);

            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.strategies.deleteFailed", 
                                                            params.toArray()));
        }

        return "redirect:strategies";
    }
}