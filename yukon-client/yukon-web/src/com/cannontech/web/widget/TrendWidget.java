package com.cannontech.web.widget;

import static com.cannontech.core.service.DateFormattingService.DateOnlyMode.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
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
import com.cannontech.common.util.MutableRange;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.users.model.PreferenceGraphVisualTypeOption;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.cannontech.web.widget.support.impl.CachingWidgetParameterGrabber;
import com.google.common.collect.Sets;

/**
 * Widget used to display point data in a trend
 */
public class TrendWidget extends WidgetControllerBase {

    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceDao deviceDao;
    @Autowired private AttributeService attributeService;
    @Autowired private DateFormattingService dateFormattingService;

    private Map<String, AttributeGraphType> supportedAttributeGraphMap = null;
    private CachingWidgetParameterGrabber cachingWidgetParameterGrabber = null;
    private BuiltInAttribute defaultAttribute = null;

    @Override
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("trendWidget/render.jsp");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        String defaultAttributeStr = 
                WidgetParameterHelper.getStringParameter(request, "defaultAttribute", defaultAttribute.name());
        String attributeStr = 
                cachingWidgetParameterGrabber.getCachedStringParameter(request, "attribute", defaultAttributeStr);
        BuiltInAttribute attribute = BuiltInAttribute.valueOf(attributeStr);
        
        List<AttributeGraphType> availableAttributeGraphs = new ArrayList<>();
        Set<Attribute> existingAttributes = attributeService.getExistingAttributes(device, getAttributesFromGraphMap());
        
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

        ChartPeriod chartPeriod = getChartPeriod(request, userContext.getYukonUser());
        MutableRange<Date> dateRange = getDateRange(chartPeriod, userContext, request);
        ChartInterval chartInterval = chartPeriod.getChartUnit(dateRange.getImmutableRange());
        LitePoint point = attributeService.getPointForAttribute(device, attribute);
        String tabularDataViewer = WidgetParameterHelper.getRequiredStringParameter(request, "tabularDataViewer");
        String title = getTitle(chartPeriod, dateRange.getImmutableRange(), userContext, attribute, attributeGraphType);

        Date stopDateAdjusted = dateRange.getMax();
        if (chartPeriod == ChartPeriod.NOPERIOD) {
            // Custom time stop date will include full day until midnight in graph
            stopDateAdjusted = DateUtils.addMilliseconds(dateRange.getMax(), 
                                                        (int) TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS) - 1);
        }

        mav.addObject("attributeGraphType", attributeGraphType);
        mav.addObject("availableAttributeGraphs", availableAttributeGraphs);
        mav.addObject("period", chartPeriod);
        mav.addObject("interval", chartInterval);
        mav.addObject("startDate", dateRange.getMin());
        mav.addObject("stopDate", dateRange.getMax());
        mav.addObject("stopDateAdjusted", stopDateAdjusted);
        mav.addObject("graphType", getGraphType(request, userContext.getYukonUser()));
        mav.addObject("title", title);
        mav.addObject("pointId", point.getPointID());
        mav.addObject("tabularDataViewer", tabularDataViewer);
        return mav;
    }

    private MutableRange<Date> getDateRange(ChartPeriod chartPeriod, YukonUserContext userContext, 
                                            HttpServletRequest request) {
        Date now = new Date();
        MutableRange<Date> dateRange = new MutableRange<>(now, now);
        if (chartPeriod == ChartPeriod.NOPERIOD) {
            String startDate = cachingWidgetParameterGrabber.getCachedStringParameter(request, "startDateParam", null);
            String stopDate = cachingWidgetParameterGrabber.getCachedStringParameter(request, "stopDateParam", null);
            if (startDate != null) {
                try {
                    dateRange.setMin(dateFormattingService.flexibleDateParser(startDate, START_OF_DAY, userContext));
                } catch(ParseException e) {}
            }
            if (stopDate != null) {
                try {
                    dateRange.setMax(dateFormattingService.flexibleDateParser(stopDate, START_OF_DAY, userContext));
                } catch(ParseException e) {}
            }
        } else {
            dateRange.setMin(chartPeriod.backdate(now));
            cachingWidgetParameterGrabber.removeFromCache("startDateParam");
            cachingWidgetParameterGrabber.removeFromCache("stopDateParam");
        }
        return dateRange;
    }
    
    private String getTitle(ChartPeriod chartPeriod, Range<Date> dateRange, YukonUserContext userContext,
                            BuiltInAttribute attribute, AttributeGraphType attributeGraphType) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        if (chartPeriod != ChartPeriod.NOPERIOD) {
            MessageSourceResolvable chartPeriodResolvable = new YukonMessageSourceResolvable(chartPeriod.getFormatKey());
            MessageSourceResolvable converterTypeResolvable = 
                    new YukonMessageSourceResolvable(attributeGraphType.getConverterType().getFormatKey() + ".label");
            MessageSourceResolvable attributeResolvable = attribute.getMessage();
            
            String title = accessor.getMessage("yukon.web.widgetClasses.TrendWidget.hasPeriod", 
                    accessor.getMessage(new YukonMessageSourceResolvable(chartPeriod.getFormatKey())),
                    accessor.getMessage(converterTypeResolvable),
                    accessor.getMessage(attributeResolvable));
            return title;
        }

        MessageSourceResolvable converterTypeResolvable = 
                new YukonMessageSourceResolvable(attributeGraphType.getConverterType().getFormatKey() + ".label");
        MessageSourceResolvable attributeResolvable = attribute.getMessage();

        String title = accessor.getMessage("yukon.web.widgetClasses.TrendWidget.noPeriod",
            accessor.getMessage(converterTypeResolvable),
            accessor.getMessage(attributeResolvable),
            dateFormattingService.format(dateRange.getMin(), DateFormattingService.DateFormatEnum.DATE, userContext),
            dateFormattingService.format(dateRange.getMax(), DateFormattingService.DateFormatEnum.DATE, userContext));
        return title;
    }

    private ChartPeriod getChartPeriod(HttpServletRequest request, LiteYukonUser user) {
        ChartPeriod chartPeriod = ChartPeriod.fromString(request.getParameter("period"));
        if (chartPeriod == null) {
            return userPreferenceService.getDefaultChartPeriod(user);
        }
        if (chartPeriod != ChartPeriod.NOPERIOD) {
            return userPreferenceService.updatePreferenceChartPeriod(chartPeriod, user);
        }
        return ChartPeriod.NOPERIOD;
    }

    private GraphType getGraphType(HttpServletRequest request, LiteYukonUser user) {
        GraphType requestedGraphType = GraphType.fromString(request.getParameter("graphType"));
        PreferenceGraphVisualTypeOption prefGraph = null;
        if (requestedGraphType == null) {
            prefGraph = userPreferenceService.getDefaultGraphType(user);
        } else {
            prefGraph = userPreferenceService.updatePreferenceGraphType(requestedGraphType, user);
        }
        return prefGraph.getGraphType();
    }

    private Set<Attribute> getAttributesFromGraphMap() {
        Set<Attribute> graphMapAttributes = Sets.newHashSet(); 
        for (AttributeGraphType agt : supportedAttributeGraphMap.values()) {
            graphMapAttributes.add(agt.getAttribute());
        }
        return graphMapAttributes;
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