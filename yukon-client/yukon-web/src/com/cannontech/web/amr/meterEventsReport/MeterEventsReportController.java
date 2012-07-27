package com.cannontech.web.amr.meterEventsReport;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.meter.service.impl.MeterEventLookupService;
import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.amr.meterEventsReport.model.MeterEventsReportFilterBackingBean;
import com.cannontech.web.amr.meterEventsReport.model.MeterReportEvent;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("/meterEventsReport/*")
public class MeterEventsReportController {
	
	@Autowired private DatePropertyEditorFactory datePropertyEditorFactory;
	@Autowired private RawPointHistoryDao rawPointHistoryDao;
	@Autowired private PointFormattingService pointFormattingService;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private DeviceCollectionFactory deviceCollectionFactory;
	@Autowired private MeterDao meterDao;
	@Autowired private PointDao pointDao;
	@Autowired private MeterEventLookupService meterEventLookupService;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	
	private final static String reportJspPath = "meterEventsReport/report.jsp";
	private final static String baseKey = "yukon.web.modules.amr.meterEventsReport.report";
	private Map<String, Comparator<MeterReportEvent>> sorters;

    @PostConstruct
    public void initialize() {
        Builder<String, Comparator<MeterReportEvent>> builder = ImmutableMap.builder();
        builder.put("NAME", getMeterNameComparator());
        builder.put("METER_NUMBER", getMeterNumberComparator());
        builder.put("TYPE", getDeviceTypeComparator());
        builder.put("DATE", getDateComparator());
        builder.put("EVENT", getEventComparator());
        builder.put("VALUE", getFormattedValueComparator());
        sorters = builder.build();
    }
    
    private Validator filterValidator =
            new SimpleValidator<MeterEventsReportFilterBackingBean>(MeterEventsReportFilterBackingBean.class) {
                @Override
                public void doValidation(MeterEventsReportFilterBackingBean backingBean, Errors errors) {
                    /* Dates & Hours */
                    if (backingBean.getFromInstant() == null && !errors.hasFieldErrors("fromInstant")) {
                        errors.rejectValue("fromInstant", "yukon.web.error.required");
                    } else if (backingBean.getToInstant() == null && !errors.hasFieldErrors("toInstant")) {
                        errors.rejectValue("toInstant", "yukon.web.error.required");
                    } else if(backingBean.getFromInstant().isAfterNow()) {
                        // If the from Instant is in the future
                        errors.rejectValue("fromInstant", baseKey + ".validation.fromDateInFuture");
                    } else if (!backingBean.getFromInstant().isBefore(backingBean.getToInstant())) {
                        errors.rejectValue("fromInstant", baseKey + ".validation.fromDateAfterToDate");
                    } else if(backingBean.getToInstant().isAfterNow()) {
                        // If the to Instant is in the future
                        errors.rejectValue("toInstant", baseKey + ".validation.toDateInFuture");
                    }
                }
            };
    
    @RequestMapping
    public String selectDevices(YukonUserContext userContext, ModelMap model) {
        return "meterEventsReport/selectDevices.jsp";
    }
    
