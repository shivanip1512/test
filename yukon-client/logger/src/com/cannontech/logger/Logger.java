package com.cannontech.logger;

/**
 * Insert the type's description here.
 * Creation date: (5/10/00 9:20:59 AM)
 * @author: 
 * @Version: <version>
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.print.PrinterJob;

import javax.swing.Timer;

import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.parametersfile.ParametersFile;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.SqlStatement;
import com.cannontech.logger.config.LoggerMainFrame;
import com.cannontech.logger.scm.SCMEvent;
import com.cannontech.logger.scm.SCMEventListener;
import com.cannontech.logger.scm.SCMEventManager;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.klg.jclass.page.JCDocument;
import com.klg.jclass.page.JCFlow;
import com.klg.jclass.page.JCPrinter;
import com.klg.jclass.page.JCTextStyle;
import com.klg.jclass.page.awt.JCAWTPrinter;

public class Logger implements java.awt.event.ActionListener, SCMEventListener
{
	public static final String MODE_PRINTER = "PRINTER";
	public static final String MODE_FILE = "FILE";
	public static final String MODE_SCREEN = "SCREEN";

	private String printMode = MODE_PRINTER;//MODE_SCREEN;
	
	// register for everything by default
	private int pointRegistration = PointRegistration.REG_ALL_PTS_MASK | 
									PointRegistration.REG_EVENTS | 
					  				PointRegistration.REG_ALARMS;

	public final String VERSION = 
			com.cannontech.common.version.VersionTools.getYUKON_VERSION() + "2.0";

	Color priorityColors[] = null;
	private boolean colorEnabled = false;	
	private int pageNumber = 0;	
	private Timer timer = null;
	public final ParametersFile parametersFile = new ParametersFile( LoggerMainFrame.PARAMETER_FILE_NAME );
	public static final int MAX_LINES = 49;
	private int lineCount = 0;
	private JCPrinter printer = null;
	private JCFlow flow = null;
	private JCDocument document = null;
	private JCTextStyle normal = null;
	private String headerText = "TIMESTAMP           DEVICE NAME          POINT NAME      DESCRIPTION               ACTION";
	private String titleText = null;
		
	public static final String BLANK_SPACE = "                                                                                                        ";
	public static final String HEADER_LINE = "=================== ==================== =============== ========================= =========================";
	public static final int LINE_BREAK = 10;

	// lengths of each column
	public static final int TIMESTAMP_LENGTH = 19;
	public static final int DEVICENAME_LENGTH = 20;
	public static final int POINTNAME_LENGTH = 15;
	public static final int DESCRIPTION_LENGTH = 25;
	public static final int ACTION_LENGTH = 25;	
	public static final int TYPE_LENGTH = 10;

	public static final int[] COLUMN_LENGTHS =
	{
		TIMESTAMP_LENGTH,
		DEVICENAME_LENGTH,
		POINTNAME_LENGTH,
		DESCRIPTION_LENGTH,
		ACTION_LENGTH
	};

	public static final String[] DEFAULT_COLUMN_NAMES =
	{
		"TIMESTAMP",
		"DEVICENAME",
		"POINTNAME",
		"DESCRIPTION",
		"ACTION"
	};
	
/**
 * OutPutStream constructor comment.
 */
public Logger() 
{
	super();

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:59:27 PM)
 * Version: <version>
 * @return java.io.FileOutputStream
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if( e.getSource() == Logger.this.timer )
	{
		printPageNow();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/00 2:05:55 PM)
 * Version: <version>
 */
private void createNewPrinterTools() 
{
	printer = null;
	flow = null;
	document = null;
	System.gc();
	
	createPrintJob();
	document = new JCDocument( printer );	
	
	flow = new JCFlow( document );		
	document.setFlow( flow );		
	flow.setCurrentTextStyle( getTextStyle() );

	getTextStyle().setFontStyle( Font.BOLD );

//	if( pageNumber == 0 )
	printTitle();
	
	flow.print( BLANK_SPACE + pageNumber );
	flow.newLine();flow.newLine();
	
	flow.print( headerText );	
	flow.newLine();
	
	flow.print( HEADER_LINE );
	flow.newLine();
	lineCount = 0;
	
	getTextStyle().setFontStyle( Font.PLAIN );
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:41:11 PM)
 * Version: <version>
 * @return com.klg.jclass.page.JCPrinter
 */
