package com.cannontech.web.dr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.events.loggers.DemandResponseEventLogService;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.IntegerRange;
import com.cannontech.common.util.MutableRange;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DurationFormattingService;
import com.cannontech.core.service.durationFormatter.DurationFormat;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.assetavailability.AssetAvailabilityCombinedStatus;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityPingService;
import com.cannontech.dr.controlarea.filter.PriorityFilter;
import com.cannontech.dr.controlarea.filter.StateFilter;
import com.cannontech.dr.controlarea.model.ControlAreaNameField;
import com.cannontech.dr.controlarea.model.TriggerType;
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
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonUtils;
import com.cannontech.web.util.ListBackingBean;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Ordering;

@Controller
@CheckRoleProperty(value={YukonRoleProperty.SHOW_CONTROL_AREAS, YukonRoleProperty.DEMAND_RESPONSE},requireAll=true)
public class ControlAreaController extends DemandResponseControllerBase {

    @Autowired private AssetAvailabilityPingService assetAvailabilityPingService;
    @Autowired private ControlAreaFieldService controlAreaFieldService;
    @Autowired private ControlAreaNameField controlAreaNameField;
    @Autowired private ControlAreaService controlAreaService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DemandResponseEventLogService demandResponseEventLogService;
    @Autowired private DurationFormattingService durationFormattingService;
    @Autowired private PaoAuthorizationService paoAuthorizationService;
    @Autowired private ProgramControllerHelper programControllerHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private TriggerFieldService triggerFieldService;

    public static class ControlAreaListBackingBean extends ListBackingBean {
        private String state;
        private IntegerRange priority = new IntegerRange();

        // TODO:
        // START and STOP (HH:MM only)
        // private String start;
        // private String stop;

        // probably can move this up to ListBackingBean
        private MutableRange<Double> loadCapacity = new MutableRange<Double>();

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public IntegerRange getPriority() {
            return priority;
        }

        public void setPriority(IntegerRange priority) {
            this.priority = priority;
        }

        public MutableRange<Double> getLoadCapacity() {
            return loadCapacity;
        }

        public void setLoadCapacity(MutableRange<Double> loadCapacity) {
            this.loadCapacity = loadCapacity;
        }
    }

    private SimpleValidator<ControlAreaListBackingBean> filterValidator =
        new SimpleValidator<ControlAreaListBackingBean>(ControlAreaListBackingBean.class) {
            @Override
            protected void doValidation(ControlAreaListBackingBean target,
                    Errors errors) {
                if (!target.priority.isValid() || target.priority.isEmpty()) {
                    errors.reject("priorityFromAfterTo");
                }
            }
    };

    @RequestMapping("/controlArea/list")
    public String list(ModelMap model,
            @ModelAttribute("backingBean") ControlAreaListBackingBean backingBean,
            BindingResult bindingResult, 
            FlashScope flashScope,
            YukonUserContext userContext) {
        
        filterValidator.validate(backingBean, bindingResult);

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
        model.addAttribute("isFiltered", isFiltered);

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
        SearchResults<DisplayablePao> searchResult =
            controlAreaService.filterControlAreas(filter, sorter, startIndex,
                                                  backingBean.getItemsPerPage(), userContext);

        model.addAttribute("searchResult", searchResult);
        model.addAttribute("controlAreas", searchResult.getResultList());

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);

