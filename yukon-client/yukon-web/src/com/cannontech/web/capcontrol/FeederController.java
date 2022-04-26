package com.cannontech.web.capcontrol;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.models.CapBankAssignment;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.service.FeederService;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.capcontrol.validators.FeederValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class FeederController {

    @Autowired private FeederValidator validator;
    @Autowired private HolidayScheduleDao hoidayScheduleDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private SeasonScheduleDao seasonScheduleDao;
    @Autowired private StrategyDao strategyDao;
    @Autowired private StrategyService strategyService;
    @Autowired private FeederService feederService;
    @Autowired private FeederDao feederDao;
    @Autowired private PointDao pointDao;
    @Autowired private SubstationBusDao busDao;
    @Autowired private SubstationDao substationDao;

    private Logger log = YukonLogManager.getLogger(getClass());

    private final static String feederKey = "yukon.web.modules.capcontrol.feeder";

    @RequestMapping(value="feeders/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, LiteYukonUser user) {
        CapControlFeeder feeder = feederService.get(id);
        model.addAttribute("mode", PageEditMode.VIEW);
        return setUpModel(model, feeder, user);
    }

    @RequestMapping(value="feeders/{id}/edit", method=RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String edit(ModelMap model, @PathVariable int id, LiteYukonUser user) {
        CapControlFeeder feeder = feederService.get(id);
        model.addAttribute("mode", PageEditMode.EDIT);
        return setUpModel(model, feeder, user);
    }

    @RequestMapping("feeders/create")
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model, LiteYukonUser user, HttpServletRequest request) {

        CapControlFeeder feeder = new CapControlFeeder();
        model.addAttribute("mode",  PageEditMode.CREATE);
        
        //check for parentId to assign to
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            LiteYukonPAObject parent = dbCache.getAllPaosMap().get(Integer.parseInt(parentId));
            model.addAttribute("parent", parent);
        }

        return setUpModel(model, feeder, user);
    }

    private String setUpModel(ModelMap model, CapControlFeeder feeder, LiteYukonUser user) {

        Object modelFeeder = model.get("feeder");
        if (modelFeeder instanceof CapControlFeeder) {
            feeder = (CapControlFeeder) modelFeeder;
        }
        model.addAttribute("feeder", feeder);
        model.addAttribute("feederName", feeder.getName());
        model.addAttribute("feederId", feeder.getId());
        
        if(feeder.getId() != null){
            Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(feeder.getId());
            model.addAttribute("points", points);
             try {
                Integer parentId = feederDao.getParentSubBusID(feeder.getId());
                if (parentId != null) {
                    LiteYukonPAObject parent = dbCache.getAllPaosMap().get(parentId);
                    model.addAttribute("parent", parent);
                }
            }catch( EmptyResultDataAccessException e ){
                //do nothing
            }
            
            SeasonSchedule scheduleSchedule = seasonScheduleDao.getScheduleForPao(feeder.getId());
            model.addAttribute("seasonSchedule", scheduleSchedule);

            Map<Season, LiteCapControlStrategy> seasonToStrat = strategyService.getSeasonStrategyAssignments(feeder.getId());
            model.addAttribute("seasons", seasonToStrat);

            // HOLIDAY SCHEDULING
            HolidaySchedule holidaySchedule = hoidayScheduleDao.getScheduleForPao(feeder.getId());
            model.addAttribute("holidaySchedule", holidaySchedule);

            if (holidaySchedule.getHolidayScheduleId() != -1) {
                int strategyId = hoidayScheduleDao.getStrategyForPao(feeder.getId());
                if (strategyId != -1) {
                    model.addAttribute("holidayStrat", strategyDao.getForId(strategyId));
                }
            }

            List<ViewableCapBank> capBankList = feederService.getCapBanksForFeeder(feeder.getId());
            model.addAttribute("capBankList", capBankList);

            try {
                model.addAttribute("orphan", false);

                int parentSubstationBusId = feederDao.getParentSubBusID(feeder.getId());
                LiteYukonPAObject bus = dbCache.getAllPaosMap().get(parentSubstationBusId);
                model.addAttribute("busId", bus.getLiteID());
                model.addAttribute("busName", bus.getPaoName());

                Integer parentSubstationId = busDao.getParent(parentSubstationBusId);
                if (parentSubstationId == null) {
                    throw new NotFoundException("Substation not found for substation bus with id " + parentSubstationBusId);
                }
                LiteYukonPAObject substation = dbCache.getAllPaosMap().get(parentSubstationId);
                model.addAttribute("substationId", substation.getLiteID());
                model.addAttribute("substationName", substation.getPaoName());

                Integer parentAreaID = substationDao.getParentAreaID(parentSubstationId);
                if (parentAreaID == null) {
                    throw new NotFoundException("Area not found for substation with id " + parentSubstationId);
                }
                LiteYukonPAObject area = dbCache.getAllPaosMap().get(parentAreaID);
                model.addAttribute("areaId", area.getLiteID());
                model.addAttribute("areaName", area.getPaoName());

            } catch (NotFoundException | EmptyResultDataAccessException e) {
                log.error(e);
                model.addAttribute("orphan", true);
            }
        }

        return "feeder.jsp";
    }

    @RequestMapping(value="feeders", method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("feeder") CapControlFeeder feeder,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            FlashScope flash, HttpServletRequest request) {
        
        validator.validate(feeder, result);

        if (result.hasErrors()) {
            redirectAttributes.addAttribute("parentId", request.getParameter("parentId"));
            return bindAndForward(feeder, result, redirectAttributes);
        }

        int id;
        try {
            //Saving the Cap Bank List occurs at a separate call
            feeder.setUpdateBankList(false);
            id = feederService.save(feeder);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e);
            log.error("Error saving feeder:", e);
            return bindAndForward(feeder, result, redirectAttributes);
        }
        
        //assign to parent if parentId is there
        String parentId = request.getParameter("parentId");
        if (parentId != null) {
            feederDao.assignFeeder(Integer.parseInt(parentId), id);
        }
        
        // Success
        flash.setConfirm(new YukonMessageSourceResolvable(feederKey + ".saved"));
        return "redirect:/capcontrol/feeders/" + id;
    }

    private String bindAndForward(CapControlFeeder feeder, BindingResult result, RedirectAttributes attrs) {

        attrs.addFlashAttribute("feeder", feeder);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.feeder", result);

        if (feeder.getId() == null) {
            return "redirect:feeders/create";
        }

        return "redirect:feeders/" + feeder.getId() + "/edit";
    }

    @RequestMapping(value="feeders/{id}", method=RequestMethod.DELETE)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String delete(@PathVariable int id, FlashScope flash) {
        CapControlFeeder feeder = feederService.get(id);
        try {
            boolean isCapbankAssigned = feederService.isCapBanksAssignedToZone(id);
            if (isCapbankAssigned) {
                flash.setError(new YukonMessageSourceResolvable(feederKey + ".delete.error", feeder.getName()));
                return "redirect:/capcontrol/feeders/" + id;
            } else {
                boolean isFeederAssignedToVoltagePoint = feederService.isFeederAssignedToVoltagePointForZone(id);
                if (isFeederAssignedToVoltagePoint) {
                    flash.setError(new YukonMessageSourceResolvable(feederKey + ".delete.voltagePoint.error", feeder.getName()));
                    return "redirect:/capcontrol/feeders/" + id;
                }
                Integer parentId = feederDao.getParentSubBusID(id);
                feederService.delete(id);
                flash.setConfirm(new YukonMessageSourceResolvable(feederKey + ".delete.success", feeder.getName()));
                if (parentId != null && parentId > 0) {
                    return "redirect:/capcontrol/buses/" + parentId;
                }
            }
        } catch (EmptyResultDataAccessException|NotFoundException e) {
            feederService.delete(id);
            flash.setConfirm(new YukonMessageSourceResolvable(feederKey + ".delete.success", feeder.getName()));
            // do nothing and return to orphan page
        }
        return "redirect:/capcontrol/search/searchResults?cbc_lastSearch=__cti_oFeeders__";
    }

    @RequestMapping("feeders/{feederId}/capbanks/edit")
    public String editCapBanks(ModelMap model, @PathVariable int feederId) {
        List<CapBankAssignment> assigned = feederService.getAssignedCapBanksForFeeder(feederId);
        model.addAttribute("assigned", assigned);
        List<CapBankAssignment> tripOrders = feederService.getAssignedCapBanksForFeeder(feederId);
        Collections.sort(tripOrders, BANK_TRIP_ORDER_COMPARATOR);
        model.addAttribute("tripOrders", tripOrders);
        List<CapBankAssignment> closeOrders = feederService.getAssignedCapBanksForFeeder(feederId);
        Collections.sort(closeOrders, BANK_CLOSE_ORDER_COMPARATOR);
        model.addAttribute("closeOrders", closeOrders);

        List<CapBankAssignment> unassigned = feederService.getUnassignedCapBanks();
        model.addAttribute("unassigned", unassigned);
        
        model.addAttribute("createUrl", "/capcontrol/capbanks/create?parentId=" + feederId);

        return "assignment-popup-feeder.jsp";
    }
    

    private static final Comparator<CapBankAssignment> BANK_TRIP_ORDER_COMPARATOR = new Comparator<CapBankAssignment>() {
        @Override
        public int compare(CapBankAssignment o1, CapBankAssignment o2) {
            Float order1 = o1.getTripOrder();
            Float order2 = o2.getTripOrder();
            int result = order1.compareTo(order2);
            if (result == 0) {
                result = new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }

            return result;
        }
    };
    
    private static final Comparator<CapBankAssignment> BANK_CLOSE_ORDER_COMPARATOR = new Comparator<CapBankAssignment>() {
        @Override
        public int compare(CapBankAssignment o1, CapBankAssignment o2) {
            Float order1 = o1.getCloseOrder();
            Float order2 = o2.getCloseOrder();
            int result = order1.compareTo(order2);
            if (result == 0) {
                result = new Integer(o1.getId()).compareTo(new Integer(o2.getId()));
            }

            return result;
        }
    };

    @RequestMapping(value="feeders/{feederId}/capbanks", method=RequestMethod.POST)
    public void saveCapBanks(HttpServletResponse resp, @PathVariable int feederId, FlashScope flash,
            @RequestParam(value="children[]", required=false, defaultValue="") Integer[] capBankIds,
            @RequestParam(value="available[]", required=false, defaultValue="") List<Integer> availableCapBanksIds,
            @RequestParam(value="tripOrder[]", required=false, defaultValue="") Integer[] tripOrder,
            @RequestParam(value = "closeOrder[]", required = false, defaultValue = "") Integer[] closeOrder) {
        boolean isCapbankAssigned = false;
        if (!availableCapBanksIds.isEmpty()) {
            isCapbankAssigned = feederService.isCapBanksAssignedToZone(availableCapBanksIds);
        }
        if (isCapbankAssigned) {
            flash.setError(new YukonMessageSourceResolvable(feederKey + ".capbanks.update.error"));
        } else {
            feederService.assignCapBanks(feederId, Arrays.asList(capBankIds), Arrays.asList(closeOrder), Arrays.asList(tripOrder));
            flash.setConfirm(new YukonMessageSourceResolvable(feederKey + ".capbanks.updated"));
            resp.setStatus(HttpStatus.NO_CONTENT.value());
        }
    }
}