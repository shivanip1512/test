package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */

import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.CollectableStreamer;

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
			{
				HandleMessage handleThr = new HandleMessage( this.conn, o );
				handleThr.start();
			}
			else
			{
				synchronized( in )
				{
					in.add( o );
					in.notifyAll();
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
		com.cannontech.clientutils.CTILogger.info("  IOException in inThread occured : " + e.getMessage());
	}	
	
}
}
