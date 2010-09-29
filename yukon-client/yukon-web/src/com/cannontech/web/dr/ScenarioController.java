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
import org.springframework.web.bind.support.SessionStatus;

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
@CheckRoleProperty(YukonRoleProperty.SHOW_SCENARIOS)
public class ScenarioController {
    private ScenarioDao scenarioDao;
    private ScenarioService scenarioService;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;
    private FavoritesDao favoritesDao;

    @RequestMapping("/scenario/list")
    public String list(ModelMap modelMap, 
                       YukonUserContext userContext,
                       @ModelAttribute("backingBean") ListBackingBean backingBean, 
                       BindingResult result,
                       SessionStatus status) {
        // TODO:  validation on backing bean

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService, 
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));

        boolean isFiltered = false;
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
            isFiltered = true;
        }
        modelMap.addAttribute("isFiltered", isFiltered);

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

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("scenarios", searchResult.getResultList());
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        modelMap.addAttribute("favoritesByPaoId", favoritesByPaoId);

        return "dr/scenario/list.jsp";
    }

    @RequestMapping("/scenario/detail")
    public String detail(int scenarioId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramListBackingBean backingBean,
            BindingResult bindingResult,
            SessionStatus status, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }

    	DisplayablePao scenario = scenarioDao.getScenario(scenarioId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     scenario, 
                                                     Permission.LM_VISIBLE);

        favoritesDao.detailPageViewed(scenarioId);
        modelMap.addAttribute("scenario", scenario);
        boolean isFavorite =
            favoritesDao.isFavorite(scenarioId, userContext.getYukonUser());
        modelMap.addAttribute("isFavorite", isFavorite);

        UiFilter<DisplayablePao> detailFilter = new ForScenarioFilter(scenarioId);
        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               bindingResult, status, detailFilter);

        return "dr/scenario/detail.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext, "programList");
    }

    @Autowired
    public void setScenarioDao(ScenarioDao scenarioDao) {
        this.scenarioDao = scenarioDao;
    }
    
    @Autowired
    public void setScenarioService(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
}
