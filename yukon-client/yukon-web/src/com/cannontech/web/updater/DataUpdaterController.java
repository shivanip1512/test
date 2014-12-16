package com.cannontech.web.updater;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.exception.CacheManagementException;

@Controller
public class DataUpdaterController {
    @Autowired private DataUpdaterService dataUpdaterService;

    private static final Logger log = YukonLogManager.getLogger(DataUpdaterController.class);

    @RequestMapping("update") public @ResponseBody
    UpdateResponse update(@RequestBody UpdateRequest dataUpdateRequest, HttpServletResponse response,
            YukonUserContext userContext) {
        long fromDate = dataUpdateRequest.getFromDate();
        Set<String> tokens = new HashSet<>(dataUpdateRequest.getRequestTokens());

        try {
            Instant start = Instant.now();
            UpdateResponse updateResponse = dataUpdaterService.getUpdates(tokens, fromDate, userContext);
            long timeSpent = new Interval(start, Instant.now()).toDurationMillis();

            log.debug("Time for data updaters: " + timeSpent + "ms");

            return updateResponse;
        } catch (CacheManagementException e) {
            // In this case we want to return a 409 instead of a 500 error.
            response.setContentType("application/json");
            response.setStatus(HttpStatus.CONFLICT.value());
        }

        return null;
    }
}
