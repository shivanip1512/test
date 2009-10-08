package com.cannontech.web.dr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.dao.DemandResponseFavoritesDao;
import com.cannontech.user.YukonUserContext;

@Controller
public class HomeController {
    private DemandResponseFavoritesDao favoritesDao;

    @RequestMapping("/home")
    public String home(ModelMap model, YukonUserContext userContext) {
        LiteYukonUser user = userContext.getYukonUser();
        model.addAttribute("favorites", favoritesDao.getFavorites(user));
        model.addAttribute("recents", favoritesDao.getRecentlyViewed(user, 20));
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
}
