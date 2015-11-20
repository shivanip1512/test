package com.cannontech.web.picker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.service.PickerFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
public class PickerController {
    
    @Autowired private PickerFactory pickerFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ObjectFormattingService objectFormattingService;

    @RequestMapping("build")
    public String build(Model model, String type, String id, Boolean multiSelectMode, Boolean immediateSelectMode,
            String mode, YukonUserContext userContext) {
        Picker<?> picker = pickerFactory.getPicker(StringEscapeUtils.escapeXml(type));

        model.addAttribute("title", picker.getDialogTitle());
        model.addAttribute("id", StringEscapeUtils.escapeXml(id));
        model.addAttribute("multiSelectMode", multiSelectMode);
        model.addAttribute("immediateSelectMode", immediateSelectMode);

        return "inline".equals(mode) ? "inlinePicker" : "pickerDialog";
    }

    @RequestMapping("idSearch")
    public @ResponseBody Map<String, ?> idSearch(String type, Integer[] initialIds, String extraArgs, YukonUserContext context) {
        Picker<?> picker = pickerFactory.getPicker(type);

        SearchResults<?> searchResult = picker.search(Lists.newArrayList(initialIds), extraArgs, context);

        searchResult = resolveDisplayables(searchResult, context);

        return Collections.singletonMap("hits", searchResult);
    }

    @RequestMapping("search")
    public @ResponseBody Map<String, Object> search(HttpServletResponse response, String type, String ss,
            @RequestParam(value = "start", required = false) String startStr,
            Integer count, String extraArgs, YukonUserContext context) {
        int start = NumberUtils.toInt(startStr, 0);
        count = count == null ? 20 : count;
        if (count == -1) {
            count = Integer.MAX_VALUE;
        }

        Picker<?> picker = pickerFactory.getPicker(type);
        SearchResults<?> searchResult = picker.search(ss, start, count, extraArgs, context);

        response.setContentType("application/json");

        searchResult = resolveDisplayables(searchResult, context);
        Map<String, Object> json = Maps.newHashMapWithExpectedSize(4);
        json.put("hits", searchResult);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        MessageSourceResolvable resolvable = new YukonMessageSourceResolvable("yukon.common.paging.viewing",
            searchResult.getStartIndex() + 1, searchResult.getEndIndex(), searchResult.getHitCount());
        json.put("pages", messageSourceAccessor.getMessage(resolvable));

        resolvable =  new YukonMessageSourceResolvable("yukon.web.picker.selectAllPages", searchResult.getHitCount());
        json.put("selectAllPages", messageSourceAccessor.getMessage(resolvable));

        resolvable = new YukonMessageSourceResolvable("yukon.web.picker.allPagesSelected", searchResult.getHitCount());
        json.put("allPagesSelected", messageSourceAccessor.getMessage(resolvable));
        json.put("hits", searchResult);

        return json;
    }

    /**
     * Special support for maps. This allows us to support enums and resolve displayable objs
     */
    private SearchResults<?> resolveDisplayables(SearchResults<?> searchResult, YukonUserContext context) {
        if (searchResult.getResultList().isEmpty()) {
            return searchResult;
        }

        if (searchResult.getResultList().get(0) instanceof Map) {
            List<Map<String, String>> newHits = new ArrayList<>();
            for (Object hitObj : searchResult.getResultList()) {
                Map<String, String> newHit = new HashMap<>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) hitObj).entrySet()) {
                    Object propertyName = entry.getKey();
                    Object propertyValue = entry.getValue();
                    String translatedValue = objectFormattingService.formatObjectAsString(propertyValue, context);
                    newHit.put((String) propertyName, translatedValue);
                }
                newHits.add(newHit);
            }
            searchResult = SearchResults.pageBasedForSublist(newHits, searchResult.getCurrentPage(),
                searchResult.getCount(), searchResult.getHitCount());
        }
        
        return searchResult;
    }
    
}
