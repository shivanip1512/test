package com.cannontech.web.amr.meter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/historicalReadings/*")
public class HistoricalReadingsController {
    
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private static String baseKey = "yukon.web.modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings.";
    
    private static int MAX_ROWS_DISPLAY = 100;
    
    private static final String PERIOD = "period";
    
    private static final String ALL = "all";
    private static final String ONE_MONTH = "oneMonth";
    private static final String DISPLAY = "display";

    @RequestMapping("view")
    public String view(ModelMap model, HttpServletRequest request, Integer deviceId, String attribute,
                       @RequestParam(value="div_id", required=true) String dialogId, int pointId,
                       @RequestParam(value="sort", required=false) String sortBy,
                       @RequestParam(value="descending", required=false, defaultValue="false") Boolean isDescending,
                       final YukonUserContext context) throws ServletRequestBindingException {
        //default sort
        OrderBy orderBy =  OrderBy.TIMESTAMP;   
        Order order = Order.REVERSE;
        
        if (!StringUtils.isEmpty(sortBy)) {
            //change the sort order
            orderBy = OrderBy.valueOf(sortBy);         
            if (isDescending) {
                order = Order.FORWARD;
            } else {
                order = Order.REVERSE; 
            }
        } else {
            model.addAttribute("descending", false);
        }

        List<List<String>> points  = getLimitedPointData(DISPLAY, context, order, orderBy, pointId); 
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

        String attributeMsg = getAttributeMessage(deviceId, attribute, pointId, context);      
        String readings = accessor.getMessage(baseKey+"readings");
        String title = StringUtils.isBlank(attributeMsg) ? readings : attributeMsg + " " + readings;
        
        //setup ModelMap
        model.addAttribute("points", points);
        model.addAttribute("pointId", pointId);
        model.addAttribute("dialogId", dialogId);
        model.addAttribute("resultLimit", accessor.getMessage(baseKey + "resultLimit", MAX_ROWS_DISPLAY));
        model.addAttribute("attribute", attribute);
        model.addAttribute(ALL, accessor.getMessage(baseKey + ALL));
        model.addAttribute(ONE_MONTH,  accessor.getMessage(baseKey + ONE_MONTH));
        model.addAttribute("title", title);
        model.addAttribute("allUrl", getDownloadUrl(ALL, pointId, order, orderBy));
        model.addAttribute("oneMonthUrl", getDownloadUrl(ONE_MONTH, pointId, order, orderBy));
        
        return "historicalReadings/home.jsp";
    }
     
    @RequestMapping("download")
    public String download(ModelMap model,
                       HttpServletRequest request, 
                       HttpServletResponse response,
                       YukonUserContext context) throws ServletRequestBindingException, IOException{
        
        String period = ServletRequestUtils.getRequiredStringParameter(request, PERIOD);
        int pointId = ServletRequestUtils.getRequiredIntParameter(request, "pointId");
        Order order = Order.valueOf(ServletRequestUtils.getRequiredStringParameter(request, "order"));
        OrderBy orderBy = OrderBy.valueOf(ServletRequestUtils.getRequiredStringParameter(request, "orderBy"));
        
        List<List<String>> points = getLimitedPointData(period, context, order, orderBy, pointId);
        
        buildCsv(context, points, response);

        return null;
    }
    
    /**
     * Get the i18n'd message for an attribute.
     * @param deviceId - the deviceId for the device used for lookup of the attribute
     * @param pointId - the pointId for the device's point for which the attribute might exist
     * @param attribute - the attribute's default description (if known)
     * @return the i18n'd message for the attribute if it exists, an empty string otherwise.
     */
    private String getAttributeMessage(Integer deviceId, String attribute, int pointId, YukonUserContext context) {
        
        String attributeMsg = "";
        if (attribute != null) {
            attributeMsg = objectFormattingService.formatObjectAsString(BuiltInAttribute.valueOf(attribute), context);
        } else if (deviceId != null) {
            // We have a deviceId and a pointId, we can find the attribute.
            Map<Integer, PointInfo> pointInfoByPointIds = pointDao.getPointInfoByPointIds(Sets.newHashSet(pointId));
            PointInfo pointInfo = pointInfoByPointIds.get(pointId);
            if (pointInfo != null) {
                PaoType paoType = paoDao.getYukonPao(deviceId).getPaoIdentifier().getPaoType();
                PointIdentifier pointIdentifier = pointInfo.getPointIdentifier();
                BuiltInAttribute builtInAttribute = 
                        paoDefinitionDao.findOneAttributeForPaoTypeAndPoint(
                                PaoTypePointIdentifier.of(paoType, pointIdentifier));
                if (builtInAttribute != null) {
                    attributeMsg = objectFormattingService.formatObjectAsString(builtInAttribute, context);
                }
            }
        }
        return attributeMsg;
    }
    
    private void buildCsv(YukonUserContext userContext, List<List<String>> points, HttpServletResponse response) 
            throws IOException{
        
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[2];
        headerRow[0] = accessor.getMessage(baseKey + "tableHeader.timestamp.linkText");
        headerRow[1] = accessor.getMessage(baseKey + "tableHeader.value.linkText");
                
        List<String[]> dataRows = Lists.transform( points, new Function<List<String>, String[]>() {
            @Override
            public String[] apply(List<String> point) {
                String[] row = new String[]{point.get(0),  point.get(1)};
                return row;
            }
        });
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "HistoryReadings.csv");
    }
        
    private List<List<String>> getLimitedPointData(String period, 
            final YukonUserContext userContext, 
            Order order, 
            OrderBy orderBy, 
            int pointId) {
        
        List<PointValueHolder> data = null;
        if (period.equals(DISPLAY)) {
            DateTime startDate = new DateTime(userContext.getJodaTimeZone());
            startDate = startDate.minusDays(30);
            DateTime endDate = new DateTime(userContext.getJodaTimeZone());
            data = rawPointHistoryDao.getLimitedPointData(pointId, 
                    startDate.toDate(), 
                    endDate.toDate(), 
                    Clusivity.INCLUSIVE_EXCLUSIVE, 
                    false, 
                    order, 
                    MAX_ROWS_DISPLAY);
        }
        else if(period.equals(ONE_MONTH)){
            DateTime startDate = new DateTime(userContext.getJodaTimeZone());
            startDate = startDate.minusDays(30);
            DateTime endDate = new DateTime(userContext.getJodaTimeZone());
            data = rawPointHistoryDao.getPointData(pointId, 
                    startDate.toDate(), 
                    endDate.toDate(), 
                    Clusivity.INCLUSIVE_EXCLUSIVE, 
                    order); 
        }else if(period.equals(ALL)){
            Instant startDate = new Instant(0);
            data = rawPointHistoryDao.getPointData(pointId, 
                    startDate.toDate(), 
                    null, 
                    Clusivity.INCLUSIVE_INCLUSIVE, 
                    order);
        }
        data = sort(data, order, orderBy);
        List<List<String>> points = Lists.transform(data, new Function<PointValueHolder, List<String>>() {
            @Override
            public List<String> apply(PointValueHolder pvh) {
                List<String> row = Lists.newArrayList();
                row.add(pointFormattingService.getValueString(pvh, Format.DATE, userContext));
                row.add(pointFormattingService.getValueString(pvh, Format.SHORT, userContext));
                return row;
            }
        });
        return points;
    }
    
    private String getDownloadUrl(String period, int pointId, Order order, OrderBy orderBy){
        return "/meter/historicalReadings/download?" 
                + PERIOD + "=" + period + "&pointId=" + pointId + "&orderBy=" + orderBy + "&order=" + order;
    }

    private List<PointValueHolder> sort(List<PointValueHolder> data, final Order order, OrderBy orderBy) {
        List<PointValueHolder> modifiableList = new ArrayList<PointValueHolder>(data);
        if (orderBy == OrderBy.TIMESTAMP) {
            Collections.sort(modifiableList, new Comparator<PointValueHolder>() {
                @Override
                public int compare(PointValueHolder pvh1, PointValueHolder pvh2) {
                    if (order == Order.FORWARD) {
                        return pvh1.getPointDataTimeStamp().compareTo(pvh2.getPointDataTimeStamp());
                    } else {
                        return -pvh1.getPointDataTimeStamp().compareTo(pvh2.getPointDataTimeStamp());
                    }
                }
            });
        }
        else if (orderBy == OrderBy.VALUE) {
            Collections.sort(modifiableList, new Comparator<PointValueHolder>() {
                @Override
                public int compare(PointValueHolder pvh1, PointValueHolder pvh2) {
                    if (order == Order.FORWARD) {
                        return -new Double(pvh1.getValue()).compareTo(pvh2.getValue());
                    } else {
                        return new Double(pvh1.getValue()).compareTo(pvh2.getValue());
                    }
                }
            });
        }
        return modifiableList;
    }
    
}
