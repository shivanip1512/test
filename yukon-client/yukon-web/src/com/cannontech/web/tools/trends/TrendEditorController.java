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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.trend.model.GraphColors;
import com.cannontech.common.trend.model.RenderType;
import com.cannontech.common.trend.model.TrendAxis;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.trend.model.TrendType;
import com.cannontech.common.trend.model.TrendType.GraphType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
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
import com.cannontech.web.security.annotation.CheckPermissionLevel;
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
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RolePropertyDao rolePropertyDao;

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String redirectLink = "redirect:/tools/trends";
    
    private static final Logger log = YukonLogManager.getLogger(TrendEditorController.class);

    @GetMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TrendModel trendModel = new TrendModel();
        if (model.containsKey("trendModel")) {
            trendModel = (TrendModel) model.get("trendModel");
        }
        model.addAttribute("trendModel", trendModel);
        return "trends/setup/view.jsp";
    }

    @GetMapping("/renderSetupPopup")
    public String renderSetupPopup(ModelMap model, @RequestParam("isMarker") boolean isMarker, @RequestParam("numberOfRows") Integer numberOfRows) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TrendSeries trendSeries = new TrendSeries(GraphColors.getNextDefaultColor(numberOfRows));
        trendSeries.applyDefaults();
        model.addAttribute("trendSeries", trendSeries);
        if (isMarker) {
            trendSeries.setMarkerDefaults();
        } else {
            model.addAttribute("graphTypeDateEnumValue", TrendType.GraphType.DATE_TYPE);
        }
        setModel(model, isMarker);
        return getSetupDialogJsp(isMarker);
    }

    @GetMapping("/renderEditSetupPopup")
    public String renderEditSetupPopup(ModelMap model, @RequestParam("trendSeries") TrendSeries trendSeries) {
        boolean isMarker = trendSeries.getType().isMarkerType();
        model.addAttribute("trendSeries", trendSeries);
        if (!isMarker) {
            LiteYukonPAObject yukonPao = paoDao.getLiteYukonPaoByPointId(trendSeries.getPointId());
            model.addAttribute("deviceName", yukonPao.getPaoName());
            model.addAttribute("isDateTypeSelected", trendSeries.getType().isDateType());
        }
        setModel(model, isMarker);
        return getSetupDialogJsp(isMarker);
    }
    
    @GetMapping("{id}/edit")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.UPDATE)
    public String edit(ModelMap model, @PathVariable int id, HttpServletRequest request, YukonUserContext userContext, FlashScope flashScope) {
        model.addAttribute("mode", PageEditMode.EDIT);
        TrendModel trendModel = null;
        if (model.containsKey("trendModel")) {
            trendModel = (TrendModel) model.get("trendModel");
        } else {
            // Call REST API to retrieve trend
            try {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.trendUrl + "/" +id);

                ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, TrendModel.class, trendModel);

                if (response.getStatusCode() == HttpStatus.OK) {
                    trendModel = (TrendModel) response.getBody();
                }
            } catch (ApiCommunicationException e) {
                log.error(e);
                flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
                return redirectLink;
            } catch (RestClientException ex) {
                log.error("Error in retrieving trend. Error: {}", trendModel.getName(), ex.getMessage());
                MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.retrieve.error", accessor.getMessage("yukon.web.modules.tools.trend"), ex.getMessage()));
                return redirectLink;
            }
        }
        model.addAttribute("trendModel", trendModel);
        return "trends/setup/view.jsp";
    }


    @PostMapping("/save")
    public String save(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("trendDefinition") TrendModel trendModel, BindingResult result, RedirectAttributes redirectAttributes,
            FlashScope flashScope, HttpServletRequest request) throws JsonProcessingException {

        if (trendModel.getTrendId() != null && !rolePropertyDao.checkLevel(YukonRoleProperty.MANAGE_TRENDS, HierarchyPermissionLevel.UPDATE, userContext.getYukonUser())) {
            throw new NotAuthorizedException("User not authorized to edit trends.");
        } else if (trendModel.getTrendId() == null && !rolePropertyDao.checkLevel(YukonRoleProperty.MANAGE_TRENDS, HierarchyPermissionLevel.CREATE, userContext.getYukonUser()) ) {
            throw new NotAuthorizedException("User not authorized to create trends.");
        }
        
        trendEditorValidator.validate(trendModel, result);
        if (result.hasErrors()) {
            return bindAndForward(trendModel, result, redirectAttributes);
        }

        // Call REST API to create or update trend
        try {
            HttpMethod httpMethod = HttpMethod.POST;
            String url = helper.findWebServerUrl(request, userContext, ApiURL.trendUrl);
            if (trendModel.getTrendId() != null) {
                httpMethod = HttpMethod.PUT;
                url = url + "/"+ trendModel.getTrendId();
            }

            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url, httpMethod, Object.class, trendModel);
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                HashMap<String, Object> responseMap = (HashMap<String, Object>) response.getBody();
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", responseMap.get("trendName")));
                return redirectLink + "/" + responseMap.get("trendId");
            }
            
            /**
             *  The validations at the MVC side and the API side for save trends are the same. Since the inputs are already validated at the MVC side, this condition below
             *  will never execute. This code is added just to ensure that if we receive HttpStatus.UNPROCESSABLE_ENTITY in the response for some reason, UI does not 
             *  break. So we display a generic error message in this case.
             */
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                log.error("Error saving trend:{}", JsonUtils.toJson(response.getBody()));
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.error.genericMainMessage"));
                BindException error = new BindException(trendModel, "trendDefinition");
                result = helper.populateBindingErrorForApiErrorModel(result, error, response, "yukon.web.error.");
                return bindAndForward(trendModel, result, redirectAttributes);
            }

        } catch (ApiCommunicationException e) {
            log.error(e);
            flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectLink;
        } catch (RestClientException ex) {
            log.error("Error saving trend: {}. Error: {}", trendModel.getName(), ex.getMessage());
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", trendModel.getName(), ex.getMessage()));
            return redirectLink;
        }
        return null;
    }

    @PostMapping("/addPointOrMarker")
    public String addPointOrMarker(ModelMap model, YukonUserContext userContext, HttpServletResponse response,
            @ModelAttribute("trendSeries") TrendSeries trendSeries, BindingResult result, FlashScope flashScope)
            throws JsonGenerationException, JsonMappingException, IOException {
        trendSeriesValidator.validate(trendSeries, result);
        LitePoint litePoint = null;
        LiteYukonPAObject yukonPao = null;
        
        Boolean isMarker = trendSeries.getType().isMarkerType();
        
        if (trendSeries.getPointId() != null && !isMarker) {
            litePoint = pointDao.getLitePoint(trendSeries.getPointId());
            yukonPao = cache.getAllPaosMap().get(litePoint.getPaobjectID());
        }
        
        if (result.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            setModel(model, isMarker);
            if (!isMarker) {
                model.addAttribute("deviceName", yukonPao != null ? yukonPao.getPaoName() : "");
                model.addAttribute("isDateTypeSelected", trendSeries.getType().isDateType());
            }
            return getSetupDialogJsp(isMarker);
        }

        model.clear();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        json.put("trendSeries", trendSeries);
        if (!isMarker) {
            json.put("pointName", litePoint.getPointName());
            json.put("deviceName", yukonPao.getPaoName());
            json.put("graphType", accessor.getMessage(trendSeries.getType().getFormatKey()));
            json.put("style", accessor.getMessage(trendSeries.getStyle().getFormatKey()));
            if (trendSeries.getType().isDateType()) {
                json.put("dateStr", dateFormattingService.format(trendSeries.getDate(), DateFormatEnum.DATE, userContext));
            }
        }
        json.put("color", accessor.getMessage(trendSeries.getColor().getYukonColor().getFormatKey()));
        json.put("colorHexValue", trendSeries.getColor().getHexValue());
        json.put("axis", accessor.getMessage(trendSeries.getAxis().getFormatKey()));
        
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), json);
        return null;
    }
    
    @DeleteMapping("/{id}/delete")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_TRENDS, level = HierarchyPermissionLevel.OWNER)
    public String delete(@PathVariable int id, @ModelAttribute TrendModel trendModel, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {
        try {
            // Api call to delete trend
            String url = helper.findWebServerUrl(request, userContext, ApiURL.trendUrl + "/"+ id);
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.DELETE, Object.class, Integer.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", trendModel.getName()));
                return redirectLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e);
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting trend: {}. Error: {}", trendModel.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", trendModel.getName(), ex.getMessage()));
            return redirectLink;
        }
        return redirectLink;
    }

    private void setModel(ModelMap model, boolean isMarker) {
        model.addAttribute("colors", GraphColors.values());
        model.addAttribute("axes", Lists.newArrayList(TrendAxis.values()));
        
        if (!isMarker) {
            List<GraphType> graphTypes = Lists.newArrayList(GraphType.values());
            graphTypes.remove(GraphType.MARKER_TYPE);
            model.addAttribute("styles", RenderType.getWebSupportedTypes());
            model.addAttribute("graphTypes", graphTypes);
            model.addAttribute("now", DateTime.now());
            model.addAttribute("graphTypeDateEnumValue", GraphType.DATE_TYPE);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(TrendType.GraphType.class, new EnumPropertyEditor<>(TrendType.GraphType.class));
        binder.registerCustomEditor(TrendAxis.class, new EnumPropertyEditor<>(TrendAxis.class));
        binder.registerCustomEditor(RenderType.class, new EnumPropertyEditor<>(RenderType.class));
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
        
        binder.registerCustomEditor(GraphColors.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String color) throws IllegalArgumentException {
                setValue(GraphColors.getByYukonColor(YukonColorPalette.getColorByHexValue(color)));
            }
        });
    }
    
    private String getSetupDialogJsp(boolean isMarker) {
        return isMarker ? "trends/setup/markerSetupPopup.jsp" : "trends/setup/pointSetupPopup.jsp";
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