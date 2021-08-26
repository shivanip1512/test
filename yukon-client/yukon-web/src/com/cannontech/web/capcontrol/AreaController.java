package com.cannontech.web.capcontrol;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.area.dao.AreaDao;
import com.cannontech.web.capcontrol.area.model.Area;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableArea;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.capcontrol.util.service.CapControlWebUtilsService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class AreaController {
    
    @Autowired private ServerDatabaseCache dbcache;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private AreaDao areaDao;
    @Autowired private SubstationDao substationDao;
    @Autowired private SeasonScheduleDao seasonSchedules;
    @Autowired private HolidayScheduleDao holidaySchedules;
    @Autowired private StrategyDao strategyDao;
    @Autowired private StrategyService strategyService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DurationFormattingService durationFormatting;
    @Autowired private CapControlWebUtilsService capControlWebUtilsService;
    @Autowired private CapControlCache capControlCache;

    private static final Logger log = YukonLogManager.getLogger(AreaController.class);
    private final static String areaKey = "yukon.web.modules.capcontrol.area.";
    private static final String areaUrl = "/capcontrol/tier/areas";
    
    private final Validator validator = new SimpleValidator<Area>(Area.class) {
        
        private final static String key = "yukon.common.pao.info.error.";
        
        @Override
        protected void doValidation(Area area, Errors errors) {
            
            // Device Name
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", key + "name.required");
            if (!errors.hasFieldErrors("name")) {
                YukonValidationUtils.checkExceedsMaxLength(errors, "name", area.getName(), 60);
            }
            if(!errors.hasFieldErrors("name")){
                if(!PaoUtils.isValidPaoName(area.getName())){
                    errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
                }
            }
            if (!errors.hasFieldErrors("name")) {
                LiteYukonPAObject unique = paoDao.findUnique(area.getName(), area.getType());
                if (unique != null) {
                    if (area.getId() == null || unique.getPaoIdentifier().getPaoId() != area.getId()) {
                        errors.rejectValue("name", key + "name.unique");
                    }
                }
            }
            
            YukonValidationUtils.checkExceedsMaxLength(errors, "description", area.getDescription(), 60);
        }
    };
    
    /** CREATE */
    @RequestMapping("areas/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, LiteYukonUser user) {
        Area area = new Area();
        area.setType(PaoType.CAP_CONTROL_AREA);
        model.addAttribute("mode",  PageEditMode.CREATE);
        return setupModel(model, area, user);
    }
    
    /** CREATE SPECIAL AREA*/
    @RequestMapping("areas/special/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String createSpecial(ModelMap model, LiteYukonUser user) {
        Area area = new Area();
        area.setType(PaoType.CAP_CONTROL_SPECIAL_AREA);
        //default special areas to disabled
        area.setDisabled(true);
        model.addAttribute("mode",  PageEditMode.CREATE);
        return setupModel(model, area, user);
    }
    
    /** VIEW */
    @RequestMapping("areas/{areaId}")
    public String view(ModelMap model, LiteYukonUser user, @PathVariable int areaId) {
        model.addAttribute("mode", PageEditMode.VIEW);
        Area area = areaDao.getArea(areaId);
        return setupModel(model, area, user);
    }
    
    /** EDIT */
    @RequestMapping("areas/{areaId}/edit")
    public String edit(ModelMap model, LiteYukonUser user, @PathVariable int areaId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Area area = areaDao.getArea(areaId);
        return setupModel(model, area, user);
    }
    
    private String setupModel(ModelMap model, Area area, LiteYukonUser user) {
        
        Instant startPage = null;
        if (log.isDebugEnabled()) {
            startPage = Instant.now();
        }
        
        Object modelArea = model.get("area");
        if (modelArea instanceof Area) {
            area = (Area) modelArea;
        }
        model.addAttribute("area", area);
        model.addAttribute("areaName", area.getName());
        model.addAttribute("description", area.getDescription());
        
        model.addAttribute("updater", area.getType() == PaoType.CAP_CONTROL_AREA 
                ? "CBCAREA" : "CBCSPECIALAREA");
        
        if(area.getId() != null){
            int areaId = area.getId();
            
            boolean specialArea = area.getType().equals(PaoType.CAP_CONTROL_SPECIAL_AREA);
            
            //needed for trends/events
            try {
                com.cannontech.message.capcontrol.streamable.StreamableCapObject cachedArea = specialArea ? capControlCache.getSpecialArea(areaId) : capControlCache.getArea(areaId);
                List<ViewableArea> viewableArea = capControlWebUtilsService.createViewableAreas(Arrays.asList(cachedArea), capControlCache, specialArea);
                model.addAttribute("viewableArea", viewableArea.get(0));
            } catch (NotFoundException e) {
                //area was most likely newly created and not in cache yet
            }

            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(area.getId());
            model.addAttribute("points", points);
            
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
            model.addAttribute("subStations", subs);
            
            // SEASON SCHEDULING
            SeasonSchedule scheduleSchedule = seasonSchedules.getScheduleForPao(areaId);
            model.addAttribute("seasonSchedule", scheduleSchedule);
            
            Map<Season, LiteCapControlStrategy> seasonToStrat = strategyService.getSeasonStrategyAssignments(areaId);
            model.addAttribute("seasons", seasonToStrat);
            
            // HOLIDAY SCHEDULING
            HolidaySchedule holidaySchedule = holidaySchedules.getScheduleForPao(areaId);
            model.addAttribute("holidaySchedule", holidaySchedule);
            
            if (holidaySchedule.getHolidayScheduleId() != -1) {
                int strategyId = holidaySchedules.getStrategyForPao(areaId);
                if (strategyId != -1) {
                    model.addAttribute("holidayStrat", strategyDao.getForId(strategyId));
                }
            }
        }

        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideGraphs);
        
        if (log.isDebugEnabled()) {
            Duration d = new Duration(startPage, Instant.now());
            String duration = durationFormatting.formatDuration(d, DurationFormat.MS_ABBR, YukonUserContext.system);
            
            log.debug("Area page model built in:" + duration);
        }
  
        return "areas/area.jsp";
    }
    
    /** SAVE INFO */
    @RequestMapping(value="areas", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String saveArea(HttpServletResponse resp, 
            @ModelAttribute("area") Area area, BindingResult result, 
            RedirectAttributes redirectAttributes,
            FlashScope flash) {
        
        validator.validate(area, result);
        
        if (result.hasErrors()) {
            // Failure
            return bindAndForward(area, result, redirectAttributes);
        }
        
        int id = 0;
        try {
            id = areaDao.save(area);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e);
            log.error("Error saving area:", e);
            return bindAndForward(area, result, redirectAttributes);
        }
        
        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(areaKey + "info.saved"));
        return "redirect:/capcontrol/areas/" + id;
    }
    
    private String bindAndForward(Area area, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("area", area);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.area", result);

        if (area.getId() == null) {
            if(area.getType().equals(PaoType.CAP_CONTROL_SPECIAL_AREA)){
                return "redirect:areas/special/create";
            }
            return "redirect:areas/create";
        }

        return "redirect:areas/" + area.getId() + "/edit";
    }
    
    /**
     * Method deletes the Capcontrol Substation area / Special area
     */
    @RequestMapping(value = "areas/{areaId}", method = RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String deleteArea(HttpServletResponse resp, @PathVariable int areaId, FlashScope flash) {
        Area area = areaDao.getArea(areaId);
        areaDao.delete(area.getPaoIdentifier());
        flash.setConfirm(new YukonMessageSourceResolvable(areaKey + "deleted.success", area.getName()));
        return "redirect:" + areaUrl;
    }
    
    /** SUB ASSIGNMENT POPUP */
    @RequestMapping("areas/{areaId}/stations/edit")
    public String subAssignmentPopup(ModelMap model, @PathVariable int areaId) {
        
        LiteYukonPAObject pao = dbcache.getAllPaosMap().get(areaId);
        
        List<Substation> subs = substationDao.getSubstationsByArea(areaId);
        sortSubs(areaId, subs);
        
        List<Assignment> assigned = Lists.transform(subs, new Function<Substation, Assignment>() {
            @Override
            public Assignment apply(Substation o) {
                return Assignment.of(o.getId(), o.getName());
            }
        });
        model.addAttribute("assigned", assigned);
        
        List<LiteCapControlObject> available = pao.getPaoType() == PaoType.CAP_CONTROL_AREA 
                ? substationDao.getOrphans()
                : substationDao.getSubstationsNotInSpecialArea(areaId);
        List<Assignment> unassigned = Lists.transform(available, new Function<LiteCapControlObject, Assignment>() {
            @Override
            public Assignment apply(LiteCapControlObject o) {
                return Assignment.of(o.getId(), o.getName());
            }
        });
        model.addAttribute("unassigned", unassigned);
        
        model.addAttribute("createUrl", "/capcontrol/substations/create?parentId=" + areaId);
        
        return "assignment-popup.jsp";
    }
    
    /** SUB ASSIGNMENT SAVE */
    @RequestMapping(value="areas/{areaId}/stations", method=RequestMethod.POST)
    public void subAssignmentSave(HttpServletResponse resp, @PathVariable int areaId, FlashScope flash,
            @RequestParam(value="stations[]", required=false, defaultValue="") Integer[] stations) {
        
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