package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.clientutils.CTILogger;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;

class InThread extends Thread {

	ClientConnection conn;
	
	VirtualInputStream istrm;
	CollectableStreamer streamer;
	java.util.ArrayList in;

	class HandleMessage extends Thread
	{
		ClientConnection connection;
		Object message;
		
		public HandleMessage(ClientConnection connection, Object message)
		{
			super("InThreadMsgHandler");
			this.connection = connection;
			this.message = message;
		}

		public void run()
		{
			connection.doHandleMessage( message );
		}
	}
/**
 * InThread constructor comment.
 */
public InThread(ClientConnection conn, VirtualInputStream istrm, CollectableStreamer streamer, java.util.ArrayList in) {
	super("InThread");
	setDaemon(true);
	this.conn = conn;
	
	this.istrm = istrm;
	this.streamer = streamer;
	this.in = in;
}
/**
 * This method was created in VisualAge.
 */
public void run() {
	
	try
	{
		for( ; ; )
		{
			Object o = istrm.restoreObject( streamer );
			
			if( conn.handleMessage( o ) )
			{	// Handle each incoming message in a new thread
				HandleMessage handleThr = new HandleMessage( this.conn, o );
				handleThr.start();
			}
			else
			if(conn.isQueueMessages())
			{   // Add this message to the in queue so it can be 'read'
				synchronized( in )
				{
					in.add( o );								
					in.notifyAll();
				}			
			}
			
			if(o instanceof Message)
			{	// Tell out listeners about this message
				try { // protect against misbehaved listeners											
					conn.fireMessageEvent((Message) o);
				}
				catch(Throwable t) {
					CTILogger.error(getClass(), t);
				}
			}

			if( this.isInterrupted() )
			{
				com.cannontech.clientutils.CTILogger.info("inThread was Interrupted");
				return;
			}
		}		
	}
	catch( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.debug("  IOException in inThread occured : " + e.getMessage());
	}	
	
}
}
