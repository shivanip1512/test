package com.cannontech.tools.email;
/**
 * Insert the type's description here.
 * Creation date: (11/8/2001 11:03:50 PM)
 * @author: 
 */
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import com.cannontech.clientutils.commandlineparameters.CommandLineParser;

class Rat
{
	private String type = "javamail";
	public final static String[] COMMAND_LINE_PARAM_NAMES = 
	{
		"mailhost",
		"toaddress",
		"fromaddress",
		"mailusername",
		"mailpassword",
		"type",
		"test"
	};

	private boolean emailSent = false;
	public final int CHECK_INTERVAL = 600; //seconds to check
	private String DISPATCH_HOST = "localhost";
	private int DISPATCH_PORT = 1510;
	private final String PROGRAM_NAME = "dispatch.exe";
	
	private java.util.ArrayList processNames = new java.util.ArrayList(100);
	
	private Address fromAddress = null;
	private Address toAddress = null;
	private String userName = null;
	private String password = null;
	private String host = "65.165.200.66"; //CTI notesserver address

/**
 * ProcessRunner constructor comment.
 */
public Rat() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:11:03 AM)
 * @return boolean
 */
public Message createMailMessage( MailAccount account, String subject, String text )
{

	try 
	{
		//temp code for testing
		Session session = Session.getDefaultInstance( System.getProperties() );
		MimeMessage mm = new MimeMessage(session);


		Address from = getFromAddress();
		Address[] replyto = { getFromAddress() };
		Address[] sendto = { getToAddress() };
		Address[] cc = null;
		Address[] bcc = null;
		String content = text;
	
		if (from == null) 
		{
		    System.err.println("No FROM specified");
		} 
		else if (sendto.length == 0) 
		{
		    System.err.println("No SENDTO specified");
		} 
		else 
		{
		   // must headers
		   mm.addHeader("X-Mailer", "CTI Error Detector");
		   mm.addHeader("X-Priority","3 (Normal)");
		   mm.addHeader("Organization",account.getOrganization());
		   mm.setFrom(from);
		   mm.setReplyTo(replyto);
		   mm.setRecipients(Message.RecipientType.CC, cc);
		   mm.setRecipients(Message.RecipientType.BCC, bcc);
		   mm.setRecipients(Message.RecipientType.TO, sendto);
		   mm.setSubject(subject);
			mm.setSentDate(new java.util.Date());			
			mm.setContent(content,"text/plain");
		   mm.saveChanges();
		   
		   return(mm);
		}

	}
	catch (Exception ex) 
	{
	    ex.printStackTrace( System.out );
	}
	
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:11:03 AM)
 * @return boolean
 */
public void executeCheck() 
{
	for(;;)
	{
		try
		{
			//first check our connection to dispatch
			if( !executeCheckConnection() )
			{
				System.out.println("No connection to " + PROGRAM_NAME );
				if( !isEmailSent() )
				{
					sendMailMessage( PROGRAM_NAME + " is NOT responding!!", 
							PROGRAM_NAME + " is NOT responding to a loopback!!  You better check it out." );
					
					setEmailSent( true );
				}
			}
/*			else if(  !executeCheckProcess() )
			{
				
				if( !isEmailSent() )
				{
					sendMailMessage( PROGRAM_NAME + " is NOT running!!", 
							PROGRAM_NAME + " is NOT running!!" );
					
					setEmailSent( true );
				}

			}*/
			else
			{
				if( isEmailSent() )
				{
					setEmailSent( false );
					sendMailMessage( PROGRAM_NAME + " is running.", 
							PROGRAM_NAME + " is running now!" );
				}

				System.out.println( PROGRAM_NAME + " was found!" );
			}
			

			Thread.currentThread().sleep( CHECK_INTERVAL * 1000 );
		}
		catch( InterruptedException e )
		{
			handleException( e );
		}
	}
	

}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:11:03 AM)
 * @return boolean
 */
