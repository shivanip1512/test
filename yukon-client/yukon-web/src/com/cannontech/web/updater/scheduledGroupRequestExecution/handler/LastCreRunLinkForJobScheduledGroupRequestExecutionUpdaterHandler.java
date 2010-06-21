package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import java.util.Date;
import java.util.Map;

import org.apache.ecs.html.A;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;
import com.google.common.collect.Maps;

public class LastCreRunLinkForJobScheduledGroupRequestExecutionUpdaterHandler implements ScheduledGroupRequestExecutionUpdaterHandler {

	private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
	private JobStatusDao jobStatusDao;
	private DateFormattingService dateFormattingService;
	private YukonUserContextMessageSourceResolver messageSourceResolver;
	
	@Override
	public String handle(int jobId, YukonUserContext userContext) {
		
		CommandRequestExecution lastCre = scheduledGroupRequestExecutionDao.findLatestCommandRequestExecutionForJobId(jobId, null);
		
		// link to latest cre
		if (lastCre != null) {
		
			Date lastRunDate = jobStatusDao.getJobLastSuccessfulRunDate(jobId);
			String dateStr = dateFormattingService.format(lastRunDate, DateFormatEnum.DATEHM, userContext);
			
			Map<String, String> urlParametersMap = Maps.newHashMap();
			urlParametersMap.put("commandRequestExecutionId", String.valueOf(lastCre.getId()));
			String queryString = ServletUtil.buildSafeQueryStringFromMap(urlParametersMap, true);
			String url = "/spring/common/commandRequestExecutionResults/detail?" + queryString;
			
			A href = new A();
			href.addElement(dateStr);
			href.setHref(url);
			
			return href.toString();
		
		// no last cre - "N/A"
		} else {
			
			MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
			return messageSourceAccessor.getMessage("yukon.web.defaults.na");
		}
	}
	
	@Override
	public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
		return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_CRE_RUN_LINK;
	}

	@Autowired
	public void setScheduledGroupRequestExecutionDao(
			ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
		this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
	}
	
	@Autowired
	public void setJobStatusDao(JobStatusDao jobStatusDao) {
		this.jobStatusDao = jobStatusDao;
	}
	
	@Autowired
	public void setDateFormattingService(DateFormattingService dateFormattingService) {
		this.dateFormattingService = dateFormattingService;
	}
	
	@Autowired
	public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
		this.messageSourceResolver = messageSourceResolver;
	}
}
