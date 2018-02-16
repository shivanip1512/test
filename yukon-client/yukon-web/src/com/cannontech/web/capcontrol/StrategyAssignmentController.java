
package com.cannontech.web.capcontrol;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.HolidayScheduleDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.database.db.holiday.HolidaySchedule;
import com.cannontech.database.db.season.SeasonSchedule;
import com.cannontech.database.model.Season;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.capcontrol.models.SeasonStrategyAssignment;
import com.cannontech.web.capcontrol.models.StrategyAssignment;
import com.cannontech.web.capcontrol.service.StrategyService;
import com.cannontech.web.common.flashScope.FlashScope;

/**
 * Handles a strategy assignment popup for all objects that 
 * can have strategies assigned to them. (Area, Special Area, Substation Bus, Feeder)
 */
@Controller
public class StrategyAssignmentController {
    
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private SeasonScheduleDao seasonSchedules;
    @Autowired private HolidayScheduleDao holidaySchedules;
    @Autowired private StrategyDao strategyDao;
    @Autowired private StrategyService strategyService;
    
    private final List<String> unsupportedAlgosforFeeder = Arrays.asList(ControlAlgorithm.INTEGRATED_VOLT_VAR.name(),
                                                                         ControlAlgorithm.MULTI_VOLT.name(),
                                                                         ControlAlgorithm.MULTI_VOLT_VAR.name(),
                                                                         ControlAlgorithm.TIME_OF_DAY.name());

    /** ASSIGNMENT POPUP */
    @RequestMapping(value="strategy-assignment/{id}", method=RequestMethod.GET)
    public String stratAssignmentPopup(ModelMap model, LiteYukonUser user, @PathVariable int id) {
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        SeasonSchedule seasonSchedule = seasonSchedules.getScheduleForPao(id);
        Map<Season, LiteCapControlStrategy> seasonToStrat = strategyService.getSeasonStrategyAssignments(id);
        
        StrategyAssignment assignment = new StrategyAssignment();
        assignment.setPaoId(id);
        
        assignment.setSeasonSchedule(seasonSchedule.getScheduleId());
        
        // Add season to strategy assignments
        for (Season season : seasonToStrat.keySet()) {
            LiteCapControlStrategy strategy = seasonToStrat.get(season);
            int strategyId = strategy == null ? -1 : strategy.getId();
            SeasonStrategyAssignment ssa = 
                    SeasonStrategyAssignment.of(season.getSeasonName(), strategyId);
            assignment.getSeasonAssignments().add(ssa);
        }
        
        // Add holiday schedule/strategy
        HolidaySchedule holidaySchedule = holidaySchedules.getScheduleForPao(id);
        assignment.setHolidaySchedule(holidaySchedule.getHolidayScheduleId());
        assignment.setHolidayStrategy(holidaySchedules.getStrategyForPao(id));
        
        model.addAttribute("pao", pao);
        model.addAttribute("assignment", assignment);
        model.addAttribute("seasonSchedules", seasonSchedules.getAllSchedules());
        model.addAttribute("holidaySchedules", holidaySchedules.getAllHolidaySchedules());
        addStrategiesToModel(pao, model);
        
        return "strategy/strat-assignment-popup.jsp";
    }
    
    /** SAVE ASSIGNEMNTS */
    @RequestMapping(value="strategy-assignment/{id}", method=RequestMethod.POST)
    public void stratAssignmentSave(HttpServletResponse resp, 
            @ModelAttribute("assignment") StrategyAssignment assignment,
            FlashScope flash) {
        
        // Save season schedule and season to strategy assignments.
        Map<Season, Integer> strategies = new LinkedHashMap<>();
        for (SeasonStrategyAssignment sa : assignment.getSeasonAssignments()) {
            Season season = new Season(sa.getSeasonName(), assignment.getSeasonSchedule());
            strategies.put(season, sa.getStrategyId());
        }
        int paoId = assignment.getPaoId();
        seasonSchedules.saveSeasonStrategyAssigment(paoId, strategies, assignment.getSeasonSchedule());
        
        // Holiday schedule to strategy assignments.
        holidaySchedules.saveHolidayScheduleStrategyAssigment(paoId, assignment.getHolidaySchedule(), 
                assignment.getHolidayStrategy());
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.strat.assignment.updated"));
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    /** SEASONS FOR SCHEDULE */
    @RequestMapping(value="strategy-assignment/{paoId}/schedule/{id}/seasons", method=RequestMethod.GET)
    public String seasons(ModelMap model, @PathVariable int paoId, @PathVariable int id) {
        List<Season> seasons = seasonSchedules.getUserFriendlySeasonsForSchedule(id);
        model.addAttribute("seasons", seasons);
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
        addStrategiesToModel(pao, model);
        return "strategy/schedule-seasons.jsp";
    }
    
    private void addStrategiesToModel(LiteYukonPAObject pao, ModelMap model) {
        List<LiteCapControlStrategy> strategies;
        //don't include IVVC strategies for feeders
        if (pao.getPaoType().equals(PaoType.CAP_CONTROL_FEEDER)) {
            strategies = strategyDao.getLiteStrategiesWithoutSpecifiedAlgorithms(unsupportedAlgosforFeeder);
        } else {
            strategies = strategyDao.getAllLiteStrategies();
        }
        model.addAttribute("strategies", strategies);
    }
    
}