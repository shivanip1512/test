package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.dispatch.message.Multi;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.VirtualInputStream;

class InThread extends Thread
{
	private ClientConnection conn;
	
	private VirtualInputStream istrm;
	private CollectableStreamer streamer;
	private java.util.ArrayList in;

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
			
			processMsg( o );

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

public void processMsg( Object o )
{
	//System.out.println(o);

	if( o instanceof Multi )
	{           
		Multi mpc = (Multi)o;

		for( int i = 0; i < mpc.getVector().size(); i++ )
			processMsg( mpc.getVector().get(i) );
	}
	else
	{
		if( conn.isQueueMessages() )
		{  
			// Add this message to the in queue so it can be 'read'
			synchronized( in )
			{
				in.add( o );								
				in.notifyAll();
			}			
		}
			
		if(o instanceof Message)
		{	
			// Tell out listeners about this message
			try 
			{
				// protect against misbehaved listeners											
				conn.fireMessageEvent((Message) o);
			}
			catch(Throwable t) 
			{
				CTILogger.error(getClass(), t);
			}
		}
	}

}


}
