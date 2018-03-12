package com.cannontech.web.bulk;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao.SortBy;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilter;
import com.cannontech.common.bulk.collection.device.model.CollectionActionFilteredResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/progressReport/*")
public class ProgressReportController {

    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private CollectionActionLogDetailService collectionActionLogService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    
    private final static String baseKey = "yukon.web.modules.tools.bulk.progressReport.";
    
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(ModelMap model, Integer key) {
        CollectionActionResult result = collectionActionService.getResult(key);
        model.addAttribute("result", result);
        model.addAttribute("details", CollectionActionDetail.values());
        model.addAttribute("status", CommandRequestExecutionStatus.values());
        return "progressReport.jsp";
    }
    
    //database tables
    //https://jira.cooperpowereas.net/browse/YUK-17960
    
    // Please keep in mind that the method load fake data, you will need to completely clear the tables before
    // we connect to collection actions
    
    @RequestMapping(value = "updateProgressReport", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> updateProgressReport(Integer key) {
        Map<String, Object> json = new HashMap<>();
        CollectionActionResult result = collectionActionService.getResult(key);
        json.put("result",  result);
        
        //toggle icon display
        collectionActionLogService.hasLog(key);
        
        return json;
    }
    
    @RequestMapping(value = "cancel", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> cancel(Integer key, YukonUserContext context) {
        Map<String, Object> json = new HashMap<>();
        collectionActionService.cancel(key, context.getYukonUser());
        return json;
    }
    
    @RequestMapping(value = "log", method = RequestMethod.GET)
    public String log(HttpServletResponse response, FlashScope flashScope, Integer key, YukonUserContext userContext) {
        try (OutputStream output = response.getOutputStream();
            InputStream input = new FileInputStream(collectionActionLogService.getLog(key))) {
            response.setContentType("text/csv csv CSV");
            String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
            response.setHeader("Content-Disposition", "attachment; filename=\"CollectionActionLog-" + key + "_" + now + ".csv\"");
            IOUtils.copy(input, output);
        } catch (FileNotFoundException e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "logFileNotFoundError"));
        } catch (IOException e) {
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "logIOError"));
        }
        return null;
    }
   
    // http://localhost:8080/yukon/bulk/progressReport/ricky
    @RequestMapping(value = "ricky", method = RequestMethod.GET)
    public void ricky(ModelMap model) {
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put("Attribute(s)", "Blink Count, Delivered Demand");

       collectionActionService.loadLotsOfDataForNonCreCollectionActions(userInputs);

       //dropdown list
       System.out.println(collectionActionDao.getHistoricalActions());
       
       //buttons 
       //CommandRequestExecutionStatus - no canceling
       
       CollectionActionFilter filter = new CollectionActionFilter();
       filter.setActions(collectionActionDao.getHistoricalActions());
       filter.setStatuses(Lists.newArrayList(CommandRequestExecutionStatus.COMPLETE));
       DateTime min = new DateTime().minusDays(1);
       DateTime max = min.plusDays(2);
       filter.setStartDate(min.toDate());
       filter.setEndDate(max.toDate());
       
       
        SearchResults<CollectionActionFilteredResult> results = collectionActionDao.getCollectionActionFilteredResults(
            filter, PagingParameters.EVERYTHING, SortBy.SUCCESS, Direction.desc);
        System.out.println(results.getResultCount());
    }
    
    // http://localhost:8080/yukon/bulk/progressReport/marina
    @RequestMapping(value = "marina", method = RequestMethod.GET)
    public void marina(ModelMap model) {
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put("Attribute(s)", "Blink Count, Delivered Demand");
        DateTime stopTime = new DateTime().plusDays(1);

        CollectionActionResult result = collectionActionService.getRandomResult(60, userInputs, stopTime.toInstant(),
            CommandRequestExecutionStatus.COMPLETE, CollectionAction.MASS_CHANGE);
        collectionActionService.compareCacheAndGB(result.getCacheKey());
    }
}