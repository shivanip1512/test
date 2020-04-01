package com.cannontech.web.collectionActions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;

@Controller
@RequestMapping("/progressReport/*")
public class ProgressReportController {
    private static final Logger log = YukonLogManager.getLogger(ProgressReportController.class);

    @Autowired private CollectionActionService collectionActionService;
    @Autowired protected YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DateFormattingService dateFormattingService;
    
    private final static String baseKey = "yukon.web.modules.tools.collectionActions.progressReport.";
    
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(ModelMap model, Integer key) {
        setupModel(model, key);
        return "progressReport.jsp";
    }
    
    @RequestMapping(value = "view", method = RequestMethod.GET)
    public String view(ModelMap model, Integer key) {
        setupModel(model, key);
        return "progressReportPage.jsp";
    }
    
    private void setupModel(ModelMap model, Integer key) {
        CollectionActionResult result = collectionActionService.getResult(key);
        model.addAttribute("result", result);
        model.addAttribute("details", CollectionActionDetail.values());
        model.addAttribute("status", CommandRequestExecutionStatus.values());
        model.addAttribute("isLogAvailable", result.hasLogFile());
    }

    @RequestMapping(value = "updateProgressReport", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> updateProgressReport(Integer key) {
        Map<String, Object> json = new HashMap<>();
        CollectionActionResult result = collectionActionService.getResult(key);
        json.put("result",  result);
        
        //toggle icon display
        json.put("isLogAvailable", result.hasLogFile());
        
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
        CollectionActionResult result = collectionActionService.getResult(key);
        try (OutputStream output = response.getOutputStream();
            InputStream input = new FileInputStream(result.getLogFile())) {
            response.setContentType("text/csv csv CSV");
            String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
            response.setHeader("Content-Disposition", "attachment; filename=\"CollectionActionLog-" + key + "_" + now + ".csv\"");
            IOUtils.copy(input, output);
        } catch (FileNotFoundException e) {
            log.error("Collection Actions log file with key: " + key + " was not found.", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "logFileNotFoundError"));
        } catch (IOException e) {
            log.error("There was an issue accessing the Collection Actions log file with key: " + key + ".", e);
            flashScope.setError(new YukonMessageSourceResolvable(baseKey + "logIOError"));
        }
        return null;
    }
}