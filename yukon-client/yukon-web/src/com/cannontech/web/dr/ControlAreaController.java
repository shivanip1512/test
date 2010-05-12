package com.cannontech.web.dr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.favorites.dao.FavoritesDao;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Range;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.DurationFormattingService.DurationFormat;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.filter.PriorityFilter;
import com.cannontech.dr.controlarea.filter.StateFilter;
import com.cannontech.dr.controlarea.model.ControlAreaNameField;
import com.cannontech.dr.controlarea.service.ControlAreaFieldService;
import com.cannontech.dr.controlarea.service.ControlAreaService;
import com.cannontech.dr.controlarea.service.TriggerFieldService;
import com.cannontech.dr.filter.AuthorizedFilter;
import com.cannontech.dr.filter.NameFilter;
import com.cannontech.dr.program.filter.ForControlAreaFilter;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.ListBackingBean;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(YukonRoleProperty.SHOW_CONTROL_AREAS)
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
    private DurationFormattingService durationFormattingService;
    private DemandResponseEventLogService demandResponseEventLogService;
    private ControlAreaFieldService controlAreaFieldService;
    private TriggerFieldService triggerFieldService;
    private FavoritesDao favoritesDao;
    private ControlAreaNameField controlAreaNameField;

    @RequestMapping("/controlArea/list")
    public String list(ModelMap modelMap, YukonUserContext userContext,
            @ModelAttribute("backingBean") ControlAreaListBackingBean backingBean,
            BindingResult result, SessionStatus status) {

        List<UiFilter<DisplayablePao>> filters = new ArrayList<UiFilter<DisplayablePao>>();

        filters.add(new AuthorizedFilter<DisplayablePao>(paoAuthorizationService,
                                         userContext.getYukonUser(),
                                         Permission.LM_VISIBLE));

        boolean isFiltered = false;
        if (!StringUtils.isEmpty(backingBean.getName())) {
            filters.add(new NameFilter(backingBean.getName()));
            isFiltered = true;
        }
        String stateFilter = backingBean.getState();
        if (!StringUtils.isEmpty(stateFilter)) {
            if (stateFilter.equals("active")) {
                filters.add(new StateFilter(controlAreaService, true));
                isFiltered = true;
            } else if (stateFilter.equals("inactive")) {
                filters.add(new StateFilter(controlAreaService, false));
                isFiltered = true;
            }
        }
        if (!backingBean.getPriority().isUnbounded()) {
            filters.add(new PriorityFilter(controlAreaService, backingBean.getPriority()));
            isFiltered = true;
        }
        modelMap.addAttribute("isFiltered", isFiltered);

        // Sorting - name is default sorter
        Comparator<DisplayablePao> defaultSorter = controlAreaNameField.getSorter(userContext);
        Comparator<DisplayablePao> sorter = defaultSorter;
        if (!StringUtils.isEmpty(backingBean.getSort())) {
            // If there is a custom sorter, add it

            boolean backWithNameSorter = true;
            String sortFieldName = backingBean.getSort();
            if (sortFieldName.startsWith("CA_")) {
                DemandResponseBackingField<LMControlArea> sortField = controlAreaFieldService.getBackingField(sortFieldName.substring(3));
                sorter = sortField.getSorter(userContext);
                backWithNameSorter = controlAreaNameField != sortField;
            } else if (sortFieldName.startsWith("TR_")) {
                DemandResponseBackingField<LMControlAreaTrigger> sortField = triggerFieldService.getBackingField(sortFieldName.substring(3));
                sorter = sortField.getSorter(userContext);
            } else {
                throw new RuntimeException("invalid sort field name");
            }

            if (backingBean.getDescending()) {
                sorter = Collections.reverseOrder(sorter);
            }

            // Don't double sort if name is the sort field
            if (backWithNameSorter) {
                sorter = Ordering.from(sorter).compound(defaultSorter);
            }
        }

        UiFilter<DisplayablePao> filter = UiFilterList.wrap(filters);
        int startIndex = (backingBean.getPage() - 1) * backingBean.getItemsPerPage();
        SearchResult<DisplayablePao> searchResult =
            controlAreaService.filterControlAreas(filter, sorter, startIndex,
                                                  backingBean.getItemsPerPage(), userContext);

        modelMap.addAttribute("searchResult", searchResult);
        modelMap.addAttribute("controlAreas", searchResult.getResultList());
        Map<Integer, Boolean> favoritesByPaoId =
            favoritesDao.favoritesByPao(searchResult.getResultList(),
                                        userContext.getYukonUser());
        modelMap.addAttribute("favoritesByPaoId", favoritesByPaoId);

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

        favoritesDao.detailPageViewed(controlAreaId);
        modelMap.addAttribute("controlArea", controlArea);
        boolean isFavorite =
            favoritesDao.isFavorite(controlAreaId, userContext.getYukonUser());
        modelMap.addAttribute("isFavorite", isFavorite);

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
            YukonUserContext userContext, FlashScope flashScope) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser,
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        controlAreaService.setEnabled(controlAreaId, isEnabled);

        if(isEnabled) {
            demandResponseEventLogService.threeTierControlAreaEnabled(yukonUser,
                                                                      controlArea.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.controlArea.sendEnableConfirm.enabled"));
        } else {
            demandResponseEventLogService.threeTierControlAreaDisabled(yukonUser,
                                                                       controlArea.getName());
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.controlArea.sendEnableConfirm.disabled"));
        }

        return closeDialog(modelMap);
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
    public String resetPeak(ModelMap modelMap, int controlAreaId, YukonUserContext userContext,
                            FlashScope flashScope) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser,
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        controlAreaService.resetPeak(controlAreaId);

        demandResponseEventLogService.threeTierControlAreaPeakReset(yukonUser,
                                                                    controlArea.getName());
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.controlArea.sendResetPeakConfirm.peakReset"));
        return closeDialog(modelMap);
    }

    @RequestMapping("/controlArea/getChangeTimeWindowValues")
    public String getChangeTimeWindowValues(ModelMap modelMap, int controlAreaId,
                                            YukonUserContext userContext) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        LMControlArea controlAreaFull = controlAreaService.getControlAreaForPao(controlArea);
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

        if(!validateTimeWindow(startTime, stopTime)) {
            modelMap.addAttribute("validWindow", false);
            return "dr/controlArea/getChangeTimeWindowValues.jsp";
        }
        
        return "dr/controlArea/sendChangeTimeWindowConfirm.jsp";
    }

    @RequestMapping("/controlArea/changeTimeWindow")
    public String changeTimeWindow(ModelMap modelMap, int controlAreaId, String startTime,
                                   String stopTime, YukonUserContext userContext, 
                                   FlashScope flashScope) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser,
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        int startSeconds = parseTime(startTime);
        int stopSeconds = parseTime(stopTime);

        controlAreaService.changeTimeWindow(controlAreaId,
                                            startSeconds,
                                            stopSeconds);

        demandResponseEventLogService.threeTierControlAreaTimeWindowChanged(yukonUser,
                                                                            controlArea.getName(),
                                                                            startSeconds,
                                                                            stopSeconds);

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.controlArea.sendChangeTimeWindowConfirm.timeWindowChanged"));
        return closeDialog(modelMap);
    }

    @RequestMapping("/controlArea/getTriggerChangeValues")
    public String getTriggerChangeValues(ModelMap modelMap, int controlAreaId,
                                         YukonUserContext userContext) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        LMControlArea controlAreaFull = controlAreaService.getControlAreaForPao(controlArea);
        Vector<LMControlAreaTrigger> triggerVector = controlAreaFull.getTriggerVector();

        modelMap.addAttribute("triggers", triggerVector);
        modelMap.addAttribute("controlArea", controlArea);

        return "dr/controlArea/getChangeTriggerValues.jsp";
    }

    @RequestMapping("/controlArea/triggerChange")
    public String triggerChange(ModelMap modelMap, int controlAreaId, Double threshold1,
                                Double offset1, Double threshold2, Double offset2,
                                YukonUserContext userContext, FlashScope flashScope) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser,
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        controlAreaService.changeTriggers(controlAreaId,
                                          threshold1,
                                          offset1,
                                          threshold2,
                                          offset2);

        demandResponseEventLogService.threeTierControlAreaTriggersChanged(yukonUser,
                                                                          controlArea.getName(),
                                                                          threshold1, offset1,
                                                                          threshold2, offset2);

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.controlArea.getChangeTriggerValues.triggerValueChanged"));
        return closeDialog(modelMap);
    }

    /**
     * Helper method to parse a time string into seconds
     * @param time - Time string in the format HH:mm
     * @return Seconds for time string
     */
    private int parseTime(String time) {

        String[] timeStrings = time.split(":");

        if (timeStrings.length != 2) {
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
            startSeconds = parseTime(startTime);
            stopSeconds = parseTime(stopTime);
        } catch (NumberFormatException e) {
            // invalid time
            return false;
        } catch (IllegalArgumentException e) {
            // invalid time
            return false;
        }

        return startSeconds < stopSeconds;
    }

    private String closeDialog(ModelMap modelMap) {
        modelMap.addAttribute("popupId", "drDialog");
        return "common/closePopup.jsp";
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
    public void setDurationFormattingService(DurationFormattingService durationFormattingService) {
        this.durationFormattingService = durationFormattingService;
    }

    @Autowired
    public void setDemandResponseEventLogService(DemandResponseEventLogService demandResponseEventLogService) {
        this.demandResponseEventLogService = demandResponseEventLogService;
    }

    @Autowired
    public void setControlAreaFieldService(ControlAreaFieldService controlAreaFieldService) {
        this.controlAreaFieldService = controlAreaFieldService;
    }

    @Autowired
    public void setTriggerFieldService(TriggerFieldService triggerFieldService) {
        this.triggerFieldService = triggerFieldService;
    }

    @Autowired
    public void setFavoritesDao(FavoritesDao favoritesDao) {
        this.favoritesDao = favoritesDao;
    }
    
    @Autowired
    public void setControlAreaNameField(ControlAreaNameField controlAreaNameField) {
        this.controlAreaNameField = controlAreaNameField;
    }
}
