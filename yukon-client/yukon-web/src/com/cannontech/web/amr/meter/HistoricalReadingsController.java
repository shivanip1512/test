package com.cannontech.web.amr.meter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/historicalReadings/*")
public class HistoricalReadingsController {
    
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private PointFormattingService pointFormattingService;

    private static String baseKey = "yukon.web.modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings.";
    
    private static int MAX_ROWS_DISPLAY = 100;
    
    private static final String PERIOD = "period";
    
    private static final String ALL = "all";
    private static final String ONE_MONTH = "oneMonth";
    private static final String DISPLAY = "display";

    @RequestMapping
    public String view(ModelMap model,
                       HttpServletRequest request,
                       final YukonUserContext context) throws ServletRequestBindingException{
       
        String attribute = ServletRequestUtils.getRequiredStringParameter(request, "attribute");
        String dialogId = ServletRequestUtils.getRequiredStringParameter(request, "div_id");
        Integer pointId = ServletRequestUtils.getRequiredIntParameter(request, "pointId");

        //default sort
        OrderBy orderBy =  OrderBy.TIMESTAMP;   
        Order order = Order.REVERSE;
        
        String sortBy = ServletRequestUtils.getStringParameter(request, "sort");
        
        if(!StringUtils.isEmpty(sortBy)){
            //change the sort order
            orderBy = OrderBy.valueOf(sortBy);         
            boolean isDescending = ServletRequestUtils.getBooleanParameter(request, "descending", false);
            if(isDescending){
                order = Order.FORWARD;
            }else{
                order = Order.REVERSE; 
            }
        }else{
            model.addAttribute("descending", false);
        }

        List<List<String>> points  = getLimitedPointData(DISPLAY, context, order, orderBy, pointId); 
        
        //setup ModelMap
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        model.addAttribute("points", points);
        model.addAttribute("pointId", pointId);
        model.addAttribute("dialogId", dialogId);
        model.addAttribute("resultLimit", accessor.getMessage(baseKey + "resultLimit", MAX_ROWS_DISPLAY));
        model.addAttribute("attribute", attribute);
        model.addAttribute(ALL, accessor.getMessage(baseKey + ALL));
        model.addAttribute(ONE_MONTH,  accessor.getMessage(baseKey + ONE_MONTH));
        String attributeMsg = BuiltInAttribute.valueOf(attribute).getMessage().getDefaultMessage();
        String readings = accessor.getMessage(baseKey+"readings");
        String title = attributeMsg +" "+ readings;
        model.addAttribute("title", title);
        model.addAttribute("allUrl", getDownloadUrl(ALL, pointId, order, orderBy));
        model.addAttribute("oneMonthUrl", getDownloadUrl(ONE_MONTH, pointId, order, orderBy));
        
        return "historicalReadings/home.jsp";
    }
    
     
    @RequestMapping
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

        return "";
    }
    
    private void buildCsv(YukonUserContext userContext, List<List<String>> points, HttpServletResponse response) throws IOException{
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
  
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = "HistoryReadings.csv";
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        
        //write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        csvWriter.writeNext(headerRow);
        for (String[] line : dataRows) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
    }
        
    private List<List<String>> getLimitedPointData(String period, final YukonUserContext context, Order order, OrderBy orderBy, int pointId){
        List<PointValueHolder> data = null;
        if(period.equals(DISPLAY)){
            DateTime startDate = new DateTime(context.getJodaTimeZone());
            startDate = startDate.minusDays(30);
            DateTime endDate = new DateTime(context.getJodaTimeZone());
            data = rawPointHistoryDao.getLimitedPointData(pointId, startDate.toDate(), endDate.toDate(), Clusivity.INCLUSIVE_EXCLUSIVE, order, orderBy, MAX_ROWS_DISPLAY); 
        }
        else if(period.equals(ONE_MONTH)){
            DateTime startDate = new DateTime(context.getJodaTimeZone());
            startDate = startDate.minusDays(30);
            DateTime endDate = new DateTime(context.getJodaTimeZone());
            data = rawPointHistoryDao.getPointData(pointId, startDate.toDate(), endDate.toDate(), Clusivity.INCLUSIVE_EXCLUSIVE, order, orderBy); 
        }else if(period.equals(ALL)){
            Instant startDate = new Instant(0);
            data = rawPointHistoryDao.getPointData(pointId, startDate.toDate(), null, Clusivity.INCLUSIVE_INCLUSIVE, order, orderBy);
        }
        List<List<String>> points = Lists.transform(data, new Function<PointValueHolder, List<String>>() {
            @Override
            public List<String> apply(PointValueHolder pvh) {
                List<String> row = Lists.newArrayList();
                row.add(pointFormattingService.getValueString(pvh, Format.DATE, context));
                StringBuilder value  = new StringBuilder();
                value.append(pointFormattingService.getValueString(pvh, Format.RAWVALUE, context));
                value.append(" ");
                value.append(pointFormattingService.getValueString(pvh, Format.UNIT, context));
                value.append(" ");
                value.append(pointFormattingService.getValueString(pvh, Format.QUALITY, context));
                row.add(value.toString());
                return row;
            }
        });
        return points;
    }
    
    private String getDownloadUrl(String period, int pointId, Order order, OrderBy orderBy){
        return "/spring/meter/historicalReadings/download?" + PERIOD +"="+ period+"&pointId="+pointId+"&orderBy="+orderBy+"&order="+order;
    }
}
