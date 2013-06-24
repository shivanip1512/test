package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigStringKeysEnum;
import com.cannontech.common.exception.FileCreationException;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.fileExportHistory.service.FileExportHistoryService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

/**
 * Abstract parent class of scheduled file exports. This class specifies
 * functionality dealing with file export and the email notifications to be sent
 * after task completion. The setFileGenerationParameters method
 * must be overridden by child classes to accept the parameters required to
 * generate a particular type of file.
 */
public abstract class ScheduledFileExportTask extends YukonTaskBase {
	@Autowired protected FileExportHistoryService fileExportHistoryService;
	@Autowired protected EmailService emailService;
	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	@Autowired private DateFormattingService dateFormattingService;
	@Autowired private ConfigurationSource configurationSource;
	
	protected String defaultYukonExternalUrl;
	protected String name;
	protected boolean appendDateToFileName;
	protected String exportFileName;
	protected String exportPath;
	protected String notificationEmailAddresses;
    protected String timestampPatternField;
    protected boolean overrideFileExtension;
    protected String exportFileExtension;
    protected boolean includeExportCopy;
	
	private Logger log = YukonLogManager.getLogger(ScheduledFileExportTask.class);
	
	private static final String HISTORY_URL_PART = "/support/fileExportHistory/list?entryId=";
	
	@Override
	public abstract void start();
	
	/**
	 * This method should be overridden by each subclass such that it accepts a subclass of
	 * ExportFileGenerationParameters specific to that task.
	 */
	public abstract void setFileGenerationParameters(ExportFileGenerationParameters parameters);
	
	public ScheduledFileExportData getPartialData() {
		ScheduledFileExportData data = new ScheduledFileExportData();
		data.setAppendDateToFileName(appendDateToFileName);
		data.setExportFileName(exportFileName);
		data.setExportPath(exportPath);
		data.setNotificationEmailAddresses(notificationEmailAddresses);
		data.setScheduleName(name);
		data.setTimestampPatternField(timestampPatternField);
		data.setOverrideFileExtension(overrideFileExtension);
		data.setExportFileExtension(exportFileExtension);
		data.setIncludeExportCopy(includeExportCopy);
		return data;
	}
	
	public String getDefaultYukonExternalUrl() {
		return defaultYukonExternalUrl;
	}

	public void setDefaultYukonExternalUrl(String url) {
		this.defaultYukonExternalUrl = url;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isAppendDateToFileName() {
		return appendDateToFileName;
	}
	
	public void setAppendDateToFileName(boolean appendDateToFileName) {
		this.appendDateToFileName = appendDateToFileName;
	}
	
	public String getExportFileName() {
        return (null == exportFileName) ? name : exportFileName;
	}
	
	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}
	
