package com.cannontech.common.scheduledFileExport;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

/**
 * Base data object containing parameters required to schedule file exports.
 * Data specific to individual file export types is contained in the
 * ExportFileGenerationParameters.
 */
public class ScheduledFileExportData {
	private String scheduleName;
	private String scheduleCronString;
	private ExportFileGenerationParameters parameters;
	private String exportPath;
	private String exportFileName;
	private boolean appendDateToFileName;
	private String notificationEmailAddresses; //optional
	
	public ScheduledFileExportData() {
	}
	
	public ScheduledFileExportData(String scheduleName, String scheduleCronString, ExportFileGenerationParameters parameters,
			String exportPath, String exportFileName, boolean appendDateToFileName, String notificationEmailAddresses) {
		this.scheduleName = scheduleName;
		this.scheduleCronString = scheduleCronString;
		this.parameters = parameters;
		this.exportPath = exportPath;
		this.exportFileName = exportFileName;
		this.appendDateToFileName = appendDateToFileName;
		this.notificationEmailAddresses = notificationEmailAddresses;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public String getScheduleCronString() {
		return scheduleCronString;
	}

	public ExportFileGenerationParameters getParameters() {
		return parameters;
	}

	public String getExportPath() {
		return exportPath;
	}

	public String getExportFileName() {
		return exportFileName;
	}

	public boolean isAppendDateToFileName() {
		return appendDateToFileName;
	}

	public String getNotificationEmailAddresses() {
		return notificationEmailAddresses;
	}
	
	public List<String> getNotificationEmailAddressesAsList() {
		if(notificationEmailAddresses != null) {
			String[] emailArray = notificationEmailAddresses.split(",");
			return Arrays.asList(emailArray);
		}
		return Lists.newArrayList();
	}
	
	public ScheduledExportType getExportType() {
		return parameters.getExportType();
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public void setScheduleCronString(String scheduleCronString) {
		this.scheduleCronString = scheduleCronString;
	}

	public void setParameters(ExportFileGenerationParameters parameters) {
		this.parameters = parameters;
	}

	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	public void setAppendDateToFileName(boolean appendDateToFileName) {
		this.appendDateToFileName = appendDateToFileName;
	}
	
	public void setNotificationEmailAddresses(String notificationEmailAddresses) {
		String input = StringUtils.trimAllWhitespace(notificationEmailAddresses);
		if(org.apache.commons.lang.StringUtils.isNotBlank(input)) {
			this.notificationEmailAddresses = notificationEmailAddresses;
		} else {
			notificationEmailAddresses = null;
		}
	}
}
