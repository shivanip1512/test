package com.cannontech.web.picker.v2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
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
import com.cannontech.common.search.SearchResult;
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
            Integer[] initialIds, String extraArgs, YukonUserContext userContext)
            throws IOException {
        Picker<?> picker = pickerService.getPicker(type);

        SearchResult<?> hits =
            picker.search(Lists.newArrayList(initialIds),
                    extraArgs, userContext);
        JSONObject object = new JSONObject();
        object.put("hits", JSONObject.fromBean(hits));

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(object.toString());
        out.close();
    }

    @RequestMapping
    public void search(HttpServletResponse response, String type, String ss,
            @RequestParam(value = "start", required = false) String startStr,
            Integer count, String extraArgs, YukonUserContext userContext)
            throws ServletException, IOException {
        int start = NumberUtils.toInt(startStr, 0);
        count = count == null ? 20 : count;
        if (count == -1) {
            count = Integer.MAX_VALUE;
        }

        Picker<?> picker = pickerService.getPicker(type);
        SearchResult<?> searchHits = picker.search(ss, start, count, extraArgs, userContext);

        response.setContentType("application/json");

        JSONObject object = new JSONObject();
        if (!searchHits.getResultList().isEmpty() && searchHits.getResultList().get(0) instanceof Map) {
            List<Map<String, String>> newHits = new ArrayList<>();
            for (Object hitObj : searchHits.getResultList()) {
                Map<String, String> newHit = new HashMap<>();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) hitObj).entrySet()) {
                    String propertyName = (String) entry.getKey();
                    Object propertyValue = entry.getValue();
                    String translatedValue = objectFormattingService.formatObjectAsString(propertyValue, userContext);
                    newHit.put(propertyName, translatedValue);
                }
                newHits.add(newHit);
            }
            searchHits = SearchResult.pageBasedForSubList(searchHits.getCount(),
                          new SubList<>(newHits, (searchHits.getCurrentPage() - 1) * searchHits.getCount(), searchHits.getHitCount()));
        }
        object.put("hits", JSONObject.fromBean(searchHits));

        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        MessageSourceResolvable resolvable =
            new YukonMessageSourceResolvable("yukon.common.paging.viewing",
                                             searchHits.getStartIndex() + 1,
                                             searchHits.getEndIndex(),
                                             searchHits.getHitCount());
        object.put("pages", messageSourceAccessor.getMessage(resolvable));

        resolvable =
            new YukonMessageSourceResolvable("yukon.web.picker.selectAllPages",
                                             searchHits.getHitCount());
        object.put("selectAllPages", messageSourceAccessor.getMessage(resolvable));

        resolvable =
            new YukonMessageSourceResolvable("yukon.web.picker.allPagesSelected",
                                             searchHits.getHitCount());
        object.put("allPagesSelected", messageSourceAccessor.getMessage(resolvable));

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(object.toString());
        out.close();
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
