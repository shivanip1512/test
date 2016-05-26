package com.cannontech.web.tools.dataViewer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.tag.service.TagService;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.common.tdc.model.ColumnType;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.common.util.EnabledStatus;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.TagDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.dispatch.command.service.CommandService;
import com.cannontech.tags.Tag;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
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
    
    private final static int itemsPerPage = 50;

    private final Validator validator = new SimpleValidator<DisplayBackingBean>(DisplayBackingBean.class) {
        @Override
        protected void doValidation(DisplayBackingBean bean, Errors errors) {
            YukonValidationUtils.checkIsValidDouble(errors, "value", bean.getValue());
        }
    };

    @RequestMapping(value = "data-viewer/{displayId}", method = RequestMethod.GET)
    public String view(YukonUserContext userContext, ModelMap model, @PathVariable int displayId) {

        model.addAttribute("mode", PageEditMode.VIEW);
        Display display = displayDao.getDisplayById(displayId);
        boolean pageable = display.isPageable();
        model.addAttribute("pageable", pageable);
        
        PagingParameters paging = null;
        if (pageable) {
            paging = PagingParameters.of(itemsPerPage, 1);
        }
        DateTimeZone tz = userContext.getJodaTimeZone();
        List<DisplayData> displayData = tdcService.getDisplayData(display, tz, paging);
        int totalCount;
        
        if (pageable) {
            totalCount = tdcService.getDisplayDataCount(displayId, tz);
        } else {
            totalCount = displayData.size();
            //display all the data on one page
            if (totalCount == 0) {
                totalCount = itemsPerPage;
            }
            paging = PagingParameters.of(totalCount, 1);
        }
        model.addAttribute("displayName", display.getName());
        model.addAttribute("display", display);
        model.addAttribute("backingBean", new DisplayBackingBean());
        model.addAttribute("colorStateBoxes", tdcService.getUnackAlarmColorStateBoxes(display, displayData));
        
        if (display.getType() == DisplayType.CUSTOM_DISPLAYS) {
            model.addAttribute("hasPointValueColumn", display.hasColumn(ColumnType.POINT_VALUE));
        }
        
        SearchResults<DisplayData> result = SearchResults.pageBasedForSublist(displayData, paging, totalCount);
        model.addAttribute("result", result);
        
        return "data-viewer/display.jsp";
    }
    
    @RequestMapping(value = "data-viewer/{displayId}/page", method = RequestMethod.GET)
    public String page(YukonUserContext userContext, ModelMap model, @PathVariable int displayId,
                       PagingParameters pagingParameters) {
        
        model.addAttribute("pageable", true);
        
        model.addAttribute("mode", PageEditMode.VIEW);
        Display display = displayDao.getDisplayById(displayId);
        model.addAttribute("displayName", display.getName());
        model.addAttribute("display", display);
        model.addAttribute("backingBean", new DisplayBackingBean());
        List<DisplayData> displayData =
                tdcService.getDisplayData(display, userContext.getJodaTimeZone(), pagingParameters);
        model.addAttribute("colorStateBoxes", tdcService.getUnackAlarmColorStateBoxes(display, displayData));
        int totalCount = tdcService.getDisplayDataCount(displayId, userContext.getJodaTimeZone());
        SearchResults<DisplayData> result = SearchResults.pageBasedForSublist(displayData, pagingParameters, totalCount);
        model.addAttribute("result", result);
        
        return "data-viewer/display.jsp";
    }

    @RequestMapping(value = "data-viewer/enable-disable", method = RequestMethod.POST)
    public String enableDisable(YukonUserContext userContext, ModelMap model, int pointId) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("enableDisable", EnabledStatus.values());
        int tags = asyncDynamicDataSource.getTags(pointId);

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

        int tags = asyncDynamicDataSource.getTags(backingBean.getPointId());
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

    @RequestMapping(value = "data-viewer/trend", method = RequestMethod.POST)
    public String trend(YukonUserContext userContext, ModelMap model, int pointId) {

        LitePoint litePoint = pointDao.getLitePoint(pointId);
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.addAttribute("deviceId", liteYukonPAO.getPaoIdentifier().getPaoId());
        YukonMessageSourceResolvable title =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.trend.description",
                                             litePoint.getPointName());
        MessageSourceAccessor messageSourceAccessor =
            messageSourceResolver.getMessageSourceAccessor(userContext);
        model.addAttribute("title", messageSourceAccessor.getMessage(title));
        model.addAttribute("pointId", litePoint.getLiteID());
        Date endDate = new Date();
        Date startDate = endDate;
        startDate = DateUtils.addDays(startDate, -30);
        startDate = DateUtils.truncate(startDate, Calendar.DATE);
        ChartPeriod chartPeriod = ChartPeriod.MONTH;
        ChartInterval chartInterval = chartPeriod.getChartUnit(Range.inclusive(startDate, endDate));
        model.addAttribute("interval", chartInterval);
        if (UnitOfMeasure.getForId(litePoint.getUofmID()) == UnitOfMeasure.KWH) {
            // "Usage" data can be "normalized" delta, since it is an ever increasing number
            model.addAttribute("converterType", ConverterType.NORMALIZED_DELTA);
        } else if (UnitOfMeasure.getForId(litePoint.getUofmID()) == UnitOfMeasure.GALLONS) {
            // water usage can be delta also.
            model.addAttribute("converterType", ConverterType.DELTA_WATER);
        } else { // everything is raw
            model.addAttribute("converterType", ConverterType.RAW);
        }
        model.addAttribute("graphType", GraphType.LINE);
        model.addAttribute("startDateMillis", startDate.getTime());
        model.addAttribute("endDateMillis", endDate.getTime());
        return "data-viewer/trendPopup.jsp";
    }

    @RequestMapping(value = "data-viewer/manual-entry", method = RequestMethod.POST)
    public String manualEntry(YukonUserContext userContext, ModelMap model, int pointId) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(pointId);
        if (litePoint.getPointType() == PointTypes.STATUS_POINT
            || litePoint.getPointType() == PointTypes.CALCULATED_STATUS_POINT) {
            LiteStateGroup group = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
            List<LiteState> stateList = group.getStatesList();
            model.put("stateList", stateList);
            backingBean.setStateId((int) pointValue.getValue());
        } else {
            backingBean.setValue(pointValue.getValue());
        }
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        model.addAttribute("backingBean", backingBean);
        return "data-viewer/manualEntryPopup.jsp";
    }

    @RequestMapping(value = "data-viewer/manualEntrySend", method = RequestMethod.POST)
    public String manualEntrySend(HttpServletResponse response, YukonUserContext userContext,
                                  @ModelAttribute("backingBean") DisplayBackingBean backingBean,
                                  BindingResult bindingResult, ModelMap model, FlashScope flashScope) throws IOException {

        double newPointValue;
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        PointValueQualityHolder pointValue =
            asyncDynamicDataSource.getPointValue(backingBean.getPointId());
        if (litePoint.getPointType() == PointTypes.STATUS_POINT
            || litePoint.getPointType() == PointTypes.CALCULATED_STATUS_POINT) {
            newPointValue = backingBean.getStateId();
        } else {
            validator.validate(backingBean, bindingResult);
            if (bindingResult.hasErrors()) {
                LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
                model.put("deviceName", liteYukonPAO.getPaoName());
                model.put("pointName", litePoint.getPointName());
                model.addAttribute("backingBean", backingBean);
                List<MessageSourceResolvable> messages =
                    YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setError(messages);
                return "data-viewer/manualEntryPopup.jsp";
            }

            newPointValue = backingBean.getValue();
        }
        if (pointValue.getValue() != newPointValue) {
            tdcService.sendPointData(backingBean.getPointId(),
                                     newPointValue,
                                     userContext.getYukonUser());
        }

        response.setContentType("application/json");
        response.getWriter().write(JsonUtils.toJson(Collections.singletonMap("action", "close")));
        return null;
    }

    @RequestMapping(value = "data-viewer/manual-control", method = RequestMethod.POST)
    public String manualControl(ModelMap model, int pointId, int deviceId) {
        
        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        backingBean.setDeviceId(deviceId);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        if (litePoint.getPointType() == PointTypes.ANALOG_POINT) {
            PointValueQualityHolder pointValue = asyncDynamicDataSource.getPointValue(pointId);
            backingBean.setValue(pointValue.getValue());
        } else if (litePoint.getPointType() == PointTypes.STATUS_POINT) {
            LiteStateGroup group = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
            // Filter out the negative control states - i.e. TwoStateStatus contains Any(-1)
            List<LiteState> stateList = group.getStatesList()
                    .stream()
                    //  TODO JAVA 8
                    .filter(new Predicate<LiteState>() {
                @Override
                public boolean test(LiteState t) {
                    return t.getStateRawState() >= 0;
                }
            }).collect(Collectors.toList());
            
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
        if (litePoint.getPointType() == PointTypes.ANALOG_POINT) {
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

        } else if (litePoint.getPointType() == PointTypes.STATUS_POINT) {
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
        List<Tag> tagsToCreate = new ArrayList<Tag>();
        List<Tag> tagsToUpdate = new ArrayList<Tag>();

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
                           YukonUserContext userContext)
            throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Display display = displayDao.getDisplayById(displayId);
        List<DisplayData> displayData = tdcService.getDisplayData(display,
                userContext.getJodaTimeZone(), PagingParameters.EVERYTHING);
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
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, display.getName() + ".csv");
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
    
}