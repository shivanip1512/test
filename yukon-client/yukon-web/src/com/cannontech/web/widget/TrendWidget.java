package com.cannontech.web.widget;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.chart.model.AttributeGraphType;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartPeriod;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.users.model.PreferenceGraphTimeDurationOption;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.chart.service.ChartService;
import com.cannontech.web.common.chart.service.FlotChartService;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;

/**
 * Widget used to display point data in a trend
 */
public class TrendWidget extends WidgetControllerBase {

    @Autowired private UserPreferenceService prefService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ChartService chartService;
    @Autowired private FlotChartService flotChartService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AttributeService attributeService;
    @Autowired private DateFormattingService dateFormattingService;

    private Map<String, AttributeGraphType> supportedAttributeGraphMap = null;
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber = null;
    private BuiltInAttribute defaultAttribute = null;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        ModelAndView mav = new ModelAndView("trendWidget/render.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // DEVICE
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        // ATTRIBUTE, ATTRIBUTE GRAPH TYPE
        // - if selected attribute does not exist, choose first valid
        String defaultAttribute = WidgetParameterHelper.getStringParameter(request, "defaultAttribute", this.defaultAttribute.name());
        String attributeStr = cachingWidgetParameterGrabber.getCachedStringParameter(request, "attribute", defaultAttribute);
        BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
        
        List<AttributeGraphType> availableAttributeGraphs = new ArrayList<>();
        Set<Attribute> existingAttributes = attributeService.getAllExistingAttributes(device);
        
        AttributeGraphType attributeGraphType = null;
        
        for (AttributeGraphType agt : supportedAttributeGraphMap.values()) {
            if (existingAttributes.contains(agt.getAttribute())) {
                availableAttributeGraphs.add(agt);
                if (agt.getAttribute() == attribute){
                    attributeGraphType = agt;
                }
            }
        }
        
        //can't create a graph for this device
        if(availableAttributeGraphs.size() == 0){
            return mav;
        }
        
        if (attributeGraphType == null) {
            attributeGraphType = availableAttributeGraphs.get(0);
            attribute = attributeGraphType.getAttribute();
        }

        // SET START/END DATE
        Date startDate = new Date();
        Date stopDate = new Date();

        // GET PERIOD
        String strRequestedChartPeriod = request.getParameter("period");
        ChartPeriod requestedChartPeriod = StringUtils.isBlank(strRequestedChartPeriod) ? null : ChartPeriod.valueOf(strRequestedChartPeriod);
        PreferenceGraphTimeDurationOption prefPeriod = prefService.updatePreferenceOrGetDefaultChartPeriod(requestedChartPeriod, user);
        ChartPeriod chartPeriod = prefPeriod == null ? ChartPeriod.NOPERIOD : prefPeriod.getChartPeriod();
        String period = chartPeriod.name();
        
        if (chartPeriod == ChartPeriod.NOPERIOD) {

            String startDateParam = cachingWidgetParameterGrabber.getCachedStringParameter(request, "startDateParam", null);
            String stopDateParam = cachingWidgetParameterGrabber.getCachedStringParameter(request, "stopDateParam", null);

            if (startDateParam != null) {
            	try {
            		startDate = dateFormattingService.flexibleDateParser(startDateParam, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
            	} catch(ParseException e) {}
            }
            if (stopDateParam != null) {
                try {
                    stopDate = dateFormattingService.flexibleDateParser(stopDateParam, DateFormattingService.DateOnlyMode.START_OF_DAY, userContext);
                } catch(ParseException e) {}
            }

        } else {
            startDate = prefPeriod.backdate(startDate);
            cachingWidgetParameterGrabber.removeFromCache("startDateParam");
            cachingWidgetParameterGrabber.removeFromCache("stopDateParam");
        }

        ChartInterval chartInterval = chartPeriod.getChartUnit(startDate, stopDate);

        // GET DATES STRINGS
        String startDateStr = dateFormattingService.format(startDate, DateFormattingService.DateFormatEnum.DATE, userContext);
        String stopDateStr = dateFormattingService.format(stopDate, DateFormattingService.DateFormatEnum.DATE, userContext);

        // CHART STYLE (LINE/COLUMN)
        String strRequestedGraphType = request.getParameter("graphType");
        GraphType requestedGraphType = StringUtils.isBlank(strRequestedGraphType) ? null : GraphType.valueOf(strRequestedGraphType);
        PreferenceGraphVisualTypeOption prefGraph = prefService.updatePreferenceOrGetDefaultGraphType(requestedGraphType, user);
        GraphType graphType = prefGraph.getGraphType();

        // TABULAR DATA LINK REQUIREMENTS 
        // - point id
        // - start/stop date as seconds
        // - view controller method name
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
        int pointId = point.getPointID();
        
        Long startDateMillis = startDate.getTime();
        Long stopDateMillis = stopDate.getTime();
        
        String tabularDataViewer = WidgetParameterHelper.getRequiredStringParameter(request, "tabularDataViewer");


        // SET MAV
        mav.addObject("attributeGraphType", attributeGraphType);
        mav.addObject("availableAttributeGraphs", availableAttributeGraphs);
        mav.addObject("period", period);
        mav.addObject("interval", chartInterval);
        mav.addObject("startDate", startDate);
        mav.addObject("stopDate", stopDate);
        mav.addObject("graphType", graphType);
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
		if (chartPeriod != ChartPeriod.NOPERIOD) {
        	MessageSourceResolvable chartPeriodResolvable = new YukonMessageSourceResolvable(chartPeriod.getFormatKey());
        	MessageSourceResolvable converterTypeResolvable = new YukonMessageSourceResolvable(attributeGraphType.getConverterType().getFormatKey() + ".label");
        	MessageSourceResolvable attributeResolvable = attribute.getMessage();
        	
        	String title = accessor.getMessage("yukon.web.widgetClasses.TrendWidget.hasPeriod", 
        			accessor.getMessage(chartPeriodResolvable),
        			accessor.getMessage(converterTypeResolvable),
        			accessor.getMessage(attributeResolvable));
        	
			mav.addObject("title", title);
        } else {
        	MessageSourceResolvable converterTypeResolvable = new YukonMessageSourceResolvable(attributeGraphType.getConverterType().getFormatKey() + ".label");
        	MessageSourceResolvable attributeResolvable = attribute.getMessage();
        	
        	String title = accessor.getMessage("yukon.web.widgetClasses.TrendWidget.noPeriod",
        			accessor.getMessage(converterTypeResolvable),
        			accessor.getMessage(attributeResolvable),
        			startDateStr,
        			stopDateStr);
        	
            mav.addObject("title", title);
        }
        
        mav.addObject("pointId", pointId);
        mav.addObject("startDateMillis", startDateMillis);
        mav.addObject("stopDateMillis", stopDateMillis);
        mav.addObject("tabularDataViewer", tabularDataViewer);
        return mav;
    }

    @Required
    public void setSupportedAttributeGraphSet(List<AttributeGraphType> supportedAttributeGraphSet) {
        supportedAttributeGraphMap = new LinkedHashMap<>();
        for (AttributeGraphType agt : supportedAttributeGraphSet) {
            supportedAttributeGraphMap.put(agt.getLabel(), agt);
        }
    }

    @Required
    public void setCachingWidgetParameterGrabber(
            CachingWidgetParameterGrabber cachingWidgetParameterGrabber) {
        this.cachingWidgetParameterGrabber = cachingWidgetParameterGrabber;
    }
    
    @Required
    public void setDefaultAttribute(BuiltInAttribute defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}
}