package com.cannontech.web.scheduledFileExport.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.fileExportHistory.service.FileExportHistoryService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.scheduledFileExport.ExportFileGenerationParameters;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.tools.email.DefaultEmailMessage;
import com.cannontech.tools.email.EmailMessageHolder;
import com.cannontech.tools.email.EmailService;
import com.google.common.collect.Lists;

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
	
	protected String name;
	protected boolean appendDateToFileName;
	protected String exportFileName;
	protected String exportPath;
	protected String notificationEmailAddresses;
	
	private Logger log = YukonLogManager.getLogger(ScheduledFileExportTask.class);
	
	protected static final String DEFAULT_FILE_EXTENSION = ".csv";
	private static final String HISTORY_URL_PART = "/support/fileExportHistory/list?entryId=";
	
	
	@Override
	public abstract void start();
	
	/**
	 * This method should be overridden by each subclass such that it accepts a subclass of
	 * ExportFileGenerationParameters specific to that task.
	 */
	public abstract void setFileGenerationParameters(ExportFileGenerationParameters parameters);
	
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
		return exportFileName;
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
	 * Creates a File object whose path matches the specified export path, and
	 * whose name matches the specified export file name. In addition, the current date may 
	 * optionally be appended to the file name.
	 */
	protected File getExportFile(DateTime now, String fileExtension) {
		String finalName = exportFileName;
		if(appendDateToFileName) {
			String dateString = dateFormattingService.format(now, DateFormatEnum.DATE, getUserContext());
			finalName = finalName + "-" + dateString.replaceAll("/", "-");
		}

		File exportFile = new File(exportPath, finalName + fileExtension);
		for(int i = 1; exportFile.exists(); i++) { 
			//if an identically named file exists, add incrementing numbers
			//to the end of the file name until there are no conflicts
			exportFile = new File(exportPath, finalName + "(" + i + ")" + fileExtension);
		}
		
		return exportFile;
	}
	
	/**
	* Creates a copy of the exported file in an archive location, and adds a related entry 
	* to File Export History.
	*/
	protected ExportHistoryEntry addFileToExportHistory(FileExportType type, File exportFile) {
		ExportHistoryEntry historyEntry = null;
		try {
			String initiator = getMessage(type.getFormatKey()) + " Schedule: " + name;
			historyEntry = fileExportHistoryService.copyFile(exportFile, type, initiator);
		} catch(IOException e) {
			log.error("Unable to copy " + type + " export file to export history archive.", e);
		}
		log.debug("Scheduled " + type + " export \"" + name + "\" file added to File Export History");
		
		return historyEntry;
	}
	
	//Gets the appropriate email subject i18n key based on the type of file export.
	private String getSubjectKey(FileExportType type) {
		if(type == FileExportType.BILLING) {
			return "yukon.web.modules.amr.scheduledFileExport.notification.billingSubject";
		} else if(type == FileExportType.ARCHIVED_DATA_EXPORT) {
			return "yukon.web.modules.amr.scheduledFileExport.notification.adeSubject";
		}
		throw new IllegalArgumentException("No email subject found for file export type " + type);
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
		int historyId = historyEntry == null ? 0 : historyEntry.getId();
		String baseUrl = getMessage("yukon.web.modules.amr.scheduledFileExport.notification.yukonBaseUrl");
		String historyLink = baseUrl + HISTORY_URL_PART + historyId;
		String body = getMessage("yukon.web.modules.amr.scheduledFileExport.notification.body", name, historyLink);
		return body;
	}
}
