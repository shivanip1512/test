package com.cannontech.web.capcontrol;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.CapControlSubstation;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.WebMessageSourceResolvable;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.models.ViewableSubBus;
import com.cannontech.web.capcontrol.service.SubstationService;
import com.cannontech.web.capcontrol.validators.SubstationValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;


@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class SubstationController {

    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SubstationService substationService;
    @Autowired private PointDao pointDao;
    @Autowired private SubstationValidator validator;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CapControlCache ccCache;
    @Autowired private SubstationDao substationDao;

    private static final Logger log = YukonLogManager.getLogger(SubstationController.class);
    
    private final static String subKey = "yukon.web.modules.capcontrol.substation";
    
    @RequestMapping("substations/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, LiteYukonUser user, FlashScope flashScope, HttpServletRequest request) {

        CapControlSubstation substation = new CapControlSubstation();
        model.addAttribute("mode",  PageEditMode.CREATE);
        model.addAttribute("orphan", true);

        //check for parentId to assign to
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            LiteYukonPAObject parent = dbCache.getAllPaosMap().get(Integer.parseInt(parentId));
            model.addAttribute("parent", parent);
        }

        return setUpModel(model, substation, user, flashScope, request);
    }
    
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value="substations/{substationId}/edit", method=RequestMethod.GET)
    public String edit(HttpServletRequest request, 
            FlashScope flashScope, 
            ModelMap model,
            LiteYukonUser user, 
            @PathVariable int substationId) throws NotAuthorizedException {
        
        CapControlSubstation substation = substationService.get(substationId);
        model.addAttribute("mode", PageEditMode.EDIT);
        return setUpModel(model, substation, user, flashScope, request);
        
    }


    @RequestMapping(value="substations/{substationId}", method=RequestMethod.GET)
    public String view(HttpServletRequest request, 
            FlashScope flashScope, 
            ModelMap model,
            LiteYukonUser user, 
            @PathVariable int substationId) throws NotAuthorizedException {
       
        CapControlSubstation substation = substationService.get(substationId);
        model.addAttribute("mode", PageEditMode.VIEW);
        return setUpModel(model, substation, user, flashScope, request);
        
    }
    
    private String setUpModel(ModelMap model, CapControlSubstation substation, LiteYukonUser user, FlashScope flashScope, HttpServletRequest request) {

        Instant startPage = Instant.now();
        
        boolean hideReports = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_REPORTS, user);
        boolean hideGraphs = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.HIDE_GRAPHS, user);
        model.addAttribute("showAnalysis", !hideReports && !hideGraphs);
        
        Object modelSubstation = model.get("substation");
        if (modelSubstation instanceof CapControlSubstation) {
            substation = (CapControlSubstation) modelSubstation;
        }
        
        model.addAttribute("substation", substation);
        
        if (substation.getId() != null) {
            
            int substationId = substation.getId();

            model.addAttribute("substationName", substation.getPAOName());
            model.addAttribute("substationId", substationId);
            
            List<ViewableSubBus> subBuses = substationService.getBusesForSubstation(substationId);
            model.addAttribute("subBusList", subBuses);
            List<ViewableFeeder> feeders = substationService.getFeedersForSubBuses(subBuses);
            model.addAttribute("feederList", feeders);
            List<ViewableCapBank> capBanks = substationService.getCapBanksForFeeders(feeders);
            model.addAttribute("capBankList", capBanks);

            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(substationId);
            model.addAttribute("points", points);
            
            Integer areaId = substationDao.getParentAreaID(substationId);
            if (areaId != null) {
                model.addAttribute("areaId", areaId);
                LiteYukonPAObject area = dbCache.getAllPaosMap().get(areaId);
                String areaName = area.getPaoName();
                model.addAttribute("areaName", areaName);
                LiteYukonPAObject parent = dbCache.getAllPaosMap().get(areaId);
                model.addAttribute("parent", parent);
                model.addAttribute("orphan", false);
            } else {
                model.addAttribute("orphan", true);
            }
            
            SubStation cachedSubstation = null;
            try {
                cachedSubstation = ccCache.getSubstation(substationId);
            } catch (NotFoundException e) {
                model.addAttribute("orphan", true);
            }
            
            if (cachedSubstation != null) {

                model.addAttribute("specialAreaId", cachedSubstation.getSpecialAreaId());
                
                for (ViewableSubBus bus : subBuses) {
                    SubBus subBus = ccCache.getSubBus(bus.getCcId());
                    if(subBus != null) {
                        if (!CapControlUtils.isStrategyAttachedToSubBusOrSubBusParentArea(subBus)) {
                            SubStation subBusSubstation = ccCache.getSubstation(subBus.getParentID());
                            int parentAreaId;
                            if (subBusSubstation.getSpecialAreaEnabled()) {
                                parentAreaId = subBusSubstation.getSpecialAreaId();
                            } else {
                                parentAreaId = ccCache.getParentAreaId(bus.getCcId());
                            }
                            String contextPath = request.getContextPath();
                            StreamableCapObject streamable = ccCache.getStreamableArea(parentAreaId);
                            
                            String areaLinkHtml = "<a href='" + contextPath + "/capcontrol/areas/" + streamable.getCcId() + "'>" + StringEscapeUtils.escapeXml11(streamable.getCcName()) + "</a>";
                            String subBusLinkHtml = "<a href='" + contextPath + "/capcontrol/buses/" + bus.getCcId() + "'>" + StringEscapeUtils.escapeXml11(bus.getCcName()) + "</a>";

                            areaLinkHtml = "<strong>" + areaLinkHtml + "</strong>";
                            subBusLinkHtml = "<strong>" + subBusLinkHtml + "</strong>";
                            WebMessageSourceResolvable noStrategyMessage =
                                    new WebMessageSourceResolvable("yukon.web.modules.capcontrol.substation.noStrategyAssigned",
                                        areaLinkHtml, subBusLinkHtml);
                            flashScope.setError(noStrategyMessage);
                        }
                    }
                }
            }
        }
        
        long timeForPage = new Interval(startPage, Instant.now()).toDurationMillis();
        log.debug("Time to map substation: "  + timeForPage + "ms");

        return "tier/substation.jsp";
    }
    

    @RequestMapping(value="substations", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("substation") CapControlSubstation substation,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            FlashScope flash, HttpServletRequest request) {
        
        validator.validate(substation, result);

        if (result.hasErrors()) {
            redirectAttributes.addAttribute("parentId", request.getParameter("parentId"));
            return bindAndForward(substation, result, redirectAttributes);
        }
        
        int id;
        try {
            id = substationService.save(substation);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e);
            log.error("Error saving substation:", e);
            return bindAndForward(substation, result, redirectAttributes);
        }
        
        //assign to parent if parentId is there
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            substationDao.assignSubstation(Integer.parseInt(parentId), id);
        }
        
        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(subKey + ".saved"));
        return "redirect:/capcontrol/substations/" + id;
    }
    
    private String bindAndForward(CapControlSubstation substation, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("substation", substation);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.substation", result);

        if (substation.getId() == null) {
            return "redirect:substations/create";
        }

        return "redirect:substations/" + substation.getId() + "/edit";
    }
    
    @RequestMapping(value="substations/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(@PathVariable int id, LiteYukonUser user, FlashScope flash) {

        CapControlSubstation substation = substationService.get(id);
        
        Integer parentId = null;
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        SubStation cachedSubstation = cache.getSubstation(id);
        if (cachedSubstation != null){
            parentId = cachedSubstation.getParentID();
        }

        substationService.delete(id);
        flash.setConfirm(new YukonMessageSourceResolvable(subKey + ".delete.success", substation.getName()));

        if (parentId == null || parentId <= 0) {
            return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__";
        }
        
        return "redirect:/capcontrol/areas/" + parentId;
    }
    
    @RequestMapping("substations/{substationId}/buses/edit")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String editBuses(ModelMap model, @PathVariable int substationId) {
        
        List<Assignment> unassigned = substationService.getUnassignedBuses();
        List<Assignment> assigned = substationService.getAssignedBusesFor(substationId);

        model.addAttribute("unassigned", unassigned);
        model.addAttribute("assigned", assigned);
        
        model.addAttribute("createUrl", "/capcontrol/buses/create?parentId=" + substationId);

        return "assignment-popup.jsp";
    }
    
    @RequestMapping(value="substations/{substationId}/buses", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public void saveBuses(HttpServletResponse resp, @PathVariable int substationId, FlashScope flash,
            @RequestParam(value="children[]", required=false, defaultValue="") Integer[] busIds) {
        substationService.assignBuses(substationId, Arrays.asList(busIds));
        flash.setConfirm(new YukonMessageSourceResolvable(subKey + ".buses.updated"));

        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
}