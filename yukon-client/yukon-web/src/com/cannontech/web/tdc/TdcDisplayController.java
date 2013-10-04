package com.cannontech.web.tdc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
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
import com.cannontech.common.tag.service.TagService;
import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.AltScanRate;
import com.cannontech.common.tdc.model.ColumnType;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayData;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.common.tdc.service.TdcService;
import com.cannontech.common.util.EnabledStatus;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.TagDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
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

    @Autowired private DisplayDao displayDao;
    @Autowired private TdcService tdcService;
    @Autowired private PointDao pointDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private DynamicDataSource dynamicDataSource;
    @Autowired private CommandService commandService;
    @Autowired private TagService tagService;
    @Autowired private TagDao tagDao;
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private DateFormattingService dateFormattingService;
    
    private final Validator validator = new SimpleValidator<DisplayBackingBean>(DisplayBackingBean.class) {
        @Override
        protected void doValidation(DisplayBackingBean bean, Errors errors) {
            YukonValidationUtils.checkIsPositiveDouble(errors, "value", bean.getValue());
        }
    };

    @RequestMapping(value = "/{displayId}", method = RequestMethod.GET)
    public String view(YukonUserContext context, ModelMap model, @PathVariable int displayId) {

        model.addAttribute("mode", PageEditMode.VIEW);
        Display display = displayDao.getDisplayById(displayId);
        List<DisplayData> displayData =
            tdcService.getDisplayData(display, context.getJodaTimeZone());
        model.addAttribute("displayData", displayData);
        model.addAttribute("displayName", display.getName());
        model.addAttribute("display", display);
        model.addAttribute("backingBean", new DisplayBackingBean());
        model.addAttribute("colorStateBoxes", tdcService.getUnackAlarmColorStateBoxes(display, displayData));
        if(display.getType() == DisplayType.CUSTOM_DISPLAYS){
            model.addAttribute("hasPointValueColumn", display.hasColumn(ColumnType.POINT_VALUE));
        }
        return "display.jsp";
    }

    @RequestMapping(value = "/enableDisable", method = RequestMethod.POST)
    public String enableDisable(YukonUserContext context, ModelMap model, int pointId) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("enableDisable", EnabledStatus.values());
        int tags = dynamicDataSource.getTags(pointId);

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
        return "enableDisablePopup.jsp";
    }

    @RequestMapping(value = "/enableDisableSend", method = RequestMethod.POST)
    public @ResponseBody JSONObject enableDisableSend(YukonUserContext context,
                                 @ModelAttribute("backingBean") DisplayBackingBean backingBean) {

        int tags = dynamicDataSource.getTags(backingBean.getPointId());
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        int deviceId = liteYukonPAO.getPaoIdentifier().getPaoId();

        if (TagUtils.isDeviceOutOfService(tags)) {
            if (backingBean.getDeviceEnabledStatus() == EnabledStatus.ENABLED) {
                commandService.toggleDeviceEnablement(deviceId, false, context.getYukonUser());
            }
        } else {
            if (backingBean.getDeviceEnabledStatus() == EnabledStatus.DISABLED) {
                commandService.toggleDeviceEnablement(deviceId, true, context.getYukonUser());
            }
        }
        if (TagUtils.isPointOutOfService(tags)) {
            if (backingBean.getPointEnabledStatus() == EnabledStatus.ENABLED) {
                commandService.togglePointEnablement(backingBean.getPointId(),
                                                 false,
                                                 context.getYukonUser());
            }
        } else {
            if (backingBean.getPointEnabledStatus() == EnabledStatus.DISABLED) {
                commandService.togglePointEnablement(backingBean.getPointId(),
                                                 true,
                                                 context.getYukonUser());
            }
        }
        return getJSONSuccess();
    }

    @RequestMapping(value = "/unacknowledged", method = RequestMethod.POST)
    public String unacknowledged(YukonUserContext context, ModelMap model, int pointId) {

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
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        model.addAttribute("title", accessor.getMessage(title));
        return "unackPopup.jsp";
    }

    @RequestMapping(value = "/acknowledgeAlarm", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject acknowledgeAlarm(YukonUserContext context, ModelMap model, int pointId, int condition) {

        if(condition == 0 && tdcService.getUnackAlarmCountForPoint(pointId) == 1){
            tdcService.acknowledgeAlarmsForPoint(pointId, context.getYukonUser());
        }else{
            tdcService.acknowledgeAlarm(pointId, condition, context.getYukonUser());
        }
        return getJSONSuccess();
    }

    @RequestMapping(value = "/acknowledgeAlarmsForDisplay", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject acknowledgeAlarmsForDisplay(YukonUserContext context, ModelMap model, int displayId) {

        Display display = displayDao.getDisplayById(displayId);
        int alarms = tdcService.acknowledgeAlarmsForDisplay(display, context.getYukonUser());
        MessageSourceResolvable successMsg =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.ack.success", alarms);
        return getJSONSuccess(successMsg, context);
    }

    @RequestMapping(value = "/acknowledgeAlarmsForPoint", method = RequestMethod.POST)
    public @ResponseBody
    JSONObject acknowledgeAlarmsForPoint(YukonUserContext context, ModelMap model, int pointId) {

        int alarms = tdcService.acknowledgeAlarmsForPoint(pointId, context.getYukonUser());
        YukonMessageSourceResolvable successMsg =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.ack.success", alarms);
        return getJSONSuccess(successMsg, context);
    }

    @RequestMapping(value = "/trend", method = RequestMethod.POST)
    public String trend(YukonUserContext context, ModelMap model, int pointId) {

        LitePoint litePoint = pointDao.getLitePoint(pointId);
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.addAttribute("deviceId", liteYukonPAO.getPaoIdentifier().getPaoId());
        YukonMessageSourceResolvable title =
            new YukonMessageSourceResolvable("yukon.web.modules.tools.tdc.trend.description",
                                             litePoint.getPointName());
        MessageSourceAccessor messageSourceAccessor =
            resolver.getMessageSourceAccessor(context);
        model.addAttribute("title", messageSourceAccessor.getMessage(title));
        model.addAttribute("pointId", litePoint.getLiteID());
        Date endDate = new Date();
        Date startDate = endDate;
        startDate = DateUtils.add(startDate, Calendar.DATE, -30);
        startDate = DateUtils.truncate(startDate, Calendar.DATE);
        ChartPeriod chartPeriod = ChartPeriod.MONTH;
        ChartInterval chartInterval = chartPeriod.getChartUnit(startDate, endDate);
        model.addAttribute("interval", chartInterval);
        model.addAttribute("converterType", ConverterType.NORMALIZED_DELTA);
        model.addAttribute("graphType", GraphType.LINE);
        model.addAttribute("startDateMillis", startDate.getTime());
        model.addAttribute("endDateMillis", endDate.getTime());
        return "trendPopup.jsp";
    }

    @RequestMapping(value = "/manualEntry", method = RequestMethod.POST)
    public String manualEntry(YukonUserContext context, ModelMap model, int pointId) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
        if (litePoint.getPointType() == PointTypes.STATUS_POINT
            || litePoint.getPointType() == PointTypes.CALCULATED_STATUS_POINT) {
            LiteStateGroup group = pointDao.getStateGroup(litePoint.getStateGroupID());
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
        return "manualEntryPopup.jsp";
    }

    @RequestMapping(value = "/manualEntrySend", method = RequestMethod.POST)
    public String manualEntrySend(HttpServletResponse response, YukonUserContext context,
                                  @ModelAttribute("backingBean") DisplayBackingBean backingBean,
                                  BindingResult bindingResult, ModelMap model, FlashScope flashScope) throws IOException {

        double newPointValue;
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        PointValueQualityHolder pointValue =
            dynamicDataSource.getPointValue(backingBean.getPointId());
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
                return "manualEntryPopup.jsp";
            }

            newPointValue = backingBean.getValue();
        }
        if (pointValue.getValue() != newPointValue) {
            tdcService.sendPointData(backingBean.getPointId(),
                                     newPointValue,
                                     context.getYukonUser());
        }
        JSONObject json = new JSONObject();
        json.put("action", "close");
        
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
        return null;
    }

    @RequestMapping(value = "/manualControl", method = RequestMethod.POST)
    public String manualControl(YukonUserContext context, ModelMap model, int pointId, int deviceId) {
        
        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setPointId(pointId);
        backingBean.setDeviceId(deviceId);
        LitePoint litePoint = pointDao.getLitePoint(pointId);
        if (litePoint.getPointType() == PointTypes.ANALOG_POINT) {
            PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(pointId);
            backingBean.setValue(pointValue.getValue());
        } else if (litePoint.getPointType() == PointTypes.STATUS_POINT) {
            LiteStateGroup group = pointDao.getStateGroup(litePoint.getStateGroupID());
            // Display only the first 2 options from the list - if the option with index 0 is
            // selected the control request open will be send otherwise the control request closed
            // will be send
            List<LiteState> stateList = group.getStatesList().subList(0, 2);
            model.put("stateList", stateList);
        }
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        model.addAttribute("backingBean", backingBean);
        return "manualControlPopup.jsp";
    }

    @RequestMapping(value = "/manualControlSend", method = RequestMethod.POST)
    public String manualControlSend(HttpServletResponse response, YukonUserContext context,
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
                                               context.getYukonUser());

        } else if (litePoint.getPointType() == PointTypes.STATUS_POINT) {
            if (backingBean.getStateId() == 0) {
                commandService.toggleControlRequest(backingBean.getDeviceId(),
                                                backingBean.getPointId(),
                                                false,
                                                context.getYukonUser());
            } else if (backingBean.getStateId() == 1) {
                commandService.toggleControlRequest(backingBean.getDeviceId(),
                                                backingBean.getPointId(),
                                                true,
                                                context.getYukonUser());
            }
        }
        JSONObject json = new JSONObject();
        json.put("action", "close");
        
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
        return null;
    }

    @RequestMapping(value = "/altScanRate", method = RequestMethod.POST)
    public String altScanRate(YukonUserContext context, ModelMap model, int deviceId, String deviceName) {

        DisplayBackingBean backingBean = new DisplayBackingBean();
        backingBean.setDeviceId(deviceId);
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("deviceName", deviceName);
        model.addAttribute("altScanRates", AltScanRate.values());
        return "altScanRatePopup.jsp";
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody JSONObject altScanRateSend(YukonUserContext context, ModelMap model,
                               @ModelAttribute("backingBean") DisplayBackingBean backingBean) {

        commandService.sendAltScanRate(backingBean.getDeviceId(),
                                   backingBean.getAltScanRate(),
                                   context.getYukonUser());
        return getJSONSuccess();
    }

    @RequestMapping(value = "/tags", method = RequestMethod.POST)
    public String tags(ModelMap model, int deviceId, int pointId) {
        boolean isDeviceControlInhibited =
            TagUtils.isDeviceControlInhibited(dynamicDataSource.getTags(pointId));
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
        return "tagsPopup.jsp";
    }
    
    @RequestMapping(value = "/tagAdd", method = RequestMethod.POST)
    public String tagAdd(ModelMap model, @ModelAttribute("backingBean") DisplayBackingBean backingBean) {
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        backingBean.getTags().add(new Tag());
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("allTags",  tagDao.getAllTags());
        return "tagsPopup.jsp";
    }
    
    @RequestMapping(value = "/tagsSave", method = RequestMethod.POST)
    public @ResponseBody JSONObject tagsSave(YukonUserContext context,
                        @ModelAttribute("backingBean") DisplayBackingBean backingBean)
            throws Exception {
        List<Tag> tags = tagService.getTags(backingBean.getPointId());

        Map<Integer, Tag> mappedOldTags = Maps.uniqueIndex(tags, new Function<Tag, Integer>() {
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
            tagService.updateTag(tag, context.getYukonUser());
        }
        for (Tag tag : tagsToCreate) {
            tagService.createTag(backingBean.getPointId(),
                                 tag.getTagID(),
                                 tag.getDescriptionStr(),
                                 context.getYukonUser());
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
                                 context.getYukonUser());
        }
        boolean isDeviceControlInhibited =
            TagUtils.isDeviceControlInhibited(dynamicDataSource.getTags(backingBean.getPointId()));
        if(isDeviceControlInhibited && !backingBean.isDeviceControlInhibited()){
            commandService.toggleControlEnablement(backingBean.getDeviceId(),
                                                   false,
                                                   context.getYukonUser());
        }else if(!isDeviceControlInhibited && backingBean.isDeviceControlInhibited()){
            commandService.toggleControlEnablement(backingBean.getDeviceId(),
                                                   true,
                                                   context.getYukonUser()); 
        }
        return getJSONSuccess();
    }

    @RequestMapping(value = "/tagRemove", method = RequestMethod.POST)
    public String tagRemove(YukonUserContext context, ModelMap model,
                           @ModelAttribute("backingBean") DisplayBackingBean backingBean) {
        LitePoint litePoint = pointDao.getLitePoint(backingBean.getPointId());
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
        model.put("deviceName", liteYukonPAO.getPaoName());
        model.put("pointName", litePoint.getPointName());
        backingBean.getTags().remove(backingBean.getRowIndex());
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("allTags",  tagDao.getAllTags());
        return "tagsPopup.jsp";
    }

    @RequestMapping(value = "/{displayId}/download", method = RequestMethod.GET)
    public String download(HttpServletRequest request,
                           HttpServletResponse response, @PathVariable int displayId,
                           YukonUserContext context)
            throws ServletRequestBindingException, IOException {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        Display display = displayDao.getDisplayById(displayId);
        List<DisplayData> displayData =
            tdcService.getDisplayData(display, context.getJodaTimeZone());
        TdcDownloadHelper helper =
            new TdcDownloadHelper(accessor,
                                  registrationService,
                                  dateFormattingService,
                                  tdcService,
                                  display,
                                  displayData,
                                  context);
        List<String> columnNames = helper.getColumnNames();
        List<List<String>> dataGrid = helper.getDataGrid();
        WebFileUtils.writeToCSV(response, columnNames, dataGrid, display.getName() + ".csv");
        return null;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(AltScanRate.class,
                                    new EnumPropertyEditor<>(AltScanRate.class));
        binder.registerCustomEditor(EnabledStatus.class,
                                    new EnumPropertyEditor<>(EnabledStatus.class));
    }

    private JSONObject getJSONSuccess(MessageSourceResolvable successMsg,
                                      YukonUserContext context) {
        JSONObject result = new JSONObject();
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        result.put("success", accessor.getMessage(successMsg));
        return result;
    }

    private JSONObject getJSONSuccess() {
        JSONObject result = new JSONObject();
        result.put("success", "success");
        return result;
    }

    /*
     * @RequestMapping(value = "/{displayId}", method = RequestMethod.PUT)
     * public String update(ModelMap model, @PathVariable int displayId) {
     * model.addAttribute("mode", "update");
     * return "display.jsp";
     * }
     * @RequestMapping(value = "/{displayId}", method = RequestMethod.DELETE)
     * public String delete(ModelMap model, @PathVariable int displayId) {
     * model.addAttribute("mode", "delete");
     * return "display.jsp";
     * }
     * @RequestMapping(value = "/create", method = RequestMethod.GET)
     * public String create(ModelMap model) {
     * model.addAttribute("mode", PageEditMode.CREATE);
     * return "display.jsp";
     * }
     * @RequestMapping(value = "/{displayId}/edit", method = RequestMethod.GET)
     * public String edit(ModelMap model, @PathVariable int displayId) {
     * model.addAttribute("mode", PageEditMode.EDIT);
     * return "display.jsp";
     * }
     */
}