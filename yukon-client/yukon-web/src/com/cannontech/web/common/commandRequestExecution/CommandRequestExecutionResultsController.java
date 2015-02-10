package com.cannontech.web.common.commandRequestExecution;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.commandRequestExecution.CommandRequestExecutionWrapperFactory.CommandRequestExecutionWrapper;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditor;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/commandRequestExecutionResults/*")
public class CommandRequestExecutionResultsController {

	@Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
	@Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
	@Autowired private DateFormattingService dateFormattingService;
	@Autowired private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	@Autowired private TemporaryDeviceGroupService temporaryDeviceGroupService;
	@Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	@Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	@Autowired private CommandRequestExecutionWrapperFactory commandRequestExecutionWrapperFactory;
	@Autowired private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	@Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

	public static class ListBackingBean {
	   
        private int creId = 0;
        private int jobId = 0;
        private String typeFilter;
        private Instant toDate = Instant.now();
        private Instant fromDate = new DateTime(toDate).minus(Months.ONE).toInstant();
        
        public int getCreId() {
            return creId;
        }

        public void setCreId(int requestId) {
            this.creId = requestId;
        }

        public int getJobId() {
            return jobId;
        }

        public void setJobId(int jobId) {
            this.jobId = jobId;
        }

        public String getTypeFilter() {
            return typeFilter;
        }

        public void setTypeFilter(String typeFilter) {
            this.typeFilter = typeFilter;
        }

        public Instant getFromDate() {
            return fromDate;
        }

        public void setFromDate(Instant fromDate) {
            this.fromDate = fromDate;
        }

        public Instant getToDate() {
            return toDate;
        }

        public void setToDate(Instant toDate) {
            this.toDate = toDate;
        }   
	}

    @RequestMapping("list")
    public String list(ModelMap model, ListBackingBean backingBean, YukonUserContext context,
                       @DefaultItemsPerPage(25) PagingParameters paging) {

        SearchResults<CommandRequestExecutionWrapper> result = getSearchResults(paging, backingBean);
        initListModel(model, context, result, backingBean);

        return "commandRequestExecution/list.jsp";
    }

    @RequestMapping("page")
    public String page(ModelMap model, int jobId, long fromDateMillies, long toDateMillies,
                       String typeFilter, YukonUserContext context, PagingParameters paging) {

        Instant to = new Instant(toDateMillies);
        Instant from = new Instant(fromDateMillies);
        ListBackingBean backingBean = new ListBackingBean();
        backingBean.setJobId(jobId);
        backingBean.setTypeFilter(typeFilter);
        backingBean.setFromDate(from);
        backingBean.setToDate(to);
        SearchResults<CommandRequestExecutionWrapper> result = getSearchResults(paging, backingBean);
        initListModel(model, context, result, backingBean);

        return "commandRequestExecution/table.jsp";
    }

    private SearchResults<CommandRequestExecutionWrapper> getSearchResults(PagingParameters paging, ListBackingBean backingBean) {

        DeviceRequestType deviceRequestType = null;
        if (!StringUtils.isEmpty(backingBean.getTypeFilter()) && !backingBean.getTypeFilter().equals("ANY")) {
            deviceRequestType = DeviceRequestType.valueOf(backingBean.getTypeFilter());
        }

        List<CommandRequestExecution> cres =
            commandRequestExecutionDao.findByRange(paging,
                                                   backingBean.getJobId(),
                                                   backingBean.getFromDate(),
                                                   backingBean.getToDate(),
                                                   deviceRequestType);
        int totalCount =
            commandRequestExecutionDao.getByRangeCount(backingBean.getJobId(),
                                                       backingBean.getFromDate(),
                                                       backingBean.getToDate(),
                                                       deviceRequestType);
        List<CommandRequestExecutionWrapper> creWrappers = Lists.newArrayListWithCapacity(cres.size());
        for (CommandRequestExecution cre : cres) {
            CommandRequestExecutionWrapper commandRequestExecutionWrapper =
                commandRequestExecutionWrapperFactory.createCommandRequestExecutionWrapper(cre);
            creWrappers.add(commandRequestExecutionWrapper);
        }

        SearchResults<CommandRequestExecutionWrapper> result =
            SearchResults.pageBasedForSublist(creWrappers, paging, totalCount);

        return result;
	}
    
    private void initListModel(ModelMap model, YukonUserContext userContext,
                               SearchResults<CommandRequestExecutionWrapper> result, ListBackingBean backingBean) {
        
        if (backingBean.getJobId() > 0) {
            ScheduledGroupRequestExecutionJobWrapper jobWrapper =
                scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(backingBean.getJobId(),
                                                                                 null,
                                                                                 null,
                                                                                 userContext);
            model.addAttribute("singleJobType", jobWrapper.getCommandRequestTypeShortName());
        }
        model.addAttribute("result", result);
        model.addAttribute("backingBean", backingBean);
        model.addAttribute("executionTypes", DeviceRequestType.values());
        
    }

	@RequestMapping("detail")
	public String detail(ModelMap model,
			int commandRequestExecutionId,
			@RequestParam(defaultValue = "0") int jobId) {

		model.addAttribute("commandRequestExecutionId", commandRequestExecutionId);
		model.addAttribute("jobId", jobId);
		
        model.addAttribute("cre", commandRequestExecutionDao.getById(commandRequestExecutionId));
        int requestCount = commandRequestExecutionDao.getRequestCountByCreId(commandRequestExecutionId);
        int successCount = commandRequestExecutionResultDao.getSucessCountByExecutionId(commandRequestExecutionId);
        int failCount = commandRequestExecutionResultDao.getFailCountByExecutionId(commandRequestExecutionId);
        int unsupported =
            commandRequestExecutionResultDao
                .getUnsupportedCountByExecutionId(commandRequestExecutionId, CommandRequestUnsupportedType.UNSUPPORTED);
        int notConfigured =
            commandRequestExecutionResultDao
                .getUnsupportedCountByExecutionId(commandRequestExecutionId,
                                                  CommandRequestUnsupportedType.NOT_CONFIGURED);
        int canceled =
            commandRequestExecutionResultDao.getUnsupportedCountByExecutionId(commandRequestExecutionId,
                                                                              CommandRequestUnsupportedType.CANCELED);
        boolean isComplete = commandRequestExecutionDao.isComplete(commandRequestExecutionId);
        model.addAttribute("requestCount", requestCount);
        model.addAttribute("successCount", successCount);
        model.addAttribute("failCount", failCount);
        model.addAttribute("unsupportedCount", unsupported);
        model.addAttribute("notConfigured", notConfigured);
        model.addAttribute("canceled", canceled);
        model.addAttribute("isComplete", isComplete);

        model.addAttribute("resultsFilterTypes", CommandRequestExecutionResultsFilterType.values());

        return "commandRequestExecution/detail.jsp";
	}
	
	@RequestMapping("detailsReport")
	public String detailsReport(ModelMap model, 
			@RequestParam int commandRequestExecutionId,
			@RequestParam String resultsFilterType) {

		model.addAttribute("commandRequestExecutionId", commandRequestExecutionId);
		model.addAttribute("resultsFilterType", resultsFilterType);

        return "commandRequestExecution/detailsReport.jsp";
	}
	
	@RequestMapping("failureStatsReport")
	public String failureStatsReport(ModelMap model,
			@RequestParam int commandRequestExecutionId) {

		model.addAttribute("commandRequestExecutionId", commandRequestExecutionId);

		return "commandRequestExecution/failureStatsReport.jsp";
	}
	
	@RequestMapping("processDevices")
	public String processDevices(ModelMap model,
			@RequestParam("commandRequestExecutionId") int commandRequestExecutionId,
			CommandRequestExecutionUpdaterTypeEnum commandRequestExecutionUpdaterType) {

        List<PaoIdentifier> paoIdentifiers;

        switch (commandRequestExecutionUpdaterType) {
        case REQUEST_COUNT:
            paoIdentifiers = commandRequestExecutionResultDao.getDeviceIdsByExecutionId(commandRequestExecutionId);
            break;
        case SUCCESS_RESULTS_COUNT:
            paoIdentifiers =
                commandRequestExecutionResultDao.getSucessDeviceIdsByExecutionId(commandRequestExecutionId);
            break;
        case FAILURE_RESULTS_COUNT:
            paoIdentifiers = commandRequestExecutionResultDao.getFailDeviceIdsByExecutionId(commandRequestExecutionId);
            break;
        case UNSUPPORTED_COUNT:
            paoIdentifiers =
                commandRequestExecutionResultDao
                    .getUnsupportedDeviceIdsByExecutionId(commandRequestExecutionId,
                                                          CommandRequestUnsupportedType.UNSUPPORTED);
            break;
        case NOT_CONFIGURED_COUNT:
            paoIdentifiers =
                commandRequestExecutionResultDao
                    .getUnsupportedDeviceIdsByExecutionId(commandRequestExecutionId,
                                                          CommandRequestUnsupportedType.NOT_CONFIGURED);
            break;
        case CANCELED_COUNT:
            paoIdentifiers =
                commandRequestExecutionResultDao
                    .getUnsupportedDeviceIdsByExecutionId(commandRequestExecutionId,
                                                          CommandRequestUnsupportedType.CANCELED);
            break;
        default:
            throw new IllegalArgumentException("Invalid commandRequestExecutionUpdaterType: "
                                               + commandRequestExecutionUpdaterType);
        }

		StoredDeviceGroup tempGroup = temporaryDeviceGroupService.createTempGroup();
		ImmutableList<YukonDevice> deviceList = PaoUtils.asDeviceList(paoIdentifiers);
		deviceGroupMemberEditorDao.addDevices(tempGroup, deviceList);

		DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
		model.addAllAttributes(deviceCollection.getCollectionParameters());

        return "redirect:/bulk/collectionActions";
	}

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, final YukonUserContext context) {
        EnumPropertyEditor.register(webDataBinder, CommandRequestExecutionUpdaterTypeEnum.class);
        webDataBinder.registerCustomEditor(Instant.class, "fromDate",
                   datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATE,
                                          context, BlankMode.NULL, DateOnlyMode.START_OF_DAY));
        webDataBinder.registerCustomEditor(Instant.class, "toDate",
                   datePropertyEditorFactory.getInstantPropertyEditor(DateFormatEnum.DATE,
                                      context, BlankMode.CURRENT, DateOnlyMode.END_OF_DAY));
    }
}
