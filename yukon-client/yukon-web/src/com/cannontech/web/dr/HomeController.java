package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.dao.DemandResponseFavoritesDao;
import com.cannontech.user.YukonUserContext;

@Controller
public class HomeController {
    private DemandResponseFavoritesDao favoritesDao;
    private RolePropertyDao rolePropertyDao;
    private PaoAuthorizationService paoAuthorizationService;

    @RequestMapping("/home")
    public String home(ModelMap model, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();
        List<DisplayablePao> favorites = favoritesDao.getFavorites(user);
        favorites = filterControlAreasAndScenarios(favorites, user);
        model.addAttribute("favorites", favorites);
        
        List<DisplayablePao> recentlyViewed = favoritesDao.getRecentlyViewed(user, 20);
        recentlyViewed = filterControlAreasAndScenarios(recentlyViewed, user);
        model.addAttribute("recents", recentlyViewed);
        return "dr/home.jsp";
    }
    
    /**
     * Helper method to filter out control areas and/or scenarios if the user cannot see them
     * @param paoList - List of paos to filter
     * @param user - Current user
     * @return Filtered list of paos
     */
    private List<DisplayablePao> filterControlAreasAndScenarios(List<DisplayablePao> paoList, 
                                                                LiteYukonUser user) {
        
        List<DisplayablePao> filteredPaoList = new ArrayList<DisplayablePao>();
        
        boolean showControlAreas = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_CONTROL_AREAS, user);
        boolean showScenarios = 
            rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_SCENARIOS, user);

        for(DisplayablePao pao : paoList) {
            PaoType paoType = pao.getPaoIdentifier().getPaoType();
            if((!paoType.equals(PaoType.LM_CONTROL_AREA) && !paoType.equals(PaoType.LM_SCENARIO)) ||
                    (paoType.equals(PaoType.LM_CONTROL_AREA) && showControlAreas) ||
                    (paoType.equals(PaoType.LM_SCENARIO) && showScenarios)) {

                boolean authorized = 
                    paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, pao);
                if(authorized) {
                    filteredPaoList.add(pao);
                }
            }
        }

        return filteredPaoList;
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