private boolean executeCheckConnection() 
{
	
	try
	{
		getExternalResources();
		com.cannontech.message.dispatch.message.Registration reg = 
					new com.cannontech.message.dispatch.message.Registration();
					
		reg.setAppName("PROGRAM_STATE_CHECKER");
		reg.setAppIsUnique(1);  //this app IS unique
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 60 );  // 1 minutes
		
		System.out.println("Trying to connect to:  " + DISPATCH_HOST + " " + DISPATCH_PORT );
		com.cannontech.message.dispatch.ClientConnection connection = 
					new com.cannontech.message.dispatch.ClientConnection();

		connection.setHost(DISPATCH_HOST);
		connection.setPort(DISPATCH_PORT);
		
		connection.setAutoReconnect( false );
		connection.setRegistrationMsg( reg );
		connection.connectWithoutWait();

		//wait for our connection to connnect
		for( int i = 0; i < 5; i++ )
		{
			if( connection.isValid() )
				break;

			try
			{
				Thread.currentThread().sleep(1000);
			}
			catch( InterruptedException e ) {}
		}

		Object ret = null;	
		if( connection.isValid() )
		{	
			System.out.println("....Connection & Registration to Server Established.");
			com.cannontech.message.dispatch.message.Command cmd = 
					new com.cannontech.message.dispatch.message.Command();

			cmd.setOperation( cmd.LOOP_CLIENT );
			cmd.setPriority(15);
			connection.write( cmd );

			//wait 60 seconds at most for a response then stop
			ret = connection.read( 60000 );
			System.out.println("		loopback returned = " + ret.toString() );


			com.cannontech.message.dispatch.message.Command cmd1 = 
					new com.cannontech.message.dispatch.message.Command();

			cmd1.setOperation( cmd1.CLIENT_APP_SHUTDOWN );
			connection.write( cmd1 );

			connection.disconnect();
		}

		connection = null;
		
		if( ret == null )
			return false;
		else
			return true;
	}	
	catch( Exception e )
	{
		handleException( e );
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:11:03 AM)
 * @return boolean
 */
