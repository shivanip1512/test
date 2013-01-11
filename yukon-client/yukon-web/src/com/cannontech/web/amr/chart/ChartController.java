package com.cannontech.web.amr.chart;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ConverterType;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.chart.service.ChartService;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.cannontech.web.input.EnumPropertyEditor;

@Controller
@RequestMapping("/chart/*")
public class ChartController {
    
	@Autowired private ChartService chartService;
	@Autowired private FlotChartService flotChartService;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody JSONObject chart(YukonUserContext userContext,
                        String title,
                        String pointIds,
                        ChartInterval interval,
                        long startDate,
                        long endDate,
                        Double yMin,
                        Double yMax,
                        @RequestParam(required = false, defaultValue = "LINE") GraphType graphType,
                        @RequestParam(required = false, defaultValue = "0") int reloadInterval,
                        @RequestParam(required = false, defaultValue = "RAW") ConverterType converterType) {
        List<Integer> ids = StringUtils.parseIntStringForList(pointIds);
        
        LitePoint point = pointDao.getLitePoint(ids.get(0));
        LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String chartIntervalString = messageSourceAccessor.getMessage(interval.getIntervalString());
        String yLabelUnits = messageSourceAccessor.getMessage(converterType.getFormattedUnits(unitMeasure, chartIntervalString));
        
        Date startDateObj = new Date(startDate);
        Date stopDateObj = new Date(endDate);
        JSONObject graphAsJSON = flotChartService.getMeterGraphData(ids,
                                                                   startDateObj,
                                                                   stopDateObj,
                                                                   interval,
                                                                   converterType, 
                                                                   graphType,
                                                                   yLabelUnits,
                                                                   userContext);
        graphAsJSON.put("title", title);
        
        return graphAsJSON;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        EnumPropertyEditor<ConverterType> converterTypeEditor =
            new EnumPropertyEditor<ConverterType>(ConverterType.class);
        EnumPropertyEditor<ChartInterval> chartIntervalEditor =
                new EnumPropertyEditor<ChartInterval>(ChartInterval.class);
        EnumPropertyEditor<GraphType> graphTypeEditor =
                new EnumPropertyEditor<GraphType>(GraphType.class);
        binder.registerCustomEditor(ConverterType.class, converterTypeEditor);
        binder.registerCustomEditor(ChartInterval.class, chartIntervalEditor);
        binder.registerCustomEditor(GraphType.class, graphTypeEditor);
    }
}
