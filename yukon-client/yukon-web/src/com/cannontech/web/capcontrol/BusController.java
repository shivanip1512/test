package com.cannontech.web.capcontrol;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.core.schedule.model.PaoSchedule;
import com.cannontech.database.data.capcontrol.CapControlSubBus;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.models.Assignment;
import com.cannontech.web.capcontrol.models.ViewableFeeder;
import com.cannontech.web.capcontrol.service.BusService;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.capcontrol.validators.BusValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.pao.service.PaoDetailUrlHelper;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class BusController {

    @Autowired private BusService busService;
    @Autowired private BusValidator validator;
    @Autowired private CapControlCache ccCache;
    @Autowired private HolidayScheduleDao hoidayScheduleDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDetailUrlHelper paoDetailUrlHelper;
    @Autowired private PointDao pointDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private SeasonScheduleDao seasonScheduleDao;
    @Autowired private StrategyDao strategyDao;
    @Autowired private StrategyService strategyService;
    @Autowired private SubstationBusDao busDao;
    @Autowired private PaoScheduleDao paoScheduleDao;

    private Logger log = YukonLogManager.getLogger(getClass());

    private final static String busKey = "yukon.web.modules.capcontrol.bus";

    @RequestMapping(value="buses/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, LiteYukonUser user) {

        CapControlSubBus bus = busService.get(id);
        
        boolean canEdit = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        PageEditMode mode = canEdit ? PageEditMode.EDIT : PageEditMode.VIEW;
        model.addAttribute("mode", mode);

        return setUpModel(model, bus, user);
    }

    @RequestMapping(value="buses/{id}/edit", method=RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, LiteYukonUser user) {

        CapControlSubBus bus = busService.get(id);

        model.addAttribute("mode", PageEditMode.EDIT);

        return setUpModel(model, bus, user);
    }

    @RequestMapping("buses/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, LiteYukonUser user) {

        CapControlSubBus bus = new CapControlSubBus();
        model.addAttribute("mode",  PageEditMode.CREATE);

        return setUpModel(model, bus, user);
    }

    private String setUpModel(ModelMap model, CapControlSubBus bus, LiteYukonUser user) {

        Object modelBus = model.get("bus");
        if (modelBus instanceof CapControlSubBus) {
            bus = (CapControlSubBus) modelBus;
        }
        
        if(bus.getId() != null){
            Integer parentId = busDao.getParent(bus.getId());
            if (parentId != null) {
                LiteYukonPAObject parent = dbCache.getAllPaosMap().get(parentId);
                model.addAttribute("parent", parent);
            }

            SeasonSchedule scheduleSchedule = seasonScheduleDao.getScheduleForPao(bus.getId());
            model.addAttribute("seasonSchedule", scheduleSchedule);

            Map<Season, LiteCapControlStrategy> seasonToStrat = strategyService.getSeasonStrategyAssignments(bus.getId());
            model.addAttribute("seasons", seasonToStrat);

            // HOLIDAY SCHEDULING
            HolidaySchedule holidaySchedule = hoidayScheduleDao.getScheduleForPao(bus.getId());
            model.addAttribute("holidaySchedule", holidaySchedule);
            

            if (holidaySchedule.getHolidayScheduleId() != -1) {
                int strategyId = hoidayScheduleDao.getStrategyForPao(bus.getId());
                if (strategyId != -1) {
                    model.addAttribute("holidayStrat", strategyDao.getForId(strategyId));
                }
            }
            
            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(bus.getId());
            model.addAttribute("points", points);

            List<ViewableFeeder> feederList = busService.getFeedersForBus(bus.getId());
            model.addAttribute("feederList", feederList);

            model.addAttribute("busId", bus.getId());
            model.addAttribute("busName", bus.getName());
            
            
            try {
                ccCache.getSubBus(bus.getId());
                model.addAttribute("orphan", false);

                SubStation substation = ccCache.getParentSubstation(bus.getId());
                model.addAttribute("substationId", substation.getCcId());
                model.addAttribute("substationName", substation.getCcName());

                int areaId = ccCache.getParentAreaId(bus.getId());
                Area area = ccCache.getArea(areaId);

                model.addAttribute("areaId", area.getCcId());
                model.addAttribute("areaName", area.getCcName());

            } catch (NotFoundException e) {
                model.addAttribute("orphan", true);
            }

        }

        model.addAttribute("bus", bus);
        
        List<PaoSchedule> schedules = paoScheduleDao.getAll();
        model.addAttribute("allSchedules", schedules);
        model.addAttribute("scheduleCommands", ScheduleCommand.values());

        model.addAttribute("canEdit", rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user));

        return "bus.jsp";
    }

    @RequestMapping(value="buses", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("bus") CapControlSubBus bus,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            FlashScope flash) {

        validator.validate(bus, result);

        if (result.hasErrors()) {
            return bindAndForward(bus, result, redirectAttributes);
        }

        int id;
        try {
            id = busService.save(bus);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e);
            log.error("Error saving bus:", e);
            return bindAndForward(bus, result, redirectAttributes);
        }

        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(busKey + ".updated"));
        return "redirect:/capcontrol/buses/" + id;
    }

    private String bindAndForward(CapControlSubBus bus, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("bus", bus);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.bus", result);

        if (bus.getId() == null) {
            return "redirect:buses/create";
        }

        return "redirect:buses/" + bus.getId() + "/edit";
    }

    @RequestMapping(value="buses/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(@PathVariable int id, FlashScope flash) {

        CapControlSubBus bus = busService.get(id);

        Integer parentId = busDao.getParent(id);

        busService.delete(id);
        flash.setConfirm(new YukonMessageSourceResolvable(busKey + ".delete.success", bus.getName()));

        if (parentId == null || parentId <= 0) {
            return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubBuses__";
        }

        LiteYukonPAObject parent = dbCache.getAllPaosMap().get(parentId);
        String parentUrl = paoDetailUrlHelper.getUrlForPaoDetailPage(parent);

        return "redirect:" + parentUrl;
    }

    @RequestMapping("buses/{busId}/feeders/edit")
    public String editFeeders(ModelMap model, @PathVariable int busId) {

        List<Assignment> assigned = busService.getAssignedFeedersFor(busId);

        model.addAttribute("assigned", assigned);

        List<Assignment> unassigned = busService.getUnassignedFeeders();

        model.addAttribute("unassigned", unassigned);

        return "assignment-popup.jsp";
    }
    
    @RequestMapping("buses/{busId}/schedules/edit")
    public String editSchedules(ModelMap model, @PathVariable int busId) {
        CapControlSubBus bus = busService.get(busId);
        model.addAttribute("bus", bus);
        model.addAttribute("allSchedules", paoScheduleDao.getAll());
        model.addAttribute("scheduleCommands", ScheduleCommand.values());

        return "schedules-popup.jsp";
    }
    
    @RequestMapping(value="buses/{busId}/schedules", method=RequestMethod.POST)
    public void saveSchedules(@ModelAttribute("bus") CapControlSubBus bus, HttpServletResponse resp, 
            YukonUserContext userContext, FlashScope flash, @PathVariable int busId) {
        bus.setId(busId);
        busService.saveSchedules(bus);
        flash.setConfirm(new YukonMessageSourceResolvable(busKey + ".schedules.updated"));
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @RequestMapping(value="buses/{busId}/feeders", method=RequestMethod.POST)
    public void saveFeeders(HttpServletResponse resp, @PathVariable int busId, FlashScope flash,
            @RequestParam(value="children[]", required=false, defaultValue="") Integer[] feederIds) {
        busService.assignFeeders(busId, Arrays.asList(feederIds));
        flash.setConfirm(new YukonMessageSourceResolvable(busKey + ".feeders.updated"));

        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
}