package com.cannontech.web.tools.trends;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.trend.model.Color;
import com.cannontech.common.trend.model.RenderType;
import com.cannontech.common.trend.model.TrendAxis;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.trend.model.TrendType;
import com.cannontech.common.trend.model.TrendType.GraphType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.trends.validator.TrendEditorValidator;
import com.cannontech.web.tools.trends.validator.TrendSeriesValidator;
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.TRENDING)
@RequestMapping("/trend")
public class TrendEditorController {

    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private TrendEditorValidator trendEditorValidator;
    @Autowired private TrendSeriesValidator trendSeriesValidator;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String redirectLink = "redirect:/tools/trends";
    
    private static final Logger log = YukonLogManager.getLogger(TrendEditorController.class);

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TrendModel trendModel = new TrendModel();
        if (model.containsKey("trendModel")) {
            trendModel = (TrendModel) model.get("trendModel");
        }
        model.addAttribute("trendModel", trendModel);
        return "trends/setup/view.jsp";
    }

    @GetMapping("/renderAddPointPopup")
    public String renderAddPointPopup(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TrendSeries trendSeries = new TrendSeries();
        trendSeries.applyDefaults();
        model.addAttribute("trendSeries", trendSeries);
        model.addAttribute("graphTypeDateEnumValue", TrendType.GraphType.DATE_TYPE);
        setPointPopupModel(model);
        return "trends/setup/pointSetupPopup.jsp";
    }

    @GetMapping("/renderEditPointPopup")
    public String renderEditPointPopup(ModelMap model,
            @RequestParam("trendSeries") TrendSeries trendSeries) {
        model.addAttribute("trendSeries", trendSeries);
        LiteYukonPAObject yukonPao = paoDao.getLiteYukonPaoByPointId(trendSeries.getPointId());
        model.addAttribute("deviceName", yukonPao.getPaoName());
        model.addAttribute("isDateTypeSelected", trendSeries.getType().isDateType());
        setPointPopupModel(model);
        return "trends/setup/pointSetupPopup.jsp";
    }

    @PostMapping("/save")
    public String save(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("trendDefinition") TrendModel trendModel, BindingResult result, RedirectAttributes redirectAttributes,
            FlashScope flashScope, HttpServletRequest request) {
        trendEditorValidator.validate(trendModel, result);
        if (result.hasErrors()) {
            return bindAndForward(trendModel, result, redirectAttributes);
        }

        // Call REST API to create or update trend
        try {
            HttpMethod httpMethod = HttpMethod.POST;
            String url = helper.findWebServerUrl(request, userContext, ApiURL.trendCreateOrUpdateUrl);
            if (trendModel.getTrendId() != null) {
                httpMethod = HttpMethod.PATCH;
                url = "/" + trendModel.getTrendId();
            }

            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url, httpMethod,
                    TrendModel.class,
                    trendModel);

            if (response.getStatusCode() == HttpStatus.OK) {
                TrendModel trend = (TrendModel) response.getBody();
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", trendModel.getName()));
                return "redirect:/tools/trends/" + trend.getTrendId();
            }

        } catch (ApiCommunicationException e) {
            log.error(e);
            flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + redirectLink;
        } catch (RestClientException ex) {
            log.error("Error saving trend: {}. Error: {}", trendModel.getName(), ex.getMessage());
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.error.save", trendModel.getName(), ex.getMessage()));
            return "redirect:" + redirectLink;
        }
        return null;
    }

    @PostMapping("/addPoint")
    public String addPoint(ModelMap model, YukonUserContext userContext, HttpServletResponse response,
            @ModelAttribute("trendSeries") TrendSeries trendSeries, BindingResult result, FlashScope flashScope)
            throws JsonGenerationException, JsonMappingException, IOException {
        trendSeriesValidator.validate(trendSeries, result);
        LitePoint litePoint = null;
        LiteYukonPAObject yukonPao = null;
        
        if (trendSeries.getPointId() != null) {
            litePoint = pointDao.getLitePoint(trendSeries.getPointId());
            yukonPao = cache.getAllPaosMap().get(litePoint.getPaobjectID());
        }
        
        if (result.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            setPointPopupModel(model);
            model.addAttribute("deviceName", yukonPao != null ? yukonPao.getPaoName() : "");
            model.addAttribute("isDateTypeSelected", trendSeries.getType().isDateType());
            return "trends/setup/pointSetupPopup.jsp";
        }

        model.clear();
        trendSeries.applyDefaults();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        json.put("trendSeries", trendSeries);
        json.put("pointName", litePoint.getPointName());
        json.put("deviceName", yukonPao.getPaoName());
        json.put("color", accessor.getMessage(trendSeries.getColor().getFormatKey()));
        json.put("axis", accessor.getMessage(trendSeries.getAxis().getFormatKey()));
        json.put("graphType", accessor.getMessage(trendSeries.getType().getFormatKey()));
        json.put("style", accessor.getMessage(trendSeries.getStyle().getFormatKey()));
        if (trendSeries.getType().isDateType()) {
            json.put("dateStr", dateFormattingService.format(trendSeries.getDate(), DateFormatEnum.DATE, userContext));
        }
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), json);
        return null;
    }

    private void setPointPopupModel(ModelMap model) {
        List<GraphType> graphTypes = Lists.newArrayList(GraphType.values());
        graphTypes.remove(GraphType.MARKER_TYPE);
        model.addAttribute("styles", RenderType.getWebSupportedTypes());
        model.addAttribute("graphTypes", graphTypes);
        model.addAttribute("axes", Lists.newArrayList(TrendAxis.values()));
        model.addAttribute("now", DateTime.now());
        model.addAttribute("graphTypeDateEnumValue", GraphType.DATE_TYPE);
        model.addAttribute("colors", Color.values());
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {

        binder.registerCustomEditor(TrendType.GraphType.class, new EnumPropertyEditor<>(TrendType.GraphType.class));
        binder.registerCustomEditor(TrendAxis.class, new EnumPropertyEditor<>(TrendAxis.class));
        binder.registerCustomEditor(RenderType.class, new EnumPropertyEditor<>(RenderType.class));
        binder.registerCustomEditor(Color.class, new EnumPropertyEditor<>(Color.class));

        PropertyEditor dateTimeEditor = datePropertyEditorFactory.getDateTimePropertyEditor(DateFormatEnum.DATE, userContext, BlankMode.NULL);
        
        binder.registerCustomEditor(DateTime.class, dateTimeEditor);
        binder.registerCustomEditor(TrendSeries.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String trendSeriesString) throws IllegalArgumentException {
                if (StringUtils.isEmpty(trendSeriesString)) {
                    setValue(null);
                    return;
                }

                try {
                    TrendSeries trendSeries = JsonUtils.fromJson(trendSeriesString, TrendSeries.class);
                    setValue(trendSeries);
                } catch (IOException e) {
                    log.error("Unable to convert JSON to Field", e);
                    setValue(null);
                }
            }

            @Override
            public String getAsText() {
                TrendSeries trendSeries = (TrendSeries) getValue();
                if (trendSeries == null) {
                    return null;
                }
                try {
                    return JsonUtils.toJson(trendSeries);
                } catch (JsonProcessingException e) {
                    log.error("Unable to convert Field to JSON", e);
                    return "";
                }
            }
        });
    }

    private String bindAndForward(TrendModel trendModel, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("trendModel", trendModel);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.trendModel", result);
        if (trendModel.getTrendId() == null) {
            return "redirect:create";
        }
        return "redirect:" + trendModel.getTrendId() + "/edit";
    }
}