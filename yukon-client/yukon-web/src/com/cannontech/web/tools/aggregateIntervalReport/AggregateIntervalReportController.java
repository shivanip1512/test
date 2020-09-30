package com.cannontech.web.tools.aggregateIntervalReport;

import static org.joda.time.DateTime.now;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.aggregateIntervalDataReport.AggregateIntervalDataReportValidator;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.type.AttributeType;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.AggregateIntervalReportFilter;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.MissingIntervalData;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.Operation;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/aggregateIntervalReport/*")
public class AggregateIntervalReportController {
    
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private AttributeService attributeService;
    @Autowired private AttributeType attributeTypeEditor;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private AggregateIntervalDataReportValidator aggregateIntervalDataReportValidator;
    
    private static final String redirectLink = "redirect:/tools/aggregateIntervalReport/view";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    
    private static final Logger log = YukonLogManager.getLogger(AggregateIntervalReportController.class);

    @GetMapping("view")
    public String view(ModelMap model, YukonUserContext userContext) {
        AggregateIntervalReportFilter filter = new AggregateIntervalReportFilter();
        DateTime firstDayOfPrevMonth = now().withTimeAtStartOfDay().withZone(userContext.getJodaTimeZone())
                                            .minusMonths(1).withDayOfMonth(1);
        filter.setStartDate(firstDayOfPrevMonth.toInstant());
        DateTime lastDayOfPrevMonth = now().withTimeAtStartOfDay().withZone(userContext.getJodaTimeZone())
                                            .withDayOfMonth(1);
        filter.setEndDate(lastDayOfPrevMonth.toInstant());
        Object filterObj = model.get("filter");
        if (filterObj instanceof AggregateIntervalReportFilter) {
            filter = (AggregateIntervalReportFilter) filterObj;
            if (filter.getAttribute() != null) {
                model.addAttribute("selectedAttributes", Set.of(filter.getAttribute()));
            }
        }
        model.addAttribute("filter", filter);
        model.addAttribute("groupedAttributes", attributeService.getAllGroupedAttributes(userContext));
        model.addAttribute("intervals", TimeIntervals.getDataReportAggregateIntervals());
        model.addAttribute("missingDataOptions", MissingIntervalData.values());
        model.addAttribute("operations", Operation.values());
        model.addAttribute("fixedValueOption", MissingIntervalData.FIXED_VALUE);
        return "aggregateIntervalReport/view.jsp";
    }
    
    @PostMapping("exportNow")
    public String exportNow(@ModelAttribute AggregateIntervalReportFilter filter, BindingResult result, YukonUserContext userContext, 
                            FlashScope flashScope, HttpServletRequest request, HttpServletResponse response, 
                            RedirectAttributes redirectAtts, ModelMap model) throws IOException {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        populateDevices(request, filter, model, flashScope);
        
        aggregateIntervalDataReportValidator.validate(filter, result);
        if (result.hasErrors()) {
            populateErrorModel(response, redirectAtts, filter, result, flashScope, model);
        }
        
        String url = helper.findWebServerUrl(request, userContext, ApiURL.aggregateDataReportUrl);
        try {
            ResponseEntity<? extends Object> resp = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, filter);

            if (resp.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(filter, "filter");
                result = helper.populateBindingError(result, error, resp);
                if (result.hasErrors()) {
                    populateErrorModel(response, redirectAtts, filter, result, flashScope, model);
                }
            }

            if (resp.getStatusCode() == HttpStatus.OK) {
                List<String> headerRow = new ArrayList<String>();
                headerRow.add(accessor.getMessage("yukon.common.date"));
                headerRow.add(accessor.getMessage("yukon.common.time"));
                headerRow.add(accessor.getMessage("yukon.common.valueTxt"));
                List<List<String>> dataRows = (List<List<String>>) resp.getBody();
                String now = dateFormattingService.format(new Date(), DateFormatEnum.FILE_TIMESTAMP, userContext);
                WebFileUtils.writeToCSV(response, headerRow, dataRows, "aggregateIntervalReport_" + now + ".csv");
                return null;
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex);
            flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectLink;        
        } catch (RestClientException e) {
            log.error("Error exporting aggregate data interval report. Error: {}", e.getMessage());
            String pageLabel = accessor.getMessage("yukon.web.modules.tools.aggregateIntervalReport.pageName");
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.retrieve.error", pageLabel, e.getMessage()));
        }
        return redirectLink;
    }
    
    private String populateErrorModel(HttpServletResponse response, RedirectAttributes redirectAtts, AggregateIntervalReportFilter filter,
                                      BindingResult result, FlashScope flashScope, ModelMap model) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        redirectAtts.addFlashAttribute("filter", filter);
        redirectAtts.addFlashAttribute("org.springframework.validation.BindingResult.filter", result);
        List<MessageSourceResolvable> globalErrors = Lists.newArrayList();
        if (result.hasFieldErrors("deviceGroup") || result.hasFieldErrors("devices")) {
            globalErrors.add(new YukonMessageSourceResolvable("yukon.web.api.error.invalidDevicesOrDeviceGroup"));
        }
        if (result.hasFieldErrors("startDate")) {
            globalErrors.add(YukonMessageSourceResolvable.createDefaultWithoutCode(result.getFieldError("startDate").getDefaultMessage()));
        }
        if (result.hasFieldErrors("endDate")) {
            globalErrors.add(YukonMessageSourceResolvable.createDefaultWithoutCode(result.getFieldError("endDate").getDefaultMessage()));
        }
        flashScope.setError(globalErrors, FlashScopeListType.NONE);
        Object deviceCollectionObj = model.get("deviceCollection");
        if (deviceCollectionObj instanceof DeviceCollection) {
            redirectAtts.addFlashAttribute("deviceCollection", (DeviceCollection) deviceCollectionObj);
        }
        return redirectLink;
    }
    
    private void populateDevices(HttpServletRequest request, AggregateIntervalReportFilter filter, ModelMap model, FlashScope flashScope) {
        DeviceCollection collection = null;
        try {
            collection = deviceCollectionFactory.createDeviceCollection(request);
            model.addAttribute("deviceCollection", collection);
        } catch (Exception e) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.error.invalidDevicesOrDeviceGroup"));
        }
        if (collection != null) {
            if (collection.getCollectionType() == DeviceCollectionType.group) {
                String groupName = collection.getCollectionParameters().get("group.name");
                filter.setDeviceGroup(groupName);
            } else {
                List<Integer> paos = collection.getDeviceList().stream()
                        .map(device -> device.getDeviceId())
                        .collect(Collectors.toList());
                filter.setDevices(paos);
            }
        }
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        binder.registerCustomEditor(Attribute.class, attributeTypeEditor.getPropertyEditor());
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.NULL);
    }
}
