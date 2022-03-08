package com.cannontech.tools.email;/**
 * Insert the type's description here.
 * Creation date: (11/14/2001 11:32:29 PM)
 * @author: 
 */
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;

class SendMessageThread extends Thread 
{
	private MailAccount account = null;
	private Message[] messages = null;
/**
 * SendMessageThread constructor comment.
 * @param name java.lang.String
 */
public SendMessageThread( MailAccount account_, Message[] messages_ )
{
	super("MailSendThread");

	account = account_;
	messages = messages_;
}
public void run() 
{
	try 
	{

		Transport trans = Session.getDefaultInstance(System.getProperties()).getTransport(
					account.getOutServer() );

		trans.connect(
				account.getOutServer(),
				account.getOutPort(),
				account.getOutLogin(),
				account.getOutPass() );
		
		for (int i = 0; i < messages.length; i++) 
		{
			trans.sendMessage(messages[i], messages[i].getAllRecipients() );
		}
		
		trans.close();
	} 
	catch (Exception ex) 
	{
		ex.printStackTrace(System.out);
	}

}
}