package com.cannontech.web.tools.dataViewer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.tag.service.TagService;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.dao.DisplayDataDao.SortBy;
import com.cannontech.common.tdc.model.AlarmFilter;
import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.common.tdc.model.ColumnType;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.common.util.EnabledStatus;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.TagDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.dispatch.command.service.CommandService;
import com.cannontech.tags.Tag;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@Controller
@CheckRole(YukonRole.TABULAR_DISPLAY_CONSOLE)
public class TdcDisplayController {

    @Autowired private ServerDatabaseCache cache;
    @Autowired private DisplayDao displayDao;
    @Autowired private TdcService tdcService;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private CommandService commandService;
    @Autowired private TagService tagService;
    @Autowired private TagDao tagDao;
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private TdcCustomDisplayValidator customDisplayValidator;
    
    private final static String baseKey = "yukon.web.modules.tools.tdc.";
    private final static int itemsPerPage = 50;
    
    private final Validator validator = new SimpleValidator<DisplayBackingBean>(DisplayBackingBean.class) {
        @Override
        protected void doValidation(DisplayBackingBean bean, Errors errors) {
            if (bean.getValue() != null) {
                YukonValidationUtils.checkIsValidDouble(errors, "value", bean.getValue());
            }
            else if (bean.getDisplayName() != null) {
                YukonValidationUtils.checkIsBlankOrExceedsMaxLength(errors, "displayName", bean.getDisplayName(), false, 30);
                Display display = displayDao.findDisplayByName(bean.getDisplayName());
                if (display != null) {
                    errors.rejectValue("displayName", "yukon.web.error.nameConflict");
                }
            }
        }
    };

    @RequestMapping(value = "data-viewer/{displayId}", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap model, @PathVariable int displayId, FlashScope flashScope) {
        model.addAttribute("mode", PageEditMode.VIEW);
        Display display;
        try {
            display = displayDao.getDisplayById(displayId);
        } catch (EmptyResultDataAccessException e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.display.load.error"));
            return "redirect:/tools/data-viewer";
        }
        if (display.isPageable()) {
            PagingParameters pagingParameter = PagingParameters.of(50, 1);
            SortingParameters sorting = SortingParameters.of(ColumnType.TIME_STAMP.name(), Direction.desc);
            return setupPageableModel(userContext, model, displayId, sorting, pagingParameter, new DateTime(userContext.getJodaTimeZone()), AlarmFilter.NO_FILTER.name());
        }
        
        PagingParameters paging = null;
        List<DisplayData> displayData = tdcService.getDisplayData(display);
        
        int totalCount = displayData.size();
        //display all the data on one page
        if (totalCount == 0) {
            totalCount = itemsPerPage;
        }
        paging = PagingParameters.of(totalCount, 1);
    
        DisplayBackingBean backingBean = new DisplayBackingBean();
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("displayName", display.getName());
        model.addAttribute("display", display);
        model.addAttribute("colorStateBoxes", tdcService.getUnackAlarmColorStateBoxes(display, displayData));
        if (display.getType() == DisplayType.CUSTOM_DISPLAYS) {
            model.addAttribute("hasPointValueColumn", display.hasColumn(ColumnType.POINT_VALUE));
        }
        SearchResults<DisplayData> result = SearchResults.pageBasedForSublist(displayData, paging, totalCount);
        model.addAttribute("result", result);
        return "data-viewer/display.jsp";
    }
    
    @RequestMapping(value = "data-viewer/{displayId}/edit", method = RequestMethod.GET)
    public String editCustomView(ModelMap model, @PathVariable int displayId, FlashScope flashScope) {
        model.addAttribute("mode", PageEditMode.EDIT);
        Display display = displayDao.getDisplayById(displayId);
        if (display == null) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.display.load.error"));
            return "redirect:/tools/data-viewer";
        }
        model.addAttribute("display", display);
        List<DisplayData> displayData = tdcService.getDisplayData(display);
        model.addAttribute("displayData", displayData);
        
