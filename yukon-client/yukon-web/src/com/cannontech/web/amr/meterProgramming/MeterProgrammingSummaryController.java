package com.cannontech.web.amr.meterProgramming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramCommandResult;
import com.cannontech.common.device.programming.service.MeterProgrammingService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckCparmLicense;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao.SortBy;
import com.cannontech.web.tools.device.programming.model.MeterProgramInfo;
import com.cannontech.web.tools.device.programming.model.MeterProgramStatistics;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter.DisplayableStatus;
import com.cannontech.web.util.WebFileUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/meterProgramming/*")
@CheckCparmLicense(license = MasterConfigLicenseKey.METER_PROGRAMMING_ENABLED)
public class MeterProgrammingSummaryController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private MeterProgrammingSummaryDao meterProgrammingSummaryDao;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private MeterProgrammingService meterProgrammingService;
    @Autowired private IDatabaseCache dbCache;

    private final static String baseKey = "yukon.web.modules.amr.meterProgramming.";

    @GetMapping("home")
    public String home(@DefaultSort(dir=Direction.asc, sort="program") SortingParameters sorting, ModelMap model, YukonUserContext userContext) {
        getSortedResults(userContext, sorting, model);
        return "meterProgramming/home.jsp";
    }
    
    @GetMapping("sortedYukonPrograms")
    public String sortedYukonPrograms(@DefaultSort(dir=Direction.asc, sort="program") SortingParameters sorting, ModelMap model, 
                                      YukonUserContext userContext) {
        getSortedResults(userContext, sorting, model);
        return "meterProgramming/sortedYukonPrograms.jsp";
    }
    
    private void getSortedResults(YukonUserContext userContext, SortingParameters sorting, ModelMap model) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        List<MeterProgramStatistics> detail = meterProgrammingSummaryDao.getProgramStatistics(userContext);
        
        List<MeterProgramStatistics> specialCases = detail.stream()
                                                          .filter(statistic -> statistic.getProgramInfo().getGuid() == null)
                                                          .collect(Collectors.toList());
        
        List<MeterProgramStatistics> yukonPrograms = detail.stream()
                                                           .filter(statistic -> !specialCases.contains(statistic))
                                                           .collect(Collectors.toList());
        
        ProgramSortBy sortBy = ProgramSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        Comparator<MeterProgramStatistics> comparator = (o1, o2) -> o1.getProgramInfo().getName().compareTo(o2.getProgramInfo().getName());
        if (sortBy == ProgramSortBy.numberSuccessful) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getDeviceTotal()).compareTo(Integer.valueOf(o2.getDeviceTotal()));
        } else if (sortBy == ProgramSortBy.numberInProgress) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getInProgressTotal()).compareTo(Integer.valueOf(o2.getInProgressTotal()));
        } else if (sortBy == ProgramSortBy.numberFailed) {
            comparator = (o1, o2) -> Integer.valueOf(o1.getFailureTotal()).compareTo(Integer.valueOf(o2.getFailureTotal()));
        }
        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(yukonPrograms, comparator);
        
        List<SortableColumn> columns = new ArrayList<>();
        for (ProgramSortBy column : ProgramSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
        
        model.addAttribute("programs", yukonPrograms);
        model.addAttribute("specialCases", specialCases);
    }
    
    @DeleteMapping("/{guid}/delete")
    public void delete(@PathVariable String guid, FlashScope flash, HttpServletResponse resp) {
        UUID uuid = UUID.fromString(guid);
        MeterProgram program = meterProgrammingDao.getMeterProgram(uuid);
        try {
            meterProgrammingDao.deleteMeterProgram(uuid);
        } catch (Exception e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", program.getName()));
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return;
        }
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", program.getName()));
    }
    
    public enum ProgramSortBy implements DisplayableEnum {

        program,
        numberSuccessful,
        numberInProgress,
        numberFailed;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
    
    @GetMapping("summary")
    public String summary(MeterProgrammingSummaryFilter filter, @DefaultSort(dir=Direction.asc, sort="DEVICE_NAME") SortingParameters sorting, 
                          @DefaultItemsPerPage(value=100) PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        List<MeterProgramInfo> programs = meterProgrammingSummaryDao.getMeterProgramInfos(userContext);
        if (filter.getPrograms() != null) {
            List<String> selectedProgramNames = filter.getPrograms().stream()
                    .map(MeterProgramInfo::getName)
                    .collect(Collectors.toList());
            model.addAttribute("selectedPrograms", selectedProgramNames);
        }

        getFilteredResults(filter, sorting, paging, model, userContext);
        
        List<MeterProgramInfo> specialCases = programs.stream()
                .filter(program -> program.getSource() != null && program.getSource().isNotYukon())
                .collect(Collectors.toList());

        List<MeterProgramInfo> yukonPrograms = programs.stream()
                        .filter(program -> program.getSource() == null || program.getSource().isYukon())
                        .collect(Collectors.toList());
        
        List<MeterProgramInfo> sortedPrograms = new ArrayList<>();
        sortedPrograms.addAll(yukonPrograms);
        sortedPrograms.addAll(specialCases);

        model.addAttribute("programList", sortedPrograms);
        model.addAttribute("statusList", DisplayableStatus.values());
        return "meterProgramming/summary.jsp";
    }
    
    @GetMapping("summaryFilter")
    public String summaryFilter(@ModelAttribute("filter") MeterProgrammingSummaryFilter filter, String deviceGroupName, 
                                @DefaultSort(dir=Direction.asc, sort="DEVICE_NAME") SortingParameters sorting,
                                @DefaultItemsPerPage(value=100) PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        if (!StringUtils.isBlank(deviceGroupName)) {
            DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroupName);
            filter.setGroups(Arrays.asList(group));
        }
        getFilteredResults(filter, sorting, paging, model, userContext);
        return "meterProgramming/summaryResults.jsp";
    }
    
    private void getFilteredResults(MeterProgrammingSummaryFilter filter, SortingParameters sorting, PagingParameters paging, ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (SortBy column : SortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        
        setDefaultFilterOptions(filter, userContext);
        
        model.addAttribute("filter", filter);

        SearchResults<MeterProgramSummaryDetail> detail = meterProgrammingSummaryDao.getSummary(filter, paging, sortBy, dir, userContext);
        model.addAttribute("searchResults", detail);
        
        //create device collection
        List<Integer> deviceIds = new ArrayList<>();
        detail.getResultList().forEach(summaryDetail -> deviceIds.add(summaryDetail.getDevice().getId()));
        DeviceCollection deviceCollection = dcProducer.createDeviceCollection(deviceIds, null);
        model.addAttribute("deviceCollection", deviceCollection);
    }
    
    private void setDefaultFilterOptions(MeterProgrammingSummaryFilter filter, YukonUserContext userContext) {
        if (filter.getPrograms() == null || filter.getPrograms().isEmpty()) {
            List<MeterProgramInfo> programs = meterProgrammingSummaryDao.getMeterProgramInfos(userContext);
            filter.setPrograms(programs);
        }
        if (filter.getStatuses() == null || filter.getStatuses().isEmpty()) {
            filter.setStatuses(Arrays.asList(DisplayableStatus.values()));
        }
    }
    
    @GetMapping("summaryDownload")
    public void download(@ModelAttribute("filter") MeterProgrammingSummaryFilter filter, String deviceGroupName, 
                         @DefaultSort(dir=Direction.asc, sort="DEVICE_NAME") SortingParameters sorting,
                         YukonUserContext userContext, HttpServletResponse response) throws IOException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String[] headerRow = new String[6];

        headerRow[0] = accessor.getMessage(SortBy.DEVICE_NAME);
        headerRow[1] = accessor.getMessage(SortBy.METER_NUMBER);
        headerRow[2] = accessor.getMessage(SortBy.DEVICE_TYPE);
        headerRow[3] = accessor.getMessage(SortBy.PROGRAM);
        headerRow[4] = accessor.getMessage(SortBy.STATUS);
        headerRow[5] = accessor.getMessage(SortBy.LAST_UPDATE);
        
        setDefaultFilterOptions(filter, userContext);
        
        if (!StringUtils.isBlank(deviceGroupName)) {
            DeviceGroup group = deviceGroupService.resolveGroupName(deviceGroupName);
            filter.setGroups(Arrays.asList(group));
        }

        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        SearchResults<MeterProgramSummaryDetail> summaryDetail = meterProgrammingSummaryDao.getSummary(filter, PagingParameters.EVERYTHING, sortBy, dir, userContext);

        List<String[]> dataRows = Lists.newArrayList();
        for (MeterProgramSummaryDetail detail : summaryDetail.getResultList()) {
            String[] dataRow = new String[6];
            dataRow[0] = detail.getDevice().getName();
            dataRow[1] = detail.getMeterNumber();
            dataRow[2] = detail.getDevice().getPaoIdentifier().getPaoType().getPaoTypeName();
            dataRow[3] = detail.getProgramInfo().getName();
            dataRow[4] = accessor.getMessage(detail.getStatus().getFormatKey());
            dataRow[5] =  dateFormattingService.format(detail.getLastUpdate(), DateFormatEnum.BOTH, userContext);
            dataRows.add(dataRow);
        }
        String now = dateFormattingService.format(Instant.now(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        WebFileUtils.writeToCSV(response, headerRow, dataRows, "MeterProgrammingSummary_" + now + ".csv");
    }
    
    @PostMapping("{id}/cancelProgramming")
    public @ResponseBody Map<String, Object> cancelProgramming(@PathVariable int id, String guid, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        SimpleDevice device = new SimpleDevice(pao);
        MeterProgramCommandResult result = meterProgrammingService.cancelMeterProgramUpload(device, userContext,  UUID.fromString(guid));
        if (result.isSuccess()) {
            json.put("successMsg", accessor.getMessage(baseKey + "summary.cancelSuccessful"));
        } else {
            json.put("errorMsg", result.getErrorText());
        }
        return json;
    }
    
    @PostMapping("{id}/acceptProgramming")
    public @ResponseBody Map<String, Object> acceptProgramming(@PathVariable int id, String guid, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        SimpleDevice device = new SimpleDevice(pao);
        MeterProgramCommandResult result = meterProgrammingService.acceptMeterProgrammingStatus(device, userContext, UUID.fromString(guid));
        if (result.isSuccess()) {
            json.put("successMsg", accessor.getMessage(baseKey + "summary.acceptSuccessful"));
        } else {
            json.put("errorMsg", result.getErrorText());
        }
        return json;
    }
    
    @PostMapping("{id}/readProgramming")
    public @ResponseBody Map<String, Object> readProgramming(@PathVariable int id, YukonUserContext userContext, HttpServletRequest request) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        SimpleDevice device = new SimpleDevice(pao);
        MeterProgramCommandResult result = meterProgrammingService.retrieveMeterProgrammingStatus(device, userContext);
        if (result.isSuccess()) {
            json.put("successMsg", accessor.getMessage(baseKey + "summary.readSuccessful"));
        } else {
            json.put("errorMsg", result.getErrorText());
        }
        return json;
    }
    
    @PostMapping("{id}/resendProgramming")
    public @ResponseBody Map<String, Object> resendProgramming(@PathVariable int id, String guid, YukonUserContext userContext, HttpServletRequest request) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        SimpleDevice device = new SimpleDevice(pao);
        MeterProgramCommandResult result = meterProgrammingService.reinitiateMeterProgramUpload(device, userContext, UUID.fromString(guid));
        if (result.isSuccess()) {
            json.put("successMsg", accessor.getMessage(baseKey + "summary.resendSuccessful"));
        } else {
            json.put("errorMsg", result.getErrorText());
        }
        return json;
    }
    
    @GetMapping("{id}/refreshDeviceRow")
    public String refreshDeviceRow(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        MeterProgramSummaryDetail detail = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(id, userContext);
        model.addAttribute("result", detail);
        return "meterProgramming/summaryResultRow.jsp";
    }

}