package com.cannontech.customer.wpsc;

import org.apache.commons.lang.StringEscapeUtils;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (5/22/00 9:44:11 AM)
 * @author: 
 */
public class LDCNTSUM implements Runnable
{
	private com.cannontech.message.dispatch.DispatchClientConnection dispatchConn = null;

	private long writeDelay = 30000;
	private java.lang.String fileName = "LDCNTSUM.SND";


	private int SIGNAL_SHED_COUNTER = 0;
	private int SIGNAL_RESTORE_COUNTER = 0;
	private int SIGNAL_TERMINATE_COUNTER = 0;
	private int SIGNAL_CYCLE_COUNTER = 0;

	
/**
 * LDCNTSUM constructor comment.
 */
public LDCNTSUM(com.cannontech.message.dispatch.DispatchClientConnection newDispatchConn, String outputFile)
{
	super();
	this.dispatchConn = newDispatchConn;
	this.fileName = outputFile;
}
/**
 * Decode message finds all the signals in msg and puts them in a list
 * Creation date: (9/14/00 11:02:30 AM)
 * @param msg com.cannontech.message.util.Message
 * @param l java.util.List
 */
private void decodeMessge(com.cannontech.message.util.Message msg, java.util.List l) 
{
	
	if( msg instanceof com.cannontech.message.dispatch.message.Multi )
	{
		java.util.Vector v = ((com.cannontech.message.dispatch.message.Multi) msg).getVector();
		for( int i = 0; i < v.size(); i++ )
		{
			com.cannontech.message.util.Message m = (com.cannontech.message.util.Message) v.elementAt(i);
			if( m instanceof com.cannontech.message.dispatch.message.Signal )
				l.add(m);
		}
	}
	else if( msg instanceof com.cannontech.message.dispatch.message.Signal )
	{
		l.add(msg);
	}

	return;	
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/00 10:40:20 AM)
 * @return java.lang.String
 * @param signal com.cannontech.message.dispatch.message.Signal
 */
private String decodeSignal(com.cannontech.message.dispatch.message.Signal signal) 
{

	String retVal = null;
	
	try
	{
		//First determine whether we need to do any work
		if( signal.getPointID() != com.cannontech.database.data.point.PointTypes.SYS_PID_LOADMANAGEMENT )
			return null;

		//make sure the action is one we care about
		if( signal.getAction().indexOf("SHED") == -1 &&
			signal.getAction().indexOf("RESTORE") == -1 &&
			signal.getAction().indexOf("TERMINATE") == -1 &&
			signal.getAction().indexOf("CYCLE") == -1 )
			return null;
			
		java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat();
		decimalFormat.setMaximumFractionDigits(0);
			
		//Parse the action to see if it is one we want
		//Will be a group or a serial #
		String device;

		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM-dd-yy HH:mm:ss");
		String timeStamp = dateFormat.format( signal.getTimeStamp() );
		
		//The action string for each relay
		String relay[] = new String[3];
		relay[0] = "...      ";
		relay[1] = "...      ";
		relay[2] = "...      ";

		//Start parsing this thing
		String rawGroup = signal.getDescription().trim();
	 
		int index;
		if( (index = rawGroup.lastIndexOf("Group:")) != -1 )
		{
			device = getGroupAddress( rawGroup.substring( index+ "Group:".length(), rawGroup.indexOf("Relay:")).trim() );
			
		} 
		else if( (index = rawGroup.lastIndexOf("Serial:")) != -1)
		{
			String serial = rawGroup.substring( index+ "Serial:".length(), rawGroup.indexOf("Relay:")).trim();
				//Strip off the leading zeros from the serial string
				// 8/7/00 WPSC IS dept sais they can't handle leading zeroes			
				device = "SERIAL #" + Integer.parseInt(serial) + " ";
				
				while( device.length() < 26 )
					device = device.concat(" ");
		}
		else
			return null;

		//Determine the relays, relayStr is in the form "r1 r2 r3"
		String relayStr = rawGroup.substring( rawGroup.indexOf("Relay:")+"Relay:".length() ).trim();
		relayStr = relayStr.replace('r',' ');
		java.util.StringTokenizer tok = new java.util.StringTokenizer(relayStr);

		while( tok.hasMoreTokens() )
		{
			String temp = tok.nextToken();
			int r = Integer.parseInt( temp );

			if( (r >= 1) && (r <= 3) )
			{
				String action = signal.getAction().trim().toUpperCase();
				
				if( action.startsWith("SHED") )
				{
					SIGNAL_SHED_COUNTER++;
					String time = action.substring( action.indexOf("SHED")+"SHED".length()).trim().toUpperCase();																	
	//				relay[r] = "CTRL=" + time;					
					relay[r-1] = "CTRL=" + time;					
				}
				else if( action.equals("RESTORE SHED") )
				{
					SIGNAL_RESTORE_COUNTER++;
	//				relay[r] = "RESTORE";
					relay[r-1] = "RESTORE";
				}
				else if( action.equals("TERMINATE CYCLE") )
				{
					SIGNAL_TERMINATE_COUNTER++;
	//				relay[r] = "CYC=TERM";
					relay[r-1] = "CYC=TERM";
				}
				else if( action.startsWith("CYCLE") )
				{
					SIGNAL_CYCLE_COUNTER++;
					String percent = action.substring( action.indexOf("CYCLE")+"CYCLE".length() ).trim().toUpperCase();
	//				relay[r] = "CYC=" + percent;
					relay[r-1] = "CYC=" + percent;					
				}

				while( relay[r-1].length() < 9 )
					relay[r-1] = relay[r-1].concat(" ");
			}		
		}
		//build up the final string
		retVal = timeStamp + " LC " + relay[0] + relay[1] + relay[2] + device + "SEQ 1 ";
	}
	catch( Exception e )
	{
		WPSCMain.logMessage("decodeSignal() - encountered badly formed signal", com.cannontech.common.util.LogWriter.ERROR);
		com.cannontech.clientutils.CTILogger.info("decodeSignal() - encountered badly formed signal");
	}	
			
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/00 10:52:00 AM)
 * @return java.lang.String
 */
public java.lang.String getFileName() {
	return fileName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/00 4:38:49 PM)
 * @return java.lang.String
 * @param name java.lang.String
 */
public String getGroupAddress(String name) {

	name = StringEscapeUtils.escapeSql(name);
	
	String sql = "SELECT UtilityAddress,ClassAddress,DivisionAddress FROM " 
		       + " LMGroupVersacom,YukonPaobject WHERE LMGroupVersacom.DeviceID = " 
		       + " YukonPaobject.PAObjectID AND YukonPaobject.PAOName = ?";
	
	String retVal = null;
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, name);
		rset = stmt.executeQuery();
		
