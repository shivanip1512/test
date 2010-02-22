package com.cannontech.web.picker.v2;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.PickerFactory;
import com.google.common.collect.Lists;

import com.cannontech.i18n.YukonMessageSourceResolvable;

@Controller
@RequestMapping("/v2/*")
public class PickerController {
    private PickerFactory pickerService;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;

    @RequestMapping
    public String build(HttpServletResponse response, Model model, String type,
            String id, Boolean immediateSelectMode, YukonUserContext userContext) {
        Picker<?> picker = pickerService.getPicker(type);

        model.addAttribute("title", picker.getDialogTitle());
        model.addAttribute("id", id);
        model.addAttribute("immediateSelectMode", immediateSelectMode);

        JSONObject object = new JSONObject();
        object.put("outputColumns",
                   makeLocal(picker.getOutputColumns(), userContext));
        object.put("idFieldName", picker.getIdFieldName());
        response.addHeader("X-JSON", object.toString());

        return "pickerDialog";
    }

    @RequestMapping
    public void search(HttpServletResponse response, String type, String ss,
            @RequestParam(value = "start", required = false) String startStr,
            Integer count, YukonUserContext userContext)
            throws ServletException {
        int start = NumberUtils.toInt(startStr, 0);
        count = count == null ? 20 : count;

        Picker<?> picker = pickerService.getPicker(type);
        SearchResult<?> hits = picker.search(ss, start, count);

        JSONObject object = new JSONObject();
        object.put("hits", JSONObject.fromBean(hits));
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(userContext);
        MessageSourceResolvable pagesResolvable =
            new YukonMessageSourceResolvable("yukon.common.paging.viewing",
                                             hits.getStartIndex() + 1,
                                             hits.getEndIndex(),
                                             hits.getHitCount());
        String pages = messageSourceAccessor.getMessage(pagesResolvable);
        object.put("pages", pages);
        response.addHeader("X-JSON", object.toString());
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

    @Autowired
    public void setPickerService(PickerFactory pickerService) {
        this.pickerService = pickerService;
    }

    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
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
