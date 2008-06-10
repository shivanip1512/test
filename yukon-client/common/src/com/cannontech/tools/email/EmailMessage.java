/*
 * Created on Oct 31, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.email;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.roles.yukon.SystemRole;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class EmailMessage
{
	private String smtpServer;	//will default to config.parameter settings
	private String from;	//will default to config.parameter settings.
	private String to;		//a comma separated string of email addresses.
	private String subject;	//email subject text
	private String body;	//email body text
	
	private String to_CC;	//a comma separated string of email addresses for CC
	private String to_BCC;	//a comma separated string of email addresses for BC
	
	//Elements of char[] values
	private java.util.ArrayList attachments;	//email files attachment char[] data 
	//Elements of String values (of filenames)
	private java.util.ArrayList attachmentNames;	//email files attachment names
	
	// Elements of DataSource values
	private java.util.ArrayList dsAttachments;	// email attachment as data source 
	
	public EmailMessage()
	{
		super();
	}
	public EmailMessage(String to_, String subject_, String body_)
	{
		super();
		to = to_;
		subject = subject_;
		body = body_;
	}
	public EmailMessage(String[] to_, String subject_, String body_)
	{
		super();
		addTo_Array(to_);
		subject = subject_;
		body = body_;
	}	
	/**
	 * Uses '=' as argument delimeter unless it doesn't find this,
	 * 	the it attempts to use ':' if found.
	 * Valid args startWith (tolower) :	'to' - setTo(arg)
	 * 									'body' - setBody(arg)
	 * 									'attach' -setAttach(arg)
	 * 									'subj' - setSubject(arg)
	 * 									'cc' - setTo_CC(arg)
	 * 									'bcc' - setTo_BCC(arg)
	 * @param args
	 */
	public static void main(String[] args)
	{
		char argDel = '=';
		EmailMessage emailMessage = new EmailMessage(); 
			
		for ( int i = 0; i < args.length; i++)
		{
			if( i == 0)	//first loop through, verify the char '=' is our delimiter, else try ':'
			{
				if( args[i].toLowerCase().indexOf(argDel) < 0)
					argDel = ':';
			}

			String argLowerCase = (String)args[i].toLowerCase();
				
			// Check the delimiter of '=', if not found check ':'
			int startIndex = argLowerCase.indexOf('=');
			if( startIndex < 0)
				startIndex = argLowerCase.indexOf(':');
			startIndex += 1;
				
			if( argLowerCase.startsWith("to"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.setTo(subString);
			}
			else if( argLowerCase.startsWith("body"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.setBody(subString);
			}
			else if( argLowerCase.startsWith("attachmessage"))
			{
				String subString = (String)args[i].substring(startIndex);
				char [] attachmentMessage = subString.toCharArray();
				emailMessage.addAttachment(attachmentMessage);
			}
			else if (argLowerCase.startsWith("attachfilename"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.addAttachmentName(subString);
			}
			else if( argLowerCase.startsWith("subj"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.setSubject(subString);
			}
			else if( argLowerCase.startsWith("cc"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.setTo_CC(subString);
			}
			else if( argLowerCase.startsWith("bcc"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.setTo_BCC(subString);
			}
		}

		try
		{
			emailMessage.send();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
		}

		System.exit(0);
	}
	
	/**
	 * Create emailMessage and send it, with attachments if exist.
	 */
	public void send() throws AddressException, MessagingException
	{
		java.util.Properties systemProps = System.getProperties();
		
		//a property used internally by the JavaMail API
		systemProps.put("mail.smtp.host", getSmtpServer());

		
		Session session = Session.getInstance(systemProps);
		
		Message	message = new MimeMessage(session);
		message.setFrom(new InternetAddress(getFrom()));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getTo()));
		if( getTo_CC() != null)
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(getTo_CC()));
		if( getTo_BCC() != null)
			message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(getTo_BCC()));
			
		message.setSubject(getSubject());

		Multipart multiPart = new MimeMultipart();
		
		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText(getBody());
		multiPart.addBodyPart(bodyPart);
		
		for (int i = 0; i < getAttachments().size(); i++)
		{
			CharArrayDataSource ds = new CharArrayDataSource( (char[])getAttachments().get(i), (String)getAttachmentNames().get(i), "" );
			
			bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler( new DataHandler(ds) );
			bodyPart.setFileName( ds.getName() );
			multiPart.addBodyPart( bodyPart );
		}
		
		for (int i = 0; i < getDSAttachments().size(); i++)
		{
			DataSource ds = (DataSource) getDSAttachments().get(i);
			
			bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler( new DataHandler(ds) );
			bodyPart.setFileName( ds.getName() );
			multiPart.addBodyPart( bodyPart );
		}
		
		message.setContent(multiPart);
		message.setHeader("X-Mailer", "CannontechEmail");
		message.setSentDate(new java.util.Date());

		Transport.send(message);
	}
	/**
	 * @return
	 */
	public String getBody()
	{
		if( body == null)
			body = "Message Body is NULL";

		return body;
	}

	/**
	 * @return
	 */
	public String getFrom()
	{
		if( from == null)
		{
			from = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.MAIL_FROM_ADDRESS );

			//still dont have a value, nothing will work then!
			if( from == null )
				CTILogger.error( 
					"Unable to find the MAIL_FROM address, " +
					"be sure this is defined correctly" );
		}
			
		return from;
	}

	/**
	 * @return
	 */
	public String getSmtpServer()
	{
		if( smtpServer == null)
		{
			smtpServer = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.SMTP_HOST );


			//still dont have a value, nothing will work then!
			if( smtpServer == null )
				CTILogger.error( 
					"Unable to find the SMTP_HOST server defined, " +
					"be sure this is defined correctly" );
		}

		return smtpServer;
	}

	/**
	 * @return
	 */
	public String getSubject()
	{
		if( subject == null)
			subject = "(none)";
		return subject;
	}

	/**
	 * @return
	 */
	public String getTo()
	{
		if (to == null)
			to = getFrom();	//send back to from...why 0not?!

		return to;
	}
	/**
	 * @return
	 */
	public java.util.ArrayList getAttachments()
	{
		if( attachments == null)
			attachments = new java.util.ArrayList(1);
		return attachments;
	}

	/**
	 * @param string
	 */
	public void setBody(String string)
	{
		body = string;
	}

	/**
	 * @param string
	 */
	public void setFrom(String string)
	{
		from = string;
	}

	/**
	 * @param string
	 */
	public void setSmtpServer(String string)
	{
		smtpServer = string;
	}

	/**
	 * @param string
	 */
	public void setSubject(String string)
	{
		subject = string;
	}

	/**
	 * @param string
	 */
	public void setTo(String string)
	{
		to = string;
	}

	/**
	 * @param list
	 */
	public void setAttachments(java.util.ArrayList list)
	{
		attachments = list;
	}

	public void addAttachment(char [] fileData)
	{
		getAttachments().add( fileData );
	}

	/**
	 * @return
	 */
	public java.util.ArrayList getAttachmentNames()
	{
		if( attachmentNames == null)
				attachmentNames = new java.util.ArrayList(1);
			return attachmentNames;
	}

	/**
	 * @param list
	 */
	public void setAttachmentNames(java.util.ArrayList list)
	{
		attachmentNames = list;
	}

	public void addAttachmentName(String fileName)
	{
		getAttachmentNames().add(fileName);	
	}

	
	public String toString()
	{
		String message= "EmailMessage - TO: "+ getTo() + "  FROM: " + getFrom() + "  SMTPSERVER: " + getSmtpServer();										    
		return message;
	}

	/**
	 * @return
	 */
	public String getTo_BCC()
	{
		return to_BCC;
	}

	/**
	 * @return
	 */
	public String getTo_CC()
	{
		return to_CC;
	}

	/**
	 * @param string
	 */
	public void setTo_BCC(String string)
	{
		to_BCC = string;
	}

	/**
	 * @param string
	 */
	public void setTo_CC(String string)
	{
		to_CC = string;
	}

	public void addTo_CC_Array(String[] ccEmailAddress)
	{
		String cc = (getTo_CC() == null ? "" : getTo_CC());

		if( ccEmailAddress != null && ccEmailAddress.length > 0)
		{
			cc += (cc.length() > 0 ? ", " + ccEmailAddress[0] : ccEmailAddress[0]);
			for (int i = 1; i < ccEmailAddress.length; i++)
				cc += ", " + ccEmailAddress[i];
		}
		setTo_CC(cc);
	}
	
	public void addTo_BCC_Array(String[] bccEmailAddress)
	{
		String bcc = (getTo_BCC() == null ? "" : getTo_BCC());

		if( bccEmailAddress != null && bccEmailAddress.length > 0)
		{
			bcc += (bcc.length() > 0 ? ", " + bccEmailAddress[0] : bccEmailAddress[0]);
			for (int i = 1; i < bccEmailAddress.length; i++)
				bcc += ", " + bccEmailAddress[i];
		}
		setTo_BCC(bcc);
	}
	
	public void addTo_Array(String[] emailAddress)
	{
		String oldTo = (to == null ? "" : to);

		if( emailAddress != null && emailAddress.length > 0)
		{
			oldTo += (oldTo.length() > 0 ? ", " + emailAddress[0] : emailAddress[0]);
			for (int i = 1; i < emailAddress.length; i++)
				oldTo += ", " + emailAddress[i];
		}
		setTo(oldTo);
	}
	
	
	private java.util.ArrayList getDSAttachments() {
		if (dsAttachments == null)
			dsAttachments = new java.util.ArrayList();
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
