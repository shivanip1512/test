package com.cannontech.web.amr.vee.review;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.events.loggers.ValidationEventLogService;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.service.ValidationHelperService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.vee.review.VeeReviewController.DisplayType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
public class ValidationEngineAdvancedProcessingController {
    
    @Autowired private ValidationHelperService validationHelperService;
    @Autowired private ValidationEventLogService validationEventLogService;
    @Autowired private RphTagUiDao rphTagUiDao;
    
    @RequestMapping("/veeReview/advancedProcessing")
    public String advancedProcessing(ModelMap model) {
    
        Map<RphTag, Integer> tagCounts = rphTagUiDao.getAllValidationTagCounts();
        
        List<DisplayType> displayTypes = new ArrayList<DisplayType>();
        for (RphTag tag : RphTag.getAllValidation()) {
            displayTypes.add(new DisplayType(tag, false, tagCounts.get(tag)));
        }
        model.addAttribute("displayTypes", displayTypes);
        
        return "vee/review/menu.jsp";
    }
    
    @RequestMapping("/veeReview/advancedProcessing/acceptAll")
    public @ResponseBody boolean acceptAllCheckedRows(HttpServletRequest request, LiteYukonUser user) {
        Set<RphTag> selectedTags = getSelectedTags(request);
        
        validationHelperService.acceptAllMatchingRows(selectedTags, user);
        validationEventLogService.acceptedAllTaggedRows(selectedTags.toString(), user);
        
        return true;
    }
    
    @RequestMapping("/veeReview/advancedProcessing/deleteAll")
    public @ResponseBody boolean deleteAllCheckedRows(HttpServletRequest request, LiteYukonUser user) {
      Set<RphTag> selectedTags = getSelectedTags(request);
      
      validationHelperService.deleteAllMatchingRows(selectedTags, user);
      validationEventLogService.deletedAllTaggedRows(selectedTags.toString(), user);
      
      return true;
    }
    
    private Set<RphTag> getSelectedTags(HttpServletRequest request) {
        List<RphTag> tags = new ArrayList<RphTag>();
        for (RphTag rphTag : RphTag.getAllValidation()) {
            boolean tagChecked = ServletRequestUtils.getBooleanParameter(request, rphTag.name(), false);
            if (tagChecked) {
                tags.add(rphTag);
            }
        }
        return new HashSet<RphTag>(tags);
    }
    @RequestMapping("/veeReview/advancedProcessing/resetAll")
    public @ResponseBody boolean resetValidationEngine(LiteYukonUser user) {
        validationHelperService.resetValidationEngine(null);
        validationEventLogService.validationEngineReset(user);
        return true;
    }

    @RequestMapping("/veeReview/advancedProcessing/resetOneYear")
    public @ResponseBody boolean resetValidationEngineOneYear(YukonUserContext context) {
        Period oneYear = Period.years(1);
        DateTime now = new DateTime(context.getJodaTimeZone());
        DateTime since = now.minus(oneYear);
        validationHelperService.resetValidationEngine(since.toDate());
        validationEventLogService.validationEnginePartialReset(since, context.getYukonUser());
        
        return true;
    }
}
