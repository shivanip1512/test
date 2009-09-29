package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.DurationFormattingService.DurationFormat;
import com.cannontech.dr.controlarea.filter.PriorityFilter;
import com.cannontech.dr.controlarea.filter.StateFilter;
import com.cannontech.dr.controlarea.model.ControlArea;
import com.cannontech.dr.controlarea.model.ControlAreaDisplayField;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;

@Controller
public class ControlAreaController {
    public static class ControlAreaListBackingBean extends ListBackingBean {
        private String state;
        private Range<Integer> priority = new Range<Integer>();

        // TODO:
        // START and STOP (HH:MM only)
        // private String start;
        // private String stop;

        // probably can move this up to ListBackingBean
        private Range<Double> loadCapacity = new Range<Double>();

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Range<Integer> getPriority() {
            return priority;
        }

        public void setPriority(Range<Integer> priority) {
            this.priority = priority;
        }

        public Range<Double> getLoadCapacity() {
            return loadCapacity;
        }

        public void setLoadCapacity(Range<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private ControlAreaService controlAreaService = null;
    private PaoAuthorizationService paoAuthorizationService;
    private ProgramControllerHelper programControllerHelper;
    private LoadControlClientConnection loadControlClientConnection;
    private DurationFormattingService durationFormattingService;

    @RequestMapping("/controlArea/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") ControlAreaListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();
        
        filters.add(new AuthorizedFilter(paoAuthorizationService, 
                                         userContext.getYukonUser(), 
                                         Permission.LM_VISIBLE));
        
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
        }
        String stateFilter = backingBean.getState();
        if (!StringUtils.isEmpty(stateFilter)) {
            if (stateFilter.equals("active")) {
                filters.add(new StateFilter(controlAreaService, true));
            } else if (stateFilter.equals("inactive")) {
                filters.add(new StateFilter(controlAreaService, false));
            }
        }
        if (!backingBean.getPriority().isUnbounded()) {
            filters.add(new PriorityFilter(controlAreaService, backingBean.getPriority()));
        }

        ControlAreaDisplayField sortField = StringUtils.isEmpty(backingBean.getSort())
            ? ControlAreaDisplayField.NAME : ControlAreaDisplayField.valueOf(backingBean.getSort());
        Comparator<DisplayablePao> sorter =
            sortField.getSorter(controlAreaService, userContext,
                                backingBean.getDescending());
        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        SearchResult<ControlArea> searchResult =
            controlAreaService.filterControlAreas(userContext, filter, sorter,
                                                  startIndex,
                                                  backingBean.getItemsPerPage());

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("controlAreas", searchResult.getResultList());

        return "dr/controlArea/list.jsp";
    }

