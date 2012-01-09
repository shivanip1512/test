package com.cannontech.web.amr.meterEventsReport;

import java.beans.PropertyEditor;
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
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.amr.meterEventsReport.model.MeterEventStatusTypeGroupings;
import com.cannontech.web.amr.meterEventsReport.model.MeterEventsReportFilterBackingBean;
import com.cannontech.web.amr.meterEventsReport.model.MeterReportEvent;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
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
	@Autowired private AttributeService attributeService;
	
	private Map<String, Comparator<MeterReportEvent>> sorters;

    @PostConstruct
    public void initialize() {
        Builder<String, Comparator<MeterReportEvent>> builder = ImmutableMap.builder();
        builder.put("NAME", getNameComparator());
        builder.put("TYPE", getDeviceTypeComparator());
        builder.put("DATE", getDateComparator());
        builder.put("ATTR", getAttributeComparator());
        builder.put("VALUE", getFormattedValueComparator());
        sorters = builder.build();
    }
    
    @RequestMapping
    public String selectDevices(YukonUserContext userContext, ModelMap model) {
        return "meterEventsReport/selectDevices.jsp";
    }
    
    @RequestMapping
    public String selected(HttpServletRequest request, YukonUserContext userContext, ModelMap model)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(new MeterEventsReportFilterBackingBean(), request, model, null, null, userContext, null);
        return "meterEventsReport/report.jsp";
    }
    
    @RequestMapping
    public String report(@ModelAttribute("meterEventsReportFilterBackingBean") MeterEventsReportFilterBackingBean backingBean,
                         BindingResult bindingResult, HttpServletRequest request, ModelMap model,
                         FlashScope flashScope, YukonUserContext userContext, String attrNames)
                                 throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(backingBean, request, model, bindingResult, flashScope, userContext, attrNames);
		return "meterEventsReport/report.jsp";
	}


    @RequestMapping
    public String reportAll(HttpServletRequest request, ModelMap model, YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        MeterEventsReportFilterBackingBean backingBean = new MeterEventsReportFilterBackingBean();
        backingBean.setFromDate(null);
        backingBean.setToDate(null);
        setupModelMap(backingBean, request, model, null, null, userContext, null);
        return "meterEventsReport/report.jsp";
    }

    private void setupModelMap(MeterEventsReportFilterBackingBean backingBean,
                               HttpServletRequest request, ModelMap model,
                               BindingResult bindingResult, FlashScope flashScope,
                               YukonUserContext userContext, String attrNames)
                                       throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupBackingBean(backingBean, request, attrNames);
        setupCommonPageAttributes(bindingResult, flashScope, userContext, model);
        setupReportFromFilter(backingBean, userContext, model);
    }

    @RequestMapping(params = "clear")
    public String clear(HttpServletRequest request, ModelMap model, YukonUserContext userContext)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(new MeterEventsReportFilterBackingBean(), request, model, null, null, userContext, null);
        return "meterEventsReport/report.jsp";
    }
    
    @RequestMapping
    public String csv(@ModelAttribute("meterEventsReportFilterBackingBean") MeterEventsReportFilterBackingBean backingBean,
                      ModelMap model, HttpServletRequest request, HttpServletResponse response,
                      YukonUserContext userContext, String attrNames) throws IOException,
                          ServletRequestBindingException, DeviceCollectionCreationException {

        setupBackingBean(backingBean, request, attrNames);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        //header row
        String[] headerRow = new String[4];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.date.linkText");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.attribute.linkText");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.amr.meterEventsReport.report.tableHeader.value.linkText");
        
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
            String[] dataRow = new String[4];
            dataRow[0] = event.getMeter().getName();
            
            DateTime timeStamp = new DateTime(event.getPointValueHolder().getPointDataTimeStamp(), userContext.getJodaTimeZone());
            String dateTimeString = timeStamp.toString(DateTimeFormat.mediumDateTime());
            dataRow[1] = dateTimeString;
            
            dataRow[2] = event.getAttribute().name();
            
            String valueString = pointFormattingService.getValueString(event.getPointValueHolder(), Format.VALUE, userContext);
            dataRow[3] = valueString;
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

        Set<PaoType> paoTypes = Sets.newHashSet();
        for (SimpleDevice device : deviceCollection.getDeviceList()) {
            paoTypes.add(device.getDeviceType());
        }

        Set<Attribute> availableAttributes = Sets.newHashSet();
        for (PaoType paoType : paoTypes) {
            availableAttributes.addAll(attributeService.getAvailableAttributes(paoType));
        }

        Map<BuiltInAttribute, Boolean> tempMap = Maps.newHashMap(backingBean.getMeterEventTypesMap());
        for (Entry<BuiltInAttribute, Boolean> entry : backingBean.getMeterEventTypesMap().entrySet()) {
            if (!availableAttributes.contains(entry.getKey())) {
                tempMap.remove(entry.getKey());
            }
        }
        
        backingBean.setMeterEventTypesMap(tempMap);
    }
    
    private void setupReportFromFilter(MeterEventsReportFilterBackingBean backingBean,
                                     YukonUserContext userContext, ModelMap model) {
        
        List<MeterReportEvent> events = getReportEvents(backingBean, userContext);

        SearchResult<MeterReportEvent> filterResult = new SearchResult<MeterReportEvent>();
        filterResult.setBounds(backingBean.getStartIndex(),
                               backingBean.getItemsPerPage(),
                               events.size());

        events = events.subList(backingBean.getStartIndex(),
                                          backingBean.getStartIndex() + 
                                          backingBean.getItemsPerPage() > events.size() ?
                                          events.size() : backingBean.getStartIndex() +
                                          backingBean.getItemsPerPage());
        
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
        
        filterResult.setResultList(events);
        model.addAttribute("filterResult", filterResult);
        model.addAttribute("backingBean", backingBean);
        model.addAllAttributes(backingBean.getDeviceCollection().getCollectionParameters());
        model.addAttribute("meterEventTypesMap", getJSONObject(backingBean.getMeterEventTypesMap()));
    }

    private JSONObject getJSONObject(Map<BuiltInAttribute, Boolean> meterEventsMap) {
        JSONObject retVal = new JSONObject();
        for (Entry<BuiltInAttribute, Boolean> entry : meterEventsMap.entrySet()) {
            retVal.put(entry.getKey().toString(), entry.getValue());
        }
        return retVal;
    }

    private void setupCommonPageAttributes(BindingResult bindingResult, FlashScope flashScope,
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
                                                           backingBean.getFromDate(),
                                                           backingBean.getToDate(),
                                                           1,
                                                           backingBean.isIncludeDisabledPaos(),
                                                           Clusivity.INCLUSIVE_INCLUSIVE,
                                                           Order.REVERSE);
            } else {
                attributeData = rawPointHistoryDao.getAttributeData(meters,
                                                    type.getKey(),
                                                    backingBean.getFromDate(),
                                                    backingBean.getToDate(),
                                                    !backingBean.isIncludeDisabledPaos(),
                                                    Clusivity.INCLUSIVE_INCLUSIVE,
                                                    Order.FORWARD);
            }

            for (Entry<PaoIdentifier, PointValueQualityHolder> entry : attributeData.entries()) {
                if (backingBean.isOnlyActiveEvents()) {
                    String valueString = pointFormattingService.getValueString(entry.getValue(), Format.VALUE, userContext);
                    // StateGroup: "Event Status" has states "cleared" and "active"
                    // StateGroup: "Outage Status" has states "good", "questionable", and "bad"
                    if ("cleared".equalsIgnoreCase(valueString) || "good".equalsIgnoreCase(valueString)) continue;
                }
                MeterReportEvent meterReportEvent = new MeterReportEvent();
                meterReportEvent.setAttribute(type.getKey());
                meterReportEvent.setMeter(getMeterFromPaoIdentifier(meters, entry.getKey()));
                meterReportEvent.setPointValueHolder(entry.getValue());
                
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
    
    private static Comparator<MeterReportEvent> getNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<MeterReportEvent> nameOrdering = normalStringComparer
            .onResultOf(new Function<MeterReportEvent, String>() {
                public String apply(MeterReportEvent from) {
                    return from.getMeter().getName();
                }
            });
        return nameOrdering;
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
        Ordering<MeterReportEvent> result = dateOrdering.compound(getNameComparator());
        return result;
    }
    
    private static Comparator<MeterReportEvent> getAttributeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterReportEvent> attributeOrdering = normalStringComparer
        .onResultOf(new Function<MeterReportEvent, String>() {
            public String apply(MeterReportEvent from) {
                return from.getAttribute().name();
            }
        });
        Ordering<MeterReportEvent> result = attributeOrdering.compound(getNameComparator());
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
        PropertyEditor fromDateEditor = datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.START_OF_DAY, userContext);
        PropertyEditor toDateEditor = datePropertyEditorFactory.getPropertyEditor(DateOnlyMode.END_OF_DAY, userContext);
        binder.registerCustomEditor(Date.class, "fromDate", fromDateEditor);
        binder.registerCustomEditor(Date.class, "toDate", toDateEditor);
    }
    
}