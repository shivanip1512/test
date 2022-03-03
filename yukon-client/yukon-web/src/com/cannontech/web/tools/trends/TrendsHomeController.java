package com.cannontech.web.tools.trends;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.core.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.trend.model.ResetPeakDuration;
import com.cannontech.common.trend.model.ResetPeakModel;
import com.cannontech.common.trend.model.ResetPeakPopupModel;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.Graph;
import com.cannontech.graph.model.TrendProperties;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.trend.ResetPeakValidatorHelper;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.trends.validator.ResetPeakPopupValidator;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.TRENDING)
public class TrendsHomeController {

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GraphDao graphDao;
    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ApiControllerHelper helper;
    @Autowired private IDatabaseCache cache;
    @Autowired private ResetPeakPopupValidator resetPeakValidator;
    @Autowired private ResetPeakValidatorHelper resetPeakValidatorHelper;
    
    private static final Logger log = YukonLogManager.getLogger(TrendsHomeController.class);
    
    @RequestMapping({"/trends", "/trends/"})
    public String trends(ModelMap model, YukonUserContext userContext) throws JsonProcessingException {
        
        List<LiteGraphDefinition> trends = graphDao.getGraphDefinitions();
        model.addAttribute("trends", trends);
        
        if (!trends.isEmpty()) {
            Integer trendId = trends.get(0).getGraphDefinitionID();
            model.addAttribute("trendId", trendId);
            model.addAttribute("trendName", trends.get(0).getName());
            model.addAttribute("pageName", "trend");
            model.addAttribute("isResetPeakApplicable", resetPeakValidatorHelper.checkIfResetPeakApplicable(trendId));
            addTrendModelToModelMap(model, trends.get(0));
        } else {
            model.addAttribute("pageName", "trends");
        }
        model.addAttribute("labels", TrendUtils.getLabels(userContext, messageResolver));
        
        model.addAttribute("autoUpdate", userPreferenceService.getDefaultTrendAutoUpdateSelection(userContext.getYukonUser()));
        return "trends/trends.jsp";
    }
    
    @RequestMapping("/trends/{id}")
    public String trend(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash)
            throws JsonProcessingException {

        List<LiteGraphDefinition> trends = graphDao.getGraphDefinitions();
        model.addAttribute("trends", trends);
        
        LiteGraphDefinition trend = graphDao.getLiteGraphDefinition(id);
        if (null == trend) {
            return "redirect:/tools/trends";
        } else {
            model.addAttribute("trendId", id);
            model.addAttribute("trendName", trend.getName());
            model.addAttribute("pageName", "trend");
            model.addAttribute("labels", TrendUtils.getLabels(userContext, messageResolver));
            model.addAttribute("isResetPeakApplicable", resetPeakValidatorHelper.checkIfResetPeakApplicable(id));
            model.addAttribute("autoUpdate", userPreferenceService.getDefaultTrendAutoUpdateSelection(userContext.getYukonUser()));
            addTrendModelToModelMap(model, trend);

            return "trends/trends.jsp";
        }
    }
    
    @RequestMapping("/trends/{id}/csv")
    public void csv(HttpServletResponse resp, @PathVariable int id, long from, long to) throws IOException {
        
        Instant start = new Instant(from);
        Instant stop = new Instant(to);
        
        Graph graph = new Graph();
        graph.setStartDate(start.toDate());
        graph.setGraphDefinition(graphDao.getLiteGraphDefinition(id));
        graph.setViewType(GraphRenderers.LINE);
        graph.setUpdateTrend(true);
        TrendProperties props = new TrendProperties();
        props.setOptionsMaskSettings(GraphRenderers.BASIC_MASK);
        graph.setTrendProperties(props);
        graph.update(start, stop);

        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, YukonUserContext.system);
        String fileName = graph.getTrendModel().getChartName().toString() + "_" + now +  ".csv";

        ServletOutputStream out = resp.getOutputStream();
        resp.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        resp.setContentType("text/x-comma-separated-values");
        graph.encodeCSV(out);
        
