package com.cannontech.web.amr.rfnEventsReport;

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
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
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
import com.cannontech.web.amr.rfnEventsReport.model.RfnEventStatusTypeGroupings;
import com.cannontech.web.amr.rfnEventsReport.model.RfnEventsReportFilterBackingBean;
import com.cannontech.web.amr.rfnEventsReport.model.RfnReportEvent;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Controller
@RequestMapping("/rfnEventsReport/*")
public class RfnEventsReportController {
	
	private DatePropertyEditorFactory datePropertyEditorFactory;
	private RawPointHistoryDao rawPointHistoryDao;
	private PointFormattingService pointFormattingService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	private DeviceCollectionFactory deviceCollectionFactory;
	private MeterDao meterDao;
	
	private Map<String, Comparator<RfnReportEvent>> sorters;

    @PostConstruct
    public void initialize() {
        Builder<String, Comparator<RfnReportEvent>> builder = ImmutableMap.builder();
        builder.put("NAME", getNameComparator());
        builder.put("TYPE", getDeviceTypeComparator());
        builder.put("DATE", getDateComparator());
        builder.put("ATTR", getAttributeComparator());
        builder.put("VALUE", getFormattedValueComparator());
        sorters = builder.build();
    }
    
    @RequestMapping
    public String selectDevices(YukonUserContext userContext, ModelMap model) {
        return "rfnEventsReport/selectDevices.jsp";
    }
    
