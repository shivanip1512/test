package com.cannontech.web.common.commandRequestExecution;

import java.util.List;

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
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultsFilterType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
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
	    private int requestId = 0;
        private int jobId = 0;
        private String typeFilter;
        private Instant fromDate;
        private Instant toDate = Instant.now();
	    public int getCommandRequestExecutionId() {return requestId;}
        public void setCommandRequestExecutionId(int requestId) {this.requestId = requestId;}
        public int getJobId() {return jobId;}
        public void setJobId(int jobId) {this.jobId = jobId;}
        public String getTypeFilter() {return typeFilter;}
        public void setTypeFilter(String typeFilter) {this.typeFilter = typeFilter;}
        public Instant getFromDate() {return fromDate;}
        public void setFromDate(Instant fromDate) {this.fromDate = fromDate;}
        public Instant getToDate() {return toDate;}
        public void setToDate(Instant toDate) {this.toDate = toDate;}
	}

	@RequestMapping("list")
	public String list(ModelMap model, ListBackingBean listBackingBean, YukonUserContext userContext) {

        Instant fromDate = listBackingBean.getFromDate();
        Instant toDate = listBackingBean.getToDate();
	    String typeFilter = listBackingBean.getTypeFilter();
	    int commandRequestExecutionId = listBackingBean.getCommandRequestExecutionId();
	    int jobId = listBackingBean.getJobId();

		if (fromDate == null) {
			fromDate = new DateTime(toDate).minus(Months.ONE).toInstant();
		}

		DeviceRequestType deviceRequestType = null;

		if (typeFilter != null && !typeFilter.equals("ANY")) {
			deviceRequestType = DeviceRequestType.valueOf(typeFilter);
		}

		model.addAttribute("commandRequestExecutionId", commandRequestExecutionId);
		model.addAttribute("jobId", jobId);
		model.addAttribute("fromDate", fromDate);
		model.addAttribute("toDate", toDate);
		model.addAttribute("typeFilter", deviceRequestType);
		model.addAttribute("commandRequestExecutionTypes", DeviceRequestType.values());

		if (jobId > 0) {
			ScheduledGroupRequestExecutionJobWrapper jobWrapper = scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(jobId, null, null, userContext);
			model.addAttribute("singleJobType", jobWrapper.getCommandRequestTypeShortName());
		}

		List<CommandRequestExecution> cres;
		if (jobId > 0) {
			cres = scheduledGroupRequestExecutionDao.getCommandRequestExecutionsByJobId(jobId, fromDate.toDate(), toDate.toDate(), false);
		} else {
			cres = commandRequestExecutionDao.findByRange(commandRequestExecutionId, fromDate.toDate(), toDate.toDate(), deviceRequestType, false);
		}

		List<CommandRequestExecutionWrapper> creWrappers = Lists.newArrayListWithCapacity(cres.size());
		for (CommandRequestExecution cre : cres) {
			CommandRequestExecutionWrapper commandRequestExecutionWrapper = commandRequestExecutionWrapperFactory.createCommandRequestExecutionWrapper(cre);
			creWrappers.add(commandRequestExecutionWrapper);
		}

		model.addAttribute("creWrappers", creWrappers);

		return "commandRequestExecution/list.jsp";
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
        int unsupported = commandRequestExecutionResultDao.getUnsupportedCountByExecutionId(commandRequestExecutionId);
        boolean isComplete = commandRequestExecutionDao.isComplete(commandRequestExecutionId);
        model.addAttribute("requestCount", requestCount);
        model.addAttribute("successCount", successCount);
        model.addAttribute("failCount", failCount);
        model.addAttribute("unsupportedCount", unsupported);
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

		switch(commandRequestExecutionUpdaterType) {
			case REQUEST_COUNT:
				paoIdentifiers = commandRequestExecutionResultDao.getDeviceIdsByExecutionId(commandRequestExecutionId);
				break;
			case SUCCESS_RESULTS_COUNT:
				paoIdentifiers = commandRequestExecutionResultDao.getSucessDeviceIdsByExecutionId(commandRequestExecutionId);
				break;
			case FAILURE_RESULTS_COUNT:
				paoIdentifiers = commandRequestExecutionResultDao.getFailDeviceIdsByExecutionId(commandRequestExecutionId);
				break;
            case UNSUPPORTED_COUNT:
                paoIdentifiers = commandRequestExecutionResultDao.getUnsupportedDeviceIdsByExecutionId(commandRequestExecutionId);
                break;
			default:
				throw new IllegalArgumentException("Invalid commandRequestExecutionUpdaterType: " + commandRequestExecutionUpdaterType);
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
