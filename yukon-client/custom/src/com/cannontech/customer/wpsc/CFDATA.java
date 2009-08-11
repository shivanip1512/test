package com.cannontech.customer.wpsc;

import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Insert the type's description here.
 * Creation date: (5/17/00 3:30:31 PM)
 * @author: 
 */
public class CFDATA extends com.cannontech.common.util.FileInterface {

/**
 * CFDATA constructor comment.
 * @param dirToWatch java.lang.String
 * @param fileExt java.lang.String
 */
public CFDATA(String dirToWatch, String fileExt) {
	super(dirToWatch, fileExt);
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/00 3:54:41 PM)
 * @return java.lang.String
 * @param line java.lang.String
 */
private String decodeLine(String line) {

	StringBuffer buf = new StringBuffer("PutConfig versacom serial ");
	
	try
	{
		int func;
		int temp;
		int classID = 0;
		int divID = 0;
			
		java.util.StringTokenizer tok = new java.util.StringTokenizer(line," ");

		//DLC Func
		func = Integer.parseInt( (String) tok.nextElement() );

		//SerialNumber
		temp = Integer.parseInt( (String) tok.nextElement() );
		buf.append( temp );
		
		if( func == 4 )
		{
			buf.append(" service in");
			return buf.toString();
		}
		else
		if( func == 5 )
		{
			buf.append(" service out");
			return buf.toString();
		}
		 	
		//Utility ID
		temp = Integer.parseInt( (String) tok.nextElement() );
		
		if( func != 2 )
		{
			buf.append(" utility ");
			buf.append(temp);
		}
		
		//Division Code
		temp = Integer.parseInt( (String) tok.nextElement() );
		buf.append(" aux ");
		buf.append(temp);

		//Operating District - section
		temp = Integer.parseInt( (String) tok.nextElement() );
		buf.append(" section ");
		buf.append(temp);

		//Class #1
		temp = Integer.parseInt( (String) tok.nextElement() );
		if( temp > 0 )
			classID |= (1 << (temp-1));
			
		//Class #2
		temp = Integer.parseInt( (String) tok.nextElement() );
		if( temp > 0 )
			classID |= (1 << (temp-1));

		//Class #3
		temp = Integer.parseInt( (String) tok.nextElement() );
		if( temp > 0 )
			classID |= (1 << (temp-1));
	
		if( classID > 65535 ) //Can only allow 16bits here
			return null;

		temp = 0;		
		for( int i = 0; i < 16; i++ )	
			temp |= (((classID >> i) & 0x0001) << (15-i));

		classID = temp;
		
		buf.append(" class 0x" + Long.toHexString( (long) classID ) );

		//DLC Division
		temp = Integer.parseInt( (String) tok.nextElement() );
		if( temp > 0 )
			divID |= (1 << (temp-1));

		if( divID > 65535 )
			return null;

		temp = 0;		
		for( int i = 0; i < 16; i++ )	
			temp |= (((divID >> i) & 0x0001) << (15-i));

		divID = temp;

		buf.append(" division 0x" + Long.toHexString( (long) divID ) );		
	}
	catch( Exception e )
	{		
		return null;
	}
	
	return buf.toString();
	
}
/**
 * handleFile method comment.
 */
protected void handleFile(java.io.InputStream in) 
{
	try
	{
	    IServerConnection porterConn = ConnPool.getInstance().getDefPorterConn();
		int countSent = 0;
		java.io.BufferedReader rdr = new java.io.BufferedReader( new java.io.InputStreamReader(in) );

		String str;
		com.cannontech.clientutils.CTILogger.info("Begin Processing CFDATA file...");
		WPSCMain.logMessage("Begin Processing CFDATA Files", com.cannontech.common.util.LogWriter.INFO);
		while( (str = rdr.readLine()) != null )
		{ 
			String decoded = decodeLine(str);

			if( decoded != null )
			{
				com.cannontech.clientutils.CTILogger.info("CFDATA: " + decoded);
				WPSCMain.logMessage(" ** CFDATA:  "+ decoded, com.cannontech.common.util.LogWriter.DEBUG);
				com.cannontech.message.porter.message.Request req = new com.cannontech.message.porter.message.Request( 0, decoded, 1L );
				porterConn.write(req);
				countSent++;
				Thread.sleep(500);
			}
		}
		com.cannontech.clientutils.CTILogger.info("...Done processing CFDATA file");
		WPSCMain.logMessage("Done Processing CFDATA Files  *  "+ countSent + "  *  read and wrote to Porter", com.cannontech.common.util.LogWriter.INFO);
	}
	catch(java.io.IOException e )
	{
		e.printStackTrace();
	}
	catch(InterruptedException e2 )
	{
		e2.printStackTrace();
	}
}
}