        out.flush();
    }
    
    @GetMapping("/trend/renderResetPeakPopup")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.UPDATE)
    public String renderResetPeakPopup(ModelMap model, @RequestParam("trendId") Integer trendId, YukonUserContext userContext) {
        ResetPeakPopupModel resetPeakPopupModel = null;
        if (model.containsKey("resetPeakPopupModel")) {
            resetPeakPopupModel = (ResetPeakPopupModel) model.get("resetPeakPopupModel");
        } else {
            resetPeakPopupModel = new ResetPeakPopupModel(trendId);
        }
        model.addAttribute("resetPeakPopupModel", resetPeakPopupModel);
        setupResetPeakPopupModel(model, userContext, resetPeakPopupModel);
        return "trends/resetPeakPopup.jsp";
    }
    
    @PostMapping("/trend/resetPeak")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.UPDATE)
    public String resetPeak(ModelMap model, YukonUserContext userContext, HttpServletRequest request, HttpServletResponse response,
            @ModelAttribute("resetPeakPopupModel") ResetPeakPopupModel resetPeakPopupModel, BindingResult bindingResult, FlashScope flashScope) throws JsonGenerationException, JsonMappingException, IOException {
        
        resetPeakValidator.validate(resetPeakPopupModel, bindingResult);
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            if (bindingResult.hasGlobalErrors()) {
                MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
                List<ObjectError> objectErrorList = bindingResult.getGlobalErrors();
                List<String> errors = objectErrorList.stream()
                                                     .map(obj -> accessor.getMessage(obj.getCode()))
                                                     .collect(Collectors.toList());
                
                flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(String.join(", ", errors)));
            }
            setupResetPeakPopupModel(model, userContext, resetPeakPopupModel);
            return "trends/resetPeakPopup.jsp";
        }

        List<String> resetPeakSuccess = Lists.newArrayList();
        List<String> resetPeakFailed = Lists.newArrayList();
        if (resetPeakPopupModel.isResetPeakForAllTrends()) {
            List<LiteGraphDefinition> graphDefinitions = cache.getAllGraphDefinitions();
            for (LiteGraphDefinition graphDefinition : graphDefinitions) {
                if (resetPeakValidatorHelper.checkIfResetPeakApplicable(graphDefinition.getGraphDefinitionID())) {
                    ResetPeakModel resetPeakModel = new ResetPeakModel();
                    resetPeakModel.setStartDate(resetPeakPopupModel.getStartDate());
                    callResetPeakApi(userContext, request, resetPeakPopupModel, graphDefinition.getGraphDefinitionID(), resetPeakSuccess, resetPeakFailed);
                }
            }
        } else {
            ResetPeakModel resetPeakMdl = new ResetPeakModel();
            resetPeakMdl.setStartDate(resetPeakPopupModel.getStartDate());
            callResetPeakApi(userContext, request, resetPeakMdl, resetPeakPopupModel.getTrendId(), resetPeakSuccess, resetPeakFailed);
        }
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        
        if (CollectionUtils.isNotEmpty(resetPeakSuccess)) {
            json.put("successMessage", accessor.getMessage("yukon.web.modules.tools.trend.resetPeak.success", String.join(", ", resetPeakSuccess)));
        }
        if (CollectionUtils.isNotEmpty(resetPeakFailed)) {
            json.put("errorMessage", accessor.getMessage("yukon.web.modules.tools.trend.resetPeak.failed", String.join(", ", resetPeakFailed)));
        }
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), json);
        return null;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(ResetPeakDuration.class, new EnumPropertyEditor<>(ResetPeakDuration.class));
        PropertyEditor dateTimeEditor = datePropertyEditorFactory.getDateTimePropertyEditor(DateFormatEnum.DATE, userContext, BlankMode.NULL);
        binder.registerCustomEditor(DateTime.class, dateTimeEditor);
    }
    
    private void addTrendModelToModelMap(ModelMap model, LiteGraphDefinition liteGraphDefinition) {
        TrendModel trendModel = new TrendModel();
        trendModel.setName(liteGraphDefinition.getName());
        model.addAttribute("trendModel", trendModel);
    }

    private void callResetPeakApi(YukonUserContext userContext, HttpServletRequest request, ResetPeakModel resetPeakModel,
            Integer trendId, List<String> resetPeakSuccess, List<String> resetPeakFailed) {
        boolean isSuccess;
        LiteGraphDefinition liteGraphDefinition = graphDao.getLiteGraphDefinition(trendId);
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.trendUrl + "/" + trendId + "/resetPeak");
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.POST, Map.class, (ResetPeakModel) resetPeakModel);

            isSuccess = response.getStatusCode() == HttpStatus.OK;
        } catch (ApiCommunicationException | RestClientException e) {
            log.error("Error while performing reset peak for trend {}.", liteGraphDefinition.getName(), e);
            isSuccess = false;
        }

        if (isSuccess) {
            resetPeakSuccess.add(liteGraphDefinition.getName());
        } else {
            resetPeakFailed.add(liteGraphDefinition.getName());
        }
    }
    
    private void setupResetPeakPopupModel(ModelMap model, YukonUserContext userContext, ResetPeakPopupModel resetPeakModel) {
        boolean isDurationSelectedDate = false;
        
        if (model.containsKey("resetPeakDuration")) {
            isDurationSelectedDate = resetPeakModel.getResetPeakDuration() == ResetPeakDuration.SELECTED_DATE;
        }
        model.addAttribute("isDurationSelectedDate", isDurationSelectedDate);
        
        String firstDayOfMonth = dateFormattingService.format(DateTime.now().withDayOfMonth(1), DateFormatEnum.DATE, userContext);
        model.addAttribute("firstDateOfMonth", firstDayOfMonth);
        String firstDayOfYear = dateFormattingService.format(DateTime.now().withDayOfYear(1), DateFormatEnum.DATE, userContext);
        model.addAttribute("firstDateOfYear", firstDayOfYear);
        String todaysDate = dateFormattingService.format(DateTime.now(), DateFormatEnum.DATE, userContext);
        model.addAttribute("todaysDate", todaysDate);
        
        model.addAttribute("resetPeakDurationValues", ResetPeakDuration.values());
        model.addAttribute("todayEnumVal", ResetPeakDuration.TODAY);
        model.addAttribute("firstDayOfMonthEnumVal", ResetPeakDuration.FIRST_DATE_OF_MONTH);
        model.addAttribute("firstDayOfYearEnumVal", ResetPeakDuration.FIRST_DATE_OF_YEAR);
        model.addAttribute("selectedDateEnumVal", ResetPeakDuration.SELECTED_DATE);
        model.addAttribute("now", DateTime.now());
    }
}