private boolean executeCheckProcess() 
{
	java.io.BufferedReader br = null;
	getProcessNames().clear();

	try
	{
		String[] s = { "cmd", "/c", "f:/ntreskit/pulist.exe" };
		Process p = Runtime.getRuntime().exec( s );

		br = new java.io.BufferedReader( new java.io.InputStreamReader(p.getInputStream()) );
		String val = null;
		while( (val = br.readLine()) != null )
		{
			getProcessNames().add( val.substring(0, val.indexOf(" ")) );
			//System.out.println("    " + val.substring(0, val.indexOf(" ") ) );
		}
		//System.out.println("Return from pulist.exe : " + p.waitFor() );
		p.waitFor();

		for( int i = 0; i < getProcessNames().size(); i++ )
			if( getProcessNames().get(i).toString().equalsIgnoreCase(PROGRAM_NAME) )
				return true;
	}
	catch( java.io.IOException e )
	{		
		e.printStackTrace( System.out );
	}
	catch( InterruptedException e )
	{		
		e.printStackTrace( System.out );
	}
	finally
	{
		try
		{
			if( br != null )
				br.close();
		}
		catch( java.io.IOException e )
		{		
			e.printStackTrace( System.out );
		}
		
	}

	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (3/21/00 2:26:52 PM)
 */
private void getExternalResources() 
{
	try
	{
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
		DISPATCH_HOST = bundle.getString("dispatch_machine");
		DISPATCH_PORT = Integer.parseInt( bundle.getString("dispatch_port") );
	}
	catch( java.util.MissingResourceException mre)
	{
		handleException( mre );
	}
	catch( NumberFormatException ex )
	{
		handleException( ex );
	}

}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @return javax.mail.Address
 */
public javax.mail.Address getFromAddress() {
	return fromAddress;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @return java.lang.String
 */
public java.lang.String getHost() {
	return host;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @return java.lang.String
 */
public java.lang.String getPassword() {
	return password;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:23:08 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getProcessNames() {
	return processNames;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @return javax.mail.Address
 */
public javax.mail.Address getToAddress() {
	return toAddress;
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2001 2:10:11 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);

}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2001 7:58:36 PM)
 * @return boolean
 */
public boolean isEmailSent() {
	return emailSent;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2001 11:04:01 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{

	try
	{
		String[] values = null;
		
		if( args.length < 3 )  // the user tried to enter some params
		{
			System.out.println("Command Syntax : " + Rat.class.getName() +
				" mailhost=? toaddress=? fromaddress=? [Optional MailUserName=?] [Optional MailPassword=?] [ Optional type=[javamail|qmail] ] [Optional test=y]\r\n" +
				" Parameters:\r\n" +
				" -----------------\r\n" +
				"   mailhost = The address of the smtp out server.\r\n" +
				"   toaddress = Email address to send the email to.\r\n" +
				"   fromaddress = Email address that will appear in the From.\r\n" +
				"   MailUserName = [Optional] User name if the smtp server requires it.\r\n" +
				"   MailPassword = [Optional] Password if the smtp server requires it.\r\n" +
				"   type = [Optional] qmail or javamail, defaults to javamail.\r\n" +
				"   test = [Optional] Y if you want to send a test message.\r\n" );

			return;
		}
		else
		{
			CommandLineParser parser = new CommandLineParser( Rat.COMMAND_LINE_PARAM_NAMES );
			values = parser.parseArgs( args );
		}
		
		Rat ratter = new Rat();
		ratter.setHost( values[0] );
		ratter.setToAddress( new InternetAddress(values[1]) );
		ratter.setFromAddress( new InternetAddress(values[2]) );

		if( values[3] != null )
			ratter.setUserName( values[3] );
			
		if( values[4] != null )
			ratter.setPassword( values[4] );

		if( values[5] != null )
			ratter.setType( values[5] );

		if( values[6] != null )
		{
			if( !ratter.getType().equalsIgnoreCase("javamail") )
				ratter.tryQSMTP(
					ratter.getHost(),
					ratter.getFromAddress().toString(),
					ratter.getToAddress().toString(),
					"QSmtp Email test SUCCESS!!!", 
					"Testing email service" );
			else
				ratter.sendMailMessage( "Email test SUCCESS!!!", "Testing email service" );

			//give the email some time to be sent
			Thread.currentThread().sleep(1000);
			return;
		}


		//waits in here forever
		ratter.executeCheck();
	}
	catch( Exception e )
	{		
		e.printStackTrace( System.out );
	}
	
		
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 11:20:21 PM)
 * @param text java.lang.String
 */
public void sendMailMessage( String subject, String text )
{
	if( !getType().equalsIgnoreCase("javamail") )
	{
		tryQSMTP( getHost(), getFromAddress().toString(),
						getToAddress().toString(),
						subject, 
						text );
	}
	else
	{
		MailAccount account = new MailAccount();
		account.setOutLogin( getUserName() );
		account.setOutPass( getPassword() );
		account.setOutServer( getHost() );
		
		Message[] m = { createMailMessage( account, subject, text ) };


		//start up the thread to send out the mail
		new SendMessageThread(
					account, m ).start();
	}

}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2001 7:58:36 PM)
 * @param newEmailSent boolean
 */
private void setEmailSent(boolean newEmailSent) {
	emailSent = newEmailSent;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @param newFromAddress javax.mail.Address
 */
public void setFromAddress(javax.mail.Address newFromAddress) {
	fromAddress = newFromAddress;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @param newHost java.lang.String
 */
public void setHost(java.lang.String newHost) {
	host = newHost;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @param newPassword java.lang.String
 */
public void setPassword(java.lang.String newPassword) {
	password = newPassword;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:23:08 AM)
 * @param newProcessNames java.util.ArrayList
 */
public void setProcessNames(java.util.ArrayList newProcessNames) {
	processNames = newProcessNames;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @param newToAddress javax.mail.Address
 */
public void setToAddress(javax.mail.Address newToAddress) {
	toAddress = newToAddress;
}


/**
 * Insert the method's description here.
 * Creation date: (11/30/2001 2:10:11 PM)
 * @param newType java.lang.String
 */
public void setType(java.lang.String newType) {
	type = newType;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:40:12 PM)
 * @param newUserName java.lang.String
 */
public void setUserName(java.lang.String newUserName) {
	userName = newUserName;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 1:11:03 AM)
 * @return boolean
 */
private boolean startProcess( String pName ) 
{

	try
	{
		String[] s = { "cmd", /*PATH*/pName };
		Process p = Runtime.getRuntime().exec( s );

		//give the process some time to start
		Thread.currentThread().sleep( 1000 );

		return true;
	}
	catch( InterruptedException e )
	{		
		e.printStackTrace( System.out );
	}
	catch( java.io.IOException e )
	{		
		e.printStackTrace( System.out );
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (11/18/2001 7:08:24 PM)
 */
public void tryQSMTP( String host, String fromAddress, String toAddress, String subject, String message )
{
	try
	{
		System.out.println("Started QSMTP..");

		//"65.165.200.66") );  //CTI address
		//Qsmtp q = new Qsmtp( java.net.InetAddress.getByName("10.100.10.1") );
		Qsmtp q = new Qsmtp( host );
		
		q.sendmsg(
			fromAddress,
			toAddress,
			subject,
			message );
		
		q.close();
		System.out.println("Closed QSMTP");
	}
	catch( java.io.IOException ie )
	{
		ie.printStackTrace( System.out );
	}
	
}
}