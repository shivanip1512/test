package com.cannontech.tools.email;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EmailMessage {
	private String smtpServer;	//will default to config.parameter settings
	private String from;	//will default to config.parameter settings.
	private String to;		//a comma separated string of email addresses.
	private String subject;	//email subject text
	private String body;	//email body text
	
	private String to_CC;	//a comma separated string of email addresses for CC
	private String to_BCC;	//a comma separated string of email addresses for BC
	
	private List<char[]> attachments;
	private List<String> attachmentNames;
	private List<DataSource> dsAttachments; 
	
	public EmailMessage() {
    }
	
	public EmailMessage(String[] to_, String subject_, String body_) {
		addTo_Array(to_);
		subject = subject_;
		body = body_;
	}	
	
	public String getBody() {
		if( body == null)
			body = "Message Body is NULL";

		return body;
	}

	public String getFrom() {
		if (from == null) {
		    from = YukonSpringHook.getBean(GlobalSettingDao.class).getString(GlobalSettingType.MAIL_FROM_ADDRESS);

			//still dont have a value, nothing will work then!
			if (from == null) {
				CTILogger.error("Unable to find the MAIL_FROM address, be sure this is defined correctly");
			}
		}
			
		return from;
	}

	public String getSmtpServer() {
		if (smtpServer == null) {
		    smtpServer = YukonSpringHook.getBean(GlobalSettingDao.class).getString(GlobalSettingType.SMTP_HOST);

			//still dont have a value, nothing will work then!
			if (smtpServer == null) {
				CTILogger.error("Unable to find the SMTP_HOST server defined, be sure this is defined correctly");
			}
		}

		return smtpServer;
	}

	public String getSubject() {
		if (subject == null) {
			subject = "(none)";
	    }
		
		return subject;
	}

	public String getTo() {
		if (to == null) {
			to = getFrom();	//send back to from...why 0not?!
		}
		
		return to;
	}

    public List<char[]> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }

        return attachments;
    }

    public void setBody(String string) {
        body = string;
    }

	public void setFrom(String string) {
		from = string;
	}

	public void setSmtpServer(String string) {
		smtpServer = string;
	}

	public void setSubject(String string) {
		subject = string;
	}

	public void setTo(String string) {
		to = string;
	}

	public void setAttachments(ArrayList<char[]> list) {
		attachments = list;
	}

	public void addAttachment(char[] fileData) {
		getAttachments().add( fileData );
	}

	public List<String> getAttachmentNames() {
		if (attachmentNames == null) {
			attachmentNames = new ArrayList<>();
		}
		
		return attachmentNames;
	}

	public void setAttachmentNames(ArrayList<String> list) {
		attachmentNames = list;
	}

	public void addAttachmentName(String fileName) {
		getAttachmentNames().add(fileName);	
	}
	
	public String toString() {
		String message= "EmailMessage - TO: "+ getTo() + "  FROM: " + getFrom() + "  SMTPSERVER: " + getSmtpServer();										    
		return message;
	}

	public String getTo_BCC() {
		return to_BCC;
	}

	public String getTo_CC() {
		return to_CC;
	}

	public void setTo_BCC(String string) {
		to_BCC = string;
	}

	public void setTo_CC(String string) {
		to_CC = string;
	}

	public void addTo_CC_Array(String[] ccEmailAddress)  {
		String cc = (getTo_CC() == null ? "" : getTo_CC());

		if( ccEmailAddress != null && ccEmailAddress.length > 0) {
			cc += (cc.length() > 0 ? ", " + ccEmailAddress[0] : ccEmailAddress[0]);
			for (int i = 1; i < ccEmailAddress.length; i++)
				cc += ", " + ccEmailAddress[i];
		}
		setTo_CC(cc);
	}
	
	public void addTo_BCC_Array(String[] bccEmailAddress) {
		String bcc = (getTo_BCC() == null ? "" : getTo_BCC());

		if( bccEmailAddress != null && bccEmailAddress.length > 0) {
			bcc += (bcc.length() > 0 ? ", " + bccEmailAddress[0] : bccEmailAddress[0]);
			for (int i = 1; i < bccEmailAddress.length; i++)
				bcc += ", " + bccEmailAddress[i];
		}
		setTo_BCC(bcc);
	}
	
	public void addTo_Array(String[] emailAddress) {
		String oldTo = (to == null ? "" : to);

		if( emailAddress != null && emailAddress.length > 0) {
			oldTo += (oldTo.length() > 0 ? ", " + emailAddress[0] : emailAddress[0]);
			for (int i = 1; i < emailAddress.length; i++)
				oldTo += ", " + emailAddress[i];
		}
		setTo(oldTo);
	}
	
	public List<DataSource> getDSAttachments() {
		if (dsAttachments == null) {
			dsAttachments = new ArrayList<>();
		}
		
		return dsAttachments;
	}
	
	public void addAttachment(File file, String alternativeName) {
		EmailFileDataSource ds = new EmailFileDataSource(file, alternativeName, null);
		getDSAttachments().add( ds );
	}
	
	public void addAttachment(char[] fileData, String fileName, String contentType) {
		CharArrayDataSource ds = new CharArrayDataSource(fileData, fileName, contentType);
		getDSAttachments().add( ds );
	}
}