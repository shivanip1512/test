package com.cannontech.web.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.user.service.UserPreferenceService;

@Component
public class UserPreferencesHelper {
    private static final String JS_SIGNAL_TO_DISPLAY_ONLY_ICONS = "iconONLY";
    private static final String CSS_CLASS_ICON_CHART_BAR = "icon-chart-bar";
    private static final String CSS_CLASS_ICON_CHART_LINE = "icon-chart-line";

    @Autowired private UserPreferenceService prefService;

    /**
     * This sets up both "userPreferenceMap" and "allPreferenceNames" within model.
     */
    public void setupUserPreferences(ModelMap model, LiteYukonUser user) {

        List<UserPreference> prefs = prefService.findAllSavedPreferencesForUser(user);
        Map<String,UserPreference> prefMap = new HashMap<>();
        for(UserPreference pp : prefs) {
            prefMap.put(pp.getName().name(), pp);
        }
        model.addAttribute("userPreferenceMap", prefMap);
        model.addAttribute("allPreferenceNames", UserPreferenceName.values());
    }

    /**
     * There are some display options which depend on the enum, but which should not be defined BY the enum.
     * 
     * @param model TODO
     * @postcondition   model has new entry "userPreferencesNameToDisplayOptions"
     *                      [userPreferenceName.toString() 
     *                          > ["iconONLY"
     *                              > [BAR > "icon-chart-bar"]
     *                              > [LINE > "icon-chart-line"]
     *                  More generally, this is processed by the JSP - add settings other than "iconONLY":
     *                      [Preference > 
     *                          [keyword:"iconONLY" > [PreferenceOption > [css class]]
     */
    public void buildPreferenceOptions(ModelMap model) {
        Map<String,Map<String,Map<String,String>>> map = new HashMap<>();
    
        Map<String,String> option1map = new HashMap<>();
        option1map.put(PreferenceGraphVisualTypeOption.BAR.toString(), CSS_CLASS_ICON_CHART_BAR);
        option1map.put(PreferenceGraphVisualTypeOption.LINE.toString(), CSS_CLASS_ICON_CHART_LINE);
        Map<String,Map<String,String>> option1Args = new HashMap<>();
        option1Args.put(JS_SIGNAL_TO_DISPLAY_ONLY_ICONS, option1map);
        map.put(UserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE.toString(), option1Args);
        model.addAttribute("userPreferencesNameToDisplayOptions", map);
    }
}
