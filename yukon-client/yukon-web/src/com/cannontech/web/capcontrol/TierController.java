package com.cannontech.web.capcontrol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.security.annotation.CheckRoleProperty;


@Controller
@RequestMapping("/tier/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class TierController {

    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;

    private static final Logger log = YukonLogManager.getLogger(TierController.class);

    @RequestMapping("areas")
    public String areas(HttpServletRequest request, LiteYukonUser user, ModelMap model) {

        Instant startPage = Instant.now();

        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);

        Map<AreaType, List<ViewableArea>> areasByType = new LinkedHashMap<>();

        List<ViewableArea> viewableAreas = capControlWebUtilsService.createViewableAreas(cache.getAreas(), cache, false);
        areasByType.put(AreaType.NORMAL, viewableAreas);

        List<ViewableArea> viewableSpecialAreas = capControlWebUtilsService.createViewableAreas(cache.getSpecialAreas(), cache, true);
        areasByType.put(AreaType.SPECIAL, viewableSpecialAreas);

        model.addAttribute("areasMap", areasByType);

        setUpAreas(model, user);

        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());

        long timeForPage = new Interval(startPage, Instant.now()).toDurationMillis();
        log.debug("Time to map dashboard: "  + timeForPage + "ms");

        return "tier/areaTier.jsp";
    }

    private final void setUpAreas(ModelMap model, LiteYukonUser user) {
        boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideReports && !hideGraphs);

        model.addAttribute("systemStatusCommandId", CommandType.SYSTEM_STATUS.getCommandId());
        model.addAttribute("resetOpCountCommandId", CommandType.RESET_SYSTEM_OP_COUNTS.getCommandId());
    }

    public enum AreaType {
        
        NORMAL("normal", "CBCAREA", false),
        SPECIAL("special", "CBCSPECIALAREA", true);

        private final String type;
        private final String updaterType;
        private final boolean isSpecialArea;

        AreaType(String type, String updaterType, boolean isSpecialArea){
            this.type = type;
            this.updaterType = updaterType;
            this.isSpecialArea = isSpecialArea;
        }

        public String getType(){
            return type;
        }

        public String getUpdaterType() {
            return updaterType;
        }

        public boolean isSpecialArea() {
            return isSpecialArea;
        }
    }
    
    @RequestMapping(value="updateSession", method=RequestMethod.POST)
    public void updateSession(HttpServletRequest request, CCSessionInfo info) {
        if(info != null) {
            info.updateState( request );
        }
    }
    
}