    @RequestMapping("/controlArea/detail")
    public String detail(int controlAreaId, ModelMap modelMap,
            YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult result, SessionStatus status) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE);
        
        modelMap.addAttribute("controlArea", controlArea);

        UiFilter<DisplayablePao> detailFilter = new ForControlAreaFilter(controlAreaId);
        programControllerHelper.filterPrograms(modelMap, userContext, backingBean,
                                               result, status, detailFilter);

        return "dr/controlArea/detail.jsp";
    }
    
    @RequestMapping("/controlArea/sendEnableConfirm")
    public String sendEnableConfirm(ModelMap modelMap, int controlAreaId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("controlArea", controlArea);
        modelMap.addAttribute("isEnabled", isEnabled);
        return "dr/controlArea/sendEnableConfirm.jsp";
    }
    
    @RequestMapping("/controlArea/setEnabled")
    public String setEnabled(ModelMap modelMap, int controlAreaId, boolean isEnabled,
            YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        controlAreaService.setEnabled(controlAreaId, isEnabled, userContext);
        
        modelMap.addAttribute("popupId", "drDialog");
        
        return "common/closePopup.jsp";
    }
    
    @RequestMapping("/controlArea/sendResetPeakConfirm")
    public String sendResetPeakConfirm(ModelMap modelMap, int controlAreaId, 
                                       YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("controlArea", controlArea);
        return "dr/controlArea/sendResetPeakConfirm.jsp";
    }
    
    @RequestMapping("/controlArea/resetPeak")
    public String resetPeak(ModelMap modelMap, int controlAreaId, YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        controlAreaService.resetPeak(controlAreaId, userContext);
        
        modelMap.addAttribute("popupId", "drDialog");
        
        return "common/closePopup.jsp";
    }
    
    @RequestMapping("/controlArea/getChangeTimeWindowValues")
    public String getChangeTimeWindowValues(ModelMap modelMap, int controlAreaId, 
                                            YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        DatedObject<LMControlArea> datedControlArea = 
            this.loadControlClientConnection.getDatedControlArea(controlAreaId);
        LMControlArea controlAreaFull = datedControlArea.getObject();
        Integer currentDailyStartTime = controlAreaFull.getCurrentDailyStartTime();
        Integer currentDailyStopTime = controlAreaFull.getCurrentDailyStopTime();

        String startTime = durationFormattingService.formatDuration(currentDailyStartTime, 
                                                                    TimeUnit.SECONDS, 
                                                                    DurationFormat.HM_SHORT, 
                                                                    userContext);
        String stopTime = durationFormattingService.formatDuration(currentDailyStopTime, 
                                                                   TimeUnit.SECONDS, 
                                                                   DurationFormat.HM_SHORT, 
                                                                   userContext);
        
        modelMap.addAttribute("startTime", startTime);
        modelMap.addAttribute("stopTime", stopTime);
        modelMap.addAttribute("controlArea", controlArea);
        
        return "dr/controlArea/getChangeTimeWindowValues.jsp";
    }
    
    @RequestMapping("/controlArea/sendChangeTimeWindowConfirm")
    public String sendChangeTimeWindowConfirm(ModelMap modelMap, int controlAreaId, 
                                              String startTime, String stopTime, 
                                              YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        modelMap.addAttribute("controlArea", controlArea);
        modelMap.addAttribute("startTime", startTime);
        modelMap.addAttribute("stopTime", stopTime);

        if(!this.validateTimeWindow(startTime, stopTime)) {
            modelMap.addAttribute("validWindow", false);
            return "dr/controlArea/getChangeTimeWindowValues.jsp";
        }
        
        return "dr/controlArea/sendChangeTimeWindowConfirm.jsp";
    }
    
    @RequestMapping("/controlArea/changeTimeWindow")
    public String changeTimeWindow(ModelMap modelMap, int controlAreaId, String startTime, 
                                   String stopTime, YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        int startSeconds = this.parseTime(startTime);
        int stopSeconds = this.parseTime(stopTime);
        
        controlAreaService.changeTimeWindow(controlAreaId, 
                                            startSeconds, 
                                            stopSeconds,
                                            userContext);
        
        modelMap.addAttribute("popupId", "drDialog");
        
        return "common/closePopup.jsp";
    }
    
    @RequestMapping("/controlArea/getTriggerChangeValues")
    public String getTriggerChangeValues(ModelMap modelMap, int controlAreaId, 
                                         YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        DatedObject<LMControlArea> datedControlArea = 
            this.loadControlClientConnection.getDatedControlArea(controlAreaId);
        LMControlArea controlAreaFull = datedControlArea.getObject();
        Vector<LMControlAreaTrigger> triggerVector = controlAreaFull.getTriggerVector();
        
        modelMap.addAttribute("triggers", triggerVector);
        modelMap.addAttribute("controlArea", controlArea);
        
        return "dr/controlArea/getChangeTriggerValues.jsp";
    }
    
    @RequestMapping("/controlArea/triggerChange")
    public String triggerChange(ModelMap modelMap, int controlAreaId, Double threshold1, 
                                Double offset1, Double threshold2, Double offset2, 
                                YukonUserContext userContext) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(), 
                                                     controlArea, 
                                                     Permission.LM_VISIBLE, 
                                                     Permission.CONTROL_COMMAND);
        
        controlAreaService.changeTriggers(controlAreaId, 
                                          threshold1, 
                                          offset1, 
                                          threshold2, 
                                          offset2,
                                          userContext);
        
        modelMap.addAttribute("popupId", "drDialog");
        
        return "common/closePopup.jsp";
    }
    
    /**
     * Helper method to parse a time string into seconds
     * @param time - Time string in the format HH:mm
     * @return Seconds for time string
     */
    private int parseTime(String time) {
        
        String[] timeStrings = time.split(":");
        
        if(timeStrings.length != 2) {
            throw new IllegalArgumentException("Invalid Time string: " + time);
        }
        
        String hoursString = timeStrings[0];
        String minutesString = timeStrings[1];
        
        int hours = Integer.parseInt(hoursString);
        int minutes = Integer.parseInt(minutesString);
        
        if(hours > 23 || minutes > 59) {
            throw new IllegalArgumentException("Invalid Time string: " + time);
        }
        
        return (hours * 60 * 60) + (minutes * 60);
    }

    /**
     * Helper method to validate that the start and stop times are valid times and that
     * the stopTime is after the startTime
     * @return True if times are valid
     */
    private boolean validateTimeWindow(String startTime, String stopTime) {
        
        int startSeconds;
        int stopSeconds;
        
        try {
            startSeconds = this.parseTime(startTime);
            stopSeconds = this.parseTime(stopTime);
        } catch (NumberFormatException e) {
            // invalid time
            return false;
        } catch (IllegalArgumentException e) {
            // invalid time
            return false;
        }
        
        return startSeconds < stopSeconds;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext);
    }

    @Autowired
    public void setControlAreaService(ControlAreaService controlAreaService) {
        this.controlAreaService = controlAreaService;
    }

    @Autowired
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

    @Autowired
    public void setProgramControllerHelper(
            ProgramControllerHelper programControllerHelper) {
        this.programControllerHelper = programControllerHelper;
    }
    
    @Autowired
    public void setLoadControlClientConnection(
                                               LoadControlClientConnection loadControlClientConnection) {
        this.loadControlClientConnection = loadControlClientConnection;
    }
    
    @Autowired
    public void setDurationFormattingService(DurationFormattingService durationFormattingService) {
        this.durationFormattingService = durationFormattingService;
    }
}