        List<Integer> selectedIds = displayData.stream().map(item -> item.getPointId()).collect(Collectors.toList());
        model.addAttribute("selectedIds", selectedIds);

        return "data-viewer/customDisplay.jsp";
    }
    
    @RequestMapping(value = "data-viewer/create", method = RequestMethod.GET)
    public String createCustomView(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        model.addAttribute("display", new Display());
        return "data-viewer/customDisplay.jsp";
    }
    
    @RequestMapping(value = "data-viewer/save", method = RequestMethod.POST)
    public String saveCustomView(@ModelAttribute("display") Display display, ModelMap model, BindingResult result, HttpServletResponse resp,
                                 @RequestParam(value="pointIds", required=false, defaultValue="") String pointIds, FlashScope flash) {
        PageEditMode pageEditMode = display.getDisplayId()!= 0 ? PageEditMode.EDIT : PageEditMode.CREATE;
        List<Integer> pointList = new ArrayList<Integer>();
        if (!pointIds.isEmpty()) {
            List<String> pointIdStrings = Lists.newArrayList(pointIds.split(","));
            pointList = Lists.transform(pointIdStrings, new Function<String, Integer>(){
                @Override public Integer apply(String input) {return Integer.parseInt(input);}
            });
        }
        customDisplayValidator.validate(display, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", pageEditMode);
            model.addAttribute("displayData", generateDisplayDataFromPoints(pointList));
            model.addAttribute("selectedIds", pointIds);
            return "data-viewer/customDisplay.jsp";
        }
        String name = display.getName();
        String description = display.getDescription();

        int id = display.getDisplayId();
        try {
            if (id != 0) {
                tdcService.updateCustomDisplay(id, name, description, pointList);
            } else {
                display = tdcService.createCustomDisplayForPoints(name, description, pointList);
            }
        } catch (DuplicateException e) {
            result.rejectValue("name", "yukon.web.error.nameConflict");
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("mode", pageEditMode);
            model.addAttribute("displayData", generateDisplayDataFromPoints(pointList));
            model.addAttribute("selectedIds", pointIds);
            return "data-viewer/customDisplay.jsp";
        }
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "display.SAVE.success"));
        return "redirect:/tools/data-viewer/" + display.getDisplayId();
    }
    
    private List<DisplayData> generateDisplayDataFromPoints (List<Integer> pointIds) {
        List<DisplayData> data = new ArrayList<DisplayData>();
        pointIds.forEach(id -> {
            DisplayData dd = new DisplayData();
            dd.setPointId(id);
            LitePoint point = pointDao.getLitePoint(id);
            dd.setPointName(point.getPointName());
            dd.setDeviceName(paoDao.getLiteYukonPAO(point.getPaobjectID()).getPaoName());
            data.add(dd);
        });
        return data;
    }
    
    @RequestMapping(value = "data-viewer/{displayId}/page", method = RequestMethod.GET)
    public String page(YukonUserContext userContext, ModelMap model, @PathVariable int displayId, 
                       @DefaultSort(dir=Direction.desc, sort="TIME_STAMP") SortingParameters sorting, 
                       @DefaultItemsPerPage(50) PagingParameters pagingParameters,
                       String date, String alarmFilter) {
        DateTime dateTime = null;
        DateTimeZone timeZone = userContext.getJodaTimeZone();
        if (date != null) {
            if (date.contains("-")) {
                dateTime = DateTime.parse(date).withZone(timeZone);
            }
            else {
                DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
                dateTime = formatter.parseDateTime(date).withTimeAtStartOfDay().withZone(timeZone);
            }
        }
        
        return setupPageableModel(userContext, model, displayId, sorting, pagingParameters, dateTime, alarmFilter);
    }
    
    private String setupPageableModel(YukonUserContext userContext, ModelMap model, int displayId, 
                                      SortingParameters sorting, PagingParameters pagingParameters, DateTime dateTime, String alarmFilter) {
        model.addAttribute("pageable", true);
        model.addAttribute("mode", PageEditMode.VIEW);
        Display display = displayDao.getDisplayById(displayId);
        model.addAttribute("displayName", display.getName());
        model.addAttribute("display", display);
        DisplayBackingBean backingBean = new DisplayBackingBean();
        
        if (dateTime == null) {
            dateTime = new DateTime();
        }
        
        backingBean.setDate(dateTime);
        AlarmFilter filter = null;
        if (StringUtils.isNotEmpty(alarmFilter)) {
            backingBean.setAlarmFilter(alarmFilter);
            filter = AlarmFilter.valueOf(alarmFilter);
        }
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("alarmFilters", AlarmFilter.values());
        ColumnType column = ColumnType.valueOf(sorting.getSort());
        SortBy sortBy =  SortBy.getSortBy(column);
        SearchResults<DisplayData> result = tdcService.getSortedDisplayData(display, userContext.getJodaTimeZone(), pagingParameters, sortBy, sorting.getDirection(), dateTime, filter);
        model.addAttribute("result", result);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<SortableColumn> sortableColumns = display.getColumns().stream()
                                                      .map(c -> SortableColumn.of(sorting, accessor.getMessage(baseKey+c.getType().name()), c.getType().name()))
                                                      .collect(Collectors.toList());
        model.addAttribute("sortableColumns", sortableColumns);
    
        return "data-viewer/display.jsp";
    }
    
    @RequestMapping(value = "data-viewer/enable-disable", method = RequestMethod.POST)
    public String enableDisable(YukonUserContext userContext, ModelMap model, int pointId) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("enableDisable", EnabledStatus.values());
        long tags = asyncDynamicDataSource.getTags(pointId);

        if (TagUtils.isDeviceOutOfService(tags)) {
            backingBean.setDeviceEnabledStatus(EnabledStatus.DISABLED);
        }
        if (TagUtils.isPointOutOfService(tags)) {
            backingBean.setPointEnabledStatus(EnabledStatus.DISABLED);
        }
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        
        return "data-viewer/enableDisablePopup.jsp";
    }

    @RequestMapping(value = "data-viewer/enableDisableSend", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> enableDisableSend(YukonUserContext userContext,
                                 @ModelAttribute("backingBean") DisplayBackingBean backingBean) {

        long tags = asyncDynamicDataSource.getTags(backingBean.getPointId());
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        int deviceId = liteYukonPAO.getPaoIdentifier().getPaoId();

        if (TagUtils.isDeviceOutOfService(tags)) {
            if (backingBean.getDeviceEnabledStatus() == EnabledStatus.ENABLED) {
                commandService.toggleDeviceEnablement(deviceId, false, userContext.getYukonUser());
            }
        } else {
            if (backingBean.getDeviceEnabledStatus() == EnabledStatus.DISABLED) {
                commandService.toggleDeviceEnablement(deviceId, true, userContext.getYukonUser());
            }
        }
        if (TagUtils.isPointOutOfService(tags)) {
            if (backingBean.getPointEnabledStatus() == EnabledStatus.ENABLED) {
                commandService.togglePointEnablement(backingBean.getPointId(),
                                                 false,
                                                 userContext.getYukonUser());
            }
        } else {
            if (backingBean.getPointEnabledStatus() == EnabledStatus.DISABLED) {
                commandService.togglePointEnablement(backingBean.getPointId(),
                                                 true,
                                                 userContext.getYukonUser());
            }
        }
        return Collections.singletonMap("success", "success");
    }

    @RequestMapping(value = "data-viewer/unacknowledged", method = RequestMethod.POST)
    public String unacknowledged(YukonUserContext userContext, ModelMap model, int pointId) {

        List<DisplayData> alarms = tdcService.getUnacknowledgedAlarms(pointId);
        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        model.addAttribute("alarms", alarms);
        model.addAttribute("backingBean", backingBean);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());

        MessageSourceResolvable title =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.unack.title",
                                             liteYukonPAO.getPaoName(), litePoint.getPointName());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("title", accessor.getMessage(title));
        
        return "data-viewer/unackPopup.jsp";
    }

    @RequestMapping(value = "data-viewer/acknowledge-alarm", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> acknowledgeAlarm(YukonUserContext userContext, int pointId, int condition) {

        if(condition == 0 && tdcService.getUnackAlarmCountForPoint(pointId) == 1){
            tdcService.acknowledgeAlarmsForPoint(pointId, userContext.getYukonUser());
        }else{
            tdcService.acknowledgeAlarm(pointId, condition, userContext.getYukonUser());
        }
        return Collections.singletonMap("success", "success");
    }

    @RequestMapping(value = "data-viewer/acknowledge-alarms-for-display", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> acknowledgeAlarmsForDisplay(YukonUserContext userContext, int displayId) {

        Display display = displayDao.getDisplayById(displayId);
        int alarms = tdcService.acknowledgeAlarmsForDisplay(display, userContext.getYukonUser());
        MessageSourceResolvable successMsg =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.ack.success", alarms);
        return getJSONSuccess(successMsg, userContext);
    }

    @RequestMapping(value = "data-viewer/acknowledge-alarms-for-point", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> acknowledgeAlarmsForPoint(YukonUserContext userContext, int pointId) {

        int alarms = tdcService.acknowledgeAlarmsForPoint(pointId, userContext.getYukonUser());
        YukonMessageSourceResolvable successMsg =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.ack.success", alarms);
        return getJSONSuccess(successMsg, userContext);
    }

    @RequestMapping(value = "data-viewer/manual-control", method = RequestMethod.POST)
    public String manualControl(ModelMap model, int pointId, int deviceId) {
        
        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        backingBean.setDeviceId(deviceId);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        if (litePoint.getPointTypeEnum() == PointType.Analog) {
            PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(pointId);
            backingBean.setValue(pointValue.getValue());
        } else if (litePoint.getPointTypeEnum() == PointType.Status) {
            LiteStateGroup group = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
            List<LiteState> stateList = new ArrayList<>(group.getStatesList());
            
            if (litePoint.getPointTypeEnum() == PointType.Status) {
                long tags = asyncDynamicDataSource.getTags(litePoint.getLiteID());
                boolean controllable = TagUtils.isControllablePoint(tags) && TagUtils.isControlEnabled(tags);
                if (controllable) {
                    stateList.removeIf(state -> state.getLiteID() < 0 || state.getLiteID() > 1);
                }
            }
            model.put("stateList", stateList);
        }
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        model.addAttribute("backingBean", backingBean);
        return "data-viewer/manualControlPopup.jsp";
    }
    
    @RequestMapping(value = "data-viewer/manualControlSend", method = RequestMethod.POST)
    public String manualControlSend(HttpServletResponse response, YukonUserContext userContext,
                                    @ModelAttribute("backingBean") DisplayBackingBean backingBean,
                                    BindingResult bindingResult, ModelMap model,
                                    FlashScope flashScope) throws IOException {
        
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        if (litePoint.getPointTypeEnum() == PointType.Analog) {
            validator.validate(backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
                model.put("deviceName", liteYukonPAO.getPaoName());
                model.put("pointName", litePoint.getPointName());
                model.addAttribute("backingBean", backingBean);
                List<MessageSourceResolvable> messages =
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setError(messages);
                return "manualControlPopup.jsp";
            }
            commandService.sendAnalogOutputRequest(backingBean.getPointId(),
                                               backingBean.getValue(),
                                               userContext.getYukonUser());

        } else if (litePoint.getPointTypeEnum() == PointType.Status) {
            if (backingBean.getStateId() == 0) {
                commandService.toggleControlRequest(backingBean.getDeviceId(),
                                                backingBean.getPointId(),
                                                false,
                                                userContext.getYukonUser());
            } else if (backingBean.getStateId() == 1) {
                commandService.toggleControlRequest(backingBean.getDeviceId(),
                                                backingBean.getPointId(),
                                                true,
                                                userContext.getYukonUser());
            }
        }

        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toJson(Collections.singletonMap("action", "close")));
        return null;
    }

    @RequestMapping("data-viewer/alt-scan-rate")
    public String altScanRate(ModelMap model, int deviceId, String deviceName) {
        
        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setDeviceId(deviceId);
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("deviceName", cache.getAllPaosMap().get(deviceId).getPaoName());
        model.addAttribute("altScanRates", AltScanRate.values());
        
        return "data-viewer/altScanRatePopup.jsp";
    }

    @RequestMapping(value="data-viewer/altScanRateSend", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> altScanRateSend(YukonUserContext userContext, ModelMap model,
                               @ModelAttribute("backingBean") DisplayBackingBean backingBean) {

        commandService.sendAltScanRate(backingBean.getDeviceId(),
                                   backingBean.getAltScanRate(),
                                   userContext.getYukonUser());
        return Collections.singletonMap("success", "success");
    }

    @RequestMapping(value = "data-viewer/tags", method = RequestMethod.POST)
    public String tags(ModelMap model, int deviceId, int pointId) {
        boolean isDeviceControlInhibited =
            TagUtils.isDeviceControlInhibited(asyncDynamicDataSource.getTags(pointId));
        List<LiteTag> allTags = tagDao.getAllTags();
        List<Tag> tags = tagService.getTags(pointId);
        model.addAttribute("allTags", allTags);
        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setTags(tags);
        backingBean.setDeviceId(deviceId);
        backingBean.setPointId(pointId);
        backingBean.setDeviceControlInhibited(isDeviceControlInhibited);
        model.addAttribute("backingBean", backingBean);
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        return "data-viewer/tagsPopup.jsp";
    }
    
    @RequestMapping(value = "data-viewer/tag-add", method = RequestMethod.POST)
    public String tagAdd(ModelMap model, @ModelAttribute("backingBean") DisplayBackingBean backingBean) {
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        backingBean.getTags().add(new Tag());
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("allTags",  tagDao.getAllTags());
        return "data-viewer/tagsPopup.jsp";
    }
    
    @RequestMapping(value = "data-viewer/tags-save", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> tagsSave(YukonUserContext userContext,
                        @ModelAttribute("backingBean") DisplayBackingBean backingBean)
            throws Exception {
        List<Tag> tags = tagService.getTags(backingBean.getPointId());

        Map<Integer, Tag> mappedOldTags = Maps.uniqueIndex(tags, new Function<Tag, Integer>() {
            @Override
            public Integer apply(Tag tag) {
                return tag.getInstanceID();
            }
        });
        List<Tag> tagsToCreate = new ArrayList<>();
        List<Tag> tagsToUpdate = new ArrayList<>();

        for (Tag tag : backingBean.getTags()) {
            if (tag.getInstanceID() == 0) {
                tagsToCreate.add(tag);
                continue;
            }
            Tag oldTag = mappedOldTags.get(tag.getInstanceID());
            if (oldTag.getTagID() != tag.getTagID()
                || !oldTag.getDescriptionStr().equals(tag.getDescriptionStr())) {
                tagsToUpdate.add(tag);
            }
        }
        for (Tag tag : tagsToUpdate) {
            tagService.updateTag(tag, userContext.getYukonUser());
        }
        for (Tag tag : tagsToCreate) {
            tagService.createTag(backingBean.getPointId(),
                                 tag.getTagID(),
                                 tag.getDescriptionStr(),
                                 userContext.getYukonUser());
        }
        
        SetView<Integer> tagsToRemove =
            Sets.difference(mappedOldTags.keySet(), Sets.newHashSet(Iterables.transform(backingBean
                .getTags(), new Function<Tag, Integer>() {
                @Override
                public Integer apply(Tag tag) {
                    return tag.getInstanceID();
                }
            })));

        for (Integer instanceId : tagsToRemove) {
            tagService.removeTag(backingBean.getPointId(),
                                 instanceId,
                                 userContext.getYukonUser());
        }
        boolean isDeviceControlInhibited =
            TagUtils.isDeviceControlInhibited(asyncDynamicDataSource.getTags(backingBean.getPointId()));
        if(isDeviceControlInhibited && !backingBean.isDeviceControlInhibited()){
            commandService.toggleControlEnablement(backingBean.getDeviceId(),
                                                   false,
                                                   userContext.getYukonUser());
        }else if(!isDeviceControlInhibited && backingBean.isDeviceControlInhibited()){
            commandService.toggleControlEnablement(backingBean.getDeviceId(),
                                                   true,
                                                   userContext.getYukonUser()); 
        }
        return Collections.singletonMap("success", "success");
    }

    @RequestMapping(value = "data-viewer/tag-remove", method = RequestMethod.POST)
    public String tagRemove(ModelMap model,
                           @ModelAttribute("backingBean") DisplayBackingBean backingBean) {
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        backingBean.getTags().remove(backingBean.getRowIndex());
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("allTags",  tagDao.getAllTags());
        return "data-viewer/tagsPopup.jsp";
    }

    @RequestMapping(value = "data-viewer/{displayId}/download", method = RequestMethod.GET)
    public String download(HttpServletResponse response, @PathVariable int displayId,
                           YukonUserContext userContext,
                           String date, String alarmFilter)
            throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Display display = displayDao.getDisplayById(displayId);
        List<DisplayData> displayData;
        if (display.isPageable()) {
            AlarmFilter filter = null;
            if (alarmFilter != null) {
                filter = AlarmFilter.valueOf(alarmFilter);
            }
            displayData = tdcService.getSortedDisplayData(display, null, PagingParameters.EVERYTHING, null, null, new DateTime(date), filter).getResultList();

        }
        else {
            displayData = tdcService.getDisplayData(display);
        }
        TdcDownloadHelper helper =
            new TdcDownloadHelper(accessor,
                                  registrationService,
                                  dateFormattingService,
                                  tdcService,
                                  display,
                                  displayData,
                                  userContext);
        List<String> columnNames = helper.getColumnNames();
        List<List<String>> dataGrid = helper.getDataGrid();
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, display.getName() + "_" + now + ".csv");
        return null;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(AltScanRate.class,
                                    new EnumPropertyEditor<>(AltScanRate.class));
        binder.registerCustomEditor(EnabledStatus.class,
                                    new EnumPropertyEditor<>(EnabledStatus.class));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    
    private Map<String, String> getJSONSuccess(MessageSourceResolvable successMsg,
                                      YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return Collections.singletonMap("success", accessor.getMessage(successMsg));
    }

    @RequestMapping(value = "data-viewer/copy", method = RequestMethod.POST)
    public String copy(ModelMap model, int displayId) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setDisplayId(displayId);
        Display display = displayDao.getDisplayById(displayId);
        model.put("displayName", display.getName());
        model.addAttribute("backingBean", backingBean);
        return "data-viewer/copyPopup.jsp";
    }
    
    @RequestMapping(value = "data-viewer/copySend", method = RequestMethod.POST)
    public String copySend(HttpServletResponse response,
                                  @ModelAttribute("backingBean") DisplayBackingBean backingBean,
                                  BindingResult bindingResult, ModelMap model, FlashScope flashScope) throws IOException {
        
        
        validator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            Display display = displayDao.getDisplayById(backingBean.getDisplayId());
            model.put("displayName", display.getName());
            model.addAttribute("backingBean", backingBean);
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setError(messages);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            model.put("displayName", display.getName());
            model.addAttribute("backingBean", backingBean);
            return "data-viewer/copyPopup.jsp";
        }

        Display newDisplay = tdcService.copyCustomDisplay(backingBean.getDisplayId(), backingBean.getDisplayName());

        Map<String, Object> data = new HashMap<>();
        data.put("displayId", newDisplay.getDisplayId());
        
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), data);
        
        return null;
    }

    @RequestMapping(value = "data-viewer/{displayId}/deleteCustomDisplay", method = RequestMethod.GET)
    public String deleteCustomDisplay(FlashScope flash, @PathVariable int displayId) {
        Display display = displayDao.getDisplayById(displayId);
        tdcService.deleteCustomDisplay(displayId);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "display.DELETE.success", display.getName()));
        return "redirect:/tools/data-viewer";
    }
}