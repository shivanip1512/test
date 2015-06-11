package com.cannontech.web.capcontrol.area;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.area.dao.AreaDao;
import com.cannontech.web.capcontrol.area.model.Area;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.PaoModel;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class AreasController {
    
    @Autowired private ServerDatabaseCache dbcache;
    @Autowired private FilterCacheFactory vvCacheFactory;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private AreaDao areaDao;
    @Autowired private SubstationDao substationDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private CapControlWebUtilsService webUtils;
    @Autowired private DurationFormattingService durationFormatting;

    private static final Logger log = YukonLogManager.getLogger(AreasController.class);
    private final static String areaKey = "yukon.web.modules.capcontrol.areas.";
    
    private final Validator validator = new SimpleValidator<PaoModel>(PaoModel.class) {
        
        private final static String key = "yukon.common.pao.info.error.";
        
        @Override
        protected void doValidation(PaoModel pao, Errors errors) {
            
            // Device Name
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "name.required");
            if (!errors.hasFieldErrors("name")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "name", pao.getName(), 60);
            }
            if (!errors.hasFieldErrors("name")) {
                LiteYukonPAObject unique = paoDao.findUnique(pao.getName(), pao.getType());
                if (unique != null) {
                    if (unique.getPaoIdentifier().getPaoId() != pao.getId()) {
                        errors.rejectValue("name", key + "name.unique");
                    }
                }
            }
            
            YukonValidationUtils.checkExceedsMaxLength(errors, "description", pao.getDescription(), 60);
        }
    };
    
    /** VIEW */
    @RequestMapping("areas/{areaId}")
    public String view(ModelMap model, LiteYukonUser user, @PathVariable int areaId) {
        
        Instant startPage = null;
        if (log.isDebugEnabled()) {
            startPage = Instant.now();
        }
        
        CapControlCache cache = vvCacheFactory.createUserAccessFilteredCache(user);
        Area area = areaDao.getArea(areaId);
        model.addAttribute("area", area);
        model.addAttribute("description", area.getDescription());
        model.addAttribute("updater", area.getType() == PaoType.CAP_CONTROL_AREA 
                ? "CBCAREA" : "CBCSPECIALAREA");
        
        Integer voltReductionPoint = area.getVoltReductionPoint();
        if (voltReductionPoint != null) {
            LitePoint point = pointDao.getLitePoint(voltReductionPoint);
            String pointName = point.getPointName();
            String paoName = dbcache.getAllPaosMap().get(point.getPaobjectID()).getPaoName();
            model.addAttribute("voltReduction", paoName + " - " + pointName);
            model.addAttribute("voltReductionId", point.getPointID());
        }
        
        List<Substation> subs = substationDao.getSubstationsByArea(areaId);
        sortSubs(areaId, subs);
        
        model.addAttribute("areaName", area.getName());
        model.addAttribute("subStations", subs);
        
        List<SubBus> busses = cache.getSubBusesByArea(areaId);
        Collections.sort(busses, CapControlUtils.SUB_DISPLAY_COMPARATOR);
        model.addAttribute("subBusList", webUtils.createViewableSubBus(busses));
        
        List<Feeder> feeders = cache.getFeedersByArea(areaId);
        model.addAttribute("feederList", webUtils.createViewableFeeder(feeders));

        List<CapBankDevice> banks = cache.getCapBanksByArea(areaId);
        model.addAttribute("capBankList", webUtils.createViewableCapBank(banks));
        
        boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideReports && !hideGraphs);
        
        boolean canEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("canEdit", canEdit);
        
        boolean hasAreaControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_AREA_CONTROLS, user);
        model.addAttribute("hasAreaControl", hasAreaControl);
        
        boolean hasSubstationControl = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_SUBSTATION_CONTROLS, user);
        model.addAttribute("hasSubstationControl", hasSubstationControl);
        
        if (log.isDebugEnabled()) {
            Duration d = new Duration(startPage, Instant.now());
            String duration = durationFormatting.formatDuration(d, DurationFormat.MS_ABBR, YukonUserContext.system);
            
            log.debug("Area page model built in:" + duration);
        }
        
        return "areas/area.jsp";
    }
    
    /** EDIT INFO POPUP */
    @RequestMapping("areas/{areaId}/info/edit")
    public String editInfoPopup(ModelMap model, @PathVariable int areaId) {
        
        Area area = areaDao.getArea(areaId);
        model.addAttribute("area", area);
        
        return "areas/info.jsp";
    }
    
    /** SAVE INFO */
    @RequestMapping(value="areas/{areaId}/info", method=RequestMethod.PUT)
    public String saveInfo(HttpServletResponse resp, ModelMap model, 
            @ModelAttribute("area") Area area, BindingResult result) {
        
        validator.validate(area, result);
        
        if (result.hasErrors()) {
            // Failure
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "areas/info.jsp";
        }
        
        areaDao.save(area);
        
        // Success
        return JsonUtils.writeResponse(resp, ImmutableMap.of("success", true));
    }
    
    /** SUB ASSIGNMENT POPUP */
    @RequestMapping("areas/{areaId}/stations/edit")
    public String subAssignmentPopup(ModelMap model, LiteYukonUser user, @PathVariable int areaId) {
        
        List<Substation> subs = substationDao.getSubstationsByArea(areaId);
        sortSubs(areaId, subs);
        
        List<Assignment> assigned = Lists.transform(subs, new Function<Substation, Assignment>() {
            @Override
            public Assignment apply(Substation o) {
                return Assignment.of(o.getId(), o.getName());
            }
        });
        model.addAttribute("assigned", assigned);
        
        List<LiteCapControlObject> orphans = substationDao.getOrphans();
        List<Assignment> unassigned = Lists.transform(orphans, new Function<LiteCapControlObject, Assignment>() {
            @Override
            public Assignment apply(LiteCapControlObject o) {
                return Assignment.of(o.getId(), o.getName());
            }
        });
        model.addAttribute("unassigned", unassigned);
        
        return "assignment-popup.jsp";
    }
    
    /** SUB ASSIGNMENT SAVE */
    @RequestMapping(value="areas/{areaId}/stations", method=RequestMethod.POST)
    public void subAssignmentSave(HttpServletResponse resp, ModelMap model,
            @PathVariable int areaId, FlashScope flash,
            @RequestParam("stations[]") Integer[] stations) {
        
        substationDao.updateSubAssignments(areaId, Arrays.asList(stations));
        flash.setConfirm(new YukonMessageSourceResolvable(areaKey + "subs.updated"));
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    /** Sort by display order */
    private void sortSubs(int areaId, List<Substation> subs) {
        Map<Integer, Double> orders = substationDao.getSubOrders(areaId);
        Collections.sort(subs, new Comparator<Substation>() { 
            @Override
            public int compare(Substation s1, Substation s2) {
                return orders.get(s1.getId()).compareTo(orders.get(s2.getId()));
            }
        });
    }
}