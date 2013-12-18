package com.cannontech.web.updater;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.exception.CacheManagementException;

@Controller
public class DataUpdaterController {

    @Autowired private DataUpdaterService dataUpdaterService;

    @RequestMapping("update")
    public @ResponseBody UpdateResponse update(@RequestBody UpdateRequest dataUpdateRequest,
            HttpServletResponse response, YukonUserContext userContext) {

        long fromDate = dataUpdateRequest.getFromDate();
        Set<String> tokens = new HashSet<>(dataUpdateRequest.getRequestTokens());

        try {
            return dataUpdaterService.getUpdates(tokens, fromDate, userContext);
        } catch (CacheManagementException e) {
            //In this case we want to return a 409 instead of a 500 error.
            response.setContentType("application/json");
            response.setStatus(HttpStatus.CONFLICT.value());
        }

        return null;
    }
}
