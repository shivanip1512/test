package com.cannontech.web.dr.cc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.trends.TrendUtils;
import com.cannontech.web.user.service.UserPreferenceService;

public class CcTrendHelper {
    @Autowired private GraphDao graphDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private UserPreferenceService userPreferenceService;
    
    public void setUpTrends(ModelMap model, YukonUserContext userContext, Integer trendId, boolean showAll) {
        
        List<LiteGraphDefinition> trends;
        if (showAll) {
            trends = graphDao.getGraphDefinitions();
        } else {
            int userId = userContext.getYukonUser().getUserID();
            trends = graphDao.getGraphDefinitionsForUser(userId);
        }
        model.addAttribute("trends", trends);
        if (trendId != null) {
            LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(trendId);
            model.addAttribute("trendId", trendId);
            model.addAttribute("trendName", trend.getName());
            model.addAttribute("showTrends", true);
        } else if (!trends.isEmpty()) {
            model.addAttribute("trendId", trends.get(0).getGraphDefinitionID());
            model.addAttribute("trendName", trends.get(0).getName());
        }
        
        model.addAttribute("labels", TrendUtils.getLabels(userContext, messageSourceResolver));
        model.addAttribute("autoUpdate", userPreferenceService.getDefaultTrendAutoUpdateSelection(userContext.getYukonUser()));
    }
}