		if( rset.next() )
		{
			int u = rset.getInt(1);
			int c = rset.getInt(2);
			int d = rset.getInt(3);

			int temp = 0;
			for( int i = 0; i < 16; i++ )
				temp |= (((c >> i) & 0x0001) << (15-i));
			
			c = temp;
			temp = 0;
			
			for( int i = 0; i < 16; i++ )
			 	temp |= (((d >> i) & 0x0001) << (15-i));

			d = temp;

			String uStr = "U=" + u;
			while( uStr.length() < 12 )
				uStr = uStr + " ";
				
			String cStr = Long.toHexString(c).toUpperCase();
			while( cStr.length() < 4 )
				cStr = "0" + cStr;

			cStr = "C=" + cStr;
			while( cStr.length() < 7 )
				cStr = cStr + " ";
				
			String dStr = Long.toHexString(d).toUpperCase();
			while( dStr.length() < 4 )
				dStr = "0" + dStr;
				
			dStr = "D=" + dStr;
			while( dStr.length() < 7 )
				dStr = dStr + " ";
					
			retVal = uStr + cStr + dStr; 
		}			
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );

	}
	
	return retVal;
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 9:47:50 AM)
 * @return long
 */
public long getWriteDelay() {
	return writeDelay;
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2001 1:45:37 PM)
 */
public void resetCounters()
{
	SIGNAL_SHED_COUNTER = 0;
	SIGNAL_RESTORE_COUNTER = 0;
	SIGNAL_TERMINATE_COUNTER = 0;
	SIGNAL_CYCLE_COUNTER = 0;

}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 9:48:06 AM)
 */