private JCPrinter createPrintJob() 
{		
	try
	{
//com.cannontech.clientutils.CTILogger.info( System.getProperty("java.awt.printerjob", null) );
//sun.awt.windows.WPrinterJob j = new sun.awt.windows.WPrinterJob();
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.printDialog();

		pj.setJobName("CTI Logger Page " + (++pageNumber) );
		printer = new JCAWTPrinter( pj, pj.defaultPage(), false );
	}
	catch( JCAWTPrinter.PrinterJobCancelledException e)
	{
		com.cannontech.clientutils.CTILogger.info("USER CANCELED PRINT JOB");
	}
		
	return printer;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/00 10:59:47 AM)
 */
private void doPreStop() 
{
	printPageNow();
}
/**
 * Code to perform when this object is garbage collected.
 * 
 * Any exception thrown by a finalize method causes the finalization to
 * halt. But otherwise, it is ignored.
 */
protected void finalize() throws Throwable 
{
	// Insert code to finalize the receiver here.
	// This implementation simply forwards the message to super.  You may replace or supplement this.
	super.finalize();
	
com.cannontech.clientutils.CTILogger.info("	FINALIZING LOGGER");

	if( printMode.equalsIgnoreCase(MODE_PRINTER) )
	{
		printPageNow();
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7/19/00 3:47:14 PM)
 */
private Color[] getDBColors() 
{
	if( priorityColors == null )
	{
		SqlStatement statement = new SqlStatement("select foregroundcolor " +
			"from state where stategroupid=" + com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ALARM +
			" order by rawstate",
			LoggerClient.DBALIAS );

		try
		{
			statement.execute();
			priorityColors = new Color[statement.getRowCount()];
			
			for( int i = 0; i < statement.getRowCount(); i++ )
			{
				priorityColors[i] = Colors.getColor( Integer.parseInt( statement.getRow(0)[i].toString() ) );
			}
		}
		catch( com.cannontech.common.util.CommandExecutionException ex )
		{
			handleException( ex );
		}
	}
	
	return priorityColors;	
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/00 1:22:31 PM)
 * @return java.lang.String
 */
private String getHeaderText() 
{
	return
		(CommonUtils.formatString( parametersFile.getParameterValue("COLUMN0_NAME", ""), Logger.TIMESTAMP_LENGTH ) + " " +
		CommonUtils.formatString( parametersFile.getParameterValue("COLUMN1_NAME", ""), Logger.DEVICENAME_LENGTH ) + " " +
		CommonUtils.formatString( parametersFile.getParameterValue("COLUMN2_NAME", ""), Logger.POINTNAME_LENGTH ) + " " +
		CommonUtils.formatString( parametersFile.getParameterValue("COLUMN3_NAME", ""), Logger.DESCRIPTION_LENGTH ) + " " +
		CommonUtils.formatString( parametersFile.getParameterValue("COLUMN4_NAME", ""), Logger.ACTION_LENGTH ));
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/00 1:36:46 PM)
 * @param regMsg java.lang.String
 */
public int getPointReg() 
{
	return pointRegistration;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/00 4:19:53 PM)
 * Version: <version>
 * @return com.klg.jclass.page.JCTextStyle
 */
private JCTextStyle getTextStyle() 
{
	if( normal == null )
	{
		normal = new JCTextStyle("Normal");
		
		// Set up the font we will be using
		normal.setParagraphSpacing(1.5);
		Font pf = new Font("Monospaced", Font.PLAIN, 8); //110 chars is max for 1 row
		normal.setFont(pf);
	}
	
	return normal;
}
/**
 * Insert the method's description here.
 * Creation date: (7/24/00 10:43:34 AM)
 * @return java.lang.String
 */
private String getTitleText() 
{
	return titleText;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;	
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 4:45:52 PM)
 *	This method is called from the JNI invocation.
 */

public void handleSCMEvent(SCMEvent event)
{
	if(event.getID() == SCMEvent.SERVICE_STOPPED)
	{
		doPreStop();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/00 2:34:55 PM)
 * Version: <version>
 */
private void initialize() 
{
	// add the listener to hear the stop service command
	SCMEventManager.getInstance().addSCMEventListener(this);

	setLoggerParameters();

	if( printMode.equalsIgnoreCase(MODE_PRINTER) )
	{
		createNewPrinterTools();
	}
	else if( printMode.equalsIgnoreCase(MODE_SCREEN) )
	{
		titleText = BLANK_SPACE + "\r\n" 
						+ headerText + "\r\n" 
						+ HEADER_LINE;
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/19/00 2:46:42 PM)
 * Version: <version>
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	try
	{
		ClientSession session = ClientSession.getInstance(); 
		if(!session.establishSession(null))
			System.exit(-1);			
	
		if(session == null) 
			System.exit(-1);
			
		LoggerClient client = new LoggerClient( null, new Logger() );
		client.startConnection();
	
		while( true )
		{
			try
			{
				Thread.sleep( 1000 );
			}
			catch( InterruptedException ex )
			{
				com.cannontech.clientutils.CTILogger.info("InterruptedException occured in Main()");
			}
		}
	}
	catch( Exception e )
	{
		e.printStackTrace( System.err );
		System.exit(-1);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/00 2:32:27 PM)
 * Version: <version>
 */
private synchronized void printPageNow() 
{
	// make sure we have some lines and the document is not currently printing
	waitForPrinter();
	
	if( lineCount > 0 )
	{
		document.print( printer );
		flow.newPage();
		createNewPrinterTools();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/10/00 11:51:35 AM)
 * Version: <version>
 * @param line java.lang.String
 */
public synchronized void printTextLine(String line, long classification ) 
{
	// printing does not work on JDK 1.2 & 1.2.1 for JCPageLayout
	if( System.getProperty("java.version").equalsIgnoreCase("1.2" ) || 
		System.getProperty("java.version").equalsIgnoreCase("1.2.0" ) || 
		System.getProperty("java.version").equalsIgnoreCase("1.2.1" ) )
	{
		com.cannontech.clientutils.CTILogger.info("Java 1.2 and Java 1.2.1 are not supported");
		return;
	}

	if( printMode.equalsIgnoreCase( MODE_PRINTER ) )
	{
		printToPageLayout( line, classification );
	}
	else if( printMode.equalsIgnoreCase( MODE_SCREEN ) )
	{
		if( lineCount == 0 )
			com.cannontech.clientutils.CTILogger.info( getTitleText() );

		lineCount++;
		com.cannontech.clientutils.CTILogger.info( line );
	}
	else
		throw new IllegalArgumentException("Unrecognized PRINT_MODE, " + printMode);
		
		
}
/**
 * Insert the method's description here.
 * Creation date: (7/21/00 2:34:05 PM)
 */
private void printTitle() 
{
	if( titleText == null )
		return;

	Font oldFont = getTextStyle().getFont();

	Font newFont = new Font( oldFont.getName(), Font.BOLD, 18 );

	getTextStyle().setFont( newFont );
		
	flow.print( getTitleText() );
	flow.newLine();

	// set the current font back to the original
	getTextStyle().setFont( oldFont );
	
	lineCount++;
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/00 1:27:06 PM)
 * @param line java.lang.String
 * @param classification long
 */
private void printToPageLayout(String line, long classification) 
{
	setLineColor( classification );

	flow.print(line);
	getTextStyle().setColor( Color.black );
	
	flow.newLine();
	lineCount++;

	// print a blank line for every 10 printed lines
	if( (lineCount % LINE_BREAK) == 0 )
	{
		flow.newLine();
		lineCount++;
	}

	if( lineCount == MAX_LINES )
	{
		waitForPrinter();			
		com.cannontech.clientutils.CTILogger.info("Starting Print Job");
		document.print( printer );
		com.cannontech.clientutils.CTILogger.info("Finish Print");
		flow.endFlow();
		createNewPrinterTools();
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (5/16/00 2:27:43 PM)
 * Version: <version>
 * @param classification long
 */
private void setLineColor(long classification) 
{
	try
	{
		if( colorEnabled )
			getTextStyle().setColor( getDBColors()[(int)classification - 1] );
		else
			getTextStyle().setColor( Color.black ); // default value
	}
	catch( Exception ex )
	{
		getTextStyle().setColor( Color.black ); // default value
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 4:45:52 PM)
 * Version: <version>
 */
private void setLoggerParameters() 
{
	com.cannontech.clientutils.CTILogger.info("	Version      : " + VERSION);
	com.cannontech.clientutils.CTILogger.info("	Param File   : " + LoggerMainFrame.PARAMETER_FILE_NAME );
	
	if( parametersFile != null && parametersFile.parametersExisted() )
	{
		try
		{
			setPointReg( parametersFile.getParameterValue("REGISTRATION", "ALL") );
			com.cannontech.clientutils.CTILogger.info("	Registration : " + parametersFile.getParameterValue("REGISTRATION", "ALL") );
			
			colorEnabled = Boolean.getBoolean( parametersFile.getParameterValue("COLOR_TOGGLE", "false") );
			com.cannontech.clientutils.CTILogger.info("	Black/White  : " + parametersFile.getParameterValue("COLOR_TOGGLE", "false") );

			int seconds = Integer.parseInt( parametersFile.getParameterValue("OUTPUT_SECONDS", "0") ) + 			
					(60 * Integer.parseInt( parametersFile.getParameterValue("OUTPUT_MINUTES", "0") ) +
					 3600 * Integer.parseInt( parametersFile.getParameterValue("OUTPUT_HOURS", "0") ) );

			if( seconds > 0 )
			{				
				timer = new Timer( (seconds > 60 ? seconds : 60) * 1000, this );
				// kick it off
				timer.start();
				com.cannontech.clientutils.CTILogger.info("	AutoOutput : " + (seconds > 60 ? seconds : 60) + " seconds");
			}
			else
				com.cannontech.clientutils.CTILogger.info("	AutoOutput : Disabled");

			headerText = getHeaderText();

			titleText = new String( parametersFile.getParameterValue("PRINT_TITLE", "") );			
		}
		catch( Exception t )  // catch all, no real biggy
		{
			com.cannontech.clientutils.CTILogger.info( t.getMessage() );
		}
		
	}
	else
		com.cannontech.clientutils.CTILogger.info("	Parameters file is NULL or does not exist");
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/00 1:36:46 PM)
 * @param regMsg java.lang.String
 */
private void setPointReg(String regMsg) 
{
	// default of pointRegistration is ALL
	if( regMsg.equalsIgnoreCase("EVENTS") )
		pointRegistration = PointRegistration.REG_EVENTS;
	else if( regMsg.equalsIgnoreCase("ALARMS") )
		pointRegistration = PointRegistration.REG_ALARMS;
	else if( regMsg.equalsIgnoreCase("CALCULATED") )
		pointRegistration = PointRegistration.REG_ALL__CALCULATED;
	else if( regMsg.equalsIgnoreCase("ACCUMULATOR") )
		pointRegistration = PointRegistration.REG_ALL_ACCUMULATOR;
	else if( regMsg.equalsIgnoreCase("ANALOG") )
		pointRegistration = PointRegistration.REG_ALL_ANALOG;
	else if( regMsg.equalsIgnoreCase("STATUS") )
		pointRegistration = PointRegistration.REG_ALL_STATUS;

}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 12:17:48 PM)
 * Version: <version>
 */
private void waitForPrinter() 
{
	while( printer.isDocumentOpen() )
	{
		try
		{
			Thread.sleep(1000);
		}
		catch( InterruptedException ex )
		{
			handleException( ex );
		}
		
		com.cannontech.clientutils.CTILogger.info("	Wating for printer");		
	}	
}
}
