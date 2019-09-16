package com.cannontech.web.picker;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.picker.PickerIdSearchCriteria;
import com.cannontech.web.api.picker.PickerSearchCriteria;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.google.common.collect.Maps;

@Controller
public class PickerController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ApiControllerHelper helper;
    private static final Logger log = YukonLogManager.getLogger(PickerController.class);


    @RequestMapping("build")
    public String build(Model model, Integer maxNumSelections, String type, String id, Boolean multiSelectMode, Boolean immediateSelectMode,
            String mode, YukonUserContext userContext, HttpServletRequest request) {
        
        String url = helper.findWebServerUrl(request, userContext, ApiURL.pickerBuildUrl + type);
        Picker<?> picker;
        try {
            ResponseEntity<? extends Object> apiResponse =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, Picker.class);
            if (apiResponse.getStatusCode() == HttpStatus.OK) {
                picker = (Picker<?>) apiResponse.getBody();
                model.addAttribute("title", picker.getDialogTitle());
            }
        } catch (RestClientException ex) {
            log.error("Error building picker: " + ex.getMessage());
        }
        
        model.addAttribute("id", StringEscapeUtils.escapeXml(id));
        model.addAttribute("multiSelectMode", multiSelectMode);
        model.addAttribute("immediateSelectMode", immediateSelectMode);
        model.addAttribute("maxNumSelections", maxNumSelections);
            
        return "inline".equals(mode) ? "inlinePicker" : "pickerDialog";
    }

    @RequestMapping("idSearch")
    public @ResponseBody Map<String, ?> idSearch(String type, Integer[] initialIds, String extraArgs,
            YukonUserContext userContext, HttpServletRequest request) {

        String url = helper.findWebServerUrl(request, userContext, ApiURL.pickerIdSearchUrl);
        PickerIdSearchCriteria searchCriteria = new PickerIdSearchCriteria();
        searchCriteria.setType(type);
        searchCriteria.setExtraArgs(extraArgs);
        searchCriteria.setInitialIds(initialIds);

        SearchResults<?> searchResult;
        try {
            ResponseEntity<? extends Object> apiResponse = apiRequestHelper.callAPIForObject(userContext, request, url,
                HttpMethod.POST, SearchResults.class, searchCriteria);
            if (apiResponse.getStatusCode() == HttpStatus.OK) {
                searchResult = (SearchResults<?>) apiResponse.getBody();
                return Collections.singletonMap("hits", searchResult);
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving search results for picker: " + ex.getMessage());
        }
        return null;

    }

    @RequestMapping("search")
    public @ResponseBody Map<String, Object> search(HttpServletRequest request, HttpServletResponse response,
            String type, String ss, @RequestParam(value = "start", required = false) String startStr, Integer count,
            String extraArgs, YukonUserContext context) {

        Map<String, Object> json = Maps.newHashMapWithExpectedSize(4);
        response.setContentType("application/json");

        int start = NumberUtils.toInt(startStr, 0);
        count = count == null ? 20 : count;
        if (count == -1) {
            count = Integer.MAX_VALUE;
        }

        PickerSearchCriteria searchCriteria = new PickerSearchCriteria();
        searchCriteria.setCount(count);
        searchCriteria.setQueryString(ss);
        searchCriteria.setStartCount(start);
        searchCriteria.setExtraArgs(extraArgs);
        searchCriteria.setType(type);

        SearchResults<?> searchResult;
        String url = helper.findWebServerUrl(request, context, ApiURL.pickerSearchUrl);
        try {
            ResponseEntity<? extends Object> apiResponse = apiRequestHelper.callAPIForObject(context, request, url,
                HttpMethod.POST, SearchResults.class, searchCriteria);

            if (apiResponse.getStatusCode() == HttpStatus.OK) {
                searchResult = (SearchResults<?>) apiResponse.getBody();
                json.put("hits", searchResult);

                MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
                MessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.common.paging.viewing",
                    searchResult.getStartIndex() + 1, searchResult.getEndIndex(), searchResult.getHitCount());
                json.put("pages", messageSourceAccessor.getMessage(resolvable));

                resolvable =
                    new YukonMessageSourceResolvable("yukon.web.picker.selectAllPages", searchResult.getHitCount());
                json.put("selectAllPages", messageSourceAccessor.getMessage(resolvable));

                resolvable =
                    new YukonMessageSourceResolvable("yukon.web.picker.allPagesSelected", searchResult.getHitCount());
                json.put("allPagesSelected", messageSourceAccessor.getMessage(resolvable));
                json.put("hits", searchResult);
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving search results for picker: " + ex.getMessage());
        }

        return json;
    }
}
