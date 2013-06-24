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
	private String exportFileName;
	private String notificationEmailAddresses; //optional

    private boolean appendDateToFileName;
    private String timestampPatternField;
    private boolean overrideFileExtension;
    private String exportFileExtension;
    private boolean includeExportCopy;
    private String exportPath;

    private static final String DEFAULT_TIMESTAMP_PATTERN  = "yyyyMMddHHmmss";

    public ScheduledFileExportData() {
        this.setTimestampPatternField(DEFAULT_TIMESTAMP_PATTERN);
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
        return (null == exportFileName) ? scheduleName : exportFileName;
    }

    public String getTimestampPatternField() {
        return timestampPatternField;
    }

    public void setTimestampPatternField(String timestampPattern) {
        this.timestampPatternField = timestampPattern;
    }

    public String getExportFileExtension() {
        return exportFileExtension;
    }

    public void setExportFileExtension(String exportFileExtension) {
        this.exportFileExtension = exportFileExtension;
    }

    public boolean isAppendDateToFileName() {
        return appendDateToFileName;
    }

    public void setAppendDateToFileName(boolean appendDateToFileName) {
        this.appendDateToFileName = appendDateToFileName;
    }

    public boolean isIncludeExportCopy() {
        return includeExportCopy;
    }

    public void setIncludeExportCopy(boolean includeExportCopy) {
        this.includeExportCopy = includeExportCopy;
    }

    public boolean isOverrideFileExtension() {
        return overrideFileExtension;
    }

    public void setOverrideFileExtension(boolean overrideFileExtension) {
        this.overrideFileExtension = overrideFileExtension;
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

    public void setNotificationEmailAddresses(String notificationEmailAddresses) {
		String input = StringUtils.trimAllWhitespace(notificationEmailAddresses);
		if(org.apache.commons.lang.StringUtils.isNotBlank(input)) {
			this.notificationEmailAddresses = input;
		} else {
			this.notificationEmailAddresses = null;
		}
	}
}
