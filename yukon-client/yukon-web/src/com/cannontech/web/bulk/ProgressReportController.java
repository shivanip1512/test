package com.cannontech.web.bulk;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/progressReport/*")
public class ProgressReportController {

    @Autowired private CollectionActionService collectionActionService;
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    
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
        return json;
    }
    
    @RequestMapping(value = "cancel", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> cancel(Integer key) {
        Map<String, Object> json = new HashMap<>();
        //TODO: Cancel the collection action
        return json;
    }
    
    @RequestMapping(value = "log", method = RequestMethod.GET)
    public String log(Integer key) {
        //TODO: Get log file
        return null;
    }
    
    private CollectionActionResult createMockedResult() {
        LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
        userInputs.put("Attribute(s)", "Blink Count, Delivered Demand");
        
        DateTime stopTime = new DateTime().plusDays(1);
        //CollectionActionResult result = collectionActionService.getResult(key);
        CollectionActionResult result = collectionActionService.getRandomResult(60, userInputs, stopTime.toInstant(),
            CommandRequestExecutionStatus.COMPLETE, CollectionAction.READ_ATTRIBUTE);
        return result;
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
       filter.setRange(new Range<>(min.toInstant(), true, max.toInstant(), false));
       
       
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