	public String getExportPath() {
		return exportPath;
	}
	
	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}
	
	public String getNotificationEmailAddresses() {
		return notificationEmailAddresses;
	}
	
	public void setNotificationEmailAddresses(String notificationEmailAddresses) {
		this.notificationEmailAddresses = notificationEmailAddresses;
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

	/**
	 * Convenience method for exporting to csv.
	 */
    protected File archiveToCsvFile(List<String[]> dataRows) {
        File archiveFile = createArchiveFile(DateTime.now(), ".csv");
        try (FileWriter writer = new FileWriter(archiveFile);) {
            CSVWriter csvWriter = new CSVWriter(writer);
            csvWriter.writeAll(dataRows);
        } catch (IOException e) {
            throw new FileCreationException("Unable to generate scheduled export file due to I/O errors: ", e);
        }
        return archiveFile;
    }
    
    
    
	/**
	 * Checks the validity of the history entry and notificationEmailAddress, then sends email
	 * notifications.
	 */
	protected void prepareAndSendNotificationEmails(ExportHistoryEntry historyEntry) {
	    if(historyEntry == null) {
            log.error("Attempted to send notification for scheduled file export, but export information was not properly archived.");
        } else {
            //send notifications
            if(org.apache.commons.lang.StringUtils.isNotEmpty(notificationEmailAddresses)) {
                sendNotificationEmails(historyEntry);
            }
        }
	}
	
	/**
	 * Sends email notifications to the specified email addresses indicating that the job is complete.
	 */
	protected void sendNotificationEmails(ExportHistoryEntry historyEntry) {
		List<EmailMessageHolder> messages = Lists.newArrayList();
		String subject = getMessage(getSubjectKey(historyEntry.getType()), historyEntry.getOriginalFileName());
		String body = getNotificationBody(historyEntry);
		
		notificationEmailAddresses = StringUtils.trimAllWhitespace(notificationEmailAddresses);
		String[] emailsArray = notificationEmailAddresses.split(",");
		
		for(String emailAddress : emailsArray) {
			DefaultEmailMessage message = new DefaultEmailMessage();
			message.setRecipient(emailAddress);
			message.setSubject(subject);
			message.setHtmlBody(body);
			messages.add(message);
		}
		
		for(EmailMessageHolder message : messages) {
			try {
				emailService.sendHTMLMessage(message);
			} catch(MessagingException e) {
				log.error("Unable to send " + historyEntry.getType() + " export notification email.", e);
			}
		}
		log.debug("Scheduled " + historyEntry.getType() + " export \"" + name + "\" email notifications complete.");
	}

    /**
     *  Copies from the archive file to the export file if selected. 
     *  Returns exportFile or null if user has selected to not include export file copy. 
     * @param archiveFile
     * @return exportFile
     */
    protected File copyExportFile(File archiveFile) {
        if (includeExportCopy) {
            String fileName = getExportFileName(archiveFile);
            // need to get the base and file extension...
            String baseName = fileName.substring(0, fileName.lastIndexOf("."));
            String fileExt = fileName.substring(fileName.lastIndexOf("."));
            File exportFile = new File(exportPath, fileName);
            for (int i = 1; exportFile.exists(); i++) {
                // if an identically named file exists, add incrementing numbers to the end.
                exportFile = new File(exportPath, baseName + "(" + i + ")" + fileExt);
            }
            try {
                Files.copy(archiveFile, exportFile);
            } catch (IOException e) {
                log.error("Unable to copy Archive file to Export file: ", e);
            }
            return exportFile;
        } else {
            return null;
        }
    }
    
    /**
     * Create the (empty) File object in the archive path...
     * @param timestamp
     * @param defaultTimeExtension
     * @return archiveFile
     */
    protected File createArchiveFile(DateTime now, String defaultFileExtension) {
        String finalName = this.getExportFileName();
        String fileExtension = defaultFileExtension;
        String filePath = CtiUtilities.getArchiveDirPath();
        if (appendDateToFileName) {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(timestampPatternField);
            String dateString = DATE_FORMAT.format(now.toDate());
            // strip characters not allowed in file names.
            dateString = dateString.replaceAll("[\\/:*?\"><|]", "");
            finalName = finalName + dateString;
        }
        if (overrideFileExtension) {
            fileExtension = exportFileExtension;
        }
        fileExtension += "_" + CtiUtilities.getUuidString();
        File archiveFile = new File(filePath, finalName + fileExtension);
        for (int i = 1; archiveFile.exists(); i++) {
            // if an identically named file exists, add incrementing numbers to the end...
            archiveFile = new File(filePath, finalName + "(" + i + ")" + fileExtension);
        }
        return archiveFile;
    }
	
    /**
     * Creates the History entry based on the Archive and Export file(s).
     * @param type
     * @param archiveFile
     * @param exportFile
     * @return historyEntry
     */
    protected ExportHistoryEntry createExportHistoryEntry(FileExportType type, File archiveFile, File exportFile) {
        ExportHistoryEntry historyEntry = null;
        try {
            String initiator = getMessage(type.getFormatKey()) + " Schedule: " + name;
            String fileName = getExportFileName(archiveFile);
            historyEntry = fileExportHistoryService.addHistoryEntry(archiveFile, exportFile, fileName, type, initiator);
        } catch(IOException e) {
            log.error("Unable to create " + type + " export file history archive.", e);
        }
        log.debug("Scheduled " + type + " export \"" + name + "\" file added to File Export History");
        
        return historyEntry;
    }
    
    /**
     * Convenience method to get the filename without the uuid appendage.
     * @param archiveFile
     * @return
     */
    protected String getExportFileName(File archiveFile) {
        String archiveName = archiveFile.getName();
        return archiveName.substring(0, archiveName.lastIndexOf("_"));
    }
    
	//Gets the appropriate email subject i18n key based on the type of file export.
	private String getSubjectKey(FileExportType type) {
		return "yukon.web.modules.amr.scheduledFileExport.notification.subject." + type.name();
	}
	
	//Gets the message String for an i18n key.
	private String getMessage(String key, Object... args) {
		YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable(key, args);
		MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(getUserContext());
		return messageSourceAccessor.getMessage(resolvable);
	}
	
	/*
	 * Gets an html body String for an email notification message, which includes a link to the
	 * related File Export History page.
	 */
	private String getNotificationBody(ExportHistoryEntry historyEntry) {
		String baseUrl = configurationSource.getString(MasterConfigStringKeysEnum.YUKON_EXTERNAL_URL, defaultYukonExternalUrl);
		
		int historyId = historyEntry == null ? 0 : historyEntry.getId();
		String historyLink = baseUrl + HISTORY_URL_PART + historyId;
		String body = getMessage("yukon.web.modules.amr.scheduledFileExport.notification.body", name, historyLink);
		return body;
	}
}
