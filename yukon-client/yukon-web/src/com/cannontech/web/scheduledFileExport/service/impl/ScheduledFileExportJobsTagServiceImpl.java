package com.cannontech.web.scheduledFileExport.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.scheduledFileExport.ScheduledExportType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.jobs.model.ScheduledRepeatingJob;
import com.cannontech.web.scheduledFileExport.ScheduledFileExportJobData;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportJobsTagService;
import com.cannontech.web.scheduledFileExport.service.ScheduledFileExportService;
import com.google.common.collect.Lists;

public class ScheduledFileExportJobsTagServiceImpl implements ScheduledFileExportJobsTagService {
	@Autowired private ScheduledFileExportService scheduledFileExportService;
	
	@Override
	public void populateModel(ModelMap model, FileExportType fileType, ScheduledExportType scheduleType, int page, int itemsPerPage) {
		model.addAttribute("jobType", fileType);
		
		List<ScheduledRepeatingJob> exportJobs = scheduledFileExportService.getJobsByType(scheduleType);
		List<ScheduledFileExportJobData> jobDataObjects = Lists.newArrayListWithCapacity(exportJobs.size());
		
		int startIndex = (page -1) * itemsPerPage;
		
		for(ScheduledRepeatingJob job : exportJobs) {
			jobDataObjects.add(scheduledFileExportService.getExportJobData(job));
		}
		Collections.sort(jobDataObjects);
		int endIndex = startIndex + itemsPerPage > exportJobs.size() ? exportJobs.size() : startIndex + itemsPerPage;
		jobDataObjects = jobDataObjects.subList(startIndex, endIndex);
		
		SearchResult<ScheduledFileExportJobData> filterResult = new SearchResult<ScheduledFileExportJobData>();
	    filterResult.setBounds(startIndex, itemsPerPage, exportJobs.size());
		filterResult.setResultList(jobDataObjects);
        model.addAttribute("filterResult", filterResult);
	}
}
