package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.clientutils.CTILogger;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualOutputStream;

class OutThread extends Thread {

	ClientConnection conn;
	
	VirtualOutputStream ostrm;
	CollectableStreamer streamer;
	java.util.ArrayList out;
/**
 * OutThread constructor comment.
 */
public OutThread(ClientConnection conn, VirtualOutputStream ostrm, CollectableStreamer streamer, java.util.ArrayList out) {
	super("OutThread");
	setDaemon(true);
	this.conn = conn;
	
	this.ostrm = ostrm;
	this.streamer = streamer;
	this.out = out;
}
/**
 * This method was created in VisualAge.
 */
public void run() {

	int size;
	
	try
	{
		//Check to see if there is a registration message to be sent
		Object reg = conn.getRegistrationMsg();		
		if( reg != null ) {
			ostrm.saveObject( reg, streamer );
		}
				
		while(true)
		{			
			synchronized( out )
			{										
				if( (size = out.size()) > 0 ) {
					
					for(int i = 0; i < size; i++) {
						ostrm.saveObject( out.get(i), streamer );
					}

					ostrm.flush();
					out.clear();
				}

				// Wait until there is a message to consume
				out.wait();
			}			
		}	
	}
	catch( java.io.IOException e )
	{
		CTILogger.info("IOException in outThread: " + e.getMessage());
	}
	catch( InterruptedException ie )
	{
		CTILogger.info("InterruptedException in outThread");
	}

	//com.cannontech.clientutils.CTILogger.info("exiting OutThread::run");
}
}
