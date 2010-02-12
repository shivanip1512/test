package com.cannontech.web.picker.v2;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.search.SearchResult;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.picker.v2.service.PickerService;
import com.cannontech.web.util.JsonView;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/v2/*")
public class PickerController {
    private PickerService pickerService;
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
    public ModelAndView search(String type, String ss,
            @RequestParam(value = "start", required = false) String startStr,
            Integer count, String pickerArgs)
            throws ServletException {
        int start = 0;
        try {
            start = Integer.parseInt(startStr);
        } catch (NumberFormatException nfe) {
            // just keep 0
        }
        count = count == null ? 20 : count;

        Picker<?> picker = pickerService.getPicker(type);
        SearchResult<?> hits = picker.search(ss, start, count);

        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("hitList", hits.getResultList());
        mav.addObject("hitCount", hits.getHitCount());
        mav.addObject("resultCount", hits.getResultCount());
        mav.addObject("startIndex", hits.getStartIndex());
        mav.addObject("endIndex", hits.getEndIndex());
        mav.addObject("previousIndex", hits.getPreviousStartIndex());
        mav.addObject("nextIndex", hits.getEndIndex());

        return mav;
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
    public void setPickerService(PickerService pickerService) {
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