public void run()
{
	try
	{
		while(WPSCMain.isService)
		{
			Object in;

			if( (in = dispatchConn.read(0L)) != null && in instanceof com.cannontech.message.util.Message )
			{				
				long writeAt = System.currentTimeMillis() + getWriteDelay();
				
				java.util.LinkedList writeList = new java.util.LinkedList();
				decodeMessge((com.cannontech.message.util.Message) in, writeList);

				if( writeList.isEmpty() )
					continue;

				while( writeAt > System.currentTimeMillis() )
				{
					if( (in = dispatchConn.read(0L)) != null && in instanceof com.cannontech.message.util.Message )
					{
						decodeMessge((com.cannontech.message.util.Message) in, writeList);
					}
					else
						Thread.sleep(200);							
				}

				
				java.io.PrintWriter writer = null;
				try
				{
					writer = new java.io.PrintWriter( new java.io.BufferedOutputStream( new java.io.FileOutputStream( getFileName(), true) ));
					com.cannontech.message.dispatch.message.Signal sig;
					java.util.Iterator iter = writeList.iterator();
					int receivedCount = 0;
					while( iter.hasNext() )
					{
						sig = (com.cannontech.message.dispatch.message.Signal) iter.next();

						if( WPSCMain.DEBUG )
						{
							com.cannontech.clientutils.CTILogger.info("\n[" + new java.util.Date() +"]   Signal received:\n" +sig);
							//Because of the logger not understanding an end of line character,
							//Each element is instead specified this way.  Hack..whatever...
							WPSCMain.logMessage("   Signal received:" , com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("                Id: " + sig.getPointID(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("          Log Type: " + sig.getLogType(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("  Logging Priority: " + sig.getCategoryID(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("       Description: " + sig.getDescription(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("            Action: " + sig.getAction(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("              Tags: " + sig.getTags(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("       Description: " + sig.getDescription(), com.cannontech.common.util.LogWriter.DEBUG);
							WPSCMain.logMessage("", com.cannontech.common.util.LogWriter.NONE);
						}
						
						String toWrite = decodeSignal(sig);
						if( toWrite != null )
						{
							com.cannontech.clientutils.CTILogger.info("LDCNTSUM:  " + toWrite );
							WPSCMain.logMessage("LDCNTSUM: "+ toWrite , com.cannontech.common.util.LogWriter.DEBUG);
							writer.write(toWrite + "\r\n");
						}
						else
						{
							com.cannontech.clientutils.CTILogger.info("Ignoring signal");
							WPSCMain.logMessage("Ignoring signal, decodeSignal(sig) returned null.: " , com.cannontech.common.util.LogWriter.DEBUG);
						}
					}

					WPSCMain.logMessage("LDCNTSUM ** "+ SIGNAL_SHED_COUNTER+ " SHEDs ** signals." , com.cannontech.common.util.LogWriter.INFO);
					WPSCMain.logMessage("LDCNTSUM ** "+ SIGNAL_RESTORE_COUNTER+ " RESTORE SHEDs ** signals." , com.cannontech.common.util.LogWriter.INFO);
					WPSCMain.logMessage("LDCNTSUM ** "+ SIGNAL_TERMINATE_COUNTER+ " TERMINATE CYCLEs ** signals." , com.cannontech.common.util.LogWriter.INFO);
					WPSCMain.logMessage("LDCNTSUM ** "+ SIGNAL_CYCLE_COUNTER+ " CYCLEs ** signals. " , com.cannontech.common.util.LogWriter.INFO);
					resetCounters();
					
						
					writer.flush();
					writeList.clear();
				}
				catch(java.io.FileNotFoundException fne )
				{
					fne.printStackTrace();
				}
				finally
				{
					if( writer != null ) writer.close();
				}
			}
			
			Thread.sleep(1000L);
		} 
	}
	catch( InterruptedException ie )
	{
		com.cannontech.clientutils.CTILogger.info( getClass() + " - interrupted");
		WPSCMain.logMessage("InterruptedException LDCNTSUM.run()."+ getClass() + " - interrupted." , com.cannontech.common.util.LogWriter.ERROR);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/00 10:52:00 AM)
 * @param newFileName java.lang.String
 */
public void setFileName(java.lang.String newFileName) {
	fileName = newFileName;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/00 9:47:50 AM)
 * @param newWriteDelay long
 */
public void setWriteDelay(long newWriteDelay) {
	writeDelay = newWriteDelay;
}
}
