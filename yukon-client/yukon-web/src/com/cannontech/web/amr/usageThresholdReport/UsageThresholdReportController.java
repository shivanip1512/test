package com.cannontech.web.amr.usageThresholdReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.amr.usageThresholdReport.model.ThresholdReportFormCriteria;

@Controller
@RequestMapping("/usageThresholdReport/*")
public class UsageThresholdReportController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DateFormattingService dateFormattingService;

    @RequestMapping(value="report", method = RequestMethod.GET)
    public String report(ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Set<BuiltInAttribute> usageAttributes = BuiltInAttribute.getAttributesForGroup(AttributeGroup.USAGE);
        List<BuiltInAttribute> sortedAttributes = new ArrayList<>(usageAttributes);
        BuiltInAttribute.sort(sortedAttributes, accessor);
        model.addAttribute("usageAttributes", sortedAttributes);
        Instant max = new Instant();
        Instant min = max.minus(Duration.standardDays(7));
        ThresholdReportFormCriteria criteria = new ThresholdReportFormCriteria();
        criteria.setStartDate(min);
        criteria.setEndDate(max);
        model.addAttribute("filter", criteria);
        return "usageThresholdReport/report.jsp";
    }
    

    @RequestMapping(value="report", method = RequestMethod.POST)
    public String runReport(@ModelAttribute ThresholdReportFormCriteria filter, ModelMap model, HttpServletRequest request, 
                            @RequestParam String minDate, @RequestParam String maxDate, YukonUserContext userContext) 
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        DeviceCollection collection = deviceCollectionFactory.createDeviceCollection(request);
        filter.setDeviceCollection(collection);
        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.DATE, userContext);
        Instant start = Instant.parse(minDate, formatter);
        Instant end = Instant.parse(maxDate, formatter);
        filter.setStartDate(start);
        filter.setEndDate(end);
        //TODO: get results
        return "usageThresholdReport/results.jsp";
    }
}