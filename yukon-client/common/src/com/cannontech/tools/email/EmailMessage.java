/*
 * Created on Oct 31, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.tools.email;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.cannontech.common.util.CtiProperties;

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
	
	//Elements of String values (of filenames)
	private java.util.ArrayList attachments;	//email files attachment names 
	
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
		setTo(to_);
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
			else if( argLowerCase.startsWith("attach"))
			{
				String subString = (String)args[i].substring(startIndex);
				emailMessage.addAttachment(subString);
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

		emailMessage.send();
		System.exit(0);
	}
	
	/**
	 * Create emailMessage and send it, with attachments if exist.
	 */
	public void send()
	{
		try
		{
			java.util.Properties systemProps = System.getProperties();
			systemProps.put(CtiProperties.KEY_SMTP_HOST, getSmtpServer());
			
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
			
			if( !getAttachments().isEmpty())
			{
				for (int i = 0; i < attachments.size(); i++)
				{
					bodyPart = new MimeBodyPart();
					FileDataSource file = new FileDataSource((String) attachments.get(i));
					bodyPart.setDataHandler(new DataHandler(file));
					bodyPart.setFileName((String) attachments.get(i));
					multiPart.addBodyPart(bodyPart);
				}
			}
			message.setContent(multiPart);
			message.setHeader("X-Mailer", "CannontechEmail");
			message.setSentDate(new java.util.Date());

			Transport.send(message);
			
		}
		catch (javax.mail.MessagingException me)
		{
			 me.printStackTrace();
		}
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
			from = CtiProperties.getInstance().getProperty(CtiProperties.KEY_MAIL_FROM_ADDRESS);
		return from;
	}

	/**
	 * @return
	 */
	public String getSmtpServer()
	{
		if( smtpServer == null)
			smtpServer = CtiProperties.getInstance().getProperty(CtiProperties.KEY_SMTP_HOST);
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

	public void addAttachment(String fileName)
	{
		getAttachments().add(fileName);	
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

	public void setTo_CC(String[] ccEmailAddress)
	{
		String cc = "";
		if( ccEmailAddress != null && ccEmailAddress.length >= 0)
		{
			cc = ccEmailAddress[0];
			for (int i = 1; i < ccEmailAddress.length; i++)
				cc += ", " + ccEmailAddress[i];
		}
		setTo_CC(cc);
	}
	public void setTo_BCC(String[] bccEmailAddress)
	{
		String bcc = "";
		if( bccEmailAddress != null && bccEmailAddress.length >= 0)
		{
			bcc = bccEmailAddress[0];
			for (int i = 1; i < bccEmailAddress.length; i++)
				bcc += ", " + bccEmailAddress[i];
		}
		setTo_BCC(bcc);
	}
	public void setTo(String[] emailAddress)
	{
		String to = "";
		if( emailAddress != null && emailAddress.length >= 0)
		{
			to = emailAddress[0];
			for (int i = 1; i < emailAddress.length; i++)
				to += ", " + emailAddress[i];
		}
		setTo(to);
	}
}