        return "dr/controlArea/list.jsp";
    }

    @RequestMapping("/controlArea/detail")
    public String detail(int controlAreaId, ModelMap model,YukonUserContext userContext,
            @ModelAttribute("backingBean") ProgramControllerHelper.ProgramListBackingBean backingBean,
            BindingResult bindingResult, FlashScope flashScope, LiteYukonUser user) {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(user, controlArea, Permission.LM_VISIBLE);

        model.addAttribute("controlArea", controlArea);
        UiFilter<DisplayablePao> detailFilter = new ForControlAreaFilter(controlAreaId);
        programControllerHelper.filterPrograms(model, userContext, backingBean,
                                               bindingResult, detailFilter);

        addFilterErrorsToFlashScopeIfNecessary(model, bindingResult, flashScope);
        
        return "dr/controlArea/detail.jsp";
    }
    
    @RequestMapping("/controlArea/assetAvailability")
    public String assetAvailability(ModelMap model, YukonUserContext userContext, int paoId) {
        model.addAttribute("paoId", paoId);
        DisplayablePao controlArea = controlAreaService.getControlArea(paoId);
        if(rolePropertyDao.checkProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser())) {
            getAssetAvailabilityInfo(controlArea, model, userContext);
        }
        return "dr/assetAvailability.jsp";
    }

    
    @RequestMapping("/controlArea/assetDetails")
    public String assetDetails(@RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                               @RequestParam(defaultValue="1") int page,
                               @RequestParam(defaultValue="SERIAL_NUM") AssetDetailsColumn sortBy,
                               final boolean descending,
                               int assetId, 
                               ModelMap model, 
                               YukonUserContext userContext) throws IOException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.SHOW_ASSET_AVAILABILITY, userContext.getYukonUser());
        DisplayablePao controlArea = controlAreaService.getControlArea(assetId);

        List<AssetAvailabilityDetails> resultsList = getResultsList(controlArea, userContext, null);
        sortAssetDetails(resultsList, sortBy, descending, userContext);
        
        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
        SearchResults<AssetAvailabilityDetails> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, resultsList);

        model = getAssetAvailabilityInfo(controlArea, model, userContext);
        
        model.addAttribute("assetId", assetId);
        model.addAttribute("controlAreaId", assetId);
        model.addAttribute("controlArea", controlArea);
        model.addAttribute("type", "controlArea");
        model.addAttribute("result", result);
        model.addAttribute("itemsPerPage", itemsPerPage);
        
        return "dr/assetDetails.jsp";
    }


    /**
     * Used for paging and filtering operations.
     * @throws IOException 
     * @throws  
     */
    @RequestMapping("/controlArea/page")
    public String page(ModelMap model, 
                       YukonUserContext userContext,
                       int assetId,
                       @RequestParam(defaultValue="SERIAL_NUM") AssetDetailsColumn sortBy,
                       final boolean descending,
                       @RequestParam(defaultValue=ITEMS_PER_PAGE) int itemsPerPage, 
                       @RequestParam(defaultValue="1") int page,
                       @RequestParam(value="filter[]", required=false) AssetAvailabilityCombinedStatus[] filters) {

        DisplayablePao controlArea = controlAreaService.getControlArea(assetId);
        List<AssetAvailabilityDetails> resultsList = getResultsList(controlArea, userContext, filters);
        sortAssetDetails(resultsList, sortBy, descending, userContext);

        itemsPerPage = CtiUtilities.itemsPerPage(itemsPerPage);
        SearchResults<AssetAvailabilityDetails> result = 
                SearchResults.pageBasedForWholeList(page, itemsPerPage, resultsList);
        
        model.addAttribute("result", result);
        model.addAttribute("type", "controlArea");
        model.addAttribute("assetId", assetId);
        model.addAttribute("colorMap", colorMap);
        model.addAttribute("itemsPerPage", itemsPerPage);
        
        return "dr/assetTable.jsp";
    }
    
    @RequestMapping("/controlArea/{id}/aa/download/{type}")
    public void downloadAssetAvailability(HttpServletResponse response, 
            YukonUserContext userContext, 
            @PathVariable int id, 
            @PathVariable String type) 
    throws IOException {
        
        List<AssetAvailabilityCombinedStatus> filters = getAssetAvailabilityFilters(type);
        
        downloadAssetAvailability(id, userContext, filters.toArray(new AssetAvailabilityCombinedStatus[]{}), response);
    }
    
    @RequestMapping("/controlArea/downloadToCsv")
    public void downloadToCsv(int assetId,
            @RequestParam(value="filter[]", required=false) AssetAvailabilityCombinedStatus[] filters,
            HttpServletResponse response,
            YukonUserContext userContext) throws IOException {
        
        downloadAssetAvailability(assetId, userContext, filters, response);
    }
    
    private void downloadAssetAvailability(int assetId, 
            YukonUserContext userContext, 
            AssetAvailabilityCombinedStatus[] filters, 
            HttpServletResponse response) throws IOException {
        
        DisplayablePao controlArea = controlAreaService.getControlArea(assetId);
        
        // get the header row
        String[] headerRow = getDownloadHeaderRow(userContext);
        // get the data rows
        List<String[]> dataRows = getDownloadDataRows(controlArea, filters, userContext);
        
        String dateStr = dateFormattingService.format(new LocalDateTime(userContext.getJodaTimeZone()), 
                DateFormatEnum.BOTH, userContext);
        String fileName = "controlArea_" + controlArea.getName() + "_" + dateStr + ".csv";
        WebFileUtils.writeToCSV(response, headerRow, dataRows, fileName);
    }
    
    @ResponseBody
    @RequestMapping("/controlArea/pingDevices")
    public void pingDevices(int assetId, LiteYukonUser user) {
        DisplayablePao controlArea = controlAreaService.getControlArea(assetId);
        assetAvailabilityPingService.readDevicesInDrGrouping(controlArea.getPaoIdentifier(), user);
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
    public @ResponseBody Map<String, String> setEnabled(int controlAreaId, boolean isEnabled, YukonUserContext userContext, FlashScope flashScope) {

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

        return Collections.singletonMap("action", "reload");
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
    public @ResponseBody Map<String, String> resetPeak(int controlAreaId, 
                            YukonUserContext userContext, FlashScope flashScope) {

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

        return Collections.singletonMap("action", "reload");
    }

    // TIME WINDOW - show
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
        
        ControlAreaTimeWindowDto controlAreaTimeWindowDto = new ControlAreaTimeWindowDto();
        controlAreaTimeWindowDto.setStartTime(startTime);
        controlAreaTimeWindowDto.setStopTime(stopTime);

        modelMap.addAttribute("controlAreaTimeWindowDto", controlAreaTimeWindowDto);
        modelMap.addAttribute("controlArea", controlArea);

        return "dr/controlArea/getChangeTimeWindowValues.jsp";
    }

    // TIME WINDOW - validate, confirm
    @RequestMapping("/controlArea/sendChangeTimeWindowConfirm")
    public String sendChangeTimeWindowConfirm(ModelMap modelMap, int controlAreaId,
                                              @ModelAttribute("controlAreaTimeWindowDto") ControlAreaTimeWindowDto controlAreaTimeWindowDto, 
                                              BindingResult bindingResult,
                                              YukonUserContext userContext, FlashScope flashScope) {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        paoAuthorizationService.verifyAllPermissions(userContext.getYukonUser(),
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);

        modelMap.addAttribute("controlArea", controlArea);
        
        controlAreaTimeWindowDtoValidator.validate(controlAreaTimeWindowDto, bindingResult);

        if(bindingResult.hasErrors()) {
            List<MessageSourceResolvable> errorsForBindingResult = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(errorsForBindingResult);
            return "dr/controlArea/getChangeTimeWindowValues.jsp";
        }
        
        return "dr/controlArea/sendChangeTimeWindowConfirm.jsp";
    }

    // TIME WINDOW - send
    @RequestMapping("/controlArea/changeTimeWindow")
    public @ResponseBody Map<String, String> changeTimeWindow(int controlAreaId, String startTime, String stopTime,
            YukonUserContext userContext, FlashScope flashScope) {

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

        return Collections.singletonMap("action", "reload");
    }

    // TRIGGER VALUES - show
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
        
        TriggersDto triggersDto = new TriggersDto();
        for (LMControlAreaTrigger trigger : triggerVector) {
            if (trigger.getTriggerNumber().intValue() == 1) {
                triggersDto.setTrigger1(trigger);
            }
            if (trigger.getTriggerNumber().intValue() == 2) {
                triggersDto.setTrigger2(trigger);
            }
        }

        modelMap.addAttribute("triggersDto", triggersDto);
        modelMap.addAttribute("controlArea", controlArea);

        return "dr/controlArea/getChangeTriggerValues.jsp";
    }

    // TRIGGER VALUES - validate, send
    @RequestMapping("/controlArea/triggerChange")
    public String triggerChange(HttpServletResponse resp, ModelMap modelMap, int controlAreaId, 
                                @ModelAttribute("triggersDto") TriggersDto triggersDto, 
                                BindingResult bindingResult, YukonUserContext userContext, 
                                FlashScope flashScope) throws IOException {

        DisplayablePao controlArea = controlAreaService.getControlArea(controlAreaId);
        LiteYukonUser yukonUser = userContext.getYukonUser();
        paoAuthorizationService.verifyAllPermissions(yukonUser,
                                                     controlArea,
                                                     Permission.LM_VISIBLE,
                                                     Permission.CONTROL_COMMAND);
        
        triggersDtoValidator.validate(triggersDto, bindingResult);
        if (bindingResult.hasErrors()) {
            
            List<MessageSourceResolvable> errorsForBindingResult = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(errorsForBindingResult);
            
            modelMap.addAttribute("controlArea", controlArea);
            return "dr/controlArea/getChangeTriggerValues.jsp";
        }
        
        controlAreaService.changeTriggers(controlAreaId,
                                          triggersDto.getTrigger1().getThreshold(), triggersDto.getTrigger1().getMinRestoreOffset(),
                                          triggersDto.getTrigger2().getThreshold(), triggersDto.getTrigger2().getMinRestoreOffset());

        demandResponseEventLogService.threeTierControlAreaTriggersChanged(yukonUser,
                                                                          controlArea.getName(),
                                                                          triggersDto.getTrigger1().getThreshold(), triggersDto.getTrigger1().getMinRestoreOffset(),
                                                                          triggersDto.getTrigger2().getThreshold(), triggersDto.getTrigger2().getMinRestoreOffset());

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.dr.controlArea.getChangeTriggerValues.triggerValueChanged"));

        resp.setContentType("application/json");
        resp.getWriter().write(JsonUtils.toJson(Collections.singletonMap("action", "reload")));
        return null;
    }
    
    private Validator triggersDtoValidator = new SimpleValidator<TriggersDto>(TriggersDto.class) {
        @Override
        public void doValidation(TriggersDto target, Errors errors) {
            
            for (LMControlAreaTrigger trigger : target.getTriggers()) {
                
                if (trigger.getTriggerType() == TriggerType.THRESHOLD || trigger.getTriggerType() == TriggerType.THRESHOLD_POINT) {
                
                    int triggerNumber = trigger.getTriggerNumber();
                    Double threshold = trigger.getThreshold();
                    Double minRestoreOffset = trigger.getMinRestoreOffset();
                    String thresholdFieldName = "trigger" + triggerNumber + ".threshold";
                    String offsetFieldName = "trigger" + triggerNumber + ".minRestoreOffset";
                    
                    if (trigger.getTriggerType() == TriggerType.THRESHOLD && 
                        threshold == null && errors.getFieldErrorCount(thresholdFieldName) == 0) {
                        errors.rejectValue(thresholdFieldName, "getChangeTriggerValues.thresholdBlank");
                    }
                    
                    if (threshold != null && threshold >= 1000000.0) {
                        errors.rejectValue(thresholdFieldName, "getChangeTriggerValues.thresholdOverLimit");
                    }
                    if (threshold != null && threshold <= -1000000.0) {
                        errors.rejectValue(thresholdFieldName, "getChangeTriggerValues.thresholdUnderLimit");
                    }
                    if (minRestoreOffset != null && minRestoreOffset >= 100000.0) {
                        errors.rejectValue(offsetFieldName, "getChangeTriggerValues.offsetOverLimit");
                    }
                    if (minRestoreOffset != null && minRestoreOffset <= -100000.0) {
                        errors.rejectValue(offsetFieldName, "getChangeTriggerValues.offsetUnderLimit");
                    }
                    
                }
            }
        }
    };

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
    
    private Validator controlAreaTimeWindowDtoValidator = new SimpleValidator<ControlAreaTimeWindowDto>(ControlAreaTimeWindowDto.class) {
        @Override
        public void doValidation(ControlAreaTimeWindowDto target, Errors errors) {
            
            int startSeconds = 0;
            int stopSeconds = 0;

            try {
                startSeconds = parseTime(target.getStartTime());
            } catch (NumberFormatException e) {
                errors.rejectValue("startTime", "getChangeTimeWindowValues.invalidStartTime");
            } catch (IllegalArgumentException e) {
                errors.rejectValue("startTime", "getChangeTimeWindowValues.invalidStartTime");
            }
            
            try {
                stopSeconds = parseTime(target.getStopTime());
            } catch (NumberFormatException e) {
                errors.rejectValue("stopTime", "getChangeTimeWindowValues.invalidStopTime");
            } catch (IllegalArgumentException e) {
                errors.rejectValue("stopTime", "getChangeTimeWindowValues.invalidStopTime");
            }

            if (!errors.hasErrors() && startSeconds >= stopSeconds) {
                errors.rejectValue("startTime", "getChangeTimeWindowValues.startTimeAfterStopTime");
            }
        }
    };

    private void addFilterErrorsToFlashScopeIfNecessary(ModelMap model,
            BindingResult bindingResult, FlashScope flashScope) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hasFilterErrors", true);
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        programControllerHelper.initBinder(binder, userContext, "controlArea");
    }
}
