package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.SiteMapCategory;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.userpage.service.UserPageService;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/favoritesWidget")
public class FavoritesWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private UserPageDao userPageDao;
    @Autowired private UserPageService userPageService;
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext userContext) throws Exception {
        
        List<UserPage> pages = userPageDao.getPagesForUser(userContext.getYukonUser());
        
        // History
        List<UserPage> history = pages;//setupDisplayableHistory(pages, userContext);
        if (history.size() > UserPageDao.MAX_HISTORY ) {
            history = history.subList(0, UserPageDao.MAX_HISTORY);
        }
        model.put("history", history);
        
        // Favorites
        Multimap<SiteMapCategory, UserPage> favoritesMap = setupDisplayableFavorites(pages, userContext);
        model.put("favorites", favoritesMap.asMap());
        
        return "favoritesWidget/render.jsp";
    }
    
    private Multimap<SiteMapCategory, UserPage> setupDisplayableFavorites(List<UserPage> pages, final YukonUserContext userContext) {
        List<UserPage> favorites = new ArrayList<>();
        for (UserPage page : pages) {
            if (page.isFavorite()) {
                favorites.add(page);
            }
        }
        Collections.sort(favorites, byModuleAsc);
    
        // Sort on the localized name.
        Function<UserPage, String> translator = new Function<UserPage, String>() {
            @Override
            public String apply(UserPage userPage) {
                return userPageService.getLocalizedPageTitle(userPage, userContext);
            }
        };
        favorites = CtiUtilities.smartTranslatedSort(favorites, translator);
  
        Multimap<SiteMapCategory, UserPage> favoritesMap = LinkedListMultimap.create();
        for (UserPage page : favorites) {
            favoritesMap.put(page.getModule().getSiteMapCategory(), page);
        }
  
        return favoritesMap;
    }
    
    private final static Comparator<UserPage> byModuleAsc = Ordering.natural().onResultOf(
        new Function<UserPage, SiteMapCategory>() {
            @Override
            public SiteMapCategory apply(UserPage userPage) {
                return userPage.getModule().getSiteMapCategory();
            }
        });
}