    @RequestMapping
    public String selected(HttpServletRequest request, YukonUserContext userContext, ModelMap model)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(new RfnEventsReportFilterBackingBean(), request, model, null, null, userContext, null);
        return "rfnEventsReport/report.jsp";
    }
    
    @RequestMapping
    public String report(@ModelAttribute("rfnEventsReportFilterBackingBean") RfnEventsReportFilterBackingBean backingBean,
                         BindingResult bindingResult, HttpServletRequest request, ModelMap model,
                         FlashScope flashScope, YukonUserContext userContext, String attrNames)
                                 throws ServletRequestBindingException, DeviceCollectionCreationException {
        setupModelMap(backingBean, request, model, bindingResult, flashScope, userContext, attrNames);
		return "rfnEventsReport/report.jsp";
	}

    private void setupModelMap(RfnEventsReportFilterBackingBean backingBean,
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
        setupModelMap(new RfnEventsReportFilterBackingBean(), request, model, null, null, userContext, null);
        return "rfnEventsReport/report.jsp";
    }
    
    @RequestMapping
    public String csv(@ModelAttribute("rfnEventsReportFilterBackingBean") RfnEventsReportFilterBackingBean backingBean,
                      ModelMap model, HttpServletRequest request, HttpServletResponse response,
                      YukonUserContext userContext, String attrNames) throws IOException,
                          ServletRequestBindingException, DeviceCollectionCreationException {

        setupBackingBean(backingBean, request, attrNames);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        //header row
        String[] headerRow = new String[4];
        headerRow[0] = messageSourceAccessor.getMessage("yukon.web.modules.amr.rfnEventsReport.report.tableHeader.deviceName.linkText");
        headerRow[1] = messageSourceAccessor.getMessage("yukon.web.modules.amr.rfnEventsReport.report.tableHeader.date.linkText");
        headerRow[2] = messageSourceAccessor.getMessage("yukon.web.modules.amr.rfnEventsReport.report.tableHeader.attribute.linkText");
        headerRow[3] = messageSourceAccessor.getMessage("yukon.web.modules.amr.rfnEventsReport.report.tableHeader.value.linkText");
        
        //data rows
        List<RfnReportEvent> events = getReportEvents(backingBean, userContext);
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
        for(RfnReportEvent event : events) {
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
        String fileName = "RfnEvents.csv";
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
    
    private void setupBackingBean(RfnEventsReportFilterBackingBean backingBean,
                                  HttpServletRequest request, String attrNames)
            throws ServletRequestBindingException, DeviceCollectionCreationException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        backingBean.setDeviceCollection(deviceCollection);
        
        if (attrNames != null) {
            List<String> attrNamesList = Lists.newArrayList(StringUtils.split(attrNames, ","));
            for (Entry<BuiltInAttribute, Boolean> type : backingBean.getRfnEventTypesMap().entrySet()) {
                if (attrNamesList.contains(type.getKey().name())) {
                    type.setValue(true);
                }
            }
        } else {
            backingBean.setEventTypesAllTrue();
        }
    }
    
    private void setupReportFromFilter(RfnEventsReportFilterBackingBean backingBean,
                                     YukonUserContext userContext, ModelMap model) {
        
        List<RfnReportEvent> events = getReportEvents(backingBean, userContext);

        SearchResult<RfnReportEvent> filterResult = new SearchResult<RfnReportEvent>();
        filterResult.setBounds(backingBean.getStartIndex(),
                               backingBean.getItemsPerPage(),
                               events.size());

        events = events.subList(backingBean.getStartIndex(),
                                          backingBean.getStartIndex() + 
                                          backingBean.getItemsPerPage() > events.size() ?
                                          events.size() : backingBean.getStartIndex() +
                                          backingBean.getItemsPerPage());
        
        for (RfnReportEvent rfnReportEvent : events) {
            String valueString = pointFormattingService.getValueString(rfnReportEvent.getPointValueHolder(), Format.VALUE, userContext);
            rfnReportEvent.setFormattedValue(valueString);
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
        model.addAttribute("rfnEventTypesMap", getJSONObject(backingBean.getRfnEventTypesMap()));
    }

    private JSONObject getJSONObject(Map<BuiltInAttribute, Boolean> rfnEventsMap) {
        JSONObject retVal = new JSONObject();
        for (Entry<BuiltInAttribute, Boolean> entry : rfnEventsMap.entrySet()) {
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
        
        model.addAttribute("generalEvents", getJSONArray(RfnEventStatusTypeGroupings.getGeneral()));
        model.addAttribute("hardwareEvents", getJSONArray(RfnEventStatusTypeGroupings.getHardware()));
        model.addAttribute("tamperEvents", getJSONArray(RfnEventStatusTypeGroupings.getTamper()));
        model.addAttribute("outageEvents", getJSONArray(RfnEventStatusTypeGroupings.getOutage()));
        model.addAttribute("meteringEvents", getJSONArray(RfnEventStatusTypeGroupings.getMetering()));
    }
    
    private List<RfnReportEvent> getReportEvents(RfnEventsReportFilterBackingBean backingBean, YukonUserContext userContext) {
        
        List<SimpleDevice> deviceList = backingBean.getDeviceCollection().getDeviceList();
        List<Meter> meters = meterDao.getMetersForYukonPaos(deviceList);
        
        List<RfnReportEvent> events = Lists.newArrayList();
        if (CollectionUtils.isEmpty(deviceList)) {
            return events;
        }
        
        for (Entry<BuiltInAttribute, Boolean> type : backingBean.getRfnEventTypesMap().entrySet()) {
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
                RfnReportEvent rfnReportEvent = new RfnReportEvent();
                rfnReportEvent.setAttribute(type.getKey());
                rfnReportEvent.setMeter(getMeterFromPaoIdentifier(meters, entry.getKey()));
                rfnReportEvent.setPointValueHolder(entry.getValue());
                
                events.add(rfnReportEvent);
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
    
    private static Comparator<RfnReportEvent> getNameComparator() {
        Ordering<String> normalStringComparer = Ordering.natural();
        Ordering<RfnReportEvent> nameOrdering = normalStringComparer
            .onResultOf(new Function<RfnReportEvent, String>() {
                public String apply(RfnReportEvent from) {
                    return from.getMeter().getName();
                }
            });
        return nameOrdering;
    }
    
    private static Comparator<RfnReportEvent> getDeviceTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<RfnReportEvent> typeOrdering = normalStringComparer
                .onResultOf(new Function<RfnReportEvent, String>() {
                    public String apply(RfnReportEvent from) {
                        return from.getMeter().getPaoType().getDbString();
                    }
                });
        return typeOrdering;
    }
    
    private static Comparator<RfnReportEvent> getDateComparator() {
        Ordering<Date> dateComparer = Ordering.natural().nullsLast();
        Ordering<RfnReportEvent> dateOrdering = dateComparer
        .onResultOf(new Function<RfnReportEvent, Date>() {
            public Date apply(RfnReportEvent from) {
                return from.getPointValueHolder().getPointDataTimeStamp();
            }
        });
        Ordering<RfnReportEvent> result = dateOrdering.compound(getNameComparator());
        return result;
    }
    
    private static Comparator<RfnReportEvent> getAttributeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<RfnReportEvent> attributeOrdering = normalStringComparer
        .onResultOf(new Function<RfnReportEvent, String>() {
            public String apply(RfnReportEvent from) {
                return from.getAttribute().name();
            }
        });
        Ordering<RfnReportEvent> result = attributeOrdering.compound(getNameComparator());
        return result;
    }
    
    private static Comparator<RfnReportEvent> getFormattedValueComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<RfnReportEvent> formattedValueOrdering = normalStringComparer
            .onResultOf(new Function<RfnReportEvent, String>() {
                public String apply(RfnReportEvent from) {
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
    
	@Autowired
	public void setDatePropertyEditorFactory(DatePropertyEditorFactory datePropertyEditorFactory) {
        this.datePropertyEditorFactory = datePropertyEditorFactory;
    }
	
	@Autowired
	public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }
	
	@Autowired
	public void setPointFormattingService(PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }
	
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
	
	@Autowired
	public void setDeviceCollectionFactory(DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
	
	@Autowired
	public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
}