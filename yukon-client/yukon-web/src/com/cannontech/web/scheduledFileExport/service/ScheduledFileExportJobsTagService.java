package com.cannontech.web.scheduledFileExport.service;

import org.springframework.ui.ModelMap;

import com.cannontech.common.scheduledFileExport.ScheduledExportType;

public interface ScheduledFileExportJobsTagService {
	
	/**
	 * Populates the model with a SearchResult containing jobs of the specified type, based on
	 * the page and itemsPerPage.
	 */
	public void populateModel(ModelMap model, ScheduledExportType type, int page, int itemsPerPage);
}
