package com.cannontech.web.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.core.users.model.YukonUserPreference;
import com.cannontech.core.users.model.YukonUserPreferenceName;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.user.service.YukonUserPreferenceService;

@Component
public class UserPreferencesHelper {
    private static final String JS_SIGNAL_TO_DISPLAY_ONLY_ICONS = "iconONLY";
    private static final String CSS_CLASS_ICON_CHART_BAR = "icon-chart-bar";
    private static final String CSS_CLASS_ICON_CHART_LINE = "icon-chart-line";

    @Autowired private YukonUserPreferenceService prefService;

    /**
     * This sets up both "userPreferenceMap" and "allPreferenceNames" within model.
     */
    public void setupUserPreferences(ModelMap model, LiteYukonUser user) {

        List<YukonUserPreference> prefs = prefService.findAllSavedPreferencesForUser(user);
        Map<String,YukonUserPreference> prefMap = new HashMap<>();
        for(YukonUserPreference pp : prefs) {
            prefMap.put(pp.getName().toString(), pp);
        }
        model.addAttribute("userPreferenceMap", prefMap);
        model.addAttribute("allPreferenceNames", YukonUserPreferenceName.values());
    }

    /**
     * There are some display options which depend on the enum, but which should not be defined BY the enum.
     * 
     * @param model TODO
     * @postcondition   model has new entry "userPreferencesNameToDisplayOptions"
     *                      [yukonUserPreferenceName.toString() 
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
        map.put(YukonUserPreferenceName.GRAPH_DISPLAY_VISUAL_TYPE.toString(), option1Args);
        model.addAttribute("userPreferencesNameToDisplayOptions", map);
    }

    /**
     * Returned object is an array of JSONObjects with:
     *          "name" String
     *          "prefType" String ["EnumType"|"StringType"|etc.]
     *          "defaultVal" String, derived from the enum possibly with message key interpreted (if not EnumType)
     */
    public JSONArray setupPreferenceDefaults(MessageSourceAccessor accessor) {
        JSONArray prefList = new JSONArray();
        for (YukonUserPreferenceName pref : YukonUserPreferenceName.values() ) {
            JSONObject jsonPref = new JSONObject();
            jsonPref.put("name", pref.toString());
            boolean isEnum = pref.getValueType().toString().startsWith("EnumType");
            if (isEnum) {
                jsonPref.put("prefType", "EnumType");
                jsonPref.put("defaultVal", pref.getDefaultValue());
            } else {
                String value = pref.getDefaultValue();
                String valueType = pref.getValueType().toString();  // REQUIRED or JSON crashes with: JSONException: There is a cycle in the hierarchy!
                jsonPref.put("prefType", valueType);
                jsonPref.put("defaultVal", value);
            }
            prefList.add(jsonPref);
        }
        return prefList;
    }

}
