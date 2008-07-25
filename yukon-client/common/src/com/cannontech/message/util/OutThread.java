package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */

import java.util.concurrent.PriorityBlockingQueue;

import com.cannontech.clientutils.CTILogger;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualOutputStream;

class OutThread extends Thread {

	ClientConnection conn;
	
	VirtualOutputStream ostrm;
	CollectableStreamer streamer;
	PriorityBlockingQueue<Message> out;
/**
 * OutThread constructor comment.
 */
public OutThread(ClientConnection conn, VirtualOutputStream ostrm, CollectableStreamer streamer, PriorityBlockingQueue<Message> out) {
	super(conn.getName() + "OutThread");
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
		    Message msg = out.take();
		    ostrm.saveObject( msg, streamer );

		    ostrm.flush();
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

}
}
