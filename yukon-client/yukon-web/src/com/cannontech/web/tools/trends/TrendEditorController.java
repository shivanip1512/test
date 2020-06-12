package com.cannontech.web.tools.trends;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.trend.model.Color;
import com.cannontech.common.trend.model.RenderType;
import com.cannontech.common.trend.model.TrendAxis;
import com.cannontech.common.trend.model.TrendModel;
import com.cannontech.common.trend.model.TrendSeries;
import com.cannontech.common.trend.model.TrendType;
import com.cannontech.common.util.JsonUtils;
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
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.security.annotation.CheckRole;
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
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

    private static final String baseKey = "yukon.web.modules.tools.trend.";
    private static final Logger log = YukonLogManager.getLogger(TrendEditorController.class);

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TrendModel trendModel = new TrendModel();
        model.addAttribute("trendModel", trendModel);
        return "trends/setup/view.jsp";
    }

    @GetMapping("/renderAddPointPopup")
    public String renderAddPointPopup(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        TrendSeries trendSeries = new TrendSeries();
        /* Set Default Values */
        trendSeries.setStyle(RenderType.LINE);
        trendSeries.setAxis(TrendAxis.LEFT);
        trendSeries.setType(TrendType.GraphType.BASIC_TYPE);
        trendSeries.setMultiplier(1d);
        trendSeries.setDate(DateTime.now());
        trendSeries.setColor(Color.BLUE);
        
        model.addAttribute("trendSeries", trendSeries);
        model.addAttribute("graphTypeDateEnumValue", TrendType.GraphType.DATE_TYPE);
        setPointPopupModel(model);
        return "trends/setup/pointSetupPopup.jsp";
    }

    @GetMapping("/renderEditPointPopup")
    public String renderEditPointPopup(ModelMap model,
            @RequestParam("trendSeries") TrendSeries trendSeries) {
        model.addAttribute("trendSeries", trendSeries);
        LitePoint litePoint = pointDao.getLitePoint(trendSeries.getPointId());
        LiteYukonPAObject yukonPao = cache.getAllPaosMap().get(litePoint.getPaobjectID());
        model.addAttribute("deviceName", yukonPao.getPaoName());
        model.addAttribute("isDateTypeSelected", trendSeries.getType() == TrendType.GraphType.DATE_TYPE);
        setPointPopupModel(model);
        return "trends/setup/pointSetupPopup.jsp";
    }

    @PostMapping("/save")
    public String save(ModelMap model, YukonUserContext userContext,
            @ModelAttribute("trendDefinition") TrendModel trendModel, FlashScope flashScope) {
        // TODO: Add Validations

        // TODO: To be removed later
        log.info("Trend Name : " + trendModel.getName());
        for (TrendSeries trendSeries : trendModel.getTrendSeries()) {
            log.info("Point Id:" + trendSeries.getPointId());
            log.info("Label:" + trendSeries.getLabel());
            log.info("Color:" + trendSeries.getColor());
            log.info("Axis:" + trendSeries.getAxis());
            log.info("Type:" + trendSeries.getType());
            log.info("Multipler:" + trendSeries.getMultiplier());
            log.info("Style:" + trendSeries.getStyle());
        }

        // TODO: Add create or update condition check here...
        flashScope.setConfirm(new YukonMessageSourceResolvable(baseKey + "create.success", trendModel.getName()));
        return "redirect:/tools/trends";
    }

    @PostMapping("/addPoint")
    public String addPoint(ModelMap model, YukonUserContext userContext, HttpServletResponse response,
            @ModelAttribute("trendSeries") TrendSeries trendSeries)
            throws JsonGenerationException, JsonMappingException, IOException {
        // TODO: Validator code will be added as a part of YUK-22229
        // Validate the data...

        model.clear();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        json.put("trendSeries", trendSeries);
        LitePoint litePoint = pointDao.getLitePoint(trendSeries.getPointId());
        LiteYukonPAObject yukonPao = cache.getAllPaosMap().get(litePoint.getPaobjectID());
        json.put("pointName", litePoint.getPointName());
        json.put("deviceName", yukonPao.getPaoName());
        json.put("color", accessor.getMessage(trendSeries.getColor().getFormatKey()));
        json.put("axis", accessor.getMessage(trendSeries.getAxis().getFormatKey()));
        json.put("graphType", accessor.getMessage(trendSeries.getType().getFormatKey()));
        json.put("style", accessor.getMessage(trendSeries.getStyle().getFormatKey()));
        if (trendSeries.getType() == TrendType.GraphType.DATE_TYPE) {
            json.put("dateStr", dateFormattingService.format(trendSeries.getDate(), DateFormatEnum.DATE, userContext));
        }
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), json);
        return null;
    }

    private void setPointPopupModel(ModelMap model) {
        model.addAttribute("styles", RenderType.getWebSupportedTypes());
        model.addAttribute("graphTypes", TrendType.GraphType.values());
        model.addAttribute("axes", Lists.newArrayList(TrendAxis.values()));
        model.addAttribute("now", DateTime.now());
        model.addAttribute("graphTypeDateEnumValue", TrendType.GraphType.DATE_TYPE);
        model.addAttribute("colors", Color.values());
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {

        binder.registerCustomEditor(TrendType.GraphType.class, new EnumPropertyEditor<>(TrendType.GraphType.class));
        binder.registerCustomEditor(TrendAxis.class, new EnumPropertyEditor<>(TrendAxis.class));
        binder.registerCustomEditor(RenderType.class, new EnumPropertyEditor<>(RenderType.class));
        binder.registerCustomEditor(Color.class, new EnumPropertyEditor<>(Color.class));

        PropertyEditor dateTimeEditor = datePropertyEditorFactory.getDateTimePropertyEditor(DateFormatEnum.DATE, userContext, BlankMode.ERROR);
        
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
}