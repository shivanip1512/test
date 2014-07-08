package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.model.ViewableStrategy;
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
    public String strategies(HttpServletRequest request, YukonUserContext userContext, ModelMap model) {
        
        List<ViewableStrategy> strategies = strategyDao.getAllViewableStrategies(userContext);
        
        model.addAttribute("strategies", strategies);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI().substring(request.getContextPath().length()) + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "strategy/strategies.jsp";
    }

    @RequestMapping(value="deleteStrategy")
    public String deleteStrategy(int strategyId, FlashScope flash) {
        
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