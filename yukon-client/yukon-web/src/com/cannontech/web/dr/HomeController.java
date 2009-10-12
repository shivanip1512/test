package com.cannontech.web.dr;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.dao.DemandResponseFavoritesDao;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NotPaoTypeFilter;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

@Controller
public class HomeController {
    private DemandResponseFavoritesDao favoritesDao;
    private RolePropertyDao rolePropertyDao;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/home")
    public String home(ModelMap model, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();

        List<UiFilter<DisplayablePao>> filters = Lists.newArrayList();
        filters.add(new AuthorizedFilter(paoAuthorizationService, user,
                                         Permission.LM_VISIBLE));

        boolean showControlAreas = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, user);
        if (!showControlAreas) {
            filters.add(new NotPaoTypeFilter(PaoType.LM_CONTROL_AREA));
        }
        boolean showScenarios = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, user);
        if (!showScenarios) {
            filters.add(new NotPaoTypeFilter(PaoType.LM_SCENARIO));
        }

        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);

        List<DisplayablePao> favorites = favoritesDao.getFavorites(user, filter);
        model.addAttribute("favorites", favorites);

        List<DisplayablePao> recentlyViewed = favoritesDao.getRecentlyViewed(user, 20, filter);
        model.addAttribute("recents", recentlyViewed);

        return "dr/home.jsp";
    }

    @RequestMapping("/addFavorite")
    public void addFavorite(int paoId, YukonUserContext userContext) {
        favoritesDao.addFavorite(paoId, userContext.getYukonUser());
    }

    @RequestMapping("/removeFavorite")
    public void removeFavorite(int paoId, YukonUserContext userContext) {
        favoritesDao.removeFavorite(paoId, userContext.getYukonUser());
    }

    @Autowired
    public void setFavoritesDao(DemandResponseFavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }
}
