package com.cannontech.web.scheduledFileExport.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.dao.ArchiveValuesExportFormatDao;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.dataRange.ChangeIdRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.LocalDateRange;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.DeviceCollectionService;
import com.cannontech.common.exception.FileCreationException;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.scheduledFileExport.ArchivedDataExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.dao.ScheduledFileExportDao;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.core.dao.RawPointHistoryDao;

public class ScheduledArchivedDataFileExportTask extends ScheduledFileExportTask implements PersistedFormatTask {
	@Autowired private ExportReportGeneratorService exportReportGeneratorService;
	@Autowired private MeterDao meterDao;
	@Autowired private ArchiveValuesExportFormatDao archiveValuesExportFormatDao;
	@Autowired private RawPointHistoryDao rawPointHistoryDao;
	@Autowired private ScheduledFileExportDao scheduledFileExportDao;
	@Autowired private DeviceCollectionService deviceCollectionService;
	
    private static final Logger log = YukonLogManager.getLogger(ScheduledArchivedDataFileExportTask.class);
	
	private int collectionId;
	private int formatId;
	private Set<Attribute> attributes;
	private DataRange dataRange = new DataRange();
	private boolean onInterval;
	private TimeIntervals interval;
	
	@Override
	public void start() {
		DeviceCollection deviceCollection = deviceCollectionService.loadCollection(collectionId);
	    List<YukonMeter> meters = meterDao.getMetersForYukonPaos(deviceCollection.getDeviceList());
		ExportFormat format = archiveValuesExportFormatDao.getByFormatId(formatId);
		populateDataRange();
		
		log.debug("Generating scheduled data file export");
		
		//Get the report data
		Attribute[] attributesArray = attributes.toArray(new Attribute[attributes.size()]);
		
		//If using SINCE_LAST_CHANGE_ID, update last id for this job
		if(dataRange.getDataRangeType() == DataRangeType.SINCE_LAST_CHANGE_ID) {
            scheduledFileExportDao.setRphIdForJob(getJob().getId(), getJob().getJobGroupId(),
                dataRange.getChangeIdRange().getLastChangeId());
		}
		
		// Create and Write to the archive file
        File archiveFile = createArchiveFile(DateTime.now(), ".csv");
		try (
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archiveFile)));
		){
		    exportReportGeneratorService.generateReport(meters, format, dataRange, getUserContext(), attributesArray, 
		                                                writer, onInterval, interval);
		} catch(IOException e) {
			throw new FileCreationException("Unable to generate Scheduled Archived Data file due to I/O errors.", e);
		}
		
		// Copy the archive file to the export file (if necessary).
		File exportFile = copyExportFile(archiveFile);
		
		// Create a new History entry record
	      ExportHistoryEntry historyEntry = createExportHistoryEntry(FileExportType.ARCHIVED_DATA_EXPORT, 
              archiveFile, exportFile, getJob().getJobGroupId());

		//Send notification emails
        prepareAndSendNotificationEmails(historyEntry);
	}

	@Override
	public void setFileGenerationParameters(ExportFileGenerationParameters parameters) {
		ArchivedDataExportFileGenerationParameters adeParameters = (ArchivedDataExportFileGenerationParameters) parameters;
		formatId = adeParameters.getFormatId();
		attributes = adeParameters.getAttributes();
		dataRange = adeParameters.getDataRange();
		onInterval = adeParameters.isOnInterval();
		interval = adeParameters.getInterval();
		
		DeviceCollection deviceCollection = adeParameters.getDeviceCollection();
		collectionId = deviceCollectionService.saveCollection(deviceCollection);
	}
	
	private void populateDataRange() {
		if(dataRange.getDataRangeType() == DataRangeType.END_DATE) {
			dataRange.setEndDate(LocalDate.now());
		} else if(dataRange.getDataRangeType() == DataRangeType.SINCE_LAST_CHANGE_ID) {
            long firstChangeId = scheduledFileExportDao.getLastRphIdByJobId(getJob().getJobGroupId());
			long lastChangeId = rawPointHistoryDao.getMaxChangeId();
			ChangeIdRange changeIdRange = new ChangeIdRange();
			changeIdRange.setFirstChangeId(firstChangeId);
			changeIdRange.setLastChangeId(lastChangeId);
			dataRange.setChangeIdRange(changeIdRange);
		}
	}
	
	public int getDeviceCollectionId() {
	    return collectionId;
	}
	
	public void setDeviceCollectionId(int collectionId) {
	    this.collectionId = collectionId;
	}
	
	@Override
	public int getFormatId() {
		return formatId;
	}

	public void setFormatId(int formatId) {
		this.formatId = formatId;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public String getDataRangeType() {
		return dataRange.getDataRangeType().name();
	}
	
	public void setDataRangeType(String dataRangeType) {
		dataRange.setDataRangeType(DataRangeType.valueOf(dataRangeType));
	}
	
	public int getDataRangeDaysPrevious() {
		return dataRange.getDaysPrevious();
	}
	
	public void setDataRangeDaysPrevious(int daysPrevious) {
		dataRange.setDaysPrevious(daysPrevious);
	}
	
	public long getDataRangeSinceLastChangeIdFirst() {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		return dataRange.getChangeIdRange().getFirstChangeId();
	}
	
	public void setDataRangeSinceLastChangeIdFirst(long firstChangeId) {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		dataRange.getChangeIdRange().setFirstChangeId(firstChangeId);
	}
	
	public long getDataRangeSinceLastChangeIdLast() {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		return dataRange.getChangeIdRange().getLastChangeId();
	}
	
	public void setDataRangeSinceLastChangeIdLast(long lastChangeId) {
		if(dataRange.getChangeIdRange() == null) {
			dataRange.setChangeIdRange(new ChangeIdRange());
		}
		dataRange.getChangeIdRange().setLastChangeId(lastChangeId);
	}
	
	public Date getDataRangeDateRangeStart() {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		return dataRange.getLocalDateRange().getStartDate().toDate();
	}
	
	public void setDataRangeDateRangeStart(Date startDate) {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		dataRange.getLocalDateRange().setStartDate(new LocalDate(startDate));
	}
	
	public Date getDataRangeDateRangeEnd() {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		return dataRange.getLocalDateRange().getEndDate().toDate();
	}
	
	public void setDataRangeDateRangeEnd(Date endDate) {
		if(dataRange.getLocalDateRange() == null) {
			dataRange.setLocalDateRange(new LocalDateRange());
		}
		dataRange.getLocalDateRange().setEndDate(new LocalDate(endDate));
	}
	
	public DataRange getDataRange() {
		return dataRange;
	}
	
    public LocalTime getDataRangeTime() {
        return dataRange.getTime();
    }

    public void setDataRangeTime(LocalTime time) {
        dataRange.setTime(time);
    }
    
    public int getDataRangeDaysOffset() {
        return dataRange.getDaysOffset();
    }

    public void setDataRangeDaysOffset(int daysOffset) {
        dataRange.setDaysOffset(daysOffset);
    }
    
    public boolean getDataRangeIsTimeSelected() {
        return dataRange.isTimeSelected();
    }

    public void setDataRangeIsTimeSelected(boolean timeSelected) {
        dataRange.setTimeSelected(timeSelected);
    }

    public boolean isOnInterval() {
        return onInterval;
    }

    public void setOnInterval(boolean onInterval) {
        this.onInterval = onInterval;
    }

    public TimeIntervals getInterval() {
        return interval;
    }

    public void setInterval(TimeIntervals interval) {
        this.interval = interval;
    }
    
}
