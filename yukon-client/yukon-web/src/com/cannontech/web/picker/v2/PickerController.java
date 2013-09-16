package com.cannontech.web.picker.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SubList;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.PickerFactory;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/v2/*")
public class PickerController {
    @Autowired private PickerFactory pickerService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ObjectFormattingService objectFormattingService;

    @RequestMapping
    public String build(Model model, String type, String id, Boolean multiSelectMode, Boolean immediateSelectMode,
            String mode, YukonUserContext userContext) {
        Picker<?> picker = pickerService.getPicker(type);

        model.addAttribute("title", picker.getDialogTitle());
        model.addAttribute("id", id);
        model.addAttribute("multiSelectMode", multiSelectMode);
        model.addAttribute("immediateSelectMode", immediateSelectMode);

        model.addAttribute("outputColumns", makeLocal(picker.getOutputColumns(), userContext));
        model.addAttribute("idFieldName", picker.getIdFieldName());

        return "inline".equals(mode) ? "inlinePicker" : "pickerDialog";
    }

    @RequestMapping
    public void idSearch(HttpServletResponse response, String type,
            Integer[] initialIds, String extraArgs, YukonUserContext context)
            throws IOException {
        Picker<?> picker = pickerService.getPicker(type);

        SearchResults<?> searchResult =
            picker.search(Lists.newArrayList(initialIds),
                    extraArgs, context);

        JSONObject json = getPopulatedJsonObj(searchResult, context);

        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
        }
    }

    @RequestMapping
    public void search(HttpServletResponse response, String type, String ss,
            @RequestParam(value = "start", required = false) String startStr,
            Integer count, String extraArgs, YukonUserContext context) throws IOException {
        int start = NumberUtils.toInt(startStr, 0);
        count = count == null ? 20 : count;
        if (count == -1) {
            count = Integer.MAX_VALUE;
        }

        Picker<?> picker = pickerService.getPicker(type);
        SearchResults<?> searchResult = picker.search(ss, start, count, extraArgs, context);

        response.setContentType("application/json");

        JSONObject json = getPopulatedJsonObj(searchResult, context);

        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(context);
        MessageSourceResolvable resolvable =
            new YukonMessageSourceResolvable("yukon.common.paging.viewing",
                                             searchResult.getStartIndex() + 1,
                                             searchResult.getEndIndex(),
                                             searchResult.getHitCount());
        json.put("pages", messageSourceAccessor.getMessage(resolvable));

        resolvable =
            new YukonMessageSourceResolvable("yukon.web.picker.selectAllPages",
                                             searchResult.getHitCount());
        json.put("selectAllPages", messageSourceAccessor.getMessage(resolvable));

        resolvable =
            new YukonMessageSourceResolvable("yukon.web.picker.allPagesSelected",
                                             searchResult.getHitCount());
        json.put("allPagesSelected", messageSourceAccessor.getMessage(resolvable));

        response.setContentType("application/json");

        try (PrintWriter out = response.getWriter()) {
            out.print(json.toString());
        }
    }

    private JSONObject getPopulatedJsonObj(SearchResults<?> searchResult, YukonUserContext context) {
        JSONObject json = new JSONObject();
        // Here we have special support for maps. This allows us to support enums and resolve displayable objs
        if (!searchResult.getResultList().isEmpty() && searchResult.getResultList().get(0) instanceof Map) {
            List<Map<String, String>> newHits = new ArrayList<>();
            for (Object hitObj : searchResult.getResultList()) {
                Map<String, String> newHit = new HashMap<>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) hitObj).entrySet()) {
                    String propertyName = (String) entry.getKey();
                    Object propertyValue = entry.getValue();
                    String translatedValue = objectFormattingService.formatObjectAsString(propertyValue, context);
                    newHit.put(propertyName, translatedValue);
                }
                newHits.add(newHit);
            }
            searchResult = SearchResults.pageBasedForSubList(searchResult.getCount(),
                          new SubList<>(newHits, (searchResult.getCurrentPage() - 1) * searchResult.getCount(), searchResult.getHitCount()));
        }
        json.put("hits", JSONObject.fromBean(searchResult));
        return json;
    }

    /**
     * The ultimate use of these output columns is in Javascript
     * (tableCreation.js via picker.js) to determine the label (title) and value
     * (field is the name of the property on the row object) of each column.
     * Because there is no way in Javascript to resolve a
     * MessageSourceResolvable, we do it here.
     */
    private List<LocalOutputColumn> makeLocal(List<OutputColumn> outputColumns,
            YukonUserContext userContext) {
        List<LocalOutputColumn> retVal = Lists.newArrayList();
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);

        for (OutputColumn outputColumn : outputColumns) {
            LocalOutputColumn translatedOutputColumn =
                new LocalOutputColumn(outputColumn.getField(),
                                      messageSourceAccessor.getMessage(outputColumn.getTitle()),
                                      outputColumn.getMaxCharsDisplayed());
            retVal.add(translatedOutputColumn);
        }

        return retVal;
    }

    public static class LocalOutputColumn {
        private String field;
        private String title;
        private int maxCharsDisplayed;

        private LocalOutputColumn(String field, String title,
                int maxCharsDisplayed) {
            this.field = field;
            this.title = title;
            this.maxCharsDisplayed = maxCharsDisplayed;
        }

        public String getField() {
            return field;
        }

        public String getTitle() {
            return title;
        }

        public int getMaxCharsDisplayed() {
            return maxCharsDisplayed;
        }
    }
}