    @RequestMapping
    public String selected(HttpServletRequest request, YukonUserContext userContext, ModelMap model)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(new MeterEventsReportFilterBackingBean(), request, model, null, null, userContext, null);
        return reportJspPath;
    }
    
    @RequestMapping
    public String report(@ModelAttribute("backingBean") MeterEventsReportFilterBackingBean backingBean,
                         BindingResult bindingResult, HttpServletRequest request, ModelMap model,
                         FlashScope flashScope, YukonUserContext userContext, String attrNames)
                                 throws ServletRequestBindingException, DeviceCollectionCreationException {
        filterValidator.validate(backingBean, bindingResult);
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            setupBackingBean(backingBean, request, attrNames);
            setupCommonPageAttributes(backingBean, bindingResult, flashScope, userContext, model);
            return reportJspPath;
        }
        setupModelMap(backingBean, request, model, bindingResult, flashScope, userContext, attrNames);
		return reportJspPath;
	}


    @RequestMapping
    public String reportAll(HttpServletRequest request, ModelMap model, YukonUserContext userContext, boolean includeDisabledPaos)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        MeterEventsReportFilterBackingBean backingBean = new MeterEventsReportFilterBackingBean();
        backingBean.setFromInstant(null);
        backingBean.setToInstant(null);
        backingBean.setIncludeDisabledPaos(includeDisabledPaos);
        setupModelMap(backingBean, request, model, null, null, userContext, null);
        return reportJspPath;
    }

    private void setupModelMap(MeterEventsReportFilterBackingBean backingBean,
                               HttpServletRequest request, ModelMap model,
                               BindingResult bindingResult, FlashScope flashScope,
                               YukonUserContext userContext, String attrNames)
                                       throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupBackingBean(backingBean, request, attrNames);
        setupCommonPageAttributes(backingBean, bindingResult, flashScope, userContext, model);
        setupReportFromFilter(backingBean, userContext, model);
    }

    @RequestMapping
    public String reset(HttpServletRequest request, ModelMap model, YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(new MeterEventsReportFilterBackingBean(), request, model, null, null, userContext, null);
        return reportJspPath;
    }
    
    @RequestMapping
    public String csv(@ModelAttribute("meterEventsReportFilterBackingBean") MeterEventsReportFilterBackingBean backingBean,
                      ModelMap model, HttpServletRequest request, HttpServletResponse response,
                      YukonUserContext userContext, String attrNames) throws IOException,
                          ServletRequestBindingException, DeviceCollectionCreationException {

        setupBackingBean(backingBean, request, attrNames);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        //header row
        String[] headerRow = new String[5];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.meterNumber.linkText");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.date.linkText");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.event.linkText");
        headerRow[4] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.value.linkText");
        
        //data rows
        List<MeterReportEvent> events = getReportEvents(backingBean, userContext);
        if (backingBean.getSort() != null) {
            if (backingBean.getDescending()) {
                Collections.sort(events, Collections.reverseOrder(sorters.get(backingBean.getSort())));
            } else {
                Collections.sort(events, sorters.get(backingBean.getSort()));
            }
        } else {
            Collections.sort(events, getDateComparator());
        }
        
        List<String[]> dataRows = Lists.newArrayList();
        for(MeterReportEvent event : events) {
            String[] dataRow = new String[5];
            dataRow[0] = event.getMeter().getName();
            dataRow[1] = event.getMeter().getMeterNumber();
            
            DateTime timeStamp = new DateTime(event.getPointValueHolder().getPointDataTimeStamp(), userContext.getJodaTimeZone());
            String dateTimeString = timeStamp.toString(DateTimeFormat.mediumDateTime());
            dataRow[2] = dateTimeString;
            
            dataRow[3] = event.getPointName();
            
            String valueString = pointFormattingService.getValueString(event.getPointValueHolder(), Format.VALUE, userContext);
            dataRow[4] = valueString;
            dataRows.add(dataRow);
        }
        
        //set up output for CSV
        response.setContentType("text/csv");
        response.setHeader("Content-Type", "application/force-download");
        String fileName = "MeterEvents.csv";
        fileName = ServletUtil.makeWindowsSafeFileName(fileName);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName);
        OutputStream outputStream = response.getOutputStream();
        
        //write out the file
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
        
        csvWriter.writeNext(headerRow);
        for (String[] line : dataRows) {
            csvWriter.writeNext(line);
        }
        csvWriter.close();
        
        return "";
    }
    
    private void setupBackingBean(MeterEventsReportFilterBackingBean backingBean,
                                  HttpServletRequest request, String attrNames)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        backingBean.setDeviceCollection(deviceCollection);
        
        if (attrNames != null) {
            List<String> attrNamesList = Lists.newArrayList(StringUtils.split(attrNames, ","));
            for (Entry<BuiltInAttribute, Boolean> type : backingBean.getMeterEventTypesMap().entrySet()) {
                if (attrNamesList.contains(type.getKey().name())) {
                    type.setValue(true);
                }
            }
        } else {
            backingBean.setEventTypesAllTrue();
        }

        Set<BuiltInAttribute> availableEventAttributes =
            meterEventLookupService.getAvailableEventAttributes(backingBean.getDeviceCollection()
                .getDeviceList());

        Map<BuiltInAttribute, Boolean> tempMap = Maps.newHashMap(backingBean.getMeterEventTypesMap());
        for (Entry<BuiltInAttribute, Boolean> entry : backingBean.getMeterEventTypesMap().entrySet()) {
            if (!availableEventAttributes.contains(entry.getKey())) {
                tempMap.remove(entry.getKey());
            }
        }
        
        backingBean.setMeterEventTypesMap(tempMap);
    }
    
    private void setupReportFromFilter(MeterEventsReportFilterBackingBean backingBean,
                                     YukonUserContext userContext, ModelMap model) {
        
        List<MeterReportEvent> events = getReportEvents(backingBean, userContext);
        
        for (MeterReportEvent meterReportEvent : events) {
            String valueString = pointFormattingService.getValueString(meterReportEvent.getPointValueHolder(), Format.VALUE, userContext);
            meterReportEvent.setFormattedValue(valueString);
        }

        if (backingBean.getSort() != null) {
            if (backingBean.getDescending()) {
                Collections.sort(events, Collections.reverseOrder(sorters.get(backingBean.getSort())));
            } else {
                Collections.sort(events, sorters.get(backingBean.getSort()));
            }
        } else {
            Collections.sort(events, Collections.reverseOrder(getDateComparator()));
        }

        DeviceCollection collectionFromReportResults = getDeviceCollectionFromReportResults(events, userContext);
        model.addAttribute("collectionFromReportResults", collectionFromReportResults);

        SearchResult<MeterReportEvent> filterResult = new SearchResult<MeterReportEvent>();
        filterResult.setBounds(backingBean.getStartIndex(),
                               backingBean.getItemsPerPage(),
                               events.size());

        events = events.subList(backingBean.getStartIndex(),
                                          backingBean.getStartIndex() + 
                                          backingBean.getItemsPerPage() > events.size() ?
                                          events.size() : backingBean.getStartIndex() +
                                          backingBean.getItemsPerPage());
        
        filterResult.setResultList(events);
        model.addAttribute("filterResult", filterResult);
        model.addAllAttributes(backingBean.getDeviceCollection().getCollectionParameters());
    }
    
    private DeviceCollection getDeviceCollectionFromReportResults(List<MeterReportEvent> events, YukonUserContext userContext) {
        Set<Meter> meters = Sets.newHashSet();
        for (MeterReportEvent reportEvent : events) {
            meters.add(reportEvent.getMeter());
        }

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String message = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.results.deviceCollectionDescription");

        DeviceCollection resultsDeviceCollection = deviceGroupCollectionHelper.createDeviceGroupCollection(meters.iterator(), message);
        return resultsDeviceCollection;
    }

    private JSONObject getJSONObject(Map<BuiltInAttribute, Boolean> meterEventsMap) {
        JSONObject retVal = new JSONObject();
        for (Entry<BuiltInAttribute, Boolean> entry : meterEventsMap.entrySet()) {
            retVal.put(entry.getKey().toString(), entry.getValue());
        }
        return retVal;
    }

    private void setupCommonPageAttributes(MeterEventsReportFilterBackingBean backingBean, BindingResult bindingResult, FlashScope flashScope,
                                               YukonUserContext userContext, ModelMap model) throws ServletRequestBindingException, DeviceCollectionCreationException {
        if (bindingResult != null && flashScope != null) {
            boolean hasFilterErrors = false;
            if (bindingResult.hasErrors()) {
                hasFilterErrors = true;
                List<MessageSourceResolvable> messages =
                      YukonValidationUtils.errorsForBindingResult(bindingResult);
                flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            }
            model.addAttribute("hasFilterErrors", hasFilterErrors);
        }
        
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("meterEventTypesMap", getJSONObject(backingBean.getMeterEventTypesMap()));
        
        model.addAttribute("generalEvents", getJSONArray(MeterEventStatusTypeGroupings.getGeneral()));
        model.addAttribute("hardwareEvents", getJSONArray(MeterEventStatusTypeGroupings.getHardware()));
        model.addAttribute("tamperEvents", getJSONArray(MeterEventStatusTypeGroupings.getTamper()));
        model.addAttribute("outageEvents", getJSONArray(MeterEventStatusTypeGroupings.getOutage()));
        model.addAttribute("meteringEvents", getJSONArray(MeterEventStatusTypeGroupings.getMetering()));
    }
    
    private List<MeterReportEvent> getReportEvents(MeterEventsReportFilterBackingBean backingBean, YukonUserContext userContext) {
        
        List<SimpleDevice> deviceList = backingBean.getDeviceCollection().getDeviceList();
        List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
        
        List<MeterReportEvent> events = Lists.newArrayList();
        if (CollectionUtils.isEmpty(deviceList)) {
            return events;
        }
        
        for (Entry<BuiltInAttribute, Boolean> type : backingBean.getMeterEventTypesMap().entrySet()) {
            if (!type.getValue()) continue;

            ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData;
            if (backingBean.isOnlyLatestEvent()) {
                attributeData = rawPointHistoryDao.getLimitedAttributeData(meters,
                                                           type.getKey(),
                                                           backingBean.getFromInstant().toDate(),
                                                           backingBean.getToInstant().toDate(),
                                                           1,
                                                           !backingBean.isIncludeDisabledPaos(),
                                                           Clusivity.INCLUSIVE_INCLUSIVE,
                                                           Order.REVERSE);
            } else {
                attributeData = rawPointHistoryDao.getAttributeData(meters,
                                                    type.getKey(),
                                                    backingBean.getFromInstant().toDate(),
                                                    backingBean.getToInstant().toDate(),
                                                    !backingBean.isIncludeDisabledPaos(),
                                                    Clusivity.INCLUSIVE_INCLUSIVE,
                                                    Order.FORWARD);
            }

            for (Entry<PaoIdentifier, PointValueQualityHolder> entry : attributeData.entries()) {
                PointValueQualityHolder pointValueHolder = entry.getValue();
                if (backingBean.isOnlyAbnormalEvents()) {
                    String valueString = pointFormattingService.getValueString(pointValueHolder, Format.VALUE, userContext);
                    // StateGroup: "Event Status" has states "cleared" and "active"
                    // StateGroup: "Outage Status" has states "good", "questionable", and "bad"
                    if ("cleared".equalsIgnoreCase(valueString) || "good".equalsIgnoreCase(valueString)) continue;
                }
                MeterReportEvent meterReportEvent = new MeterReportEvent();
                meterReportEvent.setAttribute(type.getKey());
                Meter meter = getMeterFromPaoIdentifier(meters, entry.getKey());
                meterReportEvent.setMeter(meter);
                meterReportEvent.setPointValueHolder(pointValueHolder);
                
                String pointName = pointDao.getPointName(pointValueHolder.getId());
                meterReportEvent.setPointName(pointName);
                
                events.add(meterReportEvent);
            }
        }
        return events;
    }
    
    private Meter getMeterFromPaoIdentifier(List<Meter> meters, PaoIdentifier paoIdentifier) {
        for (Meter meter : meters) {
            if (meter.getDeviceId() == paoIdentifier.getPaoId()) {
                return meter;
            }
        }
        return null;
    }
    
    private JSONArray getJSONArray(Set<BuiltInAttribute> originalSet) {
        List<String> strList = Lists.newArrayList();
        for (BuiltInAttribute attr: originalSet) {
            strList.add(attr.name());
        }
        Collections.sort(strList);
        
        JSONArray array = new JSONArray();
        for (String str : strList) {
            array.add(str);
        }
        return array;
    }
    
    private static Comparator<MeterReportEvent> getMeterNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<MeterReportEvent> nameOrdering = normalStringComparer
            .onResultOf(new Function<MeterReportEvent, String>() {
                public String apply(MeterReportEvent from) {
                    return from.getMeter().getName();
                }
            });
        return nameOrdering;
    }
    
    private static Comparator<MeterReportEvent> getMeterNumberComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterReportEvent> meterNumberOrdering = normalStringComparer
                .onResultOf(new Function<MeterReportEvent, String>() {
                    public String apply(MeterReportEvent from) {
                        return from.getMeter().getMeterNumber();
                    }
                });
        return meterNumberOrdering;
    }
    
    private static Comparator<MeterReportEvent> getDeviceTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterReportEvent> typeOrdering = normalStringComparer
                .onResultOf(new Function<MeterReportEvent, String>() {
                    public String apply(MeterReportEvent from) {
                        return from.getMeter().getPaoType().getDbString();
                    }
                });
        return typeOrdering;
    }
    
    private static Comparator<MeterReportEvent> getDateComparator() {
        Ordering<Date> dateComparer = Ordering.natural().nullsLast();
        Ordering<MeterReportEvent> dateOrdering = dateComparer
        .onResultOf(new Function<MeterReportEvent, Date>() {
            public Date apply(MeterReportEvent from) {
                return from.getPointValueHolder().getPointDataTimeStamp();
            }
        });
        Ordering<MeterReportEvent> result = dateOrdering.compound(getMeterNameComparator());
        return result;
    }
    
    private static Comparator<MeterReportEvent> getEventComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterReportEvent> attributeOrdering = normalStringComparer
        .onResultOf(new Function<MeterReportEvent, String>() {
            public String apply(MeterReportEvent from) {
                return from.getPointName();
            }
        });
        Ordering<MeterReportEvent> result = attributeOrdering.compound(getMeterNameComparator());
        return result;
    }
    
    private static Comparator<MeterReportEvent> getFormattedValueComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterReportEvent> formattedValueOrdering = normalStringComparer
            .onResultOf(new Function<MeterReportEvent, String>() {
                public String apply(MeterReportEvent from) {
                    return from.getFormattedValue();
                }
            });
        return formattedValueOrdering;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, final YukonUserContext userContext) {
        datePropertyEditorFactory.setupInstantPropertyEditor(binder, userContext, BlankMode.CURRENT);
    }
    
}