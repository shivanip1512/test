package com.cannontech.web.updater;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONException;
import net.sf.jsonOLD.JSONArray;
import net.sf.jsonOLD.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;

public class DataUpdaterController extends AbstractController {

    private static final Logger log = YukonLogManager.getLogger(DataUpdaterController.class);

    private DataUpdaterService dataUpdaterService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // get JSON data
        StringWriter jsonDataWriter = new StringWriter();
        FileCopyUtils.copy(request.getReader(), jsonDataWriter);
        String jsonStr = jsonDataWriter.toString();
        
        JSONObject data;
        try {
            data = JSONObject.fromString(jsonStr);
        } catch (JSONException e) {
            String referrer = request.getHeader("referer"); // Yes, with the misspelling.
            log.error("The system has recieved a string that it cannot parse.  ["+jsonStr+"]");
            log.error("Referring page: " + referrer);
            
            throw e;
        }
        
        long fromDate = data.getLong("fromDate");
        JSONArray dataArray = data.getJSONArray("data");
        
        int len = dataArray.length();
        Set<String> surSet = new HashSet<String>(len);
        
        for (int i = 0; i < len; ++i) {
            String id = dataArray.getString(i);
            surSet.add(id);
        }
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        UpdateResponse updates = dataUpdaterService.getUpdates(surSet, fromDate, userContext);
        
        // I could use the JsonView here, but this feels more symmetric
        JSONObject jsonUpdates = new JSONObject();
        jsonUpdates.put("toDate", updates.asOfTime);
        JSONObject jsonValueHash = new JSONObject();
        for (UpdateValue value : updates.values) {
            jsonValueHash.put(value.getFullIdentifier(), value.getValue().trim());
        }
        jsonUpdates.put("data", jsonValueHash);
        
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        
        String responseJsonStr = jsonUpdates.toString();
        writer.write(responseJsonStr);
        
        return null;
    }
    
    public void setDataUpdaterService(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }

}
