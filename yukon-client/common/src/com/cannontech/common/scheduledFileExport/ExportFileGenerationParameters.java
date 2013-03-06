package com.cannontech.common.scheduledFileExport;

/**
 * Implementors of this interface specify parameters unique to their type,
 * while shared file export parameters are specified in ScheduledFileExportData.
 */
public interface ExportFileGenerationParameters {
	public ScheduledExportType getExportType();
}
