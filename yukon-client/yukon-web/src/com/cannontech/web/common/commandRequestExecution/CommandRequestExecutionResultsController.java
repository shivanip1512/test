package com.cannontech.web.common.commandRequestExecution;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

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
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.commandRequestExecution.CommandRequestExecutionWrapperFactory.CommandRequestExecutionWrapper;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory;
import com.cannontech.web.common.scheduledGroupRequestExecution.ScheduledGroupRequestExecutionJobWrapperFactory.ScheduledGroupRequestExecutionJobWrapper;
import com.cannontech.web.updater.commandRequestExecution.CommandRequestExecutionUpdaterTypeEnum;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CommandRequestExecutionResultsController extends MultiActionController {
	
	private CommandRequestExecutionDao commandRequestExecutionDao;
	private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
	private DateFormattingService dateFormattingService;
	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private TemporaryDeviceGroupService temporaryDeviceGroupService;
	private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
	private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
	private CommandRequestExecutionWrapperFactory commandRequestExecutionWrapperFactory;
	private ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory;
	
	// LIST
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("commandRequestExecution/list.jsp");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
		String error = null;
		
		int commandRequestExecutionId = ServletRequestUtils.getIntParameter(request, "commandRequestExecutionId", 0);
		int jobId = ServletRequestUtils.getIntParameter(request, "jobId", 0);
		
		// FILTER DEFAULTS
		Date toDate = new Date();
		Date fromDate = DateUtils.addMonths(toDate, -1);
		DeviceRequestType typeFilter = null;
		
		// FILTERS
		String fromDateStr = ServletRequestUtils.getStringParameter(request, "fromDate", null);
		if (fromDateStr != null) {
			try {
				fromDate = dateFormattingService.flexibleDateParser(fromDateStr, DateOnlyMode.START_OF_DAY, userContext);
			} catch (ParseException e) {
				error = "Invalid From Date: " + fromDateStr;
			}
		}
		
		String toDateStr = ServletRequestUtils.getStringParameter(request, "toDate", null);
		if (toDateStr != null) {
			try {
				toDate = dateFormattingService.flexibleDateParser(toDateStr, DateOnlyMode.END_OF_DAY, userContext);
			} catch (ParseException e) {
				error = "Invalid To Date: " + toDateStr;
			}
		}
		
		String typeFilterStr = ServletRequestUtils.getStringParameter(request, "typeFilter", null);
		if (typeFilterStr != null && !typeFilterStr.equals("ANY")) {
			typeFilter = DeviceRequestType.valueOf(typeFilterStr);
		}
		
		// PARAMS
		mav.addObject("commandRequestExecutionId", commandRequestExecutionId);
		mav.addObject("jobId", jobId);
		mav.addObject("fromDate", fromDate);
		mav.addObject("toDate", toDate);
		mav.addObject("typeFilter", typeFilter);
		mav.addObject("error", error);
		
		// TYPES
		DeviceRequestType[] commandRequestExecutionTypes = DeviceRequestType.values();
		mav.addObject("commandRequestExecutionTypes", commandRequestExecutionTypes);
		if (jobId > 0) {
			ScheduledGroupRequestExecutionJobWrapper jobWrapper = scheduledGroupRequestExecutionJobWrapperFactory.createJobWrapper(jobId, null, null, userContext);
			String singleJobType = jobWrapper.getCommandRequestTypeShortName();
			mav.addObject("singleJobType", singleJobType);
		}
		
		// CRES
		List<CommandRequestExecution> cres;
		if (jobId > 0) {
			cres = scheduledGroupRequestExecutionDao.getCommandRequestExecutionsByJobId(jobId, fromDate, toDate, false);
		} else {
			cres = commandRequestExecutionDao.findByRange(commandRequestExecutionId, fromDate, toDate, typeFilter, false);
		}
		
		List<CommandRequestExecutionWrapper> creWrappers = Lists.newArrayListWithCapacity(cres.size());
		for (CommandRequestExecution cre : cres) {
			CommandRequestExecutionWrapper commandRequestExecutionWrapper = commandRequestExecutionWrapperFactory.createCommandRequestExecutionWrapper(cre);
			creWrappers.add(commandRequestExecutionWrapper);
		}
		
		mav.addObject("creWrappers", creWrappers);
		
		return mav;
	}
	
	// DETAIL
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("commandRequestExecution/detail.jsp");
		
		int commandRequestExecutionId = ServletRequestUtils.getRequiredIntParameter(request, "commandRequestExecutionId");
		int jobId = ServletRequestUtils.getIntParameter(request, "jobId", 0);
        mav.addObject("commandRequestExecutionId", commandRequestExecutionId);
        mav.addObject("jobId", jobId);
        
        CommandRequestExecution cre = commandRequestExecutionDao.getById(commandRequestExecutionId);
        mav.addObject("cre", cre);
        
        int requestCount = commandRequestExecutionDao.getRequestCountByCreId(commandRequestExecutionId);
        int successCount = commandRequestExecutionResultDao.getSucessCountByExecutionId(commandRequestExecutionId);
        int failCount = commandRequestExecutionResultDao.getFailCountByExecutionId(commandRequestExecutionId);
        boolean isComplete = commandRequestExecutionDao.isComplete(commandRequestExecutionId);
        mav.addObject("requestCount", requestCount);
        mav.addObject("successCount", successCount);
        mav.addObject("failCount", failCount);
        mav.addObject("isComplete", isComplete);
        
        
        mav.addObject("resultsFilterTypes", CommandRequestExecutionResultsFilterType.values());
        
        return mav;
	}
	
	// DETAILS REPORT
	public ModelAndView detailsReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("commandRequestExecution/detailsReport.jsp");
		
		int commandRequestExecutionId = ServletRequestUtils.getRequiredIntParameter(request, "commandRequestExecutionId");
		String resultsFilterType = ServletRequestUtils.getRequiredStringParameter(request, "resultsFilterType");
		
        mav.addObject("commandRequestExecutionId", commandRequestExecutionId);
        mav.addObject("resultsFilterType", resultsFilterType);
        
        return mav;
	}
	
	// FAILURE STATS REPORT
	public ModelAndView failureStatsReport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("commandRequestExecution/failureStatsReport.jsp");
		
		int commandRequestExecutionId = ServletRequestUtils.getRequiredIntParameter(request, "commandRequestExecutionId");
        mav.addObject("commandRequestExecutionId", commandRequestExecutionId);
        
        return mav;
	}
	
	// PROCESS DEVICES
	public ModelAndView processDevices(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		ModelAndView mav = new ModelAndView("redirect:/bulk/collectionActions");
		
		int commandRequestExecutionId = ServletRequestUtils.getRequiredIntParameter(request, "commandRequestExecutionId");
		String commandRequestExecutionUpdaterTypeStr = ServletRequestUtils.getRequiredStringParameter(request, "commandRequestExecutionUpdaterType");
		CommandRequestExecutionUpdaterTypeEnum commandRequestExecutionType = CommandRequestExecutionUpdaterTypeEnum.valueOf(commandRequestExecutionUpdaterTypeStr);
		
		List<PaoIdentifier> paoIdentifiers;
		
		if (commandRequestExecutionType.equals(CommandRequestExecutionUpdaterTypeEnum.REQUEST_COUNT)) {
			paoIdentifiers = commandRequestExecutionResultDao.getDeviceIdsByExecutionId(commandRequestExecutionId);
		} else if (commandRequestExecutionType.equals(CommandRequestExecutionUpdaterTypeEnum.SUCCESS_RESULTS_COUNT)) {
			paoIdentifiers = commandRequestExecutionResultDao.getSucessDeviceIdsByExecutionId(commandRequestExecutionId);
		} else if (commandRequestExecutionType.equals(CommandRequestExecutionUpdaterTypeEnum.FAILURE_RESULTS_COUNT)) {
			paoIdentifiers = commandRequestExecutionResultDao.getFailDeviceIdsByExecutionId(commandRequestExecutionId);
		} else {
			throw new IllegalArgumentException("Invalid commandRequestExecutionUpdaterType parameter: " + commandRequestExecutionUpdaterTypeStr);
		}
		
		StoredDeviceGroup tempGroup = temporaryDeviceGroupService.createTempGroup(null);
		ImmutableList<YukonDevice> deviceList = PaoUtils.asDeviceList(paoIdentifiers);
		deviceGroupMemberEditorDao.addDevices(tempGroup, deviceList);
		
		DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(tempGroup);
		mav.addAllObjects(deviceCollection.getCollectionParameters());
        
        return mav;
	}
	
	
	@Autowired
	public void setCommandRequestExecutionDao(CommandRequestExecutionDao commandRequestExecutionDao) {
		this.commandRequestExecutionDao = commandRequestExecutionDao;
	}
	
	@Autowired
	public void setCommandRequestExecutionResultDao(
			CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setTemporaryDeviceGroupService(TemporaryDeviceGroupService temporaryDeviceGroupService) {
		this.temporaryDeviceGroupService = temporaryDeviceGroupService;
	}
	
	@Autowired
	public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
		this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
	}
	
	@Autowired
	public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
	
	@Autowired
	public void setCommandRequestExecutionWrapperFactory(CommandRequestExecutionWrapperFactory commandRequestExecutionWrapperFactory) {
		this.commandRequestExecutionWrapperFactory = commandRequestExecutionWrapperFactory;
	}
	
	@Autowired
	public void setScheduledGroupRequestExecutionJobWrapperFactory(ScheduledGroupRequestExecutionJobWrapperFactory scheduledGroupRequestExecutionJobWrapperFactory) {
		this.scheduledGroupRequestExecutionJobWrapperFactory = scheduledGroupRequestExecutionJobWrapperFactory;
	}
}
