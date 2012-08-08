package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoComparator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForScenarioFilter;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.service.ScenarioService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.dr.ProgramControllerHelper.ProgramListBackingBean;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;

@Controller
@CheckRoleProperty(value={YukonRoleProperty.SHOW_SCENARIOS,YukonRoleProperty.DEMAND_RESPONSE}, requireAll=true)
public class ScenarioController {
    @Autowired private ScenarioDao scenarioDao;
    @Autowired private ScenarioService scenarioService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramControllerHelper programControllerHelper;
    @Autowired private FavoritesDao favoritesDao;

    @RequestMapping("/scenario/list")
    public String list(ModelMap model,
            @ModelAttribute("backingBean") ListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope,
            YukonUserContext userContext) {
        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));

        boolean isFiltered = false;
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
            isFiltered = true;
        }
        model.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> sorter = new DisplayablePaoComparator();
        if(backingBean.getDescending()) {
            sorter = Collections.reverseOrder(sorter);
        }
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        SearchResult<DisplayablePao> searchResult =
            scenarioService.filterScenarios(userContext, filter, sorter, startIndex,
                                            backingBean.getItemsPerPage());

        model.addAttribute("searchResult", searchResult);
        model.addAttribute("scenarios", searchResult.getResultList());
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        model.addAttribute("favoritesByPaoId", favoritesByPaoId);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/scenario/list.jsp";
    }

    @RequestMapping("/scenario/detail")
    public String detail(int scenarioId, ModelMap model,
            @ModelAttribute("backingBean") ProgramListBackingBean backingBean,
            BindingResult bindingResult, YukonUserContext userContext,
            FlashScope flashScope) {
    	DisplayablePao scenario = scenarioDao.getScenario(scenarioId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     scenario, 
                                                     Permission.LM_VISIBLE);

        favoritesDao.detailPageViewed(scenarioId);
        model.addAttribute("scenario", scenario);
        boolean isFavorite =
            favoritesDao.isFavorite(scenarioId, userContext.getYukonUser());
        model.addAttribute("isFavorite", isFavorite);

        UiFilter<DisplayablePao> detailFilter = new ForScenarioFilter(scenarioId);
        programControllerHelper.filterPrograms(model, userContext, backingBean,
                                               bindingResult, detailFilter);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/scenario/detail.jsp";
    }

    private void addFilterErrorsToFlashScopeIfNecessary(ModelMap model,
            BindingResult bindingResult, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hasFilterErrors", true);
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext, "programList");
    }
}
