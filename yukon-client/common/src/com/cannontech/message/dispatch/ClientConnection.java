package com.cannontech.message.dispatch;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.util.Message;
import com.roguewave.vsj.CollectableStreamer;

public class ClientConnection extends com.cannontech.message.util.ClientConnection 
{
/**
 * ClientConnection constructor comment.
 */
public ClientConnection() {
	super();
}
/**
 * ClientConnection constructor comment.
 * @param host java.lang.String
 * @param port int
 */
public ClientConnection(String host, int port) {
	super(host, port);
}

protected void fireMessageEvent(Message msg) 
{
	if( msg instanceof Command
		 && ((Command)msg).getOperation() == Command.ARE_YOU_THERE )
	{
		// Only instances of com.cannontech.message.dispatch.message.Command should
		// get here and it should have a ARE_YOU_THERE operation
		// echo it back so vangogh doesn't time out on us

		write( msg );
	}

	super.fireMessageEvent( msg );
}

/**
 * This method was created in VisualAge.
 */
protected void registerMappings(CollectableStreamer polystreamer) {
	super.registerMappings(polystreamer);

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();
	
	for( int i = 0; i < mappings.length; i++ )
	{
		polystreamer.register( mappings[i] );
	}

//	polystreamer.register()
}
}
