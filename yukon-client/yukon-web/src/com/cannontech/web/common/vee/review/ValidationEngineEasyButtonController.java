package com.cannontech.web.common.vee.review;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.ValidationEventLogService;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.service.ValidationHelperService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableList;

@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
@RequestMapping("/vee/helper/*")
public class ValidationEngineEasyButtonController {
    private ValidationHelperService validationHelperService;
    private ValidationEventLogService validationEventLogService;

    private List<RphTag> validationTagsAsList = ImmutableList.copyOf(RphTag.getAllValidation());

    @ModelAttribute(value = "rphTags")
    public List<RphTag> getRphTags() {
        return validationTagsAsList;
    }

    @RequestMapping(value="menu", method = RequestMethod.GET)
    public String menu() {
        return "vee/review/menu.jsp";
    }

    @RequestMapping(value="resetValidationEngine", method = RequestMethod.POST)
    public String resetValidationEngine(YukonUserContext userContext, ModelMap model) {
        validationHelperService.resetValidationEngine(null);
        validationEventLogService.validationEngineReset(userContext.getYukonUser());
        model.addAttribute("validationEngineReset", true);
        return "redirect:menu";
    }

    @RequestMapping(value="resetValidationEngineOneYear", method = RequestMethod.POST)
    public String resetValidationEngineOneYear(YukonUserContext userContext, ModelMap model) {
        Period oneYear = Period.years(1);
        DateTime now = new DateTime(userContext.getJodaTimeZone());
        DateTime since = now.minus(oneYear);
        validationHelperService.resetValidationEngine(since.toDate());
        validationEventLogService.validationEnginePartialReset(since, userContext.getYukonUser());
        model.addAttribute("validationEngineReset", true);
        return "redirect:menu";
    }

    @RequestMapping(value="deleteAllTaggedRows", method = RequestMethod.POST)
    public String deleteAllTaggedRows(String[] selectedTags, YukonUserContext userContext,
            ModelMap model) {
        Set<RphTag> tagSet = ServletUtil.convertStringArrayToEnums(selectedTags, RphTag.class);
        validationHelperService.deleteAllMatchingRows(tagSet, userContext.getYukonUser());
        validationEventLogService.deletedAllTaggedRows(tagSet.toString(),
                                                       userContext.getYukonUser());
        model.addAttribute("tagsDeleted", true);
        return "redirect:menu";
    }

    @RequestMapping(value="acceptAllTaggedRows", method = RequestMethod.POST)
    public String acceptAllTaggedRows(String[] selectedTags,
            YukonUserContext userContext, ModelMap model) {
        Set<RphTag> tagSet = ServletUtil.convertStringArrayToEnums(selectedTags, RphTag.class);
        validationHelperService.acceptAllMatchingRows(tagSet, userContext.getYukonUser());
        validationEventLogService.acceptedAllTaggedRows(tagSet.toString(),
                                                        userContext.getYukonUser());
        model.addAttribute("tagsAccepted", true);
        return "redirect:menu";
    }

    @Autowired
    public void setValidationHelperService(ValidationHelperService validationHelperService) {
        this.validationHelperService = validationHelperService;
    }

    @Autowired
    public void setValidationEventLogService(ValidationEventLogService validationEventLogService) {
        this.validationEventLogService = validationEventLogService;
    }